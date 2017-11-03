package com.vejoe.techdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vejoe.lib.activity.BaseActivity;
import com.vejoe.lib.comm.BluetoothCommManager;
import com.vejoe.lib.comm.BluetoothCommPacket;
import com.vejoe.lib.comm.BluetoothCommPacketReader;
import com.vejoe.lib.core.comm.BluetoothSession;
import com.vejoe.lib.core.utils.Tools;
import com.vejoe.lib.voice.SpeechRecognizerListener;
import com.vejoe.lib.voice.iflytek.IflyTekIat;
import com.vejoe.techdemo.widget.RockerView;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public class BalanceActivity extends BaseActivity implements BluetoothCommPacketReader.OnRawDataListener{

    private enum EnumCommand {
        Null,
        Stop,
        Up,
        Up_Right,
        Right,
        Down_Right,
        Down,
        Down_Left,
        Left,
        Up_Left,
        Front_Back
    }
    // 语音识别
    private IflyTekIat iat;
    private boolean balance = true;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_balance);

        Intent intent = getIntent();
        balance = intent.getBooleanExtra("balance", true);
        if (balance) {
            getSupportActionBar().setTitle("平衡车");
            findViewById(R.id.car_bg).setBackgroundResource(R.mipmap.balance_car);
        } else {
            getSupportActionBar().setTitle("麦克纳姆车");
            findViewById(R.id.car_bg).setBackgroundResource(R.mipmap.m_car);
            findViewById(R.id.btn_move_ctrl).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_trace).setVisibility(View.VISIBLE);
        }

        RockerView view = (RockerView) findViewById(R.id.rocerview);
        view.setZOrderOnTop(true);
        view.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        view.setRockerListener(rockerListener);

        TechDemoApp app = (TechDemoApp)getApplication();
        app.getIflyTekSettings().setShowRecognizerDialog(true);
        iat = new IflyTekIat(this, app.getIflyTekSettings());
        iat.setSpeechRecognizerListener(speechRecognizerListener);

        findViewById(R.id.btn_voice_ctrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iat.start();
            }
        });

        findViewById(R.id.btn_move_ctrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoveDialog();
            }
        });

        findViewById(R.id.btn_trace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothSession session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
                if (session == null) return;
                Intent intent = new Intent(BalanceActivity.this, TraceActivity.class);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            Tools.checkPermission(this, Manifest.permission.RECORD_AUDIO, 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothCommPacketReader reader = BluetoothCommManager.getInstance().getBluetoothCommPacketReader();
        if (reader != null) {
            reader.addOnRawDataListener(getClass().getSimpleName(), this);
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
    public void onDestroy() {
        super.onDestroy();
        iat.stop();
        iat.dispose();
    }

    @Override
    public void onDisconnect() {
        AlertDialog dialog = Tools.getAlertDialog(this, getString(com.vejoe.lib.R.string.warning),
                getString(com.vejoe.lib.R.string.device_disconnect), false, new  DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        dialog.show();

        if (balance) stopTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "连接小车").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 1) {
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private RockerView.OnRockerListener rockerListener = new RockerView.OnRockerListener() {
        @Override
        public void onRocker(int which) {
            BluetoothSession session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
            if (session != null) {
                session.getWriter().write(new byte[]{(byte)which});
            }
            if (which == 0) stopTimer();
        }

        @Override
        public void onRocker(RockerView rockerView, int which) {

        }
    };

    @Override
    public void onRawData(byte[] data, int offset, int count) {
//        for (int i = offset; i < offset + count; i ++) {
//            processData(data[i]);
//        }
    }

    byte[] recvData = new byte[42];
    int index = 0;
    private void processData(byte data) {
        if (data == '$') {
            index  = 0;
            Arrays.fill(recvData, (byte)0);
        } else if (data == '}') {
            recvData[index] = data;
            for (int i = 2; i < index; i ++) {

            }
        }

        recvData[index ++] = data;
    }

    @Override
    public void onPacketReceived(BluetoothCommPacket packet) {

    }

    private SpeechRecognizerListener speechRecognizerListener = new SpeechRecognizerListener() {
        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(final String result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BalanceActivity.this, result, Toast.LENGTH_LONG).show();
                }
            });
            EnumCommand cmd = getCommand(result);
            if (cmd != EnumCommand.Null) {

                if (cmd == EnumCommand.Front_Back && balance) {
                    startTask();
                    return;
                }

                if (cmd == EnumCommand.Stop && balance) {
                    stopTimer();
                }

                if (cmd != EnumCommand.Stop && !balance) {
                    int value = getValue(result);
                    if (value > 0) {
                        value = (int) (value * UNIT);
                        BluetoothSession session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
                        if (session != null) {
                            setData(datas, cmd.ordinal() - 2, value);
                            session.getWriter().write(datas);
                        }

                        return;
                    }
                }

                int w = cmd.ordinal() - 1;
                BluetoothSession session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
                if (session != null) {
                    session.getWriter().write(new byte[]{(byte)w});
                }
            }
        }

        @Override
        public void onError(String errorMsg) {

        }
    };

    private boolean front = true;
    private void startTask() {
        if (timer == null) {
            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    BluetoothSession session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
                    if (session != null) {
                        byte cmd = 0;//停
                        session.getWriter().write(new byte[]{cmd});

                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        cmd = (byte) (front? 1 : 5);
                        session.getWriter().write(new byte[]{cmd});
                    }
                    front = !front;
                }
            }, 0, 1000);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private int getValue(String str) {
        str = str.toLowerCase();
        int unit = 0;//0-cm, 1-dm, 2-m
        int index = str.indexOf("厘米");
        if (index < 0) {
            index = str.indexOf("cm");
        }

        if (index < 0) {
            index = str.indexOf("公分");
        }

        if (index < 0) {
            index = str.indexOf("分米");
            unit = index > 0? 1 : 0;
        }
        if (index < 0) {
            index = str.indexOf("dm");
            unit = index > 0? 1 : 0;
        }
        if (index < 0) {
            index = str.indexOf("米");
            unit = index > 0? 2 : 0;
        }
        if (index < 0) {
            index = str.indexOf("m");
            unit = index > 0? 2 : 0;
        }
        if (index < 0)  return -1;

        int endIndex = index;
        int startIndex = -1;
        for (int i = index - 1; i >= 0; i --) {
            String s = str.substring(i, endIndex);
            if (!s.matches("[0-9]")) {
                startIndex = i + 1;
                break;
            }
            endIndex = i;
        }

        if (startIndex != -1) {
            String digit = str.substring(startIndex, index);
            try {
                int value = Integer.parseInt(digit);
                if (unit == 1) value = value * 10;
                else if (unit == 2) value = value * 100;
                return value;
            }
            catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        return -1;
    }

    private EnumCommand getCommand(String str) {
        if (str.contains("前进") || str.contains("向前")) {
            return EnumCommand.Up;
        }

        if (str.contains("右转") || str.contains("右移") || str.contains("向右")) {
            return EnumCommand.Right;
        }

        if (str.contains("后退") || str.contains("向后") || str.contains("向厚") || str.contains("后移")) {
            return EnumCommand.Down;
        }

        if (str.contains("左转") || str.contains("左移") || str.contains("向左")) {
            return EnumCommand.Left;
        }

        if (str.contains("右上"))
            return EnumCommand.Up_Right;

        if (str.contains("左上"))
            return EnumCommand.Up_Left;

        if (str.contains("右后") || str.contains("右下"))
            return EnumCommand.Down_Right;

        if (str.contains("左后") || str.contains("左下"))
            return EnumCommand.Down_Left;

        if (str.contains("停")) {
            return EnumCommand.Stop;
        }

        if (str.contains("前后摇摆")) {
            return EnumCommand.Front_Back;
        }

        return EnumCommand.Null;
    }

    private int pos = -1;
    private static final float UNIT = 1000 / 12f;
    private void showMoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("移动");
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_move, null);
        Spinner spinner = (Spinner) v.findViewById(R.id.sp_direction);
        final EditText et = (EditText) v.findViewById(R.id.et_distance);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(v);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = et.getText().toString();
                if (!TextUtils.isEmpty(str) && pos != -1) {
                    try {
                        int value = Integer.parseInt(str);
                        value = (int) (value * UNIT);
                        BluetoothSession session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
                        if (session != null) {
                            setData(datas, pos, value);
                            session.getWriter().write(datas);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private byte[] datas = new byte[8];
    private void setData(byte[] bytes, int pos, int value) {
        bytes[0] = (byte)0x7B;
        bytes[1] = (byte)0x31;
        bytes[2] = (byte)(pos + 0x41);
        bytes[3] = (byte)((value >> 24) & 0x00FF);
        bytes[4] = (byte)((value >> 16) & 0x00FF);
        bytes[5] = (byte)((value >> 8) & 0x00FF);
        bytes[6] = (byte)(value & 0x00FF);
        bytes[7] = (byte)0x7D;
    }
}
