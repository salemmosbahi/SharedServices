package it.mdev.sharedservices.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.Encryption;
import it.mdev.sharedservices.util.ServerRequest;
import it.mdev.sharedservices.util.SocketIO;

/**
 * Created by salem on 4/6/16.
 */
public class Profile extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();
    Socket socket = SocketIO.getInstance();

    private TextView Username_txt, City_txt, Age_txt, Email_txt, Phone_txt, Driver_txt, PointService_txt;
    private ImageView Picture_iv;
    private RatingBar Point_rb, PointService_rb;
    private Button Logout_btn, Refuse_btn, Accept_btn;
    public static Dialog rankDialog;
    public static RatingBar ratingBar;
    public static Button Vote_btn;
    public static TextView Service_txt;

    private String activity;
    private String tokenVisitor, fname, lname, gender, dateN, country, city, email, phone, point, pointService, picture, service, idService;
    private boolean driver, vote;
    private int value;

    public Profile() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.profile));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        vote = false;
        if (getArguments() != null) {
            activity = getArguments().getString(conf.tag_activity);
            tokenVisitor = getArguments().getString(conf.tag_id);
            service = getArguments().getString(conf.tag_service);
            idService = getArguments().getString(conf.tag_idService);
        } else {
            activity = "Me";
            tokenVisitor = "";
            service = "";
            idService = "";
        }

        socket.connect();

        Picture_iv = (ImageView) v.findViewById(R.id.Picture_iv);
        Username_txt = (TextView) v.findViewById(R.id.Username_txt);
        Age_txt = (TextView) v.findViewById(R.id.Age_txt);
        City_txt = (TextView) v.findViewById(R.id.City_txt);
        Email_txt = (TextView) v.findViewById(R.id.Email_txt);
        Phone_txt = (TextView) v.findViewById(R.id.Phone_txt);
        Driver_txt = (TextView) v.findViewById(R.id.Driver_txt);
        Point_rb = (RatingBar) v.findViewById(R.id.Point_rb);
        PointService_txt = (TextView) v.findViewById(R.id.PointService_txt);
        PointService_rb = (RatingBar) v.findViewById(R.id.PointService_rb);
        Logout_btn = (Button) v.findViewById(R.id.Logout_btn);
        Refuse_btn = (Button) v.findViewById(R.id.Refuse_btn);
        Accept_btn = (Button) v.findViewById(R.id.Accept_btn);

        if (activity.equals("Me")) {
            Logout_btn.setVisibility(View.VISIBLE);
            Accept_btn.setVisibility(View.GONE);
            Refuse_btn.setVisibility(View.GONE);
            PointService_txt.setVisibility(View.GONE);
            PointService_rb.setVisibility(View.GONE);
        } else if (activity.equals("BoxUsers")) {
            Logout_btn.setVisibility(View.GONE);
            Accept_btn.setVisibility(View.VISIBLE);
            Refuse_btn.setVisibility(View.VISIBLE);
            PointService_txt.setVisibility(View.VISIBLE);
            PointService_rb.setVisibility(View.VISIBLE);
        } else {
            if (tokenVisitor.equals(pref.getString(conf.tag_token, ""))) {
                Logout_btn.setVisibility(View.VISIBLE);
            } else {
                Logout_btn.setVisibility(View.GONE);
                vote = true;
            }
            Accept_btn.setVisibility(View.GONE);
            Refuse_btn.setVisibility(View.GONE);
            PointService_txt.setVisibility(View.VISIBLE);
            PointService_rb.setVisibility(View.VISIBLE);
        }

        LayerDrawable stars = (LayerDrawable) DrawableCompat.unwrap(Point_rb.getProgressDrawable());
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

        LayerDrawable starx = (LayerDrawable) DrawableCompat.unwrap(PointService_rb.getProgressDrawable());
        starx.getDrawable(2).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        starx.getDrawable(1).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        starx.getDrawable(0).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        PointService_txt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickVote();
            }
        });

        PointService_rb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    clickVote();
                }
                return true;
            }
        });

        Logout_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    logoutFunct();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Refuse_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    submitFunct("refuse");
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Accept_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    submitFunct("accept");
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(conf.NetworkIsAvailable(getActivity())){
            Point_rb.setVisibility(View.VISIBLE);
            getProfile();
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        if (vote && controlVote()) {
            PointService_txt.setText(R.string.chv);
        }

        return v;
    }

    private void getProfile() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (tokenVisitor.equals("")) {
            params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
            params.add(new BasicNameValuePair(conf.tag_service, ""));
        } else {
            params.add(new BasicNameValuePair(conf.tag_token, tokenVisitor));
            params.add(new BasicNameValuePair(conf.tag_service, service));
        }
        JSONObject json = sr.getJson(conf.url_profile, params);
        if(json != null){
            try{
                if(json.getBoolean(conf.res)) {
                    int keyVirtual = Integer.parseInt(json.getString(conf.tag_key));
                    Encryption algo = new Encryption();
                    String newKey = algo.key(keyVirtual);
                    fname = algo.enc2dec(json.getString(conf.tag_fname), newKey);
                    lname = algo.enc2dec(json.getString(conf.tag_lname), newKey);
                    gender = algo.enc2dec(json.getString(conf.tag_gender), newKey);
                    dateN = algo.enc2dec(json.getString(conf.tag_dateN), newKey);
                    country = algo.enc2dec(json.getString(conf.tag_country), newKey);
                    city = algo.enc2dec(json.getString(conf.tag_city), newKey);
                    email = algo.enc2dec(json.getString(conf.tag_email), newKey);
                    phone = algo.enc2dec(json.getString(conf.tag_phone), newKey);
                    driver = json.getBoolean(conf.tag_driver);
                    point = json.getString(conf.tag_pt);
                    pointService = (service != "") ? json.getString(conf.tag_ptService) : "";
                    picture = json.getString(conf.tag_picture);
                    Username_txt.setText(fname + " " + lname);
                    int[] tab = new Calculator().getAge(dateN);
                    Age_txt.setText(tab[0] + "years, " + tab[1] + "month, " + tab[2] + "day");
                    City_txt.setText(gender + " from " + country + ", lives in " + city);
                    Email_txt.setText(email);
                    Phone_txt.setText(phone);
                    String str = (driver = true) ? getString(R.string.driverOn) : getString(R.string.driverOff);
                    Driver_txt.setText(str);
                    Point_rb.setRating(Float.parseFloat(point));
                    if (pointService != "") {
                        PointService_rb.setRating(Float.parseFloat(pointService));
                    }
                    if (picture.equals("")) {
                        Picture_iv.setBackgroundResource(R.drawable.profile);
                    } else {
                        byte[] imageAsBytes = Base64.decode(picture.getBytes(), Base64.DEFAULT);
                        Picture_iv.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
            Logout_btn.setVisibility(View.GONE);
            Point_rb.setVisibility(View.GONE);
        }
    }

    private void logoutFunct() {
        JSONObject jx = new JSONObject();
        try {
            jx.put(conf.tag_type, "logout");
            jx.put(conf.tag_tokenMain, pref.getString(conf.tag_token, ""));
            jx.put(conf.tag_token, "");
            socket.emit(conf.io_count, jx);
        } catch (JSONException e) { }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        JSONObject json = sr.getJson(conf.url_logout, params);
        if (json != null) {
            try{
                if(json.getBoolean(conf.res)){
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(conf.tag_token, "");
                    edit.putString(conf.tag_username, "");
                    edit.putString(conf.tag_dateN, "");
                    edit.putString(conf.tag_country, "");
                    edit.putString(conf.tag_city, "");
                    edit.putString(conf.tag_picture, "");
                    edit.commit();

                    RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.nav_header_container);
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View vi = inflater.inflate(R.layout.toolnav_drawer, null);
                    TextView tv = (TextView) vi.findViewById(R.id.usernameTool_txt);
                    tv.setText("");
                    rl.addView(vi);

                    goFragment(new Home());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void submitFunct(String status) {
        Accept_btn.setVisibility(View.GONE);
        Refuse_btn.setVisibility(View.GONE);

        JSONObject jx = new JSONObject();
        try {
            jx.put(conf.tag_type, "token");
            jx.put(conf.tag_tokenMain, pref.getString(conf.tag_token, ""));
            jx.put(conf.tag_token, tokenVisitor);
            socket.emit(conf.io_count, jx);
        } catch (JSONException e) { }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_idService, idService));
        params.add(new BasicNameValuePair(conf.tag_service, service));
        params.add(new BasicNameValuePair(conf.tag_status, status));
        params.add(new BasicNameValuePair(conf.tag_tokenMain, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_tokenVisitor, tokenVisitor));
        params.add(new BasicNameValuePair(conf.tag_usernameVisitor, fname + " " + lname));
        params.add(new BasicNameValuePair(conf.tag_ageVisitor, Age_txt.getText().toString()));
        JSONObject json = sr.getJson(conf.url_changeStatus, params);
        if (json != null) {
            try {
                Toast.makeText(getActivity(),json.getString(conf.response),Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    private void clickVote() {
        if (PointService_txt.getText().equals(getString(R.string.chv))) {
            if (conf.NetworkIsAvailable(getActivity())) {
                if (controlVote()) {
                    vote();
                } else {
                    Toast.makeText(getActivity(),fname + " is noted or " + service + " not complete",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void vote() {
        rankDialog = new Dialog(getActivity(), R.style.FullHeightDialog);
        rankDialog.setContentView(R.layout.dialog);
        rankDialog.setCancelable(true);
        rankDialog.show();
        ratingBar = (RatingBar) rankDialog.findViewById(R.id.Vote_rat);
        LayerDrawable stary = (LayerDrawable) DrawableCompat.unwrap(ratingBar.getProgressDrawable());
        stary.getDrawable(2).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stary.getDrawable(1).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stary.getDrawable(0).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        Service_txt = (TextView) rankDialog.findViewById(R.id.Service_txt);
        Service_txt.setText(service);
        Vote_btn = (Button) rankDialog.findViewById(R.id.Vote_btn);
        Vote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankDialog.dismiss();
                value = (int) ratingBar.getRating();
                value *= 2;
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(conf.tag_service, service));
                params.add(new BasicNameValuePair(conf.tag_idService, idService));
                params.add(new BasicNameValuePair(conf.tag_token, tokenVisitor));
                params.add(new BasicNameValuePair(conf.tag_pt, "" + value));
                JSONObject json = sr.getJson(conf.url_vote, params);
                if (json != null) {
                    try {
                        Toast.makeText(getActivity(), json.getString(conf.response), Toast.LENGTH_SHORT).show();
                        if (json.getBoolean(conf.res)) {
                            getProfile();
                            PointService_txt.setText(R.string.spv);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean controlVote(){
        boolean check = false;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_service, service));
        params.add(new BasicNameValuePair(conf.tag_idService, idService));
        params.add(new BasicNameValuePair(conf.tag_token, tokenVisitor));
        params.add(new BasicNameValuePair(conf.tag_usernameMain, pref.getString(conf.tag_username, "")));
        JSONObject json = sr.getJson(conf.url_controlVote, params);
        if (json != null) {
            try{
                check = json.getBoolean(conf.res);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
        return check;
    }

    private void goFragment(Fragment fr) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, fr);
        ft.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activity.equals("Me")) {
            goFragment(new Home());
        } else if (activity.equals("BoxUsers")) {
            Fragment fr = new BoxUsers();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            Bundle args = new Bundle();
            args.putString(conf.tag_idService, idService);
            args.putString(conf.tag_service, service);
            fr.setArguments(args);
            ft.replace(R.id.container_body, fr);
            ft.commit();
        } else if (activity.equals("DownloadProfile")) {
            Fragment fr = new DownloadProfile();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            Bundle args = new Bundle();
            args.putString(conf.tag_id, idService);
            args.putString(conf.tag_activity, activity);
            fr.setArguments(args);
            ft.replace(R.id.container_body, fr);
            ft.commit();
        } else if (activity.equals("CarProfile")) {
            Fragment fr = new CarProfile();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            Bundle args = new Bundle();
            args.putString(conf.tag_id, idService);
            args.putString(conf.tag_activity, activity);
            fr.setArguments(args);
            ft.replace(R.id.container_body, fr);
            ft.commit();
        } else if (activity.equals("EventProfile")) {
            Fragment fr = new EventProfile();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            Bundle args = new Bundle();
            args.putString(conf.tag_id, idService);
            args.putString(conf.tag_activity, activity);
            fr.setArguments(args);
            ft.replace(R.id.container_body, fr);
            ft.commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
