package com.onyx.jdread.shop.common;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.ClientUtils;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.shop.request.JavaNetCookieJar;
import com.onyx.jdread.shop.request.PersistentCookieStore;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;

import okhttp3.OkHttpClient;

/**
 * Created by huxiaomao on 2016/12/2.
 */

public class CloudApiContext {
    public static final String JD_BOOK_STORE_URL = "http://gw.e.jd.com/";
    public static final String JD_BASE_URL = "https://gw-e.jd.com/";

    public static class NewBookDetail {
        public static final String BOOK_SPECIAL_PRICE_TYPE = "specialPrice";
        public static final String FUNCTION_ID = "functionId";
        public static final String API_NEW_BOOK_DETAIL = "newBookDetail";
        public static final String DETAIL = "detail";
        public static final String TYPE = "type";
        public static final String BOOK_LIST = "bookList";
        public static final String BOOK_ID = "bookId";
    }

    public static class BookstoreModuleList {
        public static final String SYS_ID = "sysId";
        public static final String RETURN_MESSAGE = "returnMessage";
        public static final String API_GET_MAIN_THEME_INFO = "getMainThemeInfo";
    }

    public static class BookstoreModule {
        public static final String ID = "id";
        public static final String MODULE_TYPE = "moduleType";
        public static final String RETURN_MESSAGE = "returnMessage";
        public static final String MODULE_CHILD_INFO = "getModuleChildInfo";
        public static final int TODAY_SPECIAL_ID = 226;
        public static final int TODAY_SPECIAL_MODULE_TYPE = 10;
        public static final int NEW_BOOK_DELIVERY_ID = 68;
        public static final int NEW_BOOK_DELIVERY_MODULE_TYPE = 5;
        public static final int FREE_JOURNALS_ID = 181;
        public static final int FREE_JOURNALS_MODULE_TYPE = 6;
    }

    public static class CategoryList {
        public static final String CLIENT_PLATFORM = "clientPlatform";
        public static final int CLIENT_PLATFORM_VALUE = 1;
        public static final String CATEGORY_LIST = "CategoryList";
    }

    public static String getJDBooxBaseUrl() {
        return JD_BOOK_STORE_URL;
    }

    public static String getJdBaseUrl() {
        return JD_BASE_URL;
    }

    private static CookieHandler addCookie() {
        String a2 = ClientUtils.getWJLoginHelper().getA2();
        if (!StringUtils.isNullOrEmpty(a2)) {
            PersistentCookieStore persistentCookieStore = new PersistentCookieStore(JDReadApplication.getInstance());
            HttpCookie newCookie = new HttpCookie(Constants.COOKIE_KEY, a2);
            newCookie.setDomain(Constants.COOKIE_DOMAIN);
            newCookie.setPath("/");
            newCookie.setVersion(0);
            persistentCookieStore.removeAll();
            persistentCookieStore.add(null, newCookie);
            CookieHandler cookieHandler = new CookieManager(persistentCookieStore, CookiePolicy.ACCEPT_ALL);
            return cookieHandler;
        }
        return null;
    }

    public static OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(addCookie()))
                .build();
        return client;
    }
}