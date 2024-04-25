package com.idsoft.textreader

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import com.idsoft.textreader.FileUtils.copyFileFromAssets
import com.idsoft.textreader.FileUtils.unzipFile
import com.idsoft.textreader.ui.theme.TextReaderTheme
import com.teipreader.Main.Config_dirs.MainPath
import com.teipreader.Main.Config_dirs.NormPort
import com.teipreader.Main.Main.boot
import com.teipreader.Main.WebServer.crash_cheek
import com.teipreader.Main.WebServer.final_server
import com.teipreader.share.WebServer.final_share
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_PICK_ZIP = 1000
    var AppPath = "";

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        var is_faild = false;
        //获取外存位置
        val ex_path = this.getExternalFilesDir(null)?.path
        if (ex_path != null) {
            AppPath = ex_path
        };
        //申请权限
        //存储,网络
        val permissions =  arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET)
        this.requestPermissions(permissions,101)
        for (str in permissions) {
            if (this.checkSelfPermission(str) !== PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(permissions, 101)
                this.requestPermissions(permissions,101)
                this.requestPermissions(permissions,101)
            }
        }
        //通知
        createNotificationChannel(this)
        //初始化运行目录
        com.teipreader.Main.Config_dirs.push_data_dir(filesDir.path, ex_path)
        com.teipreader.share.Config_dirs.push_data_dir(filesDir.path, ex_path)
        //加载配置文件
        com.teipreader.Main.Config_dirs.init_configs()
        com.teipreader.share.Config_dirs.init_configs()
        //解包style
//        val unzippedFilePath = filesDir.toString() + File.separator + "/style" // 解压目标完整路径
//        if (copyFileFromAssets(this, "style.zip", "style.zip")) {
//            if(!unzipFile(
//                this,
//                filesDir.toString() + File.separator + "style.zip",
//                unzippedFilePath
//            )){
//                println("ERROR UNPACK")
//                is_faild = true;
//            }
//        }else{
//            println("ERROR OUTPUT")
//            is_faild = true;
//        }
        is_faild = unpk(filesDir.toString() + File.separator + "/style", "style.zip")
        //启动服务器
        val rootView = findViewById<View>(android.R.id.content)

        boot()
        var btn_num = 0
        super.onCreate(savedInstanceState)
        if(!is_faild){
            keep_life()
        }
        setContent {
            TextReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),color = MaterialTheme.colorScheme.background) {
                    //Text("TextReader Android Edition")
                    if (is_faild) {
                        Text(text = "!! ERROR !!\r\n"+"未能正确加载程序,请检查权限情况或尝试关掉后台重启程序.")
                    }else{
                        Text(text = "Text Reader Android Edition  240424build -- Power By.IDlike\r\n" +
                                "\n" +
                                "   240422build 更新日志\n" +
                                " 修复部分BUG\n" +
                                " 新增插件和小说管理器 (原生)\n" +
                                " [建议加入AE插件套件,其中包含多个实用功能和触屏特化设计UI]\n" +
                                "   240424build 更新日志\n" +
                                " 修复了一个闪退问题\n" +
                                " 改进WebView体验\n" +
                                "\n" +
                                "阅读服务已经启动在本机上。 (port:"+ NormPort+")\r\n" +
                                "共享服务已经启动在本机上。 (port:"+ com.teipreader.share.Config_dirs.NormPort+")"
                        )
                    }
                }

                if(!is_faild) {
                    Button(
                        onClick = {
                            val intent = Intent()
                            intent.setClass(this@MainActivity, main_web_view::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier
                            .size(width = 300.dp, height = 50.dp)
                            .offset(5.dp, (350 + btn_num * 54).dp)
                    ) {
                        Text("> 启动WebView页 <")
                    }
                    btn_num++
                    //已经移动到管理小说栏目了.
//                    Button(
//                        onClick = {
//                            val intent = Intent(Intent.ACTION_GET_CONTENT)
//                            intent.setType("*/*") // 接受任何类型的文件
//                            startActivityForResult(intent, 0)
//                        },
//                        modifier = Modifier
//                            .size(width = 300.dp, height = 50.dp)
//                            .offset(5.dp, (350 + btn_num * 54).dp)
//                    ) {
//                        Text("> 导入小说 <")
//                    }

                    btn_num++
                    Button(
                    onClick = {
                            Snackbar.make(rootView, "要为你的客户端添加推荐的插件吗? ", Snackbar.LENGTH_SHORT).setAction(
                                "一键添加") {
                                unpk("$ex_path/plugins", "plugin.zip")
                            }.show()
                    },
                    modifier = Modifier
                        .size(width = 80.dp, height = 50.dp)
                        .offset(260.dp, (350 + btn_num * 54).dp)
                    ) {
                    Text(text = "推荐")
                    }
                    Button(
                        onClick = {
                            PluginMgrActivity.mode_is_plugin = true;
                            val intent = Intent()
                            intent.setClass(this@MainActivity, PluginMgrActivity::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier
                            .size(width = 250.dp, height = 50.dp)
                            .offset(5.dp, (350 + btn_num * 54).dp)
                    ) {
                        Text(text = "管理插件")
                    }
                    btn_num++
                    Button(
                        onClick = {
                            PluginMgrActivity.mode_is_plugin = false;
                            val intent = Intent()
                            intent.setClass(this@MainActivity, PluginMgrActivity::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier
                            .size(width = 300.dp, height = 50.dp)
                            .offset(5.dp, (350 + btn_num * 54).dp)
                    ) {
                        Text(text = "管理小说")
                    }
                    btn_num++
                    Button(
                        onClick = {
                            final_server = true;
                            final_share = true;
                            finishAffinity(); // 结束当前任务的所有Activity
                            System.exit(0); // 强制结束应用进程
//                            Snackbar.make(rootView, "点错了?", Snackbar.LENGTH_SHORT).setAction(
//                                "重启服务") {
//                                final_server = false;
//                                final_share = false;
//                                boot()
//                            }.show() ;
                        },
                        modifier = Modifier
                            .size(width = 300.dp, height = 50.dp)
                            .offset(5.dp, (350 + btn_num * 54).dp)
                    ) {
                        Text(text = "关闭服务并退出")
                    }
                }else{
                    Button(
                        onClick = {
                            final_server = true;
                            final_share = true;
                            finishAffinity(); // 结束当前任务的所有Activity
                            System.exit(0); // 强制结束应用进程
                        },
                        modifier = Modifier
                            .size(width = 300.dp, height = 50.dp)
                            .offset(5.dp, (350 + btn_num * 54).dp)
                    ) {
                        Text(text = "关闭")
                    }
                }
            }
        }
    }
    fun keep_life(){
        Handler().postDelayed({
            crash_cheek -= 1;
            if(crash_cheek<=0){
                val rootView = findViewById<View>(android.R.id.content)
                Snackbar.make(rootView, "服务器崩溃! 遇到了一个问题(详情见日志)", Snackbar.LENGTH_SHORT).setAction(
                    "关闭应用") {
                    final_server = true;
                    final_share = true;
                    finish()
                }.show() ;
            } }, 1)

    }
    public fun unpk(out_path: String,res_name:String): Boolean {
        val unzippedFilePath = "$out_path" // 解压目标完整路径
        File(filesDir.toString() + File.separator + "/"+res_name).delete()
        if (copyFileFromAssets(this, res_name,res_name)) {
            if(!unzipFile(
                    this,
                    filesDir.toString() + File.separator + "/"+res_name,
                    unzippedFilePath
                )){
                return false;
            }
        }else{
            return true;
        }

        return false;
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun openDirectory(directory: String) {
        val uri = Uri.parse(directory)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "vnd.android.document/directory");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 授予读取权限

        // 尝试启动这个Intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private var handler: android.os.Handler = android.os.Handler()
    private fun copyFileFromUri(uri: Uri, destinationPath: String): Boolean {
        val contentResolver = contentResolver
        val assetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r") ?: return false
        val sourceFileDescriptor = assetFileDescriptor.createInputStream()
        val destinationStream = FileOutputStream(destinationPath)
        val buffer = ByteArray(1024)
        var length: Int
        while (sourceFileDescriptor.read(buffer).also { length = it } != -1) {
            destinationStream.write(buffer, 0, length)
        }
        destinationStream.flush()
        sourceFileDescriptor.close()
        assetFileDescriptor.close()
        destinationStream.close()
        return true
    }
    private var mediaPlayer: MediaPlayer? = null;
    private var is_use_play = false;
    @SuppressLint("WrongConstant")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            val uri = data?.data // 获取选择的文件的URI
            try {
                if (uri != null) {
                    if(side_mode){
                        copyFileFromUri(uri, "$AppPath/side-style-tmp.zip")
                        com.teipreader.Main.TeipMake.Unzip("$AppPath/side-style-tmp.zip", "$AppPath/style/")
                        File("$AppPath/side-style-tmp.zip").delete();
                        sendNotification(this, "侧载完成,这是一个临时操作,重启程序效果即消失.")
                        side_mode = false
                    }else{
                        copyFileFromUri(uri, "$AppPath/update.teip")
                        com.teipreader.Main.TeipMake.deleteFileByIO("$AppPath/tmp/")
                        com.teipreader.Main.TeipMake.Unzip("$AppPath/update.teip", "$AppPath/tmp/")
                        //检查,防止没目录的teip搞事
                        val fp = File("$AppPath/tmp/").listFiles()
                        if (fp == null || fp.isEmpty()) {
                            sendNotification(this, "导入失败!请检查文件是否为teip文件.")
                            mediaPlayer?.start(); // 开始播放MP3文件
                        } else {
                            if (!File("$AppPath/tmp/resource.ini").isFile) {
                                com.teipreader.Main.TeipMake.Unzip("$AppPath/update.teip", MainPath)
                                sendNotification(this, "导入完成.")
                            } else {
                                com.teipreader.Main.TeipMake.Unzip(
                                    "$AppPath/update.teip",
                                    "$MainPath/" + com.teipreader.Main.TeipMake.getFileMD5("$AppPath/update.teip") + "/"
                                )
                                sendNotification(this, "导入完成.")
                            }
                        }
                        //清理
                        File("$AppPath/update.teip").delete();
                        com.teipreader.Main.TeipMake.deleteFileByIO("$AppPath/tmp/")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == RESULT_CANCELED) {
            sendNotification(this,"你取消了一次导入.")
        }
    }
    private val CHANNEL_ID = "com.idlike.textreader"
    private val CHANNEL_NAME = "IDlikeSoftTextReader"
    private var side_mode = false
    private var update_mode = false
    private var download_file = false
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知通道
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            // 配置通知通道的属性，例如声音、振动等
//            channel.enableLights(true)
//            channel.lightColor = Color.RED
//            channel.enableVibration(true)
//            channel.vibrationPattern =
//                longArrayOf(100, 200, 300, 350, 500, 350, 300, 200, 100)
            // 获取通知管理器并创建通知通道
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(context: Context, message: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 使用创建的通知通道发送通知
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("TextReader")
                .setContentText(message) // 其他通知配置...
                .setSmallIcon(R.drawable.favicon)
                .build()
            notificationManager.notify(1, notification) // 使用通知ID发送通知，这里使用1作为示例ID，你可以根据需要自定义通知ID。
        } else {
            // 处理旧版本Android的代码...
        }
    }


}