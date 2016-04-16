package it.mdev.sharedservices.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
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
import it.mdev.sharedservices.activity.Download;
import it.mdev.sharedservices.activity.DownloadProfile;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<DownloadDB> data;
    Fragment fragment;

    public DownloadAdapterList(Context contxt, List<DownloadDB> data, Fragment fragment) {
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
        ReclamationHolder holder = new ReclamationHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.download_list, null);
            holder.Name_txt = (TextView) v.findViewById(R.id.Name_txt);
            holder.Status_txt = (TextView) v.findViewById(R.id.Status_txt);
            holder.Size_txt = (TextView) v.findViewById(R.id.Size_txt);
            holder.Date_txt = (TextView) v.findViewById(R.id.Date_txt);
            holder.Row_relative = (RelativeLayout) v.findViewById(R.id.Row_rl);
            v.setTag(holder);
        } else {
            holder = (ReclamationHolder) v.getTag();
        }
        holder.Name_txt.setText(data.get(position).getName());
        if (data.get(position).isStatus()) {
            holder.Status_txt.setText("Download complete");
        } else {
            holder.Status_txt.setText("Download pending");
        }
        holder.Size_txt.setText(data.get(position).getSize() + " Go");
        holder.Date_txt.setText(data.get(position).getDate());

        holder.Row_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                Fragment fr = new DownloadProfile();
                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString(conf.tag_id, data.get(position).getId());
                fr.setArguments(args);
                ft.replace(R.id.container_body, fr);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return v;
    }

    class ReclamationHolder {
        TextView Name_txt, Status_txt, Size_txt, Date_txt;
        RelativeLayout Row_relative;
    }
}
