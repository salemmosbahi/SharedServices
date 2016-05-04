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
import it.mdev.sharedservices.activity.Event;
import it.mdev.sharedservices.activity.EventProfile;
import it.mdev.sharedservices.activity.EventSearch;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 01/05/16.
 */
public class EventAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<EventDB> data;
    Fragment fragment;

    public EventAdapterList(Context contxt, List<EventDB> data, Fragment fragment) {
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
        EventHolder holder = new EventHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.event_list, null);
            holder.NameEAL_txt = (TextView) v.findViewById(R.id.NameEAL_txt);
            holder.CityEAL_txt = (TextView) v.findViewById(R.id.CityEAL_txt);
            holder.DateEAL_txt = (TextView) v.findViewById(R.id.DateEAL_txt);
            holder.RowEAL_relative = (RelativeLayout) v.findViewById(R.id.RowEAL_rl);
            v.setTag(holder);
        } else {
            holder = (EventHolder) v.getTag();
        }

        holder.NameEAL_txt.setText(data.get(position).getName());
        holder.CityEAL_txt.setText(data.get(position).getCity());
        holder.DateEAL_txt.setText(data.get(position).getDate());

        holder.RowEAL_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new EventProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getId());
                if (Event.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "Event");
                } else if (EventSearch.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "EventSearch");
                }
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.commit();
            }
        });
        return v;
    }

    class EventHolder {
        TextView NameEAL_txt, CityEAL_txt, DateEAL_txt;
        RelativeLayout RowEAL_relative;
    }
}
