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
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
//import com.amap.api.location.AMapLocationListener;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {
    private Button sendBtn;
    private TextView positionTv = null;
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
        sb.append("测试");
        positionTv.setText(sb);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendPositionService().broadcastCall();
            }
        });
//        new SendPositionService().init();
        new SendPositionService().mLocationClient.startLocation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
