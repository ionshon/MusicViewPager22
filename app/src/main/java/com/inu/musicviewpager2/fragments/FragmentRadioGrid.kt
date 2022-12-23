package com.inu.musicviewpager2.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.inu.musicviewpager2.R
import com.inu.musicviewpager2.adapter.RadioAdapter
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.databinding.FragmentRadioGridBinding
import com.inu.musicviewpager2.model.MusicDevice
import com.inu.musicviewpager2.model.Radio
import com.inu.musicviewpager2.util.CacheInterceptor
import com.inu.musicviewpager2.util.MyApplication
import com.inu.musicviewpager2.util.SetStreamUrl
import dlna.model.Devices
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXParseException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import kotlin.concurrent.thread

class FragmentRadioGrid : Fragment() {
    private var radioImages = arrayOf(
        R.drawable.kbs1, R.drawable.kbs_cool_fm,
        R.drawable.kbs973, R.drawable.kbs_classic,
        R.drawable.cbs939, R.drawable.cbs981,
        R.drawable.sbslove, R.drawable.sbs_power,
        R.drawable.mbc_fm, R.drawable.mbc_fm4u,
        R.drawable.ytn945, R.drawable.arirangfm,
        R.drawable.tbs_fm, R.drawable.tbs_efm
    )
//    var radioAddrs = listOf<String>("kbs1Radio","kbs2Radio","kbsCoolFM", "kbsClassic","sbsPower","sbsLove","mbcFm","mbcFm4u")

    lateinit var binding: FragmentRadioGridBinding
    private lateinit var adapter: RadioAdapter
    private lateinit var recyclerView: RecyclerView
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRadioGridBinding.inflate(inflater, container, false)
        radioImages
        var dataList: MutableList<Radio> = setData()
        swipeRefreshLayout = binding.refreshLayout

        adapter = RadioAdapter()
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter

        adapter.listData = dataList
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        swipeRefreshLayout!!.setOnRefreshListener {
            /*for (data in MusicDevice.radioAddr.values) {
                if (data == "") {
                    mPl = data.detail
                    var str : String = "MusicConstants.RADIO_ADDR." + mPl
                    SetStreamUrl().setStreamUrl("${"MusicConstants.RADIO_ADDR. + $mPl"}")
                    Log.d("swipeRefreshLayout", "${str}")
                }
                Log.d("swipeRefreshLayout", "$data")
            }*/
//            val i = MusicDevice.radioAddr.values.indexOf("")
//            Log.d("swipeRefreshLayout","isRefreshing = false, ${i}")
//            SetStreamUrl().setStreamUrl(mPl)
            dataList = setData()
            adapter = RadioAdapter()
            recyclerView = binding.recyclerView
            recyclerView.adapter = adapter
            adapter.listData.clear()
            adapter.listData.addAll(dataList)
            /* if (!MusicDevice.radioAddr.values.contains("")) {
                 swipeRefreshLayout!!.isRefreshing = false
                 Log.d("swipeRefreshLayout","isRefreshing = false, ${MusicDevice.radioAddr.values}")
                 for (data in dataList)
                     Log.d("swipeRefreshLayout","$data")
             } else {
                 swipeRefreshLayout!!.isRefreshing = false
                 Log.d("swipeRefreshLayout", "isRefreshing = true\n" +
                         "${dataList}")
             }*/
            swipeRefreshLayout!!.isRefreshing = false
        }

       /* for (addr in MusicDevice.temp) {
//            val title = getRadioAddress(addr)
            Log.d("MusicConstants", "${addr}")
        }*/
        return binding.root
    }

    private fun setData(): MutableList<Radio> {
        val data: MutableList<Radio> = mutableListOf()
        for (num in 1..radioImages.size) {
            val addr : String = MusicDevice.radioAddr.values.toList()[num-1]
            val detail : String = MusicDevice.radioAddr.keys.toList()[num-1]
//            Log.d("MusicConstants in setdata", "${addr}")
            val listData = Radio(radioImages[num-1], addr, detail)
            data.add(listData)
        }
        return data
    }

}