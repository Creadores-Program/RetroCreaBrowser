package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Button;

public class ExtensionsActivity extends Activity {

    private ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        }
        setContentView(R.layout.layout_config);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.top_bar);
            TextView actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            actionBarTitle.setText(R.string.exts);
        }
        this.listView = (ListView) findViewById(R.id.listViewExts);
        TextView emptyView = (TextView) findViewById(R.id.emptyViewExts);
        this.listView.setEmptyView(emptyView);
        Button viewExts = (Button) findViewById(R.id.btnSearchExts);
        viewExts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExtensionsActivity.this, MainActivity.class);
                //intent.setData(Uri.parse("https://urlextspage"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}
