package com.eric.zmappii.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.zmappii.R;
import com.eric.zmappii.pojo.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 15/7/25.
 */
public class MemberListAdapter extends BaseAdapter {

    private Context context;
    private List<Member> members = new ArrayList<>();

    private static final String TAG = "ZMAPP";

    public MemberListAdapter(Context context, List<Member> members) {
        this.context = context;
        this.members = members;
    }

    @Override
    public int getCount() {
        return null == members ? 0 : members.size();
    }

    @Override
    public Object getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView imgAvatar;
        TextView tvWhoseData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Member member = members.get(position);
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_member_list, null);

            holder = new ViewHolder();
            holder.imgAvatar = (ImageView) convertView.findViewById(R.id.img_userAvatar);
            holder.tvWhoseData = (TextView) convertView.findViewById(R.id.tv_whoseData);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (0 != member.imgResId) {
            holder.imgAvatar.setImageResource(member.imgResId);
        } else {
            holder.imgAvatar.setImageURI(Uri.parse(member.imgUrl));
        }
        if (member.whoseData.contains("我")) {
            holder.tvWhoseData.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.tvWhoseData.setTextColor(context.getResources().getColor(R.color.colorListText));
        }
        holder.tvWhoseData.setText(member.whoseData);

        return convertView;
    }
}
