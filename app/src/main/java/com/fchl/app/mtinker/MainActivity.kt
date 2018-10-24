package com.fchl.app.mtinker

import android.content.*
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.fchl.app.mtinker.util.Preference

import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.fchl.app.mtinker.service.LoadApkService


class MainActivity : AppCompatActivity() {
     private var mBroadcastReceiver: MyBroadcastReceiver? = null
    private var intentFilter: IntentFilter? = null
    private var patchApk: String by Preference(this, "patch", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //注册广播
         mBroadcastReceiver = MyBroadcastReceiver()
        intentFilter = IntentFilter()
        intentFilter!!.addAction("load_succ");
        registerReceiver(mBroadcastReceiver, intentFilter);

         fab.setOnClickListener { view ->
            if(patchApk == "true"){
                loadAlert()
            }else{
                crashAlert()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }
    fun crashAlert(){
        AlertDialog.Builder(this)
                .setMessage("确定触发事件")
                .setTitle("警告")
                .setPositiveButton("触发", DialogInterface.OnClickListener { dialogInterface, i ->
                      patchApk ="true"
                     // 10/0;
                })
                .setNeutralButton("取消", null)
                .create()
                .show()

    }


    fun loadAlert(){
        AlertDialog.Builder(this)
                .setMessage("是否要下载更新包")
                .setTitle("修复bug")
                .setPositiveButton("下载", DialogInterface.OnClickListener { dialogInterface, i ->
                    //获取intent对象
                    val inetnt = Intent()
                    intent.setClass(this,LoadApkService::class.java)
                    startService(intent)
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

    private inner class  MyBroadcastReceiver : BroadcastReceiver() {
          override fun onReceive(p0: Context?, p1: Intent?) {
              if (p1!!.action == "load_succ") {
                  Toast.makeText(this@MainActivity, "load succ!", Toast.LENGTH_SHORT).show()

              }

          }

      }

    fun updateloadAlert(){
        AlertDialog.Builder(this)
                .setMessage("是否要修复bug")
                .setTitle("修复bug")
                .setPositiveButton("下载", DialogInterface.OnClickListener { dialogInterface, i ->
                    //获取intent对象
                    val inetnt = Intent()
                    intent.setClass(this,LoadApkService::class.java)
                    startService(intent)
                })
                .setNeutralButton("取消", null)
                .create()
                .show()

    }
}
