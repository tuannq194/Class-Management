package com.ngxqt.classmanagement

import android.graphics.Color
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.classmanagement.databinding.ClassItemBinding
import com.ngxqt.classmanagement.databinding.StudentItemBinding
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class StudentAdapter(val studentItems: ArrayList<StudentItem>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null

    lateinit var binding: StudentItemBinding
    inner class StudentViewHolder(private val binding: StudentItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        var roll: TextView = binding.roll
        var studentName: TextView = binding.name
        var status: TextView = binding.status
        var cardView: CardView = binding.cardview

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        binding = StudentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.roll.setText((studentItems.get(position).roll).toString())
        holder.studentName.setText(studentItems.get(position).name)
        holder.status.setText(studentItems.get(position).status)
        holder.cardView.setCardBackgroundColor(getColor(position))
    }

    private fun getColor(position: Int): Int {
        val status = studentItems.get(position).status
        if(status.equals("P")){
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(binding.root.context,R.color.present)))
        }else if (status.equals("A")){
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(binding.root.context,R.color.absent)))
        }
        return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(binding.root.context,R.color.normal)))
    }

    override fun getItemCount(): Int {
        return studentItems.size
    }
}