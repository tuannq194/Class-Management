package com.ngxqt.classmanagement.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngxqt.classmanagement.*
import com.ngxqt.classmanagement.adapter.StudentAdapter
import com.ngxqt.classmanagement.databinding.ActivityStudentBinding
import com.ngxqt.classmanagement.fragment.MyCalendar
import com.ngxqt.classmanagement.fragment.MyDialog
import com.ngxqt.classmanagement.model.StudentItem
import org.json.JSONArray
import org.json.JSONObject


class StudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding

    lateinit var dbHelper: DbHelper
    lateinit var studentAdapter: StudentAdapter
    val studentItems: ArrayList<StudentItem> = ArrayList()

    lateinit var className: String
    lateinit var subjectName: String
    lateinit var position: String
    var cid: Long? = null
    private lateinit var calendar: MyCalendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = MyCalendar()
        dbHelper = DbHelper(this)
        //val intent = intent.extras
        className = intent?.getStringExtra("className").toString()
        subjectName = intent?.getStringExtra("subjectName").toString()
        position = intent?.getIntExtra("position", -1).toString()
        cid = intent?.getLongExtra("cid",-1)!!

        setToolbar()
        loadData()
        /**Cài đặt RecyclerView và Adapter để hiển thị item*/
        val recyclerView = binding.studentRecycler
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        studentAdapter = StudentAdapter(studentItems)
        recyclerView.adapter = studentAdapter

        studentAdapter.onItemClick = {
            changStatus(it)
        }

        //readDataJson()

        loadStatusData()
    }

    private fun readDataJson() {
        val defaultIdList = mutableListOf<Int>()
        val defaultNameList = mutableListOf<String>()

        /**Đọc list_student.json*/
        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "list_student",
                "raw",applicationContext.packageName
            )
        ).bufferedReader().use { it.readText() }
        val outputJsonArray = JSONObject(jsonData).getJSONArray("data") as JSONArray
        /**Gán Data vào List*/
        for (i in 0 until outputJsonArray.length()){
            val defaultId =  Integer.parseInt(outputJsonArray.getJSONObject(i).getString("studentId"))
            val defaultName = outputJsonArray.getJSONObject(i).getString("studentName")
            defaultIdList.add(defaultId)
            defaultNameList.add(defaultName)
        }

        addDefaultStudent(defaultIdList,defaultNameList)
    }

    /**Đặt giá trị mặc định cho danh sách Student*/
    private fun addDefaultStudent(defaultId: MutableList<Int>, defaultName: MutableList<String>) {
        val cursor = dbHelper.getStudentTabale(cid!!)
        //studentItems.clear()
        if (cursor.count == 0){
            for (i in 0..defaultId.size-1){
                val roll = defaultId[i]
                val name = defaultName[i]
                Log.i("LOG_DEFAUL", i.toString()+" "+roll.toString()+" "+i.toString()+" "+name)
                val sid = dbHelper.addStudent(cid!!,roll,name)
                val studentItem = StudentItem(sid,roll,name,"A")
                studentItems.add(studentItem)
                studentAdapter.notifyDataSetChanged()
            }
        }
        cursor.close()
    }

    private fun loadData() {
        val cursor = dbHelper.getStudentTabale(cid!!)
        Log.i("LOG_CID",cid.toString())
        studentItems.clear()

        while (cursor.moveToNext()){
            val sid = cursor.getLong(cursor.getColumnIndex(DbHelper.S_ID))
            val roll = cursor.getInt(cursor.getColumnIndex(DbHelper.STUDENT_ROLL_KEY))
            val name = cursor.getString(cursor.getColumnIndex(DbHelper.STUDENT_NAME_KEY))

            studentItems.add(StudentItem(sid,roll,name,null))
        }
        cursor.close()
    }

    private fun changStatus(position: Int) {
        var status = studentItems.get(position).status
        if (status.equals("P")) {
            status = "A"
        } else if (status.equals("A")) {
            status = ""
        } else{
            status = "P"
        }
        studentItems.get(position).status = status
        studentAdapter.notifyItemChanged(position)
    }

    private fun setToolbar() {
        binding.toolbarStudent.apply {
            titleToolbar.setText(className)
            subtitleToolbar.setText(subjectName+" | "+calendar.getDate())
            back.setOnClickListener { onBackPressed() }
            save.setOnClickListener {
                Toast.makeText(this@StudentActivity,"Saved", Toast.LENGTH_SHORT).show()
                saveStatus()
            }
            toolbar.inflateMenu(R.menu.student_menu)
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.add_student -> showAddStudentDialog()
                    R.id.show_Calendar -> showCalendar()
                    R.id.show_attendance_sheet -> openSheetList()
                }
                true
            }
        }
    }

    private fun openSheetList() {
        val idArray  = LongArray(studentItems.size)
        val rollArray = IntArray(studentItems.size)
        val nameArray = Array<String?>(studentItems.size){null}
        //val rollArray = arrayOfNulls<Int>(studentItems.size)
        for (i in idArray.indices){
            idArray[i] = studentItems.get(i).sid!!
        }
        for (i in rollArray.indices){
            rollArray[i] = studentItems.get(i).roll!!
        }
        for (i in nameArray.indices){
            nameArray[i] = studentItems.get(i).name
        }
        val intent = Intent(this, SheetListActivity::class.java)
        intent.putExtra("cid",cid)
        intent.putExtra("idArray", idArray)
        intent.putExtra("rollArray",rollArray)
        intent.putExtra("nameArray",nameArray)
        intent.putExtra("className", className)
        intent.putExtra("subjectName", subjectName)
        startActivity(intent)
    }

    private fun saveStatus() {
        for (studentItem: StudentItem in studentItems){
            val status = studentItem.status
            if (status != "P"){ status == "A" }
            val value = dbHelper.addStatus(studentItem.sid!!,cid!!,calendar.getDate(),status!!)
            Log.i("LOG_VALUE",value.toString())
            if (value==-1L){ dbHelper.updateStatus(studentItem.sid!!,calendar.getDate(),status!!) }
        }
    }

    private fun loadStatusData(){
        for (studentItem: StudentItem in studentItems){
            val status = dbHelper.getStatus(studentItem.sid!!,calendar.getDate())
            if (status!=null){ studentItem.status = status }
            else { studentItem.status="P"}
        }
        studentAdapter.notifyDataSetChanged()
    }

    private fun showCalendar() {
        calendar.show(supportFragmentManager,"")
        calendar.onCalendarOkClick = {year, month, day ->
            onCalendarOkClicked(year, month, day)
        }
    }

    private fun onCalendarOkClicked(year: Int, month: Int, day: Int) {
        calendar.setDate(year, month, day)
        binding.toolbarStudent.subtitleToolbar.setText(subjectName+" | "+calendar.getDate())
        loadStatusData()
    }

    private fun showAddStudentDialog() {
        val dialog = MyDialog()
        dialog.show(supportFragmentManager, MyDialog.STUDENT_AND_DIALOG)
        dialog.onItemStudentClick = {
            addStudent(it.roll!!, it.name)
        }
    }

    private fun addStudent(roll: Int, name: String) {
        val sid = dbHelper.addStudent(cid!!,roll,name)
        val studentItem = StudentItem(sid,roll,name,"P")
        studentItems.add(studentItem)
        studentAdapter.notifyDataSetChanged()
        Toast.makeText(this,"Add Success",Toast.LENGTH_SHORT).show()
    }

    /**Bấm giữ item để Delete hoặc Update Class*/
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            0 -> showUpdateStudentDialog(item.groupId)
            1 -> deleteClass(item.groupId)
        }
        return super.onContextItemSelected(item)
    }

    private fun showUpdateStudentDialog(position: Int) {
        val dialog = MyDialog(studentItems.get(position).roll,studentItems.get(position).name)
        dialog.show(supportFragmentManager, MyDialog.STUDENT_UPDATE_DIALOG)
        dialog.onItemStudentClick = {
            updateStudent(position,it.roll!!,it.name)
        }

    }

    private fun updateStudent(position: Int, roll: Int,name: String) {
        dbHelper.updateStudent(studentItems.get(position).sid!!,roll, name)
        studentItems.get(position).roll = roll
        studentItems.get(position).name = name
        studentAdapter.notifyItemChanged(position)
        Toast.makeText(this,"Update Success",Toast.LENGTH_SHORT).show()
    }

    private fun deleteClass(position: Int) {
        dbHelper.deleteStudent(studentItems.get(position).sid!!)
        studentItems.removeAt(position)
        studentAdapter.notifyItemRemoved(position)
    }
}