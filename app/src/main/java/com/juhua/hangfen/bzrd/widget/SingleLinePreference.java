package com.juhua.hangfen.bzrd.widget;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juhua.hangfen.bzrd.R;

/**
 * Created by congj on 2017/10/13.
 */

public class SingleLinePreference extends Preference {
    private int prefIconResId;
    private String leftText;
    private String rightText;
    private TextView leftTextview;
    public SingleLinePreference(Context context) {
        super(context);
    }

    public SingleLinePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleLinePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        ImageView prefIcon = (ImageView) view.findViewById(R.id.pref_icon);
        prefIcon.setImageDrawable(view.getResources().getDrawable(prefIconResId));

        leftTextview = (TextView) view.findViewById(R.id.pref_title);
        leftTextview.setText(leftText);

        TextView rightTextView = (TextView) view.findViewById(R.id.pref_summary);
        rightTextView.setText(rightText);

    }

    public int getPrefIconResId() {
        return prefIconResId;
    }

    public void setPrefIconResId(int prefIconResId) {
        this.prefIconResId = prefIconResId;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        if(this.leftTextview == null){
            this.leftText = leftText;
        }else{
            this.leftTextview.setText(leftText);
        }

    }

}
