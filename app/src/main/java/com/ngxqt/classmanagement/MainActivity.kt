package com.ngxqt.classmanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.classmanagement.databinding.ActivityMainBinding
import com.ngxqt.classmanagement.databinding.DialogBinding
import com.ngxqt.classmanagement.databinding.ToolbarBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingDialog: DialogBinding
    private lateinit var toolbarBinding: ToolbarBinding

    lateinit var dbHelper: DbHelper
    lateinit var classAdapter: ClassAdapter
    val classItems: ArrayList<ClassItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DbHelper(this)

        binding.fabMain.setOnClickListener { showDialog() }

        loadData()

        /**Cài đặt RecyclerView và Adapter để hiển thị item*/
        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        classAdapter = ClassAdapter(classItems)
        recyclerView.adapter = classAdapter

        classAdapter.onItemClick = {
            Log.d("LOG_POSITION", it.toString())
            //Toast.makeText(this, classItems.get(it).className, Toast.LENGTH_SHORT).show()
            gotoItemActivity(it)
        }

        setToolbar()
    }

    /**Load data Class từ Database*/
    private fun loadData() {
        val cursor = dbHelper.getClassTabale()

        classItems.clear()
        while (cursor.moveToNext()){
            val cid = cursor.getLong(cursor.getColumnIndex(DbHelper.C_ID))
            val className = cursor.getString(cursor.getColumnIndex(DbHelper.CLASS_NAME_KEY))
            val subjectName = cursor.getString(cursor.getColumnIndex(DbHelper.SUBJECT_NAME_KEY))

            classItems.add(ClassItem(cid,className,subjectName))
        }
    }

    /**Hiển thị Toolbar*/
    private fun setToolbar() {
        toolbarBinding = binding.toolbarMain
        toolbarBinding.apply {
            titleToolbar.setText("Attendance App")
            subtitleToolbar.isGone = true
            back.isInvisible = true
            save.isInvisible = true
        }
    }

    /**Điều hướng sang StudentActivity*/
    private fun gotoItemActivity(position: Int) {
        val intent = Intent(this, StudentActivity::class.java)
        intent.putExtra("className", classItems.get(position).className)
        intent.putExtra("subjectName", classItems.get(position).subjectName)
        intent.putExtra("position", position)
        intent.putExtra("cid", classItems.get(position).cid)
        startActivity(intent)
    }

    /**Hiển thị Dialog Add New Class*/
    private fun showDialog() {
        val dialog = MyDialog()
        dialog.show(supportFragmentManager, MyDialog.CLASS_AND_DIALOG)
        dialog.onItemClassClick = {
            addClass(it.className, it.subjectName)
        }
        /** Cách Dùng Dialog trực tiếp*/
        /*val builder = AlertDialog.Builder(this)
        bindingDialog = DialogBinding.inflate(LayoutInflater.from(this))
        builder.setView(bindingDialog.root)
        val dialog: AlertDialog =  builder.create()
        dialog.show()

        bindingDialog.apply {
            titleDialog.setText("Add New Class")
            edt01.setHint("Class Name")
            edt02.setHint("Subject Name")
            cancelBtn.setOnClickListener { dialog.dismiss() }
            addBtn.setOnClickListener {
                val className = bindingDialog.edt01.text.toString().trim()
                val subjectName = bindingDialog.edt02.text.toString().trim()
                addClass(className, subjectName)
                dialog.dismiss()
            }
        }*/
    }

    private fun addClass(className: String, subjectName: String) {
        val cid = dbHelper.addClass(className,subjectName)
        val classItem = ClassItem(cid,className,subjectName)
        classItems.add(classItem)
        classAdapter.notifyDataSetChanged()
        Toast.makeText(this,"Add Success",Toast.LENGTH_SHORT).show()
    }

    /**Bấm giữ item để Delete hoặc Update Class*/
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            0 -> showUpdateDialog(item.groupId)
            1 -> deleteClass(item.groupId)
        }
        return super.onContextItemSelected(item)
    }

    private fun showUpdateDialog(position: Int) {
        val dialog = MyDialog()
        dialog.show(supportFragmentManager, MyDialog.CLASS_UPDATE_DIALOG)
        dialog.onItemClassClick = {
            updateClass(position,it.className,it.subjectName)
        }

    }

    private fun updateClass(position: Int,className: String, subjectName: String) {
        dbHelper.updateClass(classItems.get(position).cid!!,className,subjectName)
        classItems.get(position).className = className
        classItems.get(position).subjectName = subjectName
        classAdapter.notifyItemChanged(position)
        Toast.makeText(this,"Update Success",Toast.LENGTH_SHORT).show()
    }

    private fun deleteClass(position: Int) {
        dbHelper.deleteClass(classItems.get(position).cid!!)
        classItems.removeAt(position)
        classAdapter.notifyItemRemoved(position)
    }
}