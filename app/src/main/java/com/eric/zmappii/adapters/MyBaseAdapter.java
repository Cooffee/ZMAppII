package com.eric.zmappii.adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.eric.zmappii.R;
import com.eric.zmappii.bean.Abnormality;

import java.util.List;

/**
 * Created by Canon on 2015/4/10.
 */
public class MyBaseAdapter extends BaseAdapter {
    private List<Abnormality> abnormalities;
    Context context;

    public MyBaseAdapter(Context context, List<Abnormality> abnormalities) {
        this.abnormalities = abnormalities;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (abnormalities == null) ? 0 : abnormalities.size();
    }

    @Override
    public Object getItem(int position) {
        return abnormalities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView tvType;
        TextView tvTime;
        TextView tvDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Abnormality abnormality = (Abnormality) getItem(position);
        ViewHolder viewHolder = null;
        if(null == convertView) {
            Log.d("MyBaseAdapter", "新建convertView, position = " + position);
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_error_list, null);

            viewHolder = new ViewHolder();
            viewHolder.tvType = (TextView)convertView.findViewById(R.id.tv_abnormal_type);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tv_abnormal_time);
            viewHolder.tvDate = (TextView)convertView.findViewById(R.id.tv_abnormal_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
            Log.d("MyBaseAdapter", "旧的convertView, position = " + position);
        }

        viewHolder.tvType.setText(abnormality.getType());
        viewHolder.tvTime.setText(abnormality.getTime());
        viewHolder.tvDate.setText(abnormality.getDate());


        return convertView;
    }
}
