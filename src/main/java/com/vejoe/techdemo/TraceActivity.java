package com.vejoe.techdemo;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.vejoe.lib.activity.BaseActivity;
import com.vejoe.lib.comm.BluetoothCommManager;
import com.vejoe.lib.core.comm.BluetoothSession;
import com.vejoe.lib.core.utils.Tools;
import com.vejoe.lib.widget.FollowFingerView;

import java.util.List;

public class TraceActivity extends BaseActivity implements View.OnClickListener{
    private final static int MAX_WIDTH = 500;// 5m
    private float ratio = 1;
    private FollowFingerView followFingerView;

    private BluetoothSession session= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("轨迹跟踪");
        setContentView(R.layout.activity_trace);
        followFingerView = (FollowFingerView) findViewById(R.id.follow_finger_view);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_done).setOnClickListener(this);

        int[] size = Tools.getScreenSize(this);
        int s = Math.max(size[0], size[1]);
        ratio = (float)MAX_WIDTH / s;

        session = BluetoothCommManager.getInstance().getBluetoothCommService().getBluetoothSession();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_clear) {
            followFingerView.clear();
            return;
        }

        if (v.getId() == R.id.btn_done) {
            List<PointF> path = followFingerView.getFingerPath();
            int size = path.size();
            if (size > 1) {
                new TraceTask().execute(path);
            }
        }
    }

    private int maxMoveDistance = 12;//一次最大移动距离12cm
    private int moveInterval = 60;
    private class TraceTask extends AsyncTask<List<PointF>, Void, Void> {

        @Override
        protected Void doInBackground(List<PointF>... params) {
            if (session == null || !session.isConnected()) return null;
            List<PointF> points = params[0];

            int size = points.size();

            for (int i = 0; i < size; i ++) {
                points.get(i).y = -points.get(i).y;//反向
            }

            PointF lastPoint = new PointF();
            lastPoint.set(points.get(0).x, points.get(0).y);
            float x;
            float y;
            byte[] data = new byte[8];
            for (int i = 1; i < size; i ++) {
                PointF curPoint = points.get(i);
                x = curPoint.x - lastPoint.x;
                y = curPoint.y - lastPoint.y;

                if (Math.abs(x * ratio) > maxMoveDistance || Math.abs(y * ratio) > maxMoveDistance) {
                    splitStep(curPoint, lastPoint);
                    continue;
                }

                if (x == 0 && y == 0) continue;

                setData(data, x * ratio, y * ratio);
                session.getWriter().write(data);
                lastPoint.x = curPoint.x;
                lastPoint.y = curPoint.y;
                try {
                    Thread.sleep(moveInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    private void splitStep(PointF curPoint, PointF lastPoint) {
        float movePixel = maxMoveDistance / ratio;
        float x = curPoint.x - lastPoint.x;
        float y = curPoint.y - lastPoint.y;
        float moveX;
        float moveY;
        byte[] data = new byte[8];
        while (true) {
            if (x >= 0) {
                if (lastPoint.x == curPoint.x) {
                    moveX = 0;
                } else if (lastPoint.x + movePixel > curPoint.x) {
                    moveX = curPoint.x - lastPoint.x;
                    lastPoint.x = curPoint.x;
                } else {
                    moveX = movePixel;
                    lastPoint.x += movePixel;
                }

            } else {
                if (lastPoint.x == curPoint.x) {
                    moveX = 0;
                } else if (lastPoint.x - movePixel <= curPoint.x) {
                    moveX = curPoint.x - lastPoint.x;
                    lastPoint.x = curPoint.x;
                } else {
                    moveX = -movePixel;
                    lastPoint.x -= movePixel;
                }
            }

            if (y >= 0) {
                if (lastPoint.y == curPoint.y) {
                    moveY = 0;
                } else if (lastPoint.y + movePixel > curPoint.y) {
                    moveY = curPoint.y - lastPoint.y;
                    lastPoint.y = curPoint.y;
                } else {
                    moveY = movePixel;
                    lastPoint.y += movePixel;
                }
            } else {
                if (lastPoint.y == curPoint.y) {
                    moveY = 0;
                } else if (lastPoint.y - movePixel <= curPoint.y) {
                    moveY = curPoint.y - lastPoint.y;
                    lastPoint.y = curPoint.y;
                } else {
                    moveY = -movePixel;
                    lastPoint.y -= movePixel;
                }
            }

            if (moveX == 0 && moveY == 0) break;

            setData(data, moveX * ratio, moveY * ratio);
            session.getWriter().write(data);

            try {
                Thread.sleep(moveInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final float UNIT = 1000 / 12f;
    private void setData(byte[] bytes, float moveX, float moveY) {
        bytes[0] = (byte)0x7B;
        bytes[1] = (byte)0x37;
        bytes[2] = (byte)(0x41);

        int x = (int) (moveX * UNIT);
        boolean positive = x >= 0? true : false;
        x = Math.abs(x);
        bytes[3] = (byte)((x >> 8) & 0x00FF);
        bytes[3] |= (positive? 0x00 : 0x80);
        bytes[4] = (byte)(x & 0x00FF);

        int y = (int) (moveY * UNIT);
        positive = y >= 0? true : false;
        y = Math.abs(y);
        bytes[5] = (byte)((y >> 8) & 0x00FF);
        bytes[5] |= (positive? 0x00 : 0x80);
        bytes[6] = (byte)(y & 0x00FF);

        bytes[7] = (byte)0x7D;
    }

}
