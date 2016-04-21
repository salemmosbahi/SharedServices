package it.mdev.sharedservices.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by salem on 4/4/16.
 */
public class Controllers {
    public static final String app = "AppSharedServices";
    public static final String res = "res";
    public static final String response = "response";
    public static final String data = "data";

    //public static final String url = "http://10.0.2.2:4000";
    public static final String url = "http://192.168.1.2:4000";
    public static final String url_getAllCountry = url + "/getAllCountry";
    public static final String url_getAllCity = url + "/getAllCity";
    public static final String url_login = url + "/login";
    public static final String url_logout = url + "/logout";
    public static final String url_signup = url + "/signup";
    public static final String url_profile = url + "/profile";
    public static final String url_getDownloadEnd = url + "/getDownloadEnd";
    public static final String url_getDownload = url + "/getDownload";
    public static final String url_addDownload = url + "/addDownload";

    public static final String tag_key = "key";
    public static final String tag_id = "_id";
    public static final String tag_token = "token";
    public static final String tag_username = "username";
    public static final String tag_name = "name";
    public static final String tag_fname = "fname";
    public static final String tag_lname = "lname";
    public static final String tag_picture = "picture";
    public static final String tag_email = "email";
    public static final String tag_password = "password";
    public static final String tag_gender = "gender";
    public static final String tag_dateN = "dateN";
    public static final String tag_country = "country";
    public static final String tag_city = "city";
    public static final String tag_phone = "phone";
    public static final String tag_driver = "driver";
    public static final String tag_pt = "pt";
    public static final String tag_size = "size";
    public static final String tag_date = "date";
    public static final String tag_status = "status";

    public boolean NetworkIsAvailable(Context cx) {
        ConnectivityManager manager = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
