package com.fchl.app.mtinker.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.fchl.app.mtinker.util.Fchlutils;
import com.tencent.tinker.lib.service.AbstractResultService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2018/10/24 0024.
 * 下载apk补丁
 */

public class LoadApkService extends IntentService {
    public LoadApkService() {
        super(LoadApkService.class.getSimpleName());
    }
    public LoadApkService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            downloadNet();
            Intent intents = new Intent("load_succ");
             sendBroadcast(intents);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void downloadNet() throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL("https://raw.githubusercontent.com/fchl/tinker/master/patch_signed_7zip.apk");

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            Fchlutils.creatSDDir("mTinker");
    //   TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");

            FileOutputStream fs = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/mTinker/patch_signed_7zip.apk");

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;

                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
