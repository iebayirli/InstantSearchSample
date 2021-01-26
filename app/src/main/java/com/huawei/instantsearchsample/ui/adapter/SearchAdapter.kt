package com.huawei.instantsearchsample.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.huawei.instantsearchsample.BR
import com.huawei.instantsearchsample.ui.listener.IUrlClickListener

class SearchAdapter<T>(
    @LayoutRes private val layoutId: Int,
    private var data: List<T>,
    private val listener: IUrlClickListener,
) : RecyclerView.Adapter<SearchAdapter<T>.ViewHolder>() {


    fun updateData(data: List<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if (item != null) {
            holder.bind(item, listener)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Any, listener: IUrlClickListener) {
            binding.apply {
                setVariable(BR.data, data)
                setVariable(BR.listener, listener)
            }
        }
    }


}