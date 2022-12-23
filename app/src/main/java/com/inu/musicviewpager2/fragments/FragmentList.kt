package com.inu.musicviewpager2.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.inu.musicviewpager2.MusicProvider.getMusicList
import com.inu.musicviewpager2.adapter.MusicAdapter
import com.inu.musicviewpager2.adapter.MusicAdapter.Companion.currentSongID
import com.inu.musicviewpager2.adapter.MusicAdapter.Companion.index
import com.inu.musicviewpager2.adapter.MusicAdapter.Companion.oldPosition
import com.inu.musicviewpager2.adapter.MusicAdapter.Companion.searching
import com.inu.musicviewpager2.database.MusicRoomDatabase
import com.inu.musicviewpager2.databinding.FragmentListBinding
import com.inu.musicviewpager2.model.Music
import com.inu.musicviewpager2.model.MusicDevice.deviceMusics
import com.inu.musicviewpager2.model.MusicDevice.isRadioOn
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.service.ForegroundService.Companion.isAlbum
import com.inu.musicviewpager2.util.BubbleListener
import com.inu.musicviewpager2.util.FastScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

const val REQ_PERMISSION_MAIN = 1001
class FragmentList : Fragment() {
    private var db: MusicRoomDatabase? = null
    lateinit var permissions: Array<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val adapter : MusicAdapter by lazy { MusicAdapter() } // outer scope
    private lateinit var binding: FragmentListBinding
    lateinit var recyclerView: RecyclerView
    lateinit var handleView: ImageView
    lateinit var bubble: View
    lateinit var bubbleText: TextView
    lateinit var bubbleListener: BubbleListener
    lateinit var groupedList: Map<Int, List<Music>>

    var searchList = mutableListOf<Music>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_AUDIO) //, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        recyclerView = binding.recyclerViewList/*
        val recyclerView = recyclerView.itemAnimator  // 빰빡임 방지
        if (recyclerView is SimpleItemAnimator) {
            (recyclerView as SimpleItemAnimator).supportsChangeAnimations = false
        }*/
        recyclerView.itemAnimator = null
        // HandleView
        handleView = binding.handleView
//        handleView.setBackgroundColor(Color.TRANSPARENT)
        handleView.clipToOutline = true
        handleView.bringToFront()
        // BubbleView (optional feature)
        // must implement with BubbleAdapter. see @SimpleAdapter
        bubble = binding.bubble
        bubble.bringToFront()
        bubbleText = binding.bubbleText

        db = MusicRoomDatabase.getInstance(requireContext())
        bubbleListener = object : BubbleListener {
            override fun setBubble(str: String) {
                bubbleText.text = str
            }
            override fun setViewY(y: Float) {
                bubble.y = y
            }
            override fun setVisible(isVisible: Boolean) {
                bubble.visibility = if (isVisible) View.VISIBLE else View.GONE
            }
        }

        binding.imageViewBookmark.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { // 코루틴 사용 비동기로 실행.
                val bookmarkData = db!!.musicDao().getAllMusics()
                for (data in bookmarkData) {
                    Log.d("db 조회", "북마크데이터 -> ${data.title}")
                }
            }
        }

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "권한 성공", Toast.LENGTH_SHORT).show()
                if (deviceMusics.size == 0) {
                    deviceMusics.addAll(getMusicList(requireContext()))
                }
                setListview()
            } else {
                Toast.makeText(requireContext(), "권한 요청 실행해야지 앱 실행", Toast.LENGTH_SHORT).show()
                exitProcess(0)
            }
        }

       /* val deniedPermission = checkPermission()
        if (deniedPermission.size > 0) {
            // 권한 요청
            permissions.forEach { p ->
                requestPermissionLauncher.launch(p)
            }
            *//*ActivityCompat.requestPermissions(
                requireActivity(),
                permissions.toTypedArray(),
                REQ_PERMISSION_MAIN
            )*//*
        }*/
        // api12 이하
       permissions.forEach { p ->
            requestPermissionLauncher.launch(p)
        }

        binding.searchViewMusic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searching = true
                currentSongID = try {
                    musicList[index].id
                } catch (e: Exception) {
                    0
                } finally {
                    0
                }
                groupedList = deviceMusics.groupBy { it.id }
                oldPosition = groupedList.keys.indexOf(currentSongID) // 전체리스트로 돌아갈때를 위해
                if (oldPosition != -1)
                    deviceMusics[oldPosition].isSelected = false          // 포지션 해제

                // 검색 버튼 누를 때 호출
                searchList.clear()
                for (m in deviceMusics) {
                    if (m.artist?.lowercase()?.contains(query.toString())!! || m.title?.lowercase()?.contains(query.toString())!!){
                        searchList.add(m)
                    }
                }
                // 실행중인 곡이 서치리스트에 있으면 index=true 없으면 index = 1
                groupedList = searchList.groupBy { it.id }
                oldPosition = groupedList.keys.indexOf(currentSongID)
                if (oldPosition != -1) {
                    index = oldPosition
                    searchList[oldPosition].isSelected = true
                }
                isAlbum = true
                setSearchListview()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return true
            }
        })

        binding.buttonAllmusic.setOnClickListener {
            searching = false
            currentSongID = musicList[index].id
            groupedList = deviceMusics.groupBy { it.id }
            oldPosition = groupedList.keys.indexOf(currentSongID)
            index = oldPosition
            isAlbum = true // 정적 화면 변경
            setListview()
        }

        binding.imageViewShuffle.setOnClickListener {
//            var groupedList = musicList.groupBy { it.id } // 데이터는 아래와 같다.
//            println("groupedList=>  ${groupedList[musicList[index].id]}") // 선택한(PLAY 중인) 송만 출력

            currentSongID = musicList[index].id
            musicList.shuffle()

            groupedList = musicList.groupBy { it.id }
            oldPosition = groupedList.keys.indexOf(currentSongID)
            index = oldPosition
            isAlbum = true // 정적 화면 변경
            adapter.updateList()
        }
        binding.imageViewGenre.setOnClickListener {
            currentSongID = musicList[index].id
            musicList.sortBy { it.genre }
            groupedList = musicList.groupBy { it.id }
            oldPosition = groupedList.keys.indexOf(currentSongID)
            index = oldPosition
            isAlbum = true // 정적 화면 변경
            adapter.updateList()
        }
        binding.imageViewAlbum.setOnClickListener {
            currentSongID = musicList[index].id
            musicList.sortBy { it.albumId }
            groupedList = musicList.groupBy { it.id }
            oldPosition = groupedList.keys.indexOf(currentSongID)
            index = oldPosition
            isAlbum = true // 정적 화면 변경
            adapter.updateList()
        }
        return binding.root
    }

/*    fun checkPermission(): MutableList<String> {
        val deniedPermissionList = mutableListOf<String>()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_DENIED) {
                // 거부된 권한
                deniedPermissionList.add(it)
            }
        }
        return deniedPermissionList
    }*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("requestpermmition","?")
        when(requestCode) {
            // 메인 퍼미션 권한 요청 코드
            REQ_PERMISSION_MAIN -> {
                val deniedPermissionList = mutableListOf<String>()
                grantResults.forEachIndexed { index, i ->
                    if (i == PackageManager.PERMISSION_DENIED) {
                        // 거부된 권한
                        deniedPermissionList.add(permissions[index])
                    }
                }
                if (deniedPermissionList.size > 0) {
                    // 거부된 권한 존재
                    Toast.makeText(requireContext(),"${deniedPermissionList}거부됨", Toast.LENGTH_SHORT).show()
                    Log.d("requestpermmition","허용안됨")
                    exitProcess(0)
                } else {
                    // 권한 모두 동의
//                    passPermission()
                    Toast.makeText(requireContext(),"모두 허용됨", Toast.LENGTH_SHORT).show()
                    Log.d("requestpermmition","허용")
//
                    if (deviceMusics.size == 0) {
                        deviceMusics.addAll(getMusicList(requireContext()))
                    }
                    setListview()

                }
            }
        }
    }

    private fun setListview() {
        recyclerView.apply {
            this.adapter = this@FragmentList.adapter // Qualified this
            this.layoutManager = LinearLayoutManager(context)
//            addItemDecoration(SimpleOffsetDecoration(20))
        }.also { recyclerView ->
            // Bind
            FastScroller(handleView, bubbleListener).bind(recyclerView)
        }
        musicList.clear()
        musicList.addAll(deviceMusics)
        /*musicList.clear()
        musicList.addAll(deviceMusics)
        recyclerViewList.adapter = adapter
        recyclerViewList.layoutManager = LinearLayoutManager(requireContext())*/
    }

    private fun setSearchListview() {
        binding.recyclerViewList.apply {
            this.adapter = this@FragmentList.adapter // Qualified this
            this.layoutManager = LinearLayoutManager(context)
        }
        musicList.clear()
        musicList.addAll(searchList)
//        recyclerViewList.adapter = adapter
//        recyclerViewList.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        adapter.updateItem()
        if (!isRadioOn) {
            recyclerView.scrollToPosition(index)
        }
//        CoroutineScope(Dispatchers.IO).launch { MusicProvider.getGenre(requireContext())
//        }
    }
}