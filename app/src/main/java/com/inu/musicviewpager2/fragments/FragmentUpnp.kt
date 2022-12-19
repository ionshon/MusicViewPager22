package com.inu.musicviewpager2.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dlna.UPnPDeviceAdapter
import dlna.UPnPDeviceFinder
import dlna.model.Devices
import dlna.model.UpnpDevice
import com.inu.musicviewpager2.databinding.FragmentUpnpBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class FragmentUpnp : Fragment() {

    var isFired = false
    private lateinit var binding: FragmentUpnpBinding
    private var recyclerview: RecyclerView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    var adapter: UPnPDeviceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpnpBinding.inflate(inflater, container, false)

        swipeRefreshLayout = binding.refreshLayout
        adapter = UPnPDeviceAdapter()
        with(binding) {
            recyclerview.adapter = adapter
            recyclerview.layoutManager = LinearLayoutManager(context)
            recyclerview.visibility = View.INVISIBLE
            spinner.visibility = View.VISIBLE
        }

        if (Devices.deviceList.size != 0) {
            binding.spinner.visibility = View.INVISIBLE
            binding.recyclerview.visibility = View.VISIBLE
        }
        swipeRefreshLayout!!.setOnRefreshListener {
            isFired = false
            swipeRefreshLayout!!.isRefreshing = false
            if (Devices.deviceList.size != 0) {
                binding.spinner.visibility = View.INVISIBLE
                binding.recyclerview.visibility = View.VISIBLE
            }
            Devices.deviceList.clear()
            onStart()
            Log.d("swiferefrash","isFired =>$isFired")
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("onstart","isFired =>$isFired")

        val observer = object : DisposableObserver<UpnpDevice>() {
            override fun onNext(t: UpnpDevice) { // 장치 출력
                Log.i(TAG, "location in onNext:  ${t.location}, ${binding.spinner.visibility}")
                if (0 == adapter!!.itemCount) { // 값과 종류까지 같은지
                    Log.d("TEST","observer onNext0: $t, ${binding.spinner.visibility}")
                    binding.spinner.animate()
                        .alpha(0f)
                        .setDuration(1000)
                        .setInterpolator(AccelerateInterpolator())
                        .start()
                    binding.recyclerview.alpha = 0f
                    binding.recyclerview.visibility = View.VISIBLE
                    binding.recyclerview.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setStartDelay(1000)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
                Log.d("TEST","observer onNext1: ${t.location}")
                if (!Devices.deviceList.keys.contains(t.location.toString())) {
                    Log.d("keykey: isLoc"," true")
                    Devices.deviceList[t.location.toString()] = t
//                    adapter?.add(t)
                    adapter?.updateDeviceList(Devices.deviceList.values.toMutableList())
                } else Log.d("keykey: isLoc"," false")

//                val valueList = Devices.deviceList.values.toList()
//                adapter?.updateDeviceList(valueList)
            }

            override fun onError(e: Throwable) {
                Log.d("TEST","observer 에러: socket is null")
            }

            override fun onComplete() {
                Log.d("TEST","observer onConplete: ${binding.spinner.visibility},  ${binding.recyclerview.visibility} ")
                Log.d("TEST","observer 컴플릿: Hash: ${Devices.deviceList["http://192.168.0.1:64640/etc/linuxigd/gatedesc.xml"]}")

                adapter?.updateDeviceList(Devices.deviceList.values.toMutableList())
                Log.d("TEST","observer 컴플릿: ${Devices.deviceList}")
            }
        }

        var i = 0
        for (value in Devices.deviceList.values.toMutableList()) { // 빨리 지나가서 안됨
            Log.d("keykeyu","whatnull => 처음에")
            if (value.friendlyName == "" || value.iconUrl == "" || value.presentationUrl == "") {
                isFired = false
                Log.d("keykeyu","whatnull => ${value.friendlyName}, ${value.iconUrl}")
            } else {
                i+=1
                if (i == Devices.deviceList.values.toMutableList().size) {
                    isFired = true
                }
                Log.d("keykeyu","whatnull =>$isFired")
            }
        }

        Log.d("keykeyu","isFired =>$isFired")
        if (!isFired) {
            UPnPDeviceFinder().observe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
        }

        adapter?.updateDeviceList(Devices.deviceList.values.toMutableList())
    }
}