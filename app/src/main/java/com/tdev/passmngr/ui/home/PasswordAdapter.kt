package com.tdev.passmngr.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tdev.passmngr.data.model.Password
import com.tdev.passmngr.databinding.ItemPasswordBinding

class PasswordAdapter(
    private val onCopy: (Password) -> Unit,
    private val onClick: (Password) -> Unit,
    private val onDelete: (Password) -> Unit
) : ListAdapter<Password, PasswordAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemPasswordBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(p: Password) {
            b.tvAccount.text = p.accountName
            b.tvUsername.text = p.username
            b.tvCategory.text = p.category.label
            b.root.setOnClickListener { onClick(p) }
            b.btnCopy.setOnClickListener { onCopy(p) }
            b.btnDelete.setOnClickListener { onDelete(p) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemPasswordBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Password>() {
            override fun areItemsTheSame(a: Password, b: Password) = a.id == b.id
            override fun areContentsTheSame(a: Password, b: Password) = a == b
        }
    }
}
