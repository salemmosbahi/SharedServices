package it.mdev.sharedservices.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import it.mdev.sharedservices.database.CarAdapterList;
import it.mdev.sharedservices.database.CarDB;
import it.mdev.sharedservices.util.Calculator;
import it.mdev.sharedservices.util.Controllers;
import it.mdev.sharedservices.util.ServerRequest;

/**
 * Created by salem on 28/04/16.
 */
public class CarSearch extends Fragment {
    SharedPreferences pref;
    Controllers conf = new Controllers();
    Calculator cal = new Calculator();
    ServerRequest sr = new ServerRequest();

    private Spinner CityDepartCS_sp, CityDestinationCS_sp;
    private TextView DateCS_txt;
    private SwitchCompat CarCS_swt, GoingComingCS_swt, HighwayCS_swt;
    private Button SearchCS_btn;
    private ListView lvCS;

    private ArrayList<String> CitysList;
    private ArrayAdapter<String> cityAdapter;
    private JSONArray citys = null;
    private int year, month, day;

    private ArrayList<CarDB> CarDBList;
    private JSONArray dataJsonArray = null;

    public CarSearch() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.car_search, container, false);
        ((Main) getActivity()).getSupportActionBar().setTitle(getString(R.string.car_search));
        pref = getActivity().getSharedPreferences(conf.app, Context.MODE_PRIVATE);

        CityDepartCS_sp = (Spinner) v.findViewById(R.id.CityDepartCS_sp);
        CityDestinationCS_sp = (Spinner) v.findViewById(R.id.CityDestinationCS_sp);
        DateCS_txt = (TextView) v.findViewById(R.id.DateCS_txt);
        CarCS_swt = (SwitchCompat) v.findViewById(R.id.CarCS_swt);
        GoingComingCS_swt = (SwitchCompat) v.findViewById(R.id.GoingComingCS_swt);
        HighwayCS_swt = (SwitchCompat) v.findViewById(R.id.HighwayCS_swt);
        SearchCS_btn = (Button) v.findViewById(R.id.SearchCS_btn);
        lvCS = (ListView) v.findViewById(R.id.CarCS_lv);

        CarCS_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CarCS_swt.setText(R.string.car_complete);
                } else {
                    CarCS_swt.setText(R.string.car_pending);
                }
            }
        });

        GoingComingCS_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GoingComingCS_swt.setText(R.string.going_coming);
                } else {
                    GoingComingCS_swt.setText(R.string.going);
                }
            }
        });

        HighwayCS_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HighwayCS_swt.setText(R.string.highway);
                } else {
                    HighwayCS_swt.setText(R.string.highway_not);
                }
            }
        });

        final Calendar calendar = cal.getCurrentTime();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DateCS_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        DateCS_txt.setOnClickListener(new View.OnClickListener() {
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
        CityDepartCS_sp.setAdapter(cityAdapter);
        CityDestinationCS_sp.setAdapter(cityAdapter);
        CityDepartCS_sp.setSelection(cityAdapter.getPosition(pref.getString(conf.tag_city, "")));

        SearchCS_btn.setOnClickListener(new View.OnClickListener() {
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
        params.add(new BasicNameValuePair(conf.tag_country, pref.getString(conf.tag_country, "")));
        params.add(new BasicNameValuePair(conf.tag_depart, CityDepartCS_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_destination, CityDestinationCS_sp.getSelectedItem().toString()));
        params.add(new BasicNameValuePair(conf.tag_date, DateCS_txt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_status, CarCS_swt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_goingComing, GoingComingCS_swt.getText().toString()));
        params.add(new BasicNameValuePair(conf.tag_highway, HighwayCS_swt.getText().toString()));
        CarDBList = new ArrayList<>();
        JSONObject json = sr.getJson(conf.url_getCar, params);
        if(json != null){
            try {
                if(json.getBoolean(conf.res)) {
                    dataJsonArray = json.getJSONArray(conf.data);
                    for (int i = 0; i < dataJsonArray.length(); i++) {
                        JSONObject c = dataJsonArray.getJSONObject(i);
                        String id = c.getString(conf.tag_id);
                        String model = c.getString(conf.tag_model);
                        String depart = c.getString(conf.tag_depart);
                        String destination = c.getString(conf.tag_destination);
                        String date = c.getString(conf.tag_date);
                        CarDB cx = new CarDB(id, model, depart, destination, date);
                        CarDBList.add(cx);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), R.string.serverunvalid,Toast.LENGTH_LONG).show();
        }
        CarAdapterList adapter = new CarAdapterList(getActivity(), CarDBList, CarSearch.this);
        lvCS.setAdapter(adapter);
        int list_height = getListViewHeight(lvCS);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) lvCS.getLayoutParams();
        lp.height = list_height;
        lvCS.setLayoutParams(lp);
    }

    private int getListViewHeight(ListView list) {
        ListAdapter adapter = list.getAdapter();
        int listviewHeight = 0;
        list.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        listviewHeight = list.getMeasuredHeight() * adapter.getCount() + (adapter.getCount() * list.getDividerHeight());
        return listviewHeight;
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            DateCS_txt.setText(new StringBuilder().append(year).append("/").append(month + 1).append("/").append(day));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.container_body, new Car());
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
}
