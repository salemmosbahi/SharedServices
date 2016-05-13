package it.mdev.sharedservices.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.mdev.sharedservices.Main;
import it.mdev.sharedservices.R;
import it.mdev.sharedservices.database.CarDB;
import it.mdev.sharedservices.database.DownloadAdapterList;
import it.mdev.sharedservices.database.DownloadDB;
import it.mdev.sharedservices.database.EventAdapterList;
import it.mdev.sharedservices.database.EventDB;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 01/05/16.
 */
public class EventSearch extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    Calculator cal = new Calculator();
    ServerRequest sr = new ServerRequest();

    private EditText Name_etxt;
    private TextInputLayout Name_input;
    private Spinner City_sp;
    private TextView Date_txt;
    private SwitchCompat Event_swt;
    private Button Search_btn;
    private ListView lv;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;
    private int year, month, day;

    private ArrayList<EventDB> EventDBList;
    private JSONArray dataJsonArray = null;

    public EventSearch() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_search, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.event_search));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        Name_input = (TextInputLayout) v.findViewById(R.id.Name_input);
        Name_etxt = (EditText) v.findViewById(R.id.Name_etxt);
        City_sp = (Spinner) v.findViewById(R.id.City_sp);
        Date_txt = (TextView) v.findViewById(R.id.Date_txt);
        Event_swt = (SwitchCompat) v.findViewById(R.id.Event_swt);
        Search_btn = (Button) v.findViewById(R.id.Search_btn);
        lv = (ListView) v.findViewById(R.id.Event_lv);

        Event_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Event_swt.setText(R.string.event_complete);
                } else {
                    Event_swt.setText(R.string.event_pending);
                }
            }
        });

        final Calendar calendar = cal.getCurrentTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        Date_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        Date_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), R.style.MyMaterialDesignTheme, dateSetListener, year, month, day).show();
            }
        });

        CitysList = new ArrayList<String>();
        List<NameValuePair> cityParams = new ArrayList<NameValuePair>();
        cityParams.add(new BasicNameValuePair(conf.tag_name, pref.getString(conf.tag_country, "")));
        JSONObject jsonx = sr.getJson(conf.url_getAllCity, cityParams);
        if (jsonx != null) {
            try {
                if (jsonx.getBoolean(conf.res)) {
                    citys = jsonx.getJSONArray(conf.data);
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

        Search_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (conf.NetworkIsAvailable(getActivity())) {
                    searchForm();
                } else {
                    Toast.makeText(getActivity(),R.string.networkunvalid,Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    private void searchForm() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(conf.tag_name, Name_etxt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_date, Date_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        params.add(new BasicNameValuePair(conf.tag_city, City_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_status, Event_swt.getText().toString()));
        EventDBList = new ArrayList<>();
        JSONObject json = sr.getJson(conf.url_getEvent, params);
        if(json != null){
            try{
                if(json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray(conf.data);
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject c = dataJsonArray.getJSONObject(i);
                        String id = c.getString(conf.tag_id);
                        String name = c.getString(conf.tag_name);
                        String city = c.getString(conf.tag_city);
                        String date = c.getString(conf.tag_date);
                        EventDB rec = new EventDB(id, name, city, date);
                        EventDBList.add(rec);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
        EventAdapterList adapter = new EventAdapterList(getActivity(), EventDBList, EventSearch.this);
        lv.setAdapter(adapter);
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            Date_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Event());
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
