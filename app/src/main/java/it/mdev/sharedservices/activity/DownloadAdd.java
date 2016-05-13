package it.mdev.sharedservices.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadAdd extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    ServerRequest sr = new ServerRequest();

    private ImageView Download_iv;
    private EditText Name_etxt, Size_etxt;
    private TextInputLayout Name_input, Size_input;
    private Spinner City_sp;
    private Button Add_btn, Empty_btn;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;
    private boolean isPicture = false;

    public DownloadAdd() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.download_add, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.download_add));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Download_iv = (ImageView) v.findViewById(R.id.Download_iv);
        Name_input = (TextInputLayout) v.findViewById(R.id.Name_input);
        Name_etxt = (EditText) v.findViewById(R.id.Name_etxt);
        Size_input = (TextInputLayout) v.findViewById(R.id.Size_input);
        Size_etxt = (EditText) v.findViewById(R.id.Size_etxt);
        City_sp = (Spinner) v.findViewById(R.id.City_sp);
        Add_btn = (Button) v.findViewById(R.id.Add_btn);
        Empty_btn = (Button) v.findViewById(R.id.Empty_btn);

        Download_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {
                    startActivityForResult(i, 2);
                } catch (ActivityNotFoundException e) { }
            }
        });

        CitysList = new ArrayList<String>();
        List<NameValuePair> cityParams = new ArrayList<NameValuePair>();
        cityParams.add(new BasicNameValuePair(conf.tag_name, pref.getString(conf.tag_country, "")));
        JSONObject jsonx = sr.getJson(conf.url_getAllCity, cityParams);
        if (jsonx != null) {
            try {
                if (jsonx.getBoolean(conf.res)) {
                    citys = jsonx.getJSONArray("data");
                    for (int i = 0; i < citys.length(); i++) {
                        JSONObject t = citys.getJSONObject(i);
                        CitysList.add(t.getString(conf.tag_name));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
        cityAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,CitysList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        City_sp.setAdapter(cityAdapter);
        City_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));

        Empty_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                emptyForm();
            }
        });

        Add_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (conf.NetworkIsAvailable(getActivity())) {
                    addForm();
                } else {
                    Toast.makeText(getActivity(),R.string.networkunvalid,Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void emptyForm() {
        Name_etxt.setText("");
        Size_etxt.setText("");
        City_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));
    }

    private void addForm() {
        if (!validateName()) { return; }
        if (!validateSize()) { return; }
        if (!validateCity()) { return; }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (isPicture) {
            params.add(new BasicNameValuePair(conf.tag_picture, getStringPicture()));
        } else {
            params.add(new BasicNameValuePair(conf.tag_picture, ""));
        }
        params.add(new BasicNameValuePair(conf.tag_name, Name_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_size, Size_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_token, pref.getString(conf.tag_token, "")));
        params.add(new BasicNameValuePair(conf.tag_username, pref.getString(conf.tag_username, "")));
        params.add(new BasicNameValuePair(conf.tag_date, pref.getString(conf.tag_dateN, "")));
        params.add(new BasicNameValuePair(conf.tag_city, City_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        JSONObject json = sr.getJson(conf.url_addDownload, params);
        if (json != null) {
            try{
                String response = json.getString(conf.response);
                if(json.getBoolean(conf.res)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.container_body, new DownloadSearch());
                    ft.commit();
                }
                Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
            }catch(JSONException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateCity() {
        if(City_sp.getSelectedItem() != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean validateSize() {
        if(Size_etxt.getText().toString().trim().isEmpty()) {
            Size_input.setError(getString(R.string.size_err));
            requestFocus(Size_etxt);
            return false;
        } else {
            if (Integer.parseInt(Size_etxt.getText().toString()) <= 2) {
                Size_input.setError(getString(R.string.size_err));
                requestFocus(Size_etxt);
                return false;
            } else {
                Size_input.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validateName() {
        if(Name_etxt.getText().toString().trim().isEmpty()) {
            Name_input.setError(getString(R.string.name_err));
            requestFocus(Name_etxt);
            return false;
        } else {
            Name_input.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private String getStringPicture() {
        Download_iv.buildDrawingCache();
        Bitmap bitmap = Download_iv.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 2) {
                Uri selectedImageUri = data.getData();
                Download_iv.setImageURI(selectedImageUri);
                isPicture = true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Download());
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
