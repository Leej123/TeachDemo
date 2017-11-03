package com.vejoe.techdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.vejoe.lib.core.utils.Tools;
import com.vejoe.lib.voice.SpeechRecognizerListener;
import com.vejoe.lib.voice.iflytek.IflyTekIat;

public class VoiceToTextActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView txtVoiceToTextResult;
    private IflyTekIat iat;//语音转文字
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_to_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("语音转文字");

        txtVoiceToTextResult = (TextView) findViewById(R.id.txt_voice_to_text_result);
        findViewById(R.id.btn_voice_to_text).setOnClickListener(this);

        TechDemoApp app = (TechDemoApp) getApplication();
        app.getIflyTekSettings().setShowRecognizerDialog(true);
        iat = new IflyTekIat(this, app.getIflyTekSettings());
        iat.setSpeechRecognizerListener(listener);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Tools.checkPermission(this, Manifest.permission.RECORD_AUDIO, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                AlertDialog dialog = Tools.getAlertDialog(this, getString(com.vejoe.lib.R.string.warning), "录音权限被禁止", true, null);
                dialog.show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        iat.stop();
        iat.dispose();
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
        iat.start();
    }

    private SpeechRecognizerListener listener = new SpeechRecognizerListener() {

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(String result) {
            txtVoiceToTextResult.setText(result);
        }

        @Override
        public void onError(String errorMsg) {

        }
    };
}
