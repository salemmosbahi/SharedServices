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
import it.mdev.sharedservices.activity.Paper;
import it.mdev.sharedservices.activity.PaperProfile;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 29/04/16.
 */
public class PaperAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<PaperDB> data;
    Fragment fragment;

    public PaperAdapterList(Context contxt, List<PaperDB> data, Fragment fragment) {
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
        PaperHolder holder = new PaperHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.paper_list, null);
            holder.NamePAL_txt = (TextView) v.findViewById(R.id.NamePAL_txt);
            holder.PlacePAL_txt = (TextView) v.findViewById(R.id.PlacePAL_txt);
            holder.RowPAL_relative = (RelativeLayout) v.findViewById(R.id.RowPAL_rl);
            v.setTag(holder);
        } else {
            holder = (PaperHolder) v.getTag();
        }

        holder.NamePAL_txt.setText(data.get(position).getName());
        holder.PlacePAL_txt.setText(data.get(position).getPlace());

        holder.RowPAL_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new PaperProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getId());
                if (Paper.class.isInstance(fragment)) {
                    args.putString(conf.tag_activity, "Paper");
                }
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.commit();
            }
        });
        return v;
    }

    class PaperHolder {
        TextView NamePAL_txt, PlacePAL_txt;
        RelativeLayout RowPAL_relative;
    }
}
