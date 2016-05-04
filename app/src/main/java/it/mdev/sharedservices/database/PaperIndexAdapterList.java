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
import it.mdev.sharedservices.activity.Paper;
import it.mdev.sharedservices.activity.PaperProfile;
import it.mdev.sharedservices.util.Controllers;

/**
 * Created by salem on 29/04/16.
 */
public class PaperIndexAdapterList extends BaseAdapter {
    Controllers conf = new Controllers();
    LayoutInflater inflater;
    Context contxt;
    List<PaperIndexDB> data;
    Fragment fragment;

    public PaperIndexAdapterList(Context contxt, List<PaperIndexDB> data, Fragment fragment) {
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
        PaperIndexHolder holder = new PaperIndexHolder();
        if (v == null) {
            inflater = (LayoutInflater) contxt.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.paper_index_list, null);
            holder.NamePIAL_txt = (TextView) v.findViewById(R.id.NamePIAL_txt);
            v.setTag(holder);
        } else {
            holder = (PaperIndexHolder) v.getTag();
        }

        holder.NamePIAL_txt.setText(data.get(position).getName());
        return v;
    }

    class PaperIndexHolder {
        TextView NamePIAL_txt;
    }
}
