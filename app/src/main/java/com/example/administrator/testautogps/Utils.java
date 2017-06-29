/**
 * 
 */
package com.example.administrator.testautogps;

import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 辅助工具类
 * @创建时间： 2015年11月24日 上午11:46:50
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: Utils.java
 * @类型名称: Utils
 */
public class Utils {
	/**
	 *  开始定位
	 */
	public final static int MSG_LOCATION_START = 0;
	/**
	 * 定位完成
	 */
	public final static int MSG_LOCATION_FINISH = 1;
	/**
	 * 停止定位
	 */
	public final static int MSG_LOCATION_STOP= 2;
	/**
	 * 指定获取位置，需要发送
	 */
	public static final int REQUEST_COMMAND = 3;
	/**
	 * 自动获取位置。不需要发送
	 */
	public static final int REQUEST_AUTO = 4;

	
	public final static String KEY_URL = "URL";
	public final static String URL_H5LOCATION = "file:///android_asset/location.html";
	private static final String TAG = "Utils";

	/**
	 * 根据定位结果返回定位信息的字符串
	 * @param location
	 * @return
	 */
	public synchronized static String getLocationStr(AMapLocation location){
		if(null == location){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
		if(location.getErrorCode() == 0){
//			sb.append("定位成功" + "\n");
//			sb.append("[定位类型:" + location.getLocationType() + "]");
//			sb.append("[经度:" + location.getLongitude() + "\n");
//			sb.append("纬度    : " + location.getLatitude() + "\n");
//			sb.append("[精度:" + location.getAccuracy() + "米]");
			sb.append("[提供者:" + location.getProvider() + "]-");
//			sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
//			sb.append("角    度    : " + location.getBearing() + "\n");
//			// 获取当前提供定位服务的卫星个数
//			sb.append("星    数    : " + location.getSatellites() + "\n");
//			sb.append("国    家    : " + location.getCountry() + "\n");
//			sb.append("省            : " + location.getProvince() + "\n");
//			sb.append("市            : " + location.getCity() + "\n");
//			sb.append("城市编码 : " + location.getCityCode() + "\n");
//			sb.append("区            : " + location.getDistrict() + "\n");
//			sb.append("区域 码   : " + location.getAdCode() + "\n");
			sb.append("[地址:" + location.getAddress() + location.getAccuracy() + "米]-");
			sb.append("[兴趣点:" + location.getPoiName() + "]");
			//定位完成的时间
			sb.append("[定位时间:" + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "]");

			LogUtil.d(TAG,"-------------------------------------------GET");

			Position position = new Position();
            position.setAccuracy(location.getAccuracy());
            position.setAddress(location.getAddress());
            position.setPoiName(location.getPoiName());
            position.setProvider(location.getProvider());
            position.setTime(formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss"));
            position.save();
		} else {
			//定位失败
			sb.append("定位失败" + "+");
			sb.append("错误码:" + location.getErrorCode() + "+");
			sb.append("错误信息:" + location.getErrorInfo() + "+");
			sb.append("错误描述:" + location.getLocationDetail() + "+");
		}
		//定位之后的回调时间
//		sb.append("[回调时间:" + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "]");
		return sb.toString();
	}

	private static SimpleDateFormat sdf = null;
	public  static String formatUTC(long l, String strPattern) {
		if (TextUtils.isEmpty(strPattern)) {
			strPattern = "yyyy-MM-dd HH:mm:ss";
		}
		if (sdf == null) {
			try {
				sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
			} catch (Throwable e) {
			}
		} else {
			sdf.applyPattern(strPattern);
		}
		return sdf == null ? "NULL" : sdf.format(l);
	}


	/**
	 * 调起系统发短信功能
	 * @param phoneNumber
	 * @param message
	 */
	public static void doSendSMSTo(String phoneNumber,String message){
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
}
