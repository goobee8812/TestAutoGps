package com.example.administrator.testautogps;



import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity implements AMapLocationListener{
    private Button sendBtn;
    private TextView positionTv = null;

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBtn = (Button)findViewById(R.id.sendMsg);
        positionTv = (TextView)findViewById(R.id.positionTv);
        //初始化数据库
        Connector.getDatabase();
        //读取数据库数据
        List<Position> positions = DataSupport.findAll(Position.class);
        StringBuffer sb = new StringBuffer();
        for (Position position : positions){
            sb.append(position.getProvider()+"--"+position.getAddress()+position.getAccuracy() + "米--" + position.getPoiName() + "--" +position.getTime() + "\n" );
            sb.append("---------\n");
        }
        positionTv.setText(sb);
        //初始化定位
        mLocationClient = new AMapLocationClient(MyApplication.getContext());
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
//        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
//        mLocationOption.setInterval(2000);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doSendSMSTo("15602907440","-");
                broadcastCall();
            }
        });
    }
    /**
     * 调起系统发短信功能
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber,String message){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            SmsManager smsManager = SmsManager.getDefault();
            //自动拆分短信
            List<String> divideContents = smsManager.divideMessage(message);
            for (String text : divideContents) {
                smsManager.sendTextMessage(phoneNumber, null, text, null, null); //无返回数据
                LogUtil.d("------------","Sending");
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                Message msg = handler.obtainMessage();
                msg.obj = amapLocation;
                msg.what = Utils.MSG_LOCATION_FINISH;
                handler.sendMessage(msg);
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                LogUtil.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                //开始定位
                case Utils.MSG_LOCATION_START:
                    positionTv.setText("正在定位...");
                    break;
                // 定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = Utils.getLocationStr(loc);
//                    positionTv.setText(result);
//                    doSendSMSTo("15602907440",result);
                    break;
                //停止定位
                case Utils.MSG_LOCATION_STOP:
                    positionTv.setText("定位停止");
                    break;
                case Utils.REQUEST_COMMAND:  //需要发送数据
                    // 启动定位
                    mLocationClient.startLocation();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        mLocationClient = null;
        mLocationClient = null;
    }

    public void broadcastCall(){
        Message msg = handler.obtainMessage();
        msg.what = Utils.REQUEST_COMMAND;
        handler.sendMessage(msg);
        LogUtil.d("broadcast","Enther");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_about:
                Toast.makeText(MainActivity.this, ""+"关于", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_settings:
                Toast.makeText(MainActivity.this, ""+"设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_quit:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
