package com.vejoe.techdemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                entryMainActivity();
            }
        }, 1500);
    }

    private void entryMainActivity() {
        Intent intent = new Intent(EntryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
