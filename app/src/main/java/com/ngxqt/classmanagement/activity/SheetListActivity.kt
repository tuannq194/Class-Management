package com.ngxqt.classmanagement.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.view.isInvisible
import com.ngxqt.classmanagement.DbHelper
import com.ngxqt.classmanagement.R
import com.ngxqt.classmanagement.databinding.ActivitySheetListBinding
import java.util.ArrayList

class SheetListActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySheetListBinding
    private lateinit var adapter: ArrayAdapter<Any>
    private var listItems = ArrayList<String>()
    private var cid: Long? = null

    lateinit var className: String
    lateinit var subjectName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySheetListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()

        cid = intent.getLongExtra("cid",-1)

        loadListItems()
        adapter = ArrayAdapter(this, R.layout.sheet_list, R.id.date_list_item, listItems as List<String>)
        binding.sheetList.adapter = adapter

        binding.sheetList.setOnItemClickListener { parent, view, position, id ->
            openSheetActivity(position)
        }
    }

    private fun setToolbar() {
        className = intent?.getStringExtra("className").toString()
        subjectName = intent?.getStringExtra("subjectName").toString()
        binding.toolbar.apply {
            titleToolbar.setText(className)
            subtitleToolbar.setText(subjectName+" | Attendance List")
            back.setOnClickListener { onBackPressed() }
            save.isInvisible = true
        }
    }

    private fun openSheetActivity(position: Int) {
        val idArray  = intent.getLongArrayExtra("idArray")
        val rollArray = intent.getIntArrayExtra("rollArray")
        val nameArray = intent.getStringArrayExtra("nameArray")
        val intent = Intent(this, SheetActivity::class.java)

        intent.putExtra("idArray",idArray)
        intent.putExtra("rollArray",rollArray)
        intent.putExtra("nameArray",nameArray)
        intent.putExtra("month",listItems.get(position))
        intent.putExtra("className", className)
        intent.putExtra("subjectName", subjectName)
        startActivity(intent)

    }

    private fun loadListItems() {
        val cursor = DbHelper(this).getDistincMonths(cid!!)

        while (cursor.moveToNext()){
            val date = cursor.getString(cursor.getColumnIndex(DbHelper.DATE_KEY))
            listItems.add(date.substring(3))
        }
    }
}