package it.mdev.sharedservices.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.mdev.sharedservices.R;

/**
 * Created by salem on 2/14/16.
 */
public class Settings extends Fragment {

    public Settings() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings, container, false);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
