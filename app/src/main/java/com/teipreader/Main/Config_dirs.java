package com.teipreader.Main;

import com.teipreader.Lib.IniLib;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class Config_dirs {
    public static String in_storage = "";
    public static String out_storage = "";
    public static String TempPath = "./tmp/";//Temp root
    public static String MainPath = "./rom";//Text root
    public static String StylePath = "./style";//Web root
    public static int NormPort = 8080;//Web port
    public static int LanguageID = 0;
    public static boolean Use_Server_LOG = true;
    public static boolean Use_Server_LOG_DEBUG = false;
    public static boolean Use_Share = true;
    public static boolean Fire_Wall = true;
    public static void push_data_dir(String v1,String v2){
        in_storage = v1;
        out_storage = v2;
        TempPath = in_storage+"/tmp";
        MainPath = out_storage+"/rom";
        StylePath = in_storage+"/style";
        new File(out_storage+"/plugins").mkdir();
        new File(TempPath).mkdir();
        new File(MainPath).mkdir();
        new File(StylePath).mkdir();
    }
    public static void init_configs() throws IOException {
        //ini lib version
        //[settings]
        //MainPath=./rom
        //Port=8080 #端口
        //LogRank=1 #0=禁止提示 1=仅重要 2=全部提示
        if (!new File(in_storage+"/config.ini").isFile()) {
            IniLib.SetThing(in_storage+"/config.ini", "settings", "MainPath", in_storage+"/rom");
            IniLib.SetThing(in_storage+"/config.ini", "settings", "Port", "8080");
            IniLib.SetThing(in_storage+"/config.ini", "settings", "LogRank", "1");
            IniLib.SetThing(in_storage+"/config.ini", "settings", "UseShare", "enable");
            IniLib.SetThing(in_storage+"/config.ini", "settings", "FireWall", "enable");
        }
        String Gl_UseShare = IniLib.GetThing(in_storage+"/config.ini", "settings", "UseShare");
        if (!Objects.equals(Gl_UseShare, "UnknownThing")) {
            Use_Share = Gl_UseShare.equals("enable");
        }
        String Gl_Fw = IniLib.GetThing(in_storage+"/config.ini", "settings", "FireWall");
        if (!Objects.equals(Gl_Fw, "UnknownThing")) {
            Fire_Wall = Gl_Fw.equals("enable");
        }
        String Gl_MainPath = IniLib.GetThing(in_storage+"/config.ini", "settings", "MainPath");
        if (!Objects.equals(Gl_MainPath, "UnknownThing")) {
            if (!new File(Gl_MainPath).isDirectory()) new File(Gl_MainPath).mkdir();
            //MainPath = Gl_MainPath;
        }
        String Gl_Port = IniLib.GetThing(in_storage+"/config.ini", "settings", "Port");
        if (!Objects.equals(Gl_Port, "UnknownThing")) {
            if (Integer.parseInt(Gl_Port) > 0 & Integer.parseInt(Gl_Port) < 25565) {
                NormPort = Integer.parseInt(Gl_Port);
            }
        }
        String Gl_LogRank = IniLib.GetThing(in_storage+"/config.ini", "settings", "LogRank");
        if (!Objects.equals(Gl_LogRank, "UnknownThing")) {
            Use_Server_LOG = false;
            Use_Server_LOG_DEBUG = false;
            if (Gl_LogRank.equals("1")) {
                Use_Server_LOG = true;
            }
            if (Gl_LogRank.equals("2")) {
                Use_Server_LOG = true;
                Use_Server_LOG_DEBUG = true;
            }
        } else {
            Gl_LogRank = "1";
        }
        System.out.println("[C]: 主路径=" + MainPath + " | 服务端口=" + NormPort + " | 日志等级=" + Gl_LogRank + " | 启用防火墙=" + Fire_Wall);
    }
}
