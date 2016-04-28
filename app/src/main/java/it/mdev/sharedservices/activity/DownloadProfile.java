package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.database.DownloadAdapterList;
import it.mdev.sharedservices.database.DownloadDB;
import it.mdev.sharedservices.database.UserCopyAdapterList;
import it.mdev.sharedservices.database.UserCopyDB;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;
import it.mdev.sharedservices.util.SocketIO;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadProfile extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    Calculator cal = new Calculator();
    ServerRequest sr = new ServerRequest();
    Socket socket = SocketIO.getInstance();

    private ArrayList<UserCopyDB> UserCopyList;
    private JSONArray usersCopy = null, dataJsonArray = null;
    private ImageView Picture_iv;
    private TextView Name_txt, Location_txt, Size_txt, Status_txt, Date_txt, DateComplete_txt, UserMain_txt;
    private Button Demand_btn, Complete_btn;
    private ListView UserCopy_lv;
    private String tokenApp, tokenMain, usernameApp, usernameMain, idService, picture, name, date, dateComplete, city, country, status;
    private String activity;
    private int size;

    public DownloadProfile() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.download_profile, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.download_profile));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        idService = getArguments().getString(conf.tag_id);
        activity = getArguments().getString(conf.tag_activity);
        tokenApp = pref.getString(conf.tag_token, "");
        usernameApp = pref.getString(conf.tag_username, "");

        socket.connect();

        Picture_iv = (ImageView) v.findViewById(R.id.Picture_iv);
        Name_txt = (TextView) v.findViewById(R.id.Name_txt);
        Size_txt = (TextView) v.findViewById(R.id.Size_txt);
        Location_txt = (TextView) v.findViewById(R.id.Location_txt);
        Status_txt = (TextView) v.findViewById(R.id.Status_txt);
        Date_txt = (TextView) v.findViewById(R.id.Date_txt);
        DateComplete_txt = (TextView) v.findViewById(R.id.DateComplete_txt);
        UserMain_txt = (TextView) v.findViewById(R.id.UserMain_txt);
        Demand_btn = (Button) v.findViewById(R.id.Demand_btn);
        Complete_btn = (Button) v.findViewById(R.id.Complete_btn);
        UserCopy_lv = (ListView) v.findViewById(R.id.UserCopy_lv);

        if(conf.NetworkIsAvailable(getActivity())){
            checkDemandFunc();
            getProfile();
        }else{
            Toast.makeText(getActivity(), R.string.networkunvalid, Toast.LENGTH_SHORT).show();
        }

        if (picture.equals("")) {
            Picture_iv.setImageResource(R.drawable.ic_profile_r);
        } else {
            byte[] imageAsBytes = Base64.decode(picture.getBytes(), Base64.DEFAULT);
            Picture_iv.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
        Name_txt.setText(name);
        Size_txt.setText(size + " go");
        Location_txt.setText(city + " - " + country);
        Date_txt.setText("Created in " + date);
        DateComplete_txt.setText("Completed in " + dateComplete);
        UserMain_txt.setText("Main: " + usernameMain);

        if (tokenApp.equals(tokenMain)) {
            Complete_btn.setVisibility(View.VISIBLE);
        } else {
            Complete_btn.setVisibility(View.GONE);
        }
        if (status.equals("complete")) {
            Demand_btn.setVisibility(View.GONE);
            Complete_btn.setVisibility(View.GONE);
            DateComplete_txt.setVisibility(View.VISIBLE);
        }

        Demand_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addDemandFunc();
            }
        });

        Complete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                completeDownloadFunc();
            }
        });

        return v;
    }

    private void completeDownloadFunc() {
        JSONObject jx = new JSONObject();
        try {
            jx.put(conf.tag_type, "nottoken");
            socket.emit(conf.io_count, jx);
        } catch (JSONException e) { }

        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair(conf.tag_id, idService));
        paramx.add(new BasicNameValuePair(conf.tag_token, tokenApp));
        JSONObject jsonx = sr.getJson(conf.url_completeDownload, paramx);
        if (jsonx != null) {
            try {
                Toast.makeText(getActivity(), jsonx.getString(conf.response), Toast.LENGTH_LONG).show();
                if (jsonx.getBoolean(conf.res)) {
                    dateComplete = jsonx.getString(conf.tag_dateComplete);
                    Complete_btn.setVisibility(View.GONE);
                    DateComplete_txt.setText("Completed in " + dateComplete);
                    DateComplete_txt.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addDemandFunc() {
        JSONObject jx = new JSONObject();
        try {
            jx.put(conf.tag_type, "token");
            jx.put(conf.tag_tokenMain, tokenMain);
            jx.put(conf.tag_token, tokenApp);
            socket.emit(conf.io_count, jx);
        } catch (JSONException e) { }

        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair(conf.tag_service, getString(R.string.download)));
        paramx.add(new BasicNameValuePair(conf.tag_id, idService));
        paramx.add(new BasicNameValuePair(conf.tag_name, name));
        paramx.add(new BasicNameValuePair(conf.tag_token, tokenApp));
        paramx.add(new BasicNameValuePair(conf.tag_username, usernameApp));
        paramx.add(new BasicNameValuePair(conf.tag_date, pref.getString(conf.tag_dateN, "")));
        paramx.add(new BasicNameValuePair(conf.tag_usernameMain, usernameMain));
        JSONObject jsonx = sr.getJson(conf.url_addDemand, paramx);
        if (jsonx != null) {
            try {
                Toast.makeText(getActivity(), jsonx.getString(conf.response), Toast.LENGTH_LONG).show();
                if (jsonx.getBoolean(conf.res)) {
                    Demand_btn.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkDemandFunc() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idService));
        params.add(new BasicNameValuePair(conf.tag_token, tokenApp));
        JSONObject json = sr.getJson(conf.url_checkDemand, params);
        if (json != null) {
            try {
                if (json.getBoolean(conf.res)) {
                    Demand_btn.setVisibility(View.GONE);
                } else {
                    Demand_btn.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private void getProfile() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_id, idService));
        UserCopyList = new ArrayList<>();
        JSONObject json = sr.getJson(conf.url_getDownloadProfile, params);
        if (json != null) {
            try {
                if(json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray(conf.data);
                    JSONObject c = dataJsonArray.getJSONObject(0);
                    picture = c.getString(conf.tag_picture);
                    name = c.getString(conf.tag_name);
                    size = c.getInt(conf.tag_size);
                    date = c.getString(conf.tag_date);
                    dateComplete = c.getString(conf.tag_dateComplete);
                    status = c.getString(conf.tag_status);
                    tokenMain = c.getString(conf.tag_token);
                    usernameMain = c.getString(conf.tag_username);
                    city = c.getString(conf.tag_city);
                    country = c.getString(conf.tag_country);
                    usersCopy = c.getJSONArray(conf.tag_usersCopy);
                    if (usersCopy.length() != 0) {
                        for (int i = 0; i < usersCopy.length(); i++) {
                            JSONObject x = usersCopy.getJSONObject(i);
                            String token = x.getString(conf.tag_token);
                            String username = x.getString(conf.tag_username);
                            String age = x.getString(conf.tag_age);
                            UserCopyDB uc = new UserCopyDB(token, username, age, "Download", idService);
                            UserCopyList.add(uc);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UserCopyAdapterList adapter = new UserCopyAdapterList(getActivity(), UserCopyList, DownloadProfile.this);
            UserCopy_lv.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
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
        if (activity.equals("BoxNotify")) {
            goFragment(new BoxNotify());
        } else if (activity.equals("Download")) {
            goFragment(new Download());
        } else if (activity.equals("DownloadSearch")) {
            goFragment(new DownloadSearch());
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
