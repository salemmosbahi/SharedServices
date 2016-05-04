package it.mdev.sharedservices.database;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.mdev.sharedservices.R;
import it.mdev.sharedservices.activity.Car;
import it.mdev.sharedservices.activity.CarProfile;
import it.mdev.sharedservices.activity.CarSearch;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 28/04/16.
 */
public class CarAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<CarDB> data;
    Fragment fragment;

    public CarAdapterList(Context contxt, List<CarDB> data, Fragment fragment) {
        this.contxt = contxt;
        this.data = data;
        this.fragment = fragment;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return data.indexOf(getItem(position)); }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CarHolder holder = new CarHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.car_list, null);
            holder.ModelCAL_txt = (TextView) v.findViewById(R.id.ModelCAL_txt);
            holder.RouteCAL_txt = (TextView) v.findViewById(R.id.RouteCAL_txt);
            holder.DateCAL_txt = (TextView) v.findViewById(R.id.DateCAL_txt);
            holder.RowCAL_relative = (RelativeLayout) v.findViewById(R.id.RowCAL_rl);
            v.setTag(holder);
        } else {
            holder = (CarHolder) v.getTag();
        }

        holder.ModelCAL_txt.setText(data.get(position).getModel());
        holder.RouteCAL_txt.setText(data.get(position).getDepart() + " -> " + data.get(position).getDestination());
        holder.DateCAL_txt.setText(data.get(position).getDate());

        holder.RowCAL_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new CarProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getId());
                if (Car.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "Car");
                } else if (CarSearch.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "CarSearch");
                }
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.commit();
            }
        });
        return v;
    }

    class CarHolder {
        TextView ModelCAL_txt, RouteCAL_txt, DateCAL_txt;
        RelativeLayout RowCAL_relative;
    }
}
