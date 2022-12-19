package dlna

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dlna.model.UpnpDevice
import com.inu.musicviewpager2.R
import com.inu.musicviewpager2.databinding.RowupnpDeviceBinding
import com.inu.musicviewpager2.module.GlideApp


class UPnPDeviceAdapter: RecyclerView.Adapter<UPnPDeviceAdapter.Holder>() {

//    var mItems = mutableListOf<UPnPDevice>() //Devices.deviceList
    var deviceList = mutableListOf<UpnpDevice>()

    /*class SleepNightDiffCallback : DiffUtil.ItemCallback<UpnpDevice>() {
        override fun areItemsTheSame(oldItem: UpnpDevice, newItem: UpnpDevice): Boolean {
            return oldItem.location == newItem.location
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UpnpDevice, newItem: UpnpDevice): Boolean {
            return oldItem == newItem
        }
    }*/

//    private val mComparator: Comparator<UpnpDevice> = UPnPDeviceComparator()
    inner class Holder(val binding: RowupnpDeviceBinding): RecyclerView.ViewHolder(binding.root){
        fun setDevice(item: UpnpDevice) {
            with(binding) {
                    GlideApp.with(binding.root)
                        .load(item.iconUrl)
                        .error(R.drawable.ic_server_network)
                        .centerInside()
                        .into(icon)
            }
//            Log.d("item: ", "${item.iconUrl}")
            binding.friendlyName.text = item.friendlyName
            binding.location.text = item.location.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = RowupnpDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
//        Log.d("onBindViewHolder: ", "deviceList: ${deviceList}")
        val intent = Intent(holder.itemView.context, AboutActivity::class.java)

        val item: UpnpDevice = deviceList[position] // getItem(position)
        holder.setDevice(item)

        holder.binding.linearLayout.setOnClickListener {
            intent.putExtra("key0", item) //.location.toString())
            holder.itemView.context.startActivity(intent)
            Log.d("onclick root: ", "locatio=>: ${item.location}")
        }
        holder.binding.button.setOnClickListener {
            intent.putExtra("key1", item.urlBase)
            holder.itemView.context.startActivity(intent)
//            Log.d("oncLonglick root: ", "presentarionUrl: ${item.presentationUrl}")
        }
    }

    override fun getItemCount(): Int {
        return  deviceList.size
    }

    fun updateDeviceList(list: MutableList<UpnpDevice>) {
        deviceList = list
        notifyDataSetChanged() // 리스트 변경을 adapter에 알림
    }

   /* fun add(item: UpnpDevice) {
        val index = Collections.binarySearch(deviceList, item, mComparator)
        if (index < 0) {
            val position = -index - 1
            deviceList.add(position, item)
            notifyItemInserted(position)
        } else {
            deviceList[index] = item
            notifyItemChanged(index)
        }
    }*/
/*
    fun updateList(items: List<UPnPDevice>?) {
        items?.let {
            val diffCallback = DiffUtilCallback(this.mItems, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.mItems.run {
                clear()
                addAll(items)
                diffResult.dispatchUpdatesTo(this@UPnPDeviceAdapter)
            }
        }
    }*/

}