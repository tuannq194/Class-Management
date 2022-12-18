package com.ngxqt.classmanagement

import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.classmanagement.databinding.ClassItemBinding

/** Code bị comment là phương án 2*/
class ClassAdapter(val classItems: ArrayList<ClassItem>):
    RecyclerView.Adapter<ClassAdapter.ClassViewHolder>(){
    //PagingDataAdapter<ClassItem,ClassAdapter.ClassViewHolder>(CLASS_COMPARATOR) {

    private val onItemClickListener: OnItemClickListener ? = null

    interface OnItemClickListener{
        fun onItemClick(classItem: ClassItem)
    }


    var onItemClick: ((Int) -> Unit)? = null
    inner class ClassViewHolder(private val binding: ClassItemBinding) :
        RecyclerView.ViewHolder(binding.root), OnCreateContextMenuListener {

        var className: TextView = binding.classTv
        var subjectName: TextView = binding.subjectTv

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(layoutPosition)
            }
            binding.root.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(adapterPosition,0,0,"Edit")
            menu?.add(adapterPosition,1,0,"Delete")
        }

        /*init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = getItem(position)
                    if(item != null)
                        onItemClickListener?.onItemClick(item)
                }
            }
        }

        fun bind(classItem: ClassItem) {
            Log.d("TAG",classItem.className)
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding =
            ClassItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.className.setText(classItems.get(position).className)
        holder.subjectName.setText(classItems.get(position).subjectName)

        /*val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }*/
    }

    override fun getItemCount(): Int {
        return classItems.size
    }

    /*companion object {
        private val CLASS_COMPARATOR = object : DiffUtil.ItemCallback<ClassItem>() {
            override fun areItemsTheSame(oldItem: ClassItem, newItem: ClassItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ClassItem,
                newItem: ClassItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }*/
}