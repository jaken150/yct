package yct.api.app.sdk.library.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class YctApiMD5 {

    public static String encryptObjectMD5(Object object, String channel_key) {
        //VerifySign request result : false; signed output : 173B014F1370C44BD4D0947892022898,
        // request original sign : ,
        // signString : card_num=5600010205&channel_code=10000001&charset=UTF-8&coupon_fee=0&fee_type=CNY&pay_channel=WXIN&pay_fee=5000&service=yct.product.czj.order.apply&sign_type=MD5&timestamp=2016-04-08 04:34:08&total_fee=5000&user_id=deviceid_4a43f7a5e99307da&version=1.0&key=82c14c817b14af0c3b628dad3067777b
        // ===== for interfaceRequest {"card_num":"5600010205","channel_code":"10000001","charset":"UTF-8","coupon_fee":0,"fee_type":"CNY","pay_channel":"WXIN","pay_fee":5000,"service":"yct.product.czj.order.apply","sign":"","sign_type":"MD5","timestamp":"2016-04-08 04:34:08","total_fee":5000,"user_id":"deviceid_4a43f7a5e99307da","version":"1.0"}
        try {
            StringBuffer sb = new StringBuffer();
            Map<String, String> map = (Map<String, String>) JSON.toJSON(object);
            TreeMap<String, String> treeMap = new TreeMap<>();
            treeMap.putAll(map);
            Iterator entries = treeMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String) entry.getKey();
                Object valueObj = (Object) entry.getValue();
                String value = "";
                if(valueObj == null){
                    continue;
                }
                //System.out.println("Key = " + key+", class = "+valueObj.getClass());
                if (Integer.class.isInstance(valueObj)) {
                    value = "" + (int) valueObj;
                } else if (String.class.isInstance(valueObj)) {
                    value = (String) valueObj;
                } else if (com.alibaba.fastjson.JSONArray.class.isInstance(valueObj)) {
                    value = ((com.alibaba.fastjson.JSONArray) valueObj).toJSONString();
                }
                // System.out.println("Key = " + key + ", Value = " + value);
                if (value != null && !value.equalsIgnoreCase("null") && value.length() > 0 && !key.equalsIgnoreCase("sign")) {
                    sb.append(key + "=" + value + "&");
                }

            }
            sb.append("key=" + channel_key);
//            Log.e("YctApiMD5","origin data = "+sb.toString());
            return bytesToHexString(encryptMD5(sb.toString().getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
