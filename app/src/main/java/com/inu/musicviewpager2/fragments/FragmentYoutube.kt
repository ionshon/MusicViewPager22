package com.inu.musicviewpager2.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.inu.musicviewpager2.R


class FragmentYoutube : Fragment() {
    var search: EditText? = null
    var button: Button? = null

    var searchTask: AsyncTask<*, *, *>? = null

//    var sdata: ArrayList<SearchData> = ArrayList<SearchData>()

    val serverKey = "본인의 앱키를 입력하세요"


    var recyclerview: RecyclerView? = null

//    var utubeAdapter: UtubeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var request = require(true)
        var optionParams = mapOf(
            "q" to "kakao",
            "part" to "snippet",
            "key" to "★자기key값★",
            "maxResults" to 2
        )

        var url="https://www.googleapis.com/youtube/v3/search?";
        for(option in optionParams.keys){
            url += "$option" + "=" + "${optionParams[option]}" + "&"
        }
       /* request(url, function(err, res, body){
            console.log(body)
        })*/

//url의마지막에 붙어있는 & 정리
        url = url.substring(0, url.length-1);

        return inflater.inflate(R.layout.fragment_youtube, container, false)
    }

}