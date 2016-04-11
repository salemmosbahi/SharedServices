package it.mdev.sharedservices.activity;

import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by salem on 4/6/16.
 */
public class Profile extends Fragment {
    SharedPreferences pref;
    ServerRequest sr = new ServerRequest();
    Controllers conf = new Controllers();

    private TextView Username_txt, City_txt, Age_txt, Email_txt, Phone_txt, Driver_txt;
    private ImageView Picture_iv;
    private RatingBar Point_rb;
    private Button Logout_btn, Refuse_btn, Accept_btn;

    private String fname, lname, gender, dateN, country, city, email, phone, point, picture;
    private Boolean driver;

    public Profile() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Picture_iv = (ImageView) v.findViewById(R.id.Picture_iv);
        Username_txt = (TextView) v.findViewById(R.id.Username_txt);
        Age_txt = (TextView) v.findViewById(R.id.Age_txt);
        City_txt = (TextView) v.findViewById(R.id.City_txt);
        Email_txt = (TextView) v.findViewById(R.id.Email_txt);
        Phone_txt = (TextView) v.findViewById(R.id.Phone_txt);
        Driver_txt = (TextView) v.findViewById(R.id.Driver_txt);
        Point_rb = (RatingBar) v.findViewById(R.id.Pt_rb);
        Logout_btn = (Button) v.findViewById(R.id.Logout_btn);
        Refuse_btn = (Button) v.findViewById(R.id.Refuse_btn);
        Accept_btn = (Button) v.findViewById(R.id.Accept_btn);

        LayerDrawable stars = (LayerDrawable) Point_rb.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);

        Logout_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(conf.NetworkIsAvailable(getActivity())){
                    //logoutFunct();
                }else{
                    Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(conf.NetworkIsAvailable(getActivity())){
            getProfile();
        }else{
            Logout_btn.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    private void getProfile() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        JSONObject json = sr.getJson(conf.url_profile, params);
        if(json != null){
            try{
                if(json.getBoolean("res")) {
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
                    byte[] imageAsBytes = Base64.decode(picture.getBytes(), Base64.DEFAULT);
                    Picture_iv.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Logout_btn.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.serverunvalid, Toast.LENGTH_SHORT).show();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new Home());
        ft.addToBackStack(null);
        ft.commit();
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
    }
}
