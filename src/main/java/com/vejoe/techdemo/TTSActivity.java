package com.vejoe.techdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.vejoe.lib.voice.iflytek.IflyTekTTS;

public class TTSActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText inputText;
    // 文字转语音
    private IflyTekTTS tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("文字转语音");

        inputText = (EditText) findViewById(R.id.et_input_text);
        findViewById(R.id.btn_text_to_speech).setOnClickListener(this);
        findViewById(R.id.btn_clear_text).setOnClickListener(this);

        TechDemoApp app = (TechDemoApp)getApplication();
        tts = new IflyTekTTS(this, app.getIflyTekSettings());
    }

    @Override
    public void onBackPressed() {
        tts.cancel();
        tts.dispose();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_text_to_speech:
                String text = inputText.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    tts.startSpeaking(text);
                }
                break;
            case R.id.btn_clear_text:
                inputText.setText("");
                break;
        }
    }
}
