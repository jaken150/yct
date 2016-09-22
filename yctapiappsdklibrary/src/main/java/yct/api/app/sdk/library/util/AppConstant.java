package yct.api.app.sdk.library.util;


public class AppConstant {
    public static boolean DEBUGVERSION = true;
    public static boolean USE_HTTPS = true;
    public static String WX_APP_ID = "wx36d499f4e33d2d44";

    //商品中心
    public static String PRODUCT_CZJ_URL = "https://api.gzyct.com:9443/gw/product";
    public static String PRODUCT_CZJ_URL_DEBUG = "http://wxtest.gzyct.com:8000/test/gw/product";
    public static String PRODUCT_CZJ_URL_DEBUG_HTTPS = "https://wxtest.gzyct.com/gw/product";

    //支付中心
    public static String PAY_CENTER_URL = "https://api.gzyct.com:9443/gw/paycenter/";
    public static String PAY_CENTER_URL_DEBUG = "http://wxtest.gzyct.com:8000/test/gw/paycenter/";
    public static String PAY_CENTER_URL_DEBUG_HTTPS = "https://wxtest.gzyct.com/gw/paycenter/";

    //基础功能
    public static String BASE_URL = "https://api.gzyct.com:9443/gw/base/";
    public static String BASE_URL_DEBUG = "http://wxtest.gzyct.com:8000/test/gw/base/";
    public static String BASE_URL_DEBUG_HTTPS = "https://wxtest.gzyct.com/gw/base/";

    //APP后台
    public static String YCTI_URL = "http://112.94.161.30/yctI/";
    public static String YCTI_URL_DEBUG = "http://218.20.156.175:9110/yctI/";

    public static String getPayCenterUrl() {
        if (DEBUGVERSION)
            if (USE_HTTPS)
                return PAY_CENTER_URL_DEBUG_HTTPS;
            else
                return PAY_CENTER_URL_DEBUG;
        else
            return PAY_CENTER_URL;
    }

    public static String getProductCzjUrl() {
        if (DEBUGVERSION)
            if (USE_HTTPS)
                return PRODUCT_CZJ_URL_DEBUG_HTTPS;
            else
            return PRODUCT_CZJ_URL_DEBUG;
        else
            return PRODUCT_CZJ_URL;
    }

    public static String getBaseUrl() {
        if (DEBUGVERSION)
            if (USE_HTTPS)
                return BASE_URL_DEBUG_HTTPS;
            else
            return BASE_URL_DEBUG;
        else
            return BASE_URL;
    }

}
