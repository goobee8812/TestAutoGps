package com.example.administrator.testautogps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());//时间
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                LogUtil.d("------","number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + receiveTime);
                //在这里写自己的逻辑
                if (msg.getOriginatingAddress().equals("+8615602907440")) {
                    //TODO
                    SendPositionService.startActionBaz(context,"15602907440","ppp");
                }
            }
        }
    }
}
