package com.example.practicecoroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.user_item.view.*

class MainAdapter(val itemList: List<UserModel>) : RecyclerView.Adapter<MainAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(userModel: UserModel) {
            itemView.imageView.setImageResource(userModel.img)
        }
    }
}