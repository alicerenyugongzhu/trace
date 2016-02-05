package com.alice.trace;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.OnGeoFenceListener;
import com.baidu.trace.OnEntityListener;

public class MainActivity extends AppCompatActivity {
    TextView traceLog;
    LBSTraceClient client;
    OnStopTraceListener stopTraceListener;
    Trace trace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        traceLog = (TextView)findViewById(R.id.traceLog);

        //实例化轨迹服务客户端
        client = new LBSTraceClient(getApplicationContext());
//鹰眼服务ID
        long serviceId  = 109396 ;
//entity标识
        String entityName = "alice";
        //位置采集周期
        int gatherInterval = 10;
//打包周期
        int packInterval = 60;
//设置位置采集和打包周期
        client.setInterval(gatherInterval, packInterval);
//轨迹服务类型（0 : 不上传位置数据，也不接收报警信息； 1 : 不上传位置数据，但接收报警信息；2 : 上传位置数据，且接收报警信息）
        int  traceType = 2;
//实例化轨迹服务
        trace = new Trace(getApplicationContext(), serviceId, entityName, traceType);


//实例化开启轨迹服务回调接口
        OnStartTraceListener  startTraceListener = new OnStartTraceListener() {
            //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
                traceLog.setText(arg1.toString() + arg0);
            }
            //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
                traceLog.setText(arg1.toString() + arg0);
            }
        };

//开启轨迹服务
        client.startTrace(trace, startTraceListener);

//实例化停止轨迹服务回调接口
        stopTraceListener = new OnStopTraceListener(){
            // 轨迹服务停止成功
            @Override
            public void onStopTraceSuccess() {
                traceLog.setText("Stop service success");
            }
            // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onStopTraceFailed(int arg0, String arg1) {
                traceLog.setText(arg1.toString() + arg0);
            }
        };

//停止轨迹服务
        //client.stopTrace(trace,stopTraceListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.stopTrace(trace,stopTraceListener);
                Snackbar.make(view, "Stop tracing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
