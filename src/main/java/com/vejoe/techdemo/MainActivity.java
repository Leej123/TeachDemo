package com.vejoe.techdemo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setElevation(0);
        }

        findViewById(R.id.connect_device_block).setOnClickListener(this);
        findViewById(R.id.paras_setting_block).setOnClickListener(this);
        findViewById(R.id.manual_control_block).setOnClickListener(this);

        showMask(false);
    }

    private void showMask(boolean visible) {
        findViewById(R.id.manual_control_block_mask).setVisibility(visible? View.VISIBLE : View.GONE);
        findViewById(R.id.paras_setting_block_mask).setVisibility(visible? View.VISIBLE : View.GONE);
        findViewById(R.id.model_edit_block_mask).setVisibility(visible? View.VISIBLE : View.GONE);
        findViewById(R.id.machines_group_block_mask).setVisibility(visible? View.VISIBLE : View.GONE);
        findViewById(R.id.status_checking_block_mask).setVisibility(visible? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TechDemoApp app = (TechDemoApp) getApplication();
        app.exit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_device_block:// 自平衡车
                Intent intent = new Intent(this, BalanceActivity.class);
                intent.putExtra("balance", true);
                startActivity(intent);
                break;
            case R.id.manual_control_block: // 麦克拉姆轮车
                intent = new Intent(this, BalanceActivity.class);
                intent.putExtra("balance", false);
                startActivity(intent);
                break;
            case R.id.paras_setting_block: // 语音
                intent = new Intent(this, VoiceDemoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
