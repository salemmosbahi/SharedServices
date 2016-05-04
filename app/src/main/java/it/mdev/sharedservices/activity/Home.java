package it.mdev.sharedservices.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.nkzawa.socketio.client.Socket;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.SocketIO;

/**
 * Created by salem on 4/4/16.
 */
public class Home extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    //Socket socket = SocketIO.getInstance();

    private Button Car_btn, Download_btn, Paper_btn, Event_btn;

    public Home() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Car_btn = (Button) v.findViewById(R.id.Car_btn);
        Car_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFragment(new Car());
            }
        });

        Download_btn = (Button) v.findViewById(R.id.Download_btn);
        Download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFragment(new Download());
            }
        });

        Event_btn = (Button) v.findViewById(R.id.Event_btn);
        Event_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFragment(new Event());
            }
        });

        Paper_btn = (Button) v.findViewById(R.id.Paper_btn);
        Paper_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goFragment(new Paper());
            }
        });

        return  v;
    }

    private void goFragment(Fragment fr) {
        if (pref.getString(conf.tag_token, "").equals("")) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, new Login());
            ft.commit();
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            ft.replace(R.id.container_body, fr);
            ft.commit();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());
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
