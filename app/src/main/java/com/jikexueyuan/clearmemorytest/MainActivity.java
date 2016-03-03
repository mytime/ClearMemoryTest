package com.jikexueyuan.clearmemorytest;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

/**
 * 一键清理进程
 * -------------------------
 * 主要使用
 *      Context.ACTIVITY_SERVICE  系统服务
 *      ActivityManager.MemoryInfo，getMemoryInfo  内存管理
 *                      RunningAppProcessInfo.pkgList 进程包名
 *                      killBackgroundProcesses  结束进程
 *                      权限管理
 *
 */
public class MainActivity extends Activity {

    private Context context;
    private ActivityManager activityManager;
    private long initMenmory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        activityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        //加速前可用内存
        initMenmory = getAvailMemory(context);
        //执行清理
        startClear();
        //通知清理结果
        getClearResult();
        //结束Activity
        finish();
    }

    //执行清理
    private void startClear() {
        //对系统中所有正在运行的进程进行迭代，如果进程名不是当前进程，则Kill掉
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : getRunProcesses()) {
            String processName = appProcessInfo.processName;
            //取得各个进程的包
            String[] pkgList = appProcessInfo.pkgList;
            if (!processName.equals(currentProcess())) {
                for (int i = 0; i < pkgList.length; ++i) {
                    //杀死进程
                    killProcesses(pkgList[i]);
                }
            }
        }
    }

    /**
     * 通知清理结果
     */
    private void getClearResult() {
        long newAvaiMemory = getAvailMemory(context); //清理后的可用内存
        Toast.makeText(getApplicationContext(),
                "为您节省了" + (newAvaiMemory - initMenmory) + "M内存",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * 杀死进程
     * @param s
     */
    private void killProcesses(String s) {
        activityManager.killBackgroundProcesses(s);
    }

    /**
     * 获取本项目的进程
     * @return
     */
    private String currentProcess() {
        String currentProcess = context.getApplicationInfo().processName;
        return currentProcess;
    }

    /**
     * 获取所有后台运行的进程
     * @return
     */
    private  List<ActivityManager.RunningAppProcessInfo> getRunProcesses() {
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos =
                activityManager.getRunningAppProcesses();
        return appProcessInfos;
    }

    /**
     * 获取可用内存大小
     * @param context
     * @return
     */
    private long getAvailMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mi);
        return mi.availMem / (1024 * 1024);//返回值以 M 为单位
    }
}