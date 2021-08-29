package com.luthtan.simplebleproject.dashboard_feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luthtan.simplebleproject.dashboard_feature.databinding.ItemDashboardDetailBinding
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity

class DashboardAdapter : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    private val bleEntity = ArrayList<BleEntity>()

    fun setBleLogHistory(bleEntity: List<BleEntity>) {
        this.bleEntity.clear()
        this.bleEntity.addAll(bleEntity)
        notifyDataSetChanged()
    }

    inner class DashboardViewHolder(private val binding: ItemDashboardDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bleEntity: BleEntity) {
            binding.apply {
                tvItemDashboardDate.text = bleEntity.date
                tvItemDashboardRoomDescription.text = bleEntity.room
                tvItemDashboardTime.text = bleEntity.timeIn
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val itemDashboardDetailBinding = ItemDashboardDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(itemDashboardDetailBinding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(bleEntity[position])
    }

    override fun getItemCount(): Int = bleEntity.size
}