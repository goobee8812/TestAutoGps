package com.example.administrator.testautogps;

import android.app.IntentService;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendPositionService extends IntentService implements AMapLocationListener{
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.administrator.testautogps.action.FOO";
    private static final String ACTION_BAZ = "com.example.administrator.testautogps.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.administrator.testautogps.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.administrator.testautogps.extra.PARAM2";

    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    public static String result;

    private static final String TAG = "SendPositionService";



    public SendPositionService() {
        super("SendPositionService");
        //-----------------------------------定位初始化
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
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SendPositionService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SendPositionService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
        Message msg = handler.obtainMessage();
        msg.what = Utils.REQUEST_COMMAND;
        handler.sendMessage(msg);
        LogUtil.d(TAG,"handleActionFoo-----------");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        //throw new UnsupportedOperationException("Not yet implemented");
        Utils.doSendSMSTo(param1,param2);
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
                LogUtil.d(TAG,"------------------------------定位成功");
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                LogUtil.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                LogUtil.d(TAG,"------------------------------定位失败");
            }
        }
    }
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                //开始定位
                case Utils.MSG_LOCATION_START:
//                    positionTv.setText("正在定位...");
                    break;
                // 定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    result = Utils.getLocationStr(loc);
                    LogUtil.d(TAG,result);
                    break;
                //停止定位
                case Utils.MSG_LOCATION_STOP:
//                    positionTv.setText("定位停止");
                    break;
                case Utils.REQUEST_COMMAND:  //需要发送数据
                    // 启动定位
                    mLocationClient.startLocation();
                    LogUtil.d(TAG,"-------------------------------------------------START Po");
                    break;
                default:
                    break;
            }
        }
    };

    public void broadcastCall(){
        Message msg = handler.obtainMessage();
        msg.what = Utils.REQUEST_COMMAND;
        handler.sendMessage(msg);
        LogUtil.d("broadcast","Enther----------------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        mLocationClient = null;
        mLocationClient = null;
    }

//    public static String getResult() {
//        return result;
//    }
}
