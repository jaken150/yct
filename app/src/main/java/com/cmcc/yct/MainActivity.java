package com.cmcc.yct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import yct.api.app.sdk.library.YctApiReqHelper;
import okhttp3.Callback;
import yct.api.app.sdk.library.httpentity.CardAcctBalanceResp;
import yct.api.app.sdk.library.httpentity.OrderApplyResp;
import yct.api.app.sdk.library.httpentity.OrderInfo;
import yct.api.app.sdk.library.httpentity.OrderQueryResp;
import yct.api.app.sdk.library.httpentity.PaySdkApplyResp;

public class MainActivity extends AppCompatActivity {
    private static String order_id;
    private IWXAPI api;
    private String mCardNo = "0275638978";
    private boolean VersionType = YctApiReqHelper.DEBUG;
    private static String Channel_Code = "70000001";
    private static String Channel_Key = "99b9199698e156b5718508f7b03f2681";//xxt
//    private static String Channel_Code = "70000008";
//    private static String Channel_Key = "99b9199698e156b5718508f7b03f2681";//CMCC
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_recharge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    Log.e("Mainactivity","YctApiServerDemo ----Step1---- reqAcctBalance");
                    YctApiReqHelper.getInstance().reqAcctBalance(VersionType, Channel_Code,
                            Channel_Key, "70000008_1", mCardNo, "1", new Callback() {
                                @Override
                                public void onResponse(Call arg0, Response arg1) throws IOException {
                                    // TODO Auto-generated method stub
                                    final String resp = arg1.body().string();
                                    Log.e("Mainactivity","YctApiServerDemo reqAcctBalance resp = " + resp);
                                    CardAcctBalanceResp cardAcctBalanceResp = JSON.parseObject(resp, CardAcctBalanceResp.class);
                                    Log.e("Mainactivity","YctApiServerDemo Balance = " + cardAcctBalanceResp.getBalance());//单位分

                                    Log.e("Mainactivity","YctApiServerDemo ----Step2---- reqOrderApply");
                                    YctApiReqHelper.getInstance().reqOrderApply(VersionType, Channel_Code,
                                            Channel_Key, "70000008_1", mCardNo, 1, new Callback() {//单位分

                                                @Override
                                                public void onResponse(Call arg0, Response arg1) throws IOException {
                                                    final String resp = arg1.body().string();
                                                    Log.e("Mainactivity","YctApiServerDemo reqOrderApply resp = " + resp);
                                                    OrderApplyResp orderResp = JSON.parseObject(resp, OrderApplyResp.class);
                                                    order_id = orderResp.getOrder_id();
                                                    Log.e("Mainactivity","YctApiServerDemo Order_id = " + orderResp.getOrder_id());

                                                    Log.e("Mainactivity","YctApiServerDemo ----Step3---- reqSdkApply");
                                                    YctApiReqHelper.getInstance().reqSdkApply(VersionType, Channel_Code,
                                                            Channel_Key, "70000008_1", mCardNo, 1,
                                                            orderResp, new Callback() {

                                                                @Override
                                                                public void onResponse(Call arg0, Response arg1)
                                                                        throws IOException {
                                                                    final String resp = arg1.body().string();
                                                                    System.out
                                                                            .println("YctApiServerDemo reqSdkApply resp = " + resp);
                                                                    PaySdkApplyResp paySdkApplyResp = JSON.parseObject(resp,
                                                                            PaySdkApplyResp.class);

                                                                    try {
                                                                        JSONObject json = new JSONObject(paySdkApplyResp.getPay_info());
                                                                        if (paySdkApplyResp.getPay_info() == null || paySdkApplyResp.getPay_info().length() == 0) {
                                                                            return;
                                                                        }
                                                                        api = WXAPIFactory.createWXAPI(MainActivity.this, json.getString("appid"));
                                                                        PayReq req = new PayReq();
                                                                        req.appId = json.getString("appid");
                                                                        req.partnerId = json.getString("partnerid");
                                                                        req.prepayId = json.getString("prepayid");
                                                                        req.nonceStr = json.getString("noncestr");
                                                                        req.timeStamp = json.getString("timestamp");
                                                                        req.packageValue = json.getString("package");
                                                                        req.sign = json.getString("sign");
//                                                                        Toast.makeText(MainActivity.this, "正在调起支付", Toast.LENGTH_SHORT).show();
                                                                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                                                                        //ecd8f2be7927bb017f23435267d403e3正式
                                                                        //
                                                                        Log.e("MainActivity", "api.sendReq = " + api.sendReq(req));
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(Call arg0, IOException arg1) {
                                                                    Log.e("Mainactivity",
                                                                            "YctApiServerDemo onFailure " + arg1.getMessage());
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onFailure(Call arg0, IOException arg1) {
                                                    Log.e("Mainactivity","YctApiServerDemo onFailure " + arg1.getMessage());
                                                }
                                            });

                                }

                                @Override
                                public void onFailure(Call arg0, IOException arg1) {
                                    Log.e("Mainactivity","YctApiServerDemo onFailure " + arg1.getMessage());
                                }
                            });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });


    }
}
