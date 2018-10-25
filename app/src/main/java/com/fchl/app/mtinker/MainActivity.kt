package com.fchl.app.mtinker

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.fchl.app.mtinker.util.Preference

import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
    private var patchApk: String by Preference(this, "patch", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if(patchApk == "true"){
                loadAlert()
            }else{
                crashAlert()
            }
        }
    }

    fun crashAlert(){
        AlertDialog.Builder(this)
                .setMessage("确定触发事件")
                .setTitle("警告")
                .setPositiveButton("触发", DialogInterface.OnClickListener { dialogInterface, i ->
                      patchApk ="true"
                      10/0;
                })
                .setNeutralButton("取消", null)
                .create()
                .show()

    }


    fun loadAlert(){
        AlertDialog.Builder(this)
                .setMessage("是否要下载更新包")
                .setTitle("修复bug!!")
                .setPositiveButton("下载", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .setNeutralButton("取消", null)
                .create()
                .show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
