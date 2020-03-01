package com.teddybrothers.qrcodereader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.teddybrothers.qrcodereader.databinding.ListItemResultBinding

class HistoryItemAdapter(private val listener: ItemClickPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<ResultScan>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RewardItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_result, parent, false), listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val resultScan = items[position]
        if (holder is RewardItemViewHolder) {
            holder.updateData(resultScan)
        }
    }

    override fun getItemCount() = items.size

    fun updateDataSet(items: List<ResultScan>) {
        this.items = items
        notifyDataSetChanged()
    }

    class RewardItemViewHolder(private val binding: ListItemResultBinding, listener: ItemClickPresenter) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var resultScan: ResultScan

        init {
            itemView.setOnClickListener {
                listener.onItemClicked(resultScan)
            }
        }

        fun updateData(resultScan: ResultScan) {
            this.resultScan = resultScan
            binding.resultScan = resultScan
        }
    }
}