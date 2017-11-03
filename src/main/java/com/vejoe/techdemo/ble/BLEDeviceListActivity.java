package com.vejoe.techdemo.ble;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vejoe.lib.activity.BluetoothLEDeviceListActivity;

/**
 * <p>@author: Leej
 * <p>Company: VEJOE
 * <p>Comment: //TODO
 * <p>Date: 2017/11/3 0003-下午 6:11
 */

public class BLEDeviceListActivity extends BluetoothLEDeviceListActivity {

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

        getSupportActionBar().setTitle("设备连接");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (DebugCfg.isDebugOn)
//            menu.add(0, 1, 1, "通信测试").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == 1) {
//            Intent intent = new Intent(this, CommTestActivity.class);
//            startActivity(intent);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnect() {
        //连接成功，告知模特进入联机调试模式
        super.onConnect();
        //连接成功，则直接退出
        onBackPressed();
    }
}
