package com.inu.musicviewpager2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.inu.musicviewpager2.adapter.FragmentPagerAdapter
import com.inu.musicviewpager2.constant.MusicConstants
import com.inu.musicviewpager2.databinding.ActivityMainBinding
import com.inu.musicviewpager2.fragments.*
import com.inu.musicviewpager2.model.MusicDevice
import com.inu.musicviewpager2.util.SetStreamUrl

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 1. 페이지 데이터 로드
        val list = listOf(FragmentList(), /*FragmentUpnp(),*/ FragmentPlay(), /*FragmentRadio(),*/ FragmentRadioGrid())
        // 2. 아답터 생성
        val pagerAdapter = FragmentPagerAdapter(list, this)

        // 화면 유지
        binding.viewPager.offscreenPageLimit = list.size
        // 3. 아답터와 뷰페이저 연결
        binding.viewPager.adapter = pagerAdapter

        // 4. 탭 메뉴의 갯수만큼 제목을 목록으로 생성
        val titles = listOf("음악",/*"UPNP",*/ "PLAY"/*,"RADIO"*/,"라디오")
        // 5. 탭레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        getRadioAddress()
    }

    fun fSetPlayableUrl(mPls: String) {
//        val oSetStreamUrl = SetStreamUrl()
//        oSetStreamUrl.setStreamUrl(mPls)
        SetStreamUrl().setStreamUrl(mPls)
    }

    fun getRadioAddress() {
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.kbs1Radio)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.kbs2Radio)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.kbsCoolFM)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.kbsClassic)
//        fSetPlayableUrl(MusicConstants.RADIO_ADDR.cbs939)
//        fSetPlayableUrl(MusicConstants.RADIO_ADDR.cbs981)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.sbsPower)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.sbsLove)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.mbcFm)
        fSetPlayableUrl(MusicConstants.RADIO_ADDR.mbcFm4u)

//        fSetPlayableUrl(MusicConstants.RADIO_ADDR.ytn945)
//        fSetPlayableUrl(MusicConstants.RADIO_ADDR.arirang)
//        fSetPlayableUrl(MusicConstants.RADIO_ADDR.tbsfm)
//        fSetPlayableUrl(MusicConstants.RADIO_ADDR.tbsBusan)


    }
}