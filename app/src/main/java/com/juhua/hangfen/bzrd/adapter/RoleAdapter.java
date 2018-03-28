package com.juhua.hangfen.bzrd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by congj on 2018/2/5.
 */

public class RoleAdapter extends BaseAdapter{
    private List<HashMap<String, Object>>  mList;
    private Context mContext;
    private String mRoleId;
    public RoleAdapter(Context context, List<HashMap<String, Object>> list, String roleId) {
        mContext = context;
        mList = list;
        mRoleId = roleId;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.dialog_list_role, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(mList.get(position).get("id").equals(mRoleId)){
            holder.image.setVisibility(View.VISIBLE);
        }else {
            holder.image.setVisibility(View.INVISIBLE);
        }
        holder.text.setText(mList.get(position).get("name").toString());
        return view;
    }
    private  static class ViewHolder{
        public ImageView image;
        public TextView text;

        private ViewHolder(View itemView) {
            image = (ImageView) itemView.findViewById(R.id.img_dialog_roleId);
            text = (TextView) itemView.findViewById(R.id.tv_dialog_roleName);
        }
    }

}

