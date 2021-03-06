package com.zouke.wangyanan.lsnlockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    private DevicePolicyManager policyManager;

    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LockScreen();
    }


    public void LockScreen(){
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, LockReceiver.class);
        if (policyManager.isAdminActive(componentName)) {//判断是否有权限(激活了设备管理器)
            policyManager.lockNow();// 直接锁屏
            android.os.Process.killProcess(android.os.Process.myPid());
        }else{
            activeManager();//激活设备管理器获取权限
        }
    }

    // 解除绑定
    public void Bind(){
        if(componentName!=null){
            policyManager.removeActiveAdmin(componentName);
            activeManager();
        }
    }
    @Override
    protected void onResume() {//重写此方法用来在第一次激活设备管理器之后锁定屏幕
        if (policyManager!=null && policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bind();
    }

    private void activeManager() {
        //使用隐式意图调用系统方法来激活指定的设备管理器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        startActivity(intent);
    }

}
