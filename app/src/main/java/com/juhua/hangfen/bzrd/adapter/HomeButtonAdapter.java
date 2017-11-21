package com.juhua.hangfen.bzrd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;
import com.juhua.hangfen.bzrd.model.HomeButton;

import java.util.List;

/**
 * Created by congj on 2017/9/13.
 */

public class HomeButtonAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<HomeButton> homeButtons;
    private int itemHeight;
    public HomeButtonAdapter(Context context, List<HomeButton> homeButtons, int height){
        this.mContext = context;
        this.homeButtons = homeButtons;
        this.itemHeight = height;
    }

    @Override
    public View getView(final int positon, View convertView, ViewGroup parent){
        layoutInflater = LayoutInflater.from(mContext);
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.gridview_homebutton_layout, null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.home_grid_image);
        TextView textView = (TextView) convertView.findViewById(R.id.home_grid_text);
        imageView.setImageBitmap(homeButtons.get(positon).getIconImage());
        textView.setText(homeButtons.get(positon).getName());

        convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, this.itemHeight));
        return convertView;
    }


    @Override
    public int getCount(){
        return homeButtons.size();
    }

    @Override
    public Object getItem(int position){
        return this.homeButtons.get(position);
    }

    @Override
    public  long getItemId(int position){
        return position;
    }
}
