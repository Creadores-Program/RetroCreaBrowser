package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import org.CreadoresProgram.RetroCreaBrowser.RetroCreaBrowserApp;
import java.util.ArrayList;
import java.util.List;

public class TabsActivity extends Activity {
    private ImageView actionBarIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tabs);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            TextView actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            actionBarTitle.setText(R.string.tabs);
        }
        LinearLayout containerList = (LinearLayout) findViewById(R.id.containerList);
        containerList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        List<MainActivity> tabs = ((RetroCreaBrowserApp) getApplication()).getTabs();
        for(MainActivity tab : tabs){
            final MainActivity tabF = tab;
            final View tabV = inflater.inflate(R.layout.tab_bar, containerList, false);
            TextView tvTitle = (TextView) tabV.findViewById(R.id.tvTitle);
            final TextView btnClose = (TextView) tabV.findViewById(R.id.btnClose);
            tvTitle.setText(tabF.getTitleBar());
            tabV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tabF == null || tabF.isFinishing() || tabF.isDestroyed()){
                        ViewGroup parent = (ViewGroup) tabV.getParent();
                        if (parent != null) {
                            parent.removeView(tabV);
                        }
                        return;
                    }
                    Intent intent = new Intent(tabF, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    tabF.startActivity(intent);
                    finish();
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tabF.finish();
                    ViewGroup parent = (ViewGroup) tabV.getParent();
                    if (parent != null) {
                        parent.removeView(tabV);
                    }
                }
            });
            containerList.addView(tabV);
        }
    }
}
