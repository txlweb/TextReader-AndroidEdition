package com.idsoft.textreader;

import static com.teipreader.Main.Config_dirs.MainPath;
import static com.teipreader.Main.Config_dirs.in_storage;
import static com.teipreader.Main.TeipMake.deleteFileByIO;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.teipreader.Lib.IniLib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PluginMgrActivity extends AppCompatActivity {
    private RecyclerView fileListRecyclerView;
    private FileAdapter fileAdapter;
    private List<FileModel> fileList = new ArrayList<>();
    private Button btn_del;
    private Button btn_dis;
    private Button btn_ena;
    private Button btn_imp;
    public static String base_path;
    public static int select_id = 0;
    int REQUEST_CODE_GET_FILE = 114514;

    public static boolean mode_is_plugin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pluginmgr);

        fileListRecyclerView = findViewById(R.id.recyclerView2);
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取文件列表并填充数据模型

        if(mode_is_plugin){
            base_path = Objects.requireNonNull(this.getExternalFilesDir(null)).getPath()+"/plugins";
        }else {
            base_path = Objects.requireNonNull(this.getExternalFilesDir(null)).getPath()+"/rom";
            TextView V1 = findViewById(R.id.textView3);
            V1.setText("小说管理器");
        }
        File directory = new File(base_path); // 替换为实际目录路径
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String t_o = "UNKNOW";
                if(new File(base_path+"/"+file.getName()+"/resource.ini").isFile()||new File(base_path+"/"+file.getName()+"/list.info").isFile()) {
                    t_o = IniLib.GetThing(MainPath + "/" + file.getName() + "/resource.ini", "conf", "title");
                }
                if(!file.getName().contains(".encode")){
                    fileList.add(new FileModel(file.getName(), t_o, file.length()));
                }

            }
        }

        // 创建并设置适配器
        fileAdapter = new FileAdapter(fileList);
        fileListRecyclerView.setAdapter(fileAdapter);
        //按钮功能绑定
        btn_del = findViewById(R.id.button4); // 删除
        btn_dis = findViewById(R.id.button3); // 禁用
        btn_ena = findViewById(R.id.button2); // 启用
        btn_imp = findViewById(R.id.button1); // 获取按钮的引用
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = findViewById(android.R.id.content);
                FileModel file = fileList.get(select_id);
                Snackbar.make(rootView, "真的要删除\""+file.getFileName()+"\"吗?(点击其他位置取消)", Snackbar.LENGTH_SHORT)
                        .setAction("确认删除", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recreate();
                                    }
                                }, 2000);
                                if(mode_is_plugin) {
                                    if (new File(base_path + "/" + file.getFileName()).delete()) {
                                        Snackbar.make(rootView, "删除成功!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                recreate();
                                            }
                                        }).show();
                                    } else {
                                        Snackbar.make(rootView, "删除失败!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                recreate();
                                            }
                                        }).show();
                                    }
                                }else{
                                    deleteFileByIO(base_path + "/" + file.getFileName());
                                    if (!new File(base_path + "/" + file.getFileName()).isDirectory()) {
                                        Snackbar.make(rootView, "删除成功!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                recreate();
                                            }
                                        }).show();
                                    } else {
                                        Snackbar.make(rootView, "删除失败!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                recreate();
                                            }
                                        }).show();
                                    }
                                }
                            }
                        })
                        .show();

            }
        });
        btn_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 2000);
                View rootView = findViewById(android.R.id.content);
                FileModel file = fileList.get(select_id);
                if(!mode_is_plugin){
                    if(new File(base_path + "/" + file.getFileName()+"/hidden.info").isFile()){
                        Snackbar.make(rootView, "禁用失败(已经禁用)!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    }else {
                        try {
                            new File(base_path + "/" + file.getFileName()+"/hidden.info").createNewFile();

                            Snackbar.make(rootView, "禁用成功!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {recreate();}}).show();
                        } catch (IOException e) {
                            Snackbar.make(rootView, "禁用失败("+e+")!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {recreate();}}).show();
                            //throw new RuntimeException(e);
                        }
                    }
                    return;
                }
                if(file.getFileName().contains(".pluginJS") && !file.getFileName().contains(".nop")){
                    if(new File(base_path+"/"+file.getFileName()).renameTo(new File(base_path + "/" + file.getFileName() + ".nop"))){
                        Snackbar.make(rootView, "已经禁用!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    }else {
                        Snackbar.make(rootView, "禁用失败(无法操作文件)!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    }

                }else {
                    Snackbar.make(rootView, "禁用失败(可能不是pluginJS)!", Snackbar.LENGTH_SHORT).setAction("好", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {recreate();}}).show();
                }

            }
        });
        btn_ena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 2000);
                View rootView = findViewById(android.R.id.content);
                FileModel file = fileList.get(select_id);
                if(!mode_is_plugin){
                    if(!new File(base_path + "/" + file.getFileName()+"/hidden.info").isFile()){
                        Snackbar.make(rootView, "启用失败(已经启用)!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    }else {
                        if(new File(base_path + "/" + file.getFileName()+"/hidden.info").delete()){
                            Snackbar.make(rootView, "启用成功!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {recreate();}}).show();
                        }else {
                            Snackbar.make(rootView, "启用失败(文件操作失败)!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {recreate();}}).show();
                        }


                    }
                    return;
                }
                if(file.getFileName().contains(".pluginJS.nop")){
                    if(new File(base_path+"/"+file.getFileName()).renameTo(new File(base_path + "/" + file.getFileName().substring(0, file.getFileName().length() - 4)))){
                        Snackbar.make(rootView, "已经启用!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    }else {
                        Snackbar.make(rootView, "启用失败(无法操作文件)!", Snackbar.LENGTH_SHORT).setAction("重载列表", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    }

                }else {
                    Snackbar.make(rootView, "启用失败(可能不是pluginJS或者未禁用)!", Snackbar.LENGTH_SHORT).setAction("好", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {recreate();}}).show();
                }

            }
        });
        btn_imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_CODE_GET_FILE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                assert fileUri != null;
                String fileName = getFileNameFromUri(fileUri);
                View rootView = findViewById(android.R.id.content);
                if(mode_is_plugin){
                    try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
                        saveFileToPrivateDirectory(inputStream,base_path+"/"+fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Snackbar.make(rootView, "导入成功!", Snackbar.LENGTH_SHORT).setAction("好", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {recreate();}}).show();
                }else{
                    try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
                        saveFileToPrivateDirectory(inputStream,in_storage+"/update.teip");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    com.teipreader.Main.TeipMake.deleteFileByIO(in_storage+"/tmp/");
                    com.teipreader.Main.TeipMake.Unzip(in_storage+"/update.teip",in_storage+"/tmp/");
                    //检查,防止没目录的teip搞事
                    File[] fp = new File(in_storage+"/tmp/").listFiles();
                    if (fp == null) {
                        Snackbar.make(rootView, "导入失败!请检查文件是否为teip文件.", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {recreate();}}).show();
                    } else {
                        if (!new File(in_storage+"/tmp/resource.ini").isFile()) {
                            com.teipreader.Main.TeipMake.Unzip(in_storage+"/update.teip", MainPath);
                            Snackbar.make(rootView, "导入完成.", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {recreate();}}).show();
                        } else {
                            com.teipreader.Main.TeipMake.Unzip(
                                    in_storage+"/update.teip",
                                    MainPath+"/" + com.teipreader.Main.TeipMake.getFileMD5(in_storage+"/update.teip") + "/"
                            );
                            Snackbar.make(rootView, "导入完成.", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {recreate();}}).show();
                        }
                    }
                    //清理
                    new File(in_storage+"/update.teip").delete();
                    com.teipreader.Main.TeipMake.deleteFileByIO(in_storage+"/tmp/");
                }
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        }, 2000);
    }

    private void saveFileToPrivateDirectory(InputStream inputStream, String save_file) {
        File file = new File(save_file);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            // 文件保存成功，可以在这里进行后续操作，如更新UI等
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        String path = uri.getPath();

        if (path != null) {
            // 尝试从路径中提取文件名
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                fileName = path.substring(cut + 1);
            } else {
                fileName = path;
            }
        } else {
            // 如果路径为空，可能是内容提供者，尝试查询数据库
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex >= 0) {
                            fileName = cursor.getString(nameIndex);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        fileName = fileName.replaceAll("primary:","");//有多的东西
        return fileName;
    }
}