package yct.api.app.sdk.library;

import com.alibaba.fastjson.JSON;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import yct.api.app.sdk.library.httpentity.BaseParam;
import yct.api.app.sdk.library.httpentity.CardAcctBalanceReq;
import yct.api.app.sdk.library.httpentity.OrderApplyReq;
import yct.api.app.sdk.library.httpentity.OrderApplyResp;
import yct.api.app.sdk.library.httpentity.OrderQueryReq;
import yct.api.app.sdk.library.httpentity.PaySdkApplyReq;
import yct.api.app.sdk.library.util.AppConstant;
import yct.api.app.sdk.library.util.YctApiMD5;

/**
 * Created by chenjiajian on 2016/4/1.
 */
public class YctApiReqHelper {
    public static boolean DEBUG = true;
    public static boolean PRODUCT = false;
    private OkHttpClient okHttpClient;
    private static YctApiReqHelper instance;

    public YctApiReqHelper() {
//        okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };
            SSLContext e = SSLContext.getInstance("TLS");
            e.init(null, new TrustManager[]{tm}, null);

            //不做验证
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            builder.sslSocketFactory(e.getSocketFactory());
            builder.hostnameVerifier(DO_NOT_VERIFY);
            okHttpClient = builder.build();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static YctApiReqHelper getInstance() {
        if (instance == null) {
            instance = new YctApiReqHelper();
        }
        return instance;
    }

    public void okHttpPost(String url, String json, Callback callback) {
        try {
            System.out.println("YctApiReqHelper okHttpPost  url = " + url);
            System.out.println("YctApiReqHelper okHttpPost json = " + json);
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body).build();
            okHttpClient.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildBaseParams(BaseParam baseParam, String channel_Code, String user_Id) {
        baseParam.setVersion("1.0");
        baseParam.setChannel_code(channel_Code);
        baseParam.setUser_id(user_Id);
        baseParam.setTimestamp(getTimestamp());
        baseParam.setCharset("UTF-8");
        baseParam.setSign_type("MD5");
    }

    public static String getTimestamp() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     *
     * @param debug 是否测试环境
     * @param channel_Code 羊城通分配渠道号
     * @param channel_Key 羊城通分配渠密钥
     * @param user_Id 用户身份标识
     * @param card_no 羊城通卡号10位
     * @param force_flat 是否强制刷新，不使用缓存
     * @param callback 回调
     */
    public void reqAcctBalance(boolean debug,String channel_Code, String channel_Key, String user_Id, String card_no, String force_flat,Callback callback) {
        AppConstant.DEBUGVERSION = debug;
        final CardAcctBalanceReq cardAcctBalanceReq = new CardAcctBalanceReq();
        buildBaseParams(cardAcctBalanceReq, channel_Code, user_Id);
        cardAcctBalanceReq.setService("yct.base.card.acct.balance");
        if(card_no != null && card_no.length() == 10){
            cardAcctBalanceReq.setCard_num("510000" + card_no);
        }else if(card_no != null && card_no.length() == 16){
            cardAcctBalanceReq.setCard_num(card_no);
        }
        cardAcctBalanceReq.setRt_flag(force_flat);
        cardAcctBalanceReq.setSign(YctApiMD5.encryptObjectMD5(cardAcctBalanceReq, channel_Key));
        okHttpPost(AppConstant.getBaseUrl(), JSON.toJSONString(cardAcctBalanceReq), callback);
    }

    /**
     *
     * @param debug 是否测试环境
     * @param channel_Code 羊城通分配渠道号
     * @param channel_Key 羊城通分配渠密钥
     * @param user_Id 用户身份标识
     * @param card_no 羊城通卡号10位
     * @param amount 单位为分
     * @param callback 回调
     */
    public void reqOrderApply(boolean debug,String channel_Code, String channel_Key, String user_Id, String card_no, int amount, Callback callback) {
        AppConstant.DEBUGVERSION = debug;
        final OrderApplyReq req = new OrderApplyReq();
        buildBaseParams(req, channel_Code, user_Id);
        req.setService("yct.product.czj.order.apply");
        req.setCard_num("510000" + card_no);
        req.setTotal_fee(amount);
        req.setPay_channel("WXIN");
        req.setPay_channel_type("CMCCAPP");
        req.setPay_fee(amount);
        req.setFee_type("CNY");
        req.setSign(YctApiMD5.encryptObjectMD5(req, channel_Key));
        okHttpPost(AppConstant.getProductCzjUrl(), JSON.toJSONString(req), callback);
    }

    /**
     *
     * @param debug 是否测试环境
     * @param channel_Code 羊城通分配渠道号
     * @param channel_Key 羊城通分配渠密钥
     * @param user_Id 用户身份标识
     * @param card_no 羊城通卡号10位
     * @param amount 单位为分
     * @param orderApplyResp 订单申请接口的返回结果
     * @param callback 回调
     */
    public void reqSdkApply(boolean debug,String channel_Code, String channel_Key, String user_Id, String card_no, int amount, OrderApplyResp orderApplyResp, Callback callback) {
        AppConstant.DEBUGVERSION = debug;
        PaySdkApplyReq req = new PaySdkApplyReq();
        buildBaseParams(req, channel_Code, user_Id);
        req.setService("yct.paycenter.pay.sdk.apply");
        req.setPay_channel("WXIN");
        req.setPay_fee(amount);
        req.setFee_type("CNY");
        req.setSource("1001");//1001充值金加值
        req.setOrder_id(orderApplyResp.getOrder_id());
        req.setOrder_source(orderApplyResp.getOrder_source());
        req.setBody("卡号" + card_no + "充值" + (float) amount / 100 + "元");
        req.setDetail("510000" + card_no);
        req.setPay_channel_type("CMCCAPP");
        req.setYct_extend("card_num=510000" + card_no + "&channel_code=" + channel_Code + "&product_source=1001" + "&user_id=" + user_Id);
        req.setSign(YctApiMD5.encryptObjectMD5(req, channel_Key));
        okHttpPost(AppConstant.getPayCenterUrl(), JSON.toJSONString(req),callback);
    }

    /**
     *
     * @param debug 是否测试环境
     * @param channel_Code 羊城通分配渠道号
     * @param channel_Key 羊城通分配渠密钥
     * @param user_Id 用户身份标识
     * @param page 页数
     * @param pagesize 每页长度
     * @param callback 回调
     */
    public void reqOrderQuery(boolean debug,String channel_Code, String channel_Key, String user_Id,List<String> order_list,int page, int pagesize,  Callback callback) {
        AppConstant.DEBUGVERSION = debug;
        OrderQueryReq req = new OrderQueryReq();
        buildBaseParams(req, channel_Code, user_Id);
        req.setService("yct.product.czj.order.query");
        if(order_list!=null){
            req.setYct_order(order_list);
        }else {
            List<String> empty_list = new ArrayList<>();
            req.setYct_order(empty_list);
        }
        req.setPage(page);
        req.setPagesize(pagesize);
        req.setSign(YctApiMD5.encryptObjectMD5(req,channel_Key));
        okHttpPost(AppConstant.getProductCzjUrl(), JSON.toJSONString(req),callback);
    }

}
