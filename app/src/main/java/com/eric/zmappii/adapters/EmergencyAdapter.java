package com.eric.zmappii.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.eric.zmappii.R;
import com.eric.zmappii.bean.Emergency;

import java.util.ArrayList;

/**
 * Created by Canon on 2015/4/26.
 */
public class EmergencyAdapter extends ArrayAdapter<Emergency> {

    ArrayList<Emergency> emergencyList;
    LayoutInflater vi;
    int Resource;
    ViewHolder holder;


    public EmergencyAdapter(Context context, int resource, ArrayList<Emergency> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        emergencyList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.tvEmerType = (TextView) v.findViewById(R.id.tv_abnormal_type);
            holder.tvEmerDate = (TextView) v.findViewById(R.id.tv_abnormal_date);
            holder.tvEmerTime = (TextView) v.findViewById(R.id.tv_abnormal_time);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tvEmerType.setText(emergencyList.get(position).getEmerType());
        holder.tvEmerTime.setText(emergencyList.get(position).getEmerTime());
        holder.tvEmerDate.setText(emergencyList.get(position).getEmerDate());
        return  v;
    }

    static class ViewHolder {
        public TextView tvEmerType;
        public TextView tvEmerTime;
        public TextView tvEmerDate;
    }
}
