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
    public static final String url = "http://mahd.company:4000";
    public static final String url_getAllCountry = url + "/getAllCountry";
    public static final String url_getAllCity = url + "/getAllCity";
    public static final String url_login = url + "/login";
    public static final String url_logout = url + "/logout";
    public static final String url_signup = url + "/signup";
    public static final String url_profile = url + "/profile";

    public static final String url_getDownloadEnd = url + "/getDownloadEnd";
    public static final String url_getDownload = url + "/getDownload";
    public static final String url_getDownloadProfile = url + "/getDownloadProfile";
    public static final String url_addDownload = url + "/addDownload";
    public static final String url_completeDownload = url + "/completeDownload";

    public static final String url_getCarEnd = url + "/getCarEnd";
    public static final String url_getCar = url + "/getCar";
    public static final String url_getCarProfile = url + "/getCarProfile";
    public static final String url_addCar = url + "/addCar";
    public static final String url_completeCar = url + "/completeCar";

    public static final String url_getEventEnd = url + "/getEventEnd";
    public static final String url_getEvent = url + "/getEvent";
    public static final String url_getEventProfile = url + "/getEventProfile";
    public static final String url_addEvent = url + "/addEvent";
    public static final String url_completeEvent = url + "/completeEvent";

    public static final String url_getPaper = url + "/getPaper";
    public static final String url_getPaperProfile = url + "/getPaperProfile";

    public static final String url_changeStatus = url + "/changeStatus";
    public static final String url_controlVote = url + "/controlVote";
    public static final String url_vote = url + "/vote";
    public static final String url_boxList = url + "/boxList";
    public static final String url_boxUsersList = url + "/boxUsersList";
    public static final String url_checkDemand = url + "/checkDemand";
    public static final String url_addDemand = url + "/addDemand";
    public static final String url_count = url + "/count";

    public static final String tag_key = "key";
    public static final String tag_id = "_id";
    public static final String tag_token = "token";
    public static final String tag_tokenMain = "tokenMain";
    public static final String tag_tokenVisitor = "tokenVisitor";
    public static final String tag_username = "username";
    public static final String tag_usernameMain = "usernameMain";
    public static final String tag_usernameVisitor = "usernameVisitor";
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
    public static final String tag_dateComplete = "dateComplete";
    public static final String tag_status = "status";
    public static final String tag_usersCopy = "usersCopy";
    public static final String tag_service = "service";
    public static final String tag_idService = "idService";
    public static final String tag_ptService = "ptService";
    public static final String tag_main = "main";
    public static final String tag_age = "age";
    public static final String tag_ageVisitor = "ageVisitor";
    public static final String tag_activity = "activity";
    public static final String tag_count = "count";
    public static final String tag_type = "type";
    public static final String tag_model = "model";
    public static final String tag_depart = "depart";
    public static final String tag_destination = "destination";
    public static final String tag_description = "description";
    public static final String tag_time = "time";
    public static final String tag_goingComing = "goingComing";
    public static final String tag_highway = "highway";
    public static final String tag_place = "place";
    public static final String tag_latitude = "latitude";
    public static final String tag_longitude = "longitude";
    public static final String tag_index = "index";

    public static final String io_count = "countNotify";

    public boolean NetworkIsAvailable(Context cx) {
        ConnectivityManager manager = (ConnectivityManager) cx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
