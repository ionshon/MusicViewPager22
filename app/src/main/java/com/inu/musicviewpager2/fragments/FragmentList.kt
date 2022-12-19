package com.inu.musicviewpager2.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inu.musicviewpager2.adapter.MusicAdapter
import com.inu.musicviewpager2.MusicProvider
import com.inu.musicviewpager2.MusicProvider.getMusicList
import com.inu.musicviewpager2.databinding.FragmentListBinding
import com.inu.musicviewpager2.model.Music
import com.inu.musicviewpager2.model.MusicDevice.deviceMusics
import com.inu.musicviewpager2.model.MusicDevice.musicList
import com.inu.musicviewpager2.util.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.system.exitProcess

class FragmentList : Fragment() {
    private val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val adapter : MusicAdapter by lazy { MusicAdapter() } // outer scope

    private lateinit var binding: FragmentListBinding

    var searchList = mutableListOf<Music>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)



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

        return binding.root
    }

    private fun setListview() {
        binding.recyclerViewList.apply {
            this.layoutManager = LinearLayoutManager(MyApplication.applicationContext())
            this.adapter = this@FragmentList.adapter // Qualified this
            this.layoutManager = LinearLayoutManager(context)
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

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
//        CoroutineScope(Dispatchers.IO).launch { MusicProvider.getGenre(requireContext())
//        }
    }
}