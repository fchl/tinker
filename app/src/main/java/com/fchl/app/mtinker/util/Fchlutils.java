package com.fchl.app.mtinker.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;
import java.util.logging.Logger;

public class Fchlutils {


	public static boolean isConnect(Context context) {
		if(context==null){
			return true;
		}
		// 获取手机所有连接管理对象（包括对w i f i,net等连接的管理）
		try {

			ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			Log.v("error",e.toString());
		}
		return false;
	}


	/**
	 * 判断当前网络是否是wifi网络
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 得到SD卡根目录.
	 */
	public static File getRootPath(Context context) {
		if (sdCardIsAvailable()) {
			return Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
		} else {
			return context.getFilesDir();
		}
	}

	/**
	 * SD卡是否可用.
	 */
	public static boolean sdCardIsAvailable() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sd = new File(Environment.getExternalStorageDirectory().getPath());
			return sd.canWrite();
		} else
			return false;
	}

	/**
	 * 在SD卡上创建文件
	 *
	 * @throws IOException
	 */
	public static File creatSDFile(String fileName) throws IOException {

		File file = new File(fileName);
		if(isFileExist(fileName)){
			return file;
		}else{
			file.createNewFile();
		}

		return file;
	}

	/**
	 * 在SD卡上创建目录
	 *
	 * @param dirName
	 */
	public static File creatSDDir(String dirName) {
		File dir = new File(Environment.getExternalStorageDirectory().getPath()+"/"+dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	/**
	 * Create a folder, If the folder exists is not created.
	 *
	 * @param folderPath folder path.
	 * @return True: success, or false: failure.
	 */
	public static boolean createFolder(String folderPath) {
		if (!TextUtils.isEmpty(folderPath)) {
			File folder = new File(folderPath);
			return createFolder(folder);
		}
		return false;
	}

	/**
	 * Create a folder, If the folder exists is not created.
	 *
	 * @param targetFolder folder path.
	 * @return True: success, or false: failure.
	 */
	public static boolean createFolder(File targetFolder) {
		if (targetFolder.exists()) {
			if (targetFolder.isDirectory())
				return true;
			//noinspection ResultOfMethodCallIgnored
			targetFolder.delete();
		}
		return targetFolder.mkdirs();
	}

	/**
	 * 删除文件
	 *
	 * @param fileName
	 */
	public static void deleteFile(String fileName) {
		if(fileName==null || fileName.length()<2){
			return;
		}
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 删除目录下所有文件
	 *
	 * @param file
	 */
	public static void deleteDirectory(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // 如果是文件直接删除
			} else if (file.isDirectory()) { // 如果是目录
				File files[] = file.listFiles(); // 获取目录下所有文件files[];
				for (int i = 0; i < files.length; i++) {
					files[i].delete();// 遍历目录下所有文件循环删除
				}
			}
		}
	}

	public static String getAssetContent(Context context, String filename) {
		try {
			if(context==null){
				return "";
			}
			InputStream is = context.getAssets().open(filename);
			int size = is.available();
			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// Convert the buffer into a string.
			String text = new String(buffer, "UTF-8");
			// Finally stick the string into the text view.

			return text;
		} catch (IOException e) {
			// Should never happen!
			return "";
		}
	}

	/**
	 * 读取文件 
	 * nio方式 超快
	 * */
	public  static String readFile(String filename){
		if(!isFileExist(filename)){
			return "";
		}
		FileChannel fiChannel;
		MappedByteBuffer mBuf=null;
		try {
			fiChannel = new FileInputStream(filename).getChannel();
			if(fiChannel==null){
				return "";
			}
			mBuf = fiChannel.map(FileChannel.MapMode.READ_ONLY, 0, fiChannel.size());
			fiChannel.close();
			fiChannel = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getString(mBuf);

	}
	/**
	 * 保存文件
	 * */
	public static  boolean saveFile(ByteBuffer src, String filename){
		FileChannel foChannel;
		try {
			foChannel = new FileOutputStream(filename).getChannel();
			foChannel.write(src);
			foChannel.close();
			foChannel = null;
			return true;
		}  catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean writeFile(String filename, String content){
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			// 得到文件通道 
			FileChannel fc = fos.getChannel();

			ByteBuffer buf = ByteBuffer.wrap(content.getBytes());
			buf.put(content.getBytes());

			// 记得执行这个方法使得 position=0, limit=30, 才能写入正确的数据
			// 否则 position 为 30, limit 为 1024将会把 30 之后的全部空数据(0)
			//填到文件中
			buf.flip();
			// 缓冲区数据写入到文件中会把缓冲区中从 position 到 limit 之间的数据写入文件
			fc.write(buf);
			fc.close(); //关闭文件通道
			fos.close(); //关闭文件输出流
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	// 生成文件
	public static File makeFilePath( String filePath, String fileName) {
		File file = null;
		makeRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}else{
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static void checkRootDirectory(Context context) {

		File file = null;

		try {
			file = new File( getRootPath(context) +  "raku");
			if (!file.exists()) {
				file.mkdirs();
			}


		} catch (Exception e) {
			Log.i("error:", e + "");
		}
	}

	// 生成文件夹
	public static void makeRootDirectory(Context context,String filePath) {

		checkRootDirectory(context);
		File file = null;

		try {

			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			Log.i("error:", e + "");
		}
	}

	// 生成文件夹
	public static void makeRootDirectory(String filePath) {


		File file = null;

		try {

			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			Log.i("error:", e + "");
		}
	}

	public static boolean writeFile(String path, String filename, String content){

		makeFilePath(path,filename);
		try {
			FileOutputStream fos = new FileOutputStream(path+filename);
			// 得到文件通道
			FileChannel fc = fos.getChannel();

			ByteBuffer buf = ByteBuffer.wrap(content.getBytes());
			buf.put(content.getBytes());

			// 记得执行这个方法使得 position=0, limit=30, 才能写入正确的数据
			// 否则 position 为 30, limit 为 1024将会把 30 之后的全部空数据(0)
			//填到文件中
			buf.flip();
			// 缓冲区数据写入到文件中会把缓冲区中从 position 到 limit 之间的数据写入文件
			fc.write(buf);
			fc.close(); //关闭文件通道
			fos.close(); //关闭文件输出流
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * String 转换 ByteBuffer
	 * @param str
	 * @return
	 */
	public static ByteBuffer getByteBuffer(String str)
	{
		return ByteBuffer.wrap(str.getBytes());
	}

	/**
	 * ByteBuffer 转换 String
	 * @param buffer
	 * @return
	 */
	public static String getString(ByteBuffer buffer)
	{

		Charset charset = null;
		CharsetDecoder decoder = null;
		CharBuffer charBuffer = null;
		try
		{
			if(buffer==null){
				return "";
			}
			charset = Charset.forName("UTF-8");
			decoder = charset.newDecoder();
			// charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
			charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
			return charBuffer.toString();
		}
		catch (Exception ex)
		{
			 charset = null;
			 decoder = null;
			 charBuffer = null;
			try
			{
				charset = Charset.forName("GBK");
				decoder = charset.newDecoder();
				// charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
				charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
				return charBuffer.toString();
			}
			catch (Exception x){
				ex.printStackTrace();
 				return "";
			}
		}
	}

	/**
	 * 全屏广告获取缓存广告图片
	 *
	 * 获取文件列表
	 * @param
	 */
	public  static List<String> getImagPath(String paths, List<String> listPath) {

		File path = new File(paths);
		File[] files = path.listFiles();// 读取
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
//					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
//							+ file.getPath().toString());

					 getImagPath(file.getPath(),listPath);
//					Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
//							+ file.getPath().toString());
				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".img")) {
						listPath.add(fileName);
					}
				}
			}
		}
		return listPath;
	}

	/**
	 * 去掉小数点后多余的0
	 * @param s
	 * @return
	 */
	public static String trimzero(String s){
		if(s.indexOf(".") > 0){
			//正则表达
			s = s.replaceAll("0+?$", "");//去掉后面无用的零
			s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
		}
		return s;
	}




	public static boolean checkPermission(Context context, String permission) {
		boolean result = false;
		if (Build.VERSION.SDK_INT >= 23) {
			try {
				Class<?> clazz = Class.forName("android.content.Context");
				Method method = clazz.getMethod("checkSelfPermission", String.class);
				int rest = (Integer) method.invoke(context, permission);
				if (rest == PackageManager.PERMISSION_GRANTED) {
					result = true;
				} else {
					result = false;
				}
			} catch (Exception e) {
				result = false;
			}
		} else {
			PackageManager pm = context.getPackageManager();
			if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
				result = true;
			}
		}
		return result;
	}

	//获取设备信息
	public static String getDeviceId(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = null;
			if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
				device_id = tm.getDeviceId();
			}
			String mac = null;
			FileReader fstream = null;
			try {
				fstream = new FileReader("/sys/class/net/wlan0/address");
			} catch (FileNotFoundException e) {
				fstream = new FileReader("/sys/class/net/eth0/address");
			}
			BufferedReader in = null;
			if (fstream != null) {
				try {
					in = new BufferedReader(fstream, 1024);
					mac = in.readLine();
				} catch (IOException e) {
				} finally {
					if (fstream != null) {
						try {
							fstream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			json.put("mac", mac);
			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
			json.put("device_id", device_id);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//获取设备信息
	public static String getDeviceInfo(Context context) {
		try {

			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = "";
			if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
				device_id = tm.getDeviceId();
			}
			String mac = null;
			FileReader fstream = null;
			try {
				fstream = new FileReader("/sys/class/net/wlan0/address");
			} catch (FileNotFoundException e) {

				try {
					fstream = new FileReader("/sys/class/net/eth0/address");
				} catch (Exception ee) {

				}
			}
			BufferedReader in = null;
			if (fstream != null) {
				try {
					in = new BufferedReader(fstream, 1024);
					mac = in.readLine();
				} catch (IOException e) {
				} finally {
					if (fstream != null) {
						try {
							fstream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
			return  device_id;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
