package com.ngxqt.classmanagement

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.ngxqt.classmanagement.databinding.ActivitySheetBinding
import java.util.*


class SheetActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySheetBinding.inflate(layoutInflater)
        setContentView(binding.root)


        showTable()
    }

    private fun setToolbar(month: String) {
        val className = intent?.getStringExtra("className").toString()
        val subjectName = intent?.getStringExtra("subjectName").toString()
        binding.toolbar.apply {
            titleToolbar.setText(className)
            subtitleToolbar.setText("Attendance Table | "+month)
            back.setOnClickListener { onBackPressed() }
            save.isInvisible = true
        }
    }

    private fun showTable() {
        val dbHelper = DbHelper(this)

        val idArray = intent.getLongArrayExtra("idArray")

        val rollArray = intent.getIntArrayExtra("rollArray")
        val nameArray = intent.getStringArrayExtra("nameArray")
        val month = intent.getStringExtra("month")
        Log.i("LOG_MONTH",month!!)
        setToolbar(month)

        val DAY_IN_MONTH = getDayInMonth(month!!)
        val rowSize: Int = idArray!!.size + 1

        val rows = arrayOfNulls<TableRow>(rowSize)
        val roll_tvs = arrayOfNulls<TextView>(rowSize)
        val name_tvs = arrayOfNulls<TextView>(rowSize)
        val status_tvs = Array(rowSize) { arrayOfNulls<TextView>(DAY_IN_MONTH + 1) }
        Log.i(
            "LOG_SIZE", "rowSize: " + rowSize.toString() +
                    ", rows: " + rows.size +
                    ", roll_tvs: " + roll_tvs.size +
                    ", name_tvs: " + name_tvs.size +
                    ", status_tvs: " + status_tvs.size +
                    ", day_in_month: " + DAY_IN_MONTH
        )
        for (i in 0..rowSize - 1) {
            roll_tvs[i] = TextView(this)
            name_tvs[i] = TextView(this)
            roll_tvs[i] = TextView(this)
            for (j in 1..DAY_IN_MONTH) {
                status_tvs[i][j] = TextView(this)
            }
        }

        //header
        roll_tvs[0]!!.setText("Roll")
        roll_tvs[0]!!.setTypeface(roll_tvs[0]!!.typeface, Typeface.BOLD)
        name_tvs[0]!!.setText("Name")
        name_tvs[0]!!.setTypeface(name_tvs[0]!!.typeface, Typeface.BOLD)
        for (i in 1..DAY_IN_MONTH) {
            status_tvs[0][i]!!.setText(i.toString())
            status_tvs[0][i]!!.setTypeface(status_tvs[0][i]!!.typeface, Typeface.BOLD)
        }

        for (i in 1..rowSize - 1) {
            roll_tvs[i]!!.setText(rollArray!![i - 1].toString())
            name_tvs[i]!!.setText(nameArray!![i - 1])

            for (j in 1..DAY_IN_MONTH) {
                var day = j.toString()
                if (day.length == 1) {
                    day = "0" + day
                }
                val date = day + "." + month
                val status = dbHelper.getStatus(idArray[i - 1], date)
                status_tvs[i][j]!!.setText(status)
            }
        }

        for (i in 0..rowSize - 1) {
            rows[i] = TableRow(this)

            if (i%2==0){
                rows[i]!!.setBackgroundColor(Color.parseColor("#EEEEEE"))
            }else{
                rows[i]!!.setBackgroundColor(Color.parseColor("#E4E4E4"))
            }

            roll_tvs[i]!!.setPadding(16, 16, 16, 16)
            name_tvs[i]!!.setPadding(16, 16, 16, 16)


            rows[i]!!.addView(roll_tvs[i])
            rows[i]!!.addView(name_tvs[i])

            for (j in 1..DAY_IN_MONTH step 1) {
                status_tvs[i][j]!!.setPadding(16, 16, 16, 16)

                rows[i]!!.addView(status_tvs[i][j])
            }

            binding.tableLayout.addView(rows[i])
        }
        binding.tableLayout.showDividers = TableLayout.SHOW_DIVIDER_MIDDLE
    }

    private fun getDayInMonth(month: String): Int {
        val monthIndex = Integer.valueOf(month.substring(0, 1))
        val year = Integer.valueOf(month.substring(4))

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, monthIndex)
        calendar.set(Calendar.YEAR, year)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+3
    }
}