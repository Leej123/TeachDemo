package com.vejoe.techdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class VoiceDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_demo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("语音演示");

        findViewById(R.id.btn_tts_demo).setOnClickListener(this);
        findViewById(R.id.btn_voice_to_text_demo).setOnClickListener(this);
        findViewById(R.id.btn_chat_demo).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tts_demo:
                Intent intent = new Intent(this, TTSActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_voice_to_text_demo:
                intent = new Intent(this, VoiceToTextActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_chat_demo:
                intent = new Intent(this, ChatDemoActivity.class);
                startActivity(intent);
                break;
        }

    }
}
