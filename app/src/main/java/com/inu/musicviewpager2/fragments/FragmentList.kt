package com.inu.musicviewpager2.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inu.musicviewpager2.adapter.MusicAdapter
import com.inu.musicviewpager2.MusicProvider.getMusicList
import com.inu.musicviewpager2.databinding.FragmentListBinding
import com.inu.musicviewpager2.model.Music
import com.inu.musicviewpager2.model.MusicDevice.deviceMusics
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.util.BubbleListener
import com.inu.musicviewpager2.util.MyApplication
import com.inu.musicviewpager2.util.SimpleOffsetDecoration
import kotlin.system.exitProcess

class FragmentList : Fragment() {
    private val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val adapter : MusicAdapter by lazy { MusicAdapter() } // outer scope
    private lateinit var binding: FragmentListBinding
    lateinit var recyclerView: RecyclerView
    lateinit var handleView: ImageView
    lateinit var bubble: View
    lateinit var bubbleText: TextView
    lateinit var bubbleListener: BubbleListener

    var searchList = mutableListOf<Music>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewList
        // HandleView
        handleView = binding.handleView
        handleView.bringToFront()
        // BubbleView (optional feature)
        // must implement with BubbleAdapter. see @SimpleAdapter
        bubble = binding.bubble
        bubble.bringToFront()
        bubbleText = binding.bubbleText
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
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
//                Toast.makeText(requireContext(), "권한 성공", Toast.LENGTH_SHORT).show()
                if (deviceMusics.size == 0) {
                    deviceMusics.addAll(getMusicList(requireContext()))
                }
                setListview()
            } else {
                Toast.makeText(requireContext(), "권한 요청 실행해야지 앱 실행", Toast.LENGTH_SHORT).show()
                exitProcess(0)
            }
        }

        requestPermissionLauncher.launch(permission)

        binding.searchViewMusic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색 버튼 누를 때 호출
                searchList.clear()
                for (m in deviceMusics) {
                    if (m.artist?.lowercase()?.contains(query.toString())!! || m.title?.lowercase()?.contains(query.toString())!!){
                        searchList.add(m)
                    }
                }
                setSearchListview()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return true
            }
        })

        binding.buttonAllmusic.setOnClickListener {
            setListview()
        }

        binding.imageViewShuffle.setOnClickListener {
            musicList.shuffle()
            adapter.updateList()
        }
        binding.imageViewGenre.setOnClickListener {
            musicList.sortBy { it.genre }
            adapter.updateList()
        }
        binding.imageViewAlbum.setOnClickListener {
            musicList.sortBy { it.albumId }
            adapter.updateList()
        }
        return binding.root
    }

    private fun setListview() {
        recyclerView.apply {
            this.adapter = this@FragmentList.adapter // Qualified this
            this.layoutManager = LinearLayoutManager(context)
//            addItemDecoration(SimpleOffsetDecoration(20))
        }.also { recyclerView ->
            // Bind
            com.inu.musicviewpager2.util.FastScroller(handleView, bubbleListener, recyclerView).bind(recyclerView)
            Log.d("bubleAdapter","0")
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
            this.layoutManager = LinearLayoutManager(MyApplication.applicationContext())
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
//        CoroutineScope(Dispatchers.IO).launch { MusicProvider.getGenre(requireContext())
//        }
    }
}