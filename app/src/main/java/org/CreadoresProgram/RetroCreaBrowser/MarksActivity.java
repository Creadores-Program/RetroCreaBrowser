package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MarksActivity extends Activity {
    private ImageView actionBarIcon;
    private ListView listView;
    private MarkAdapter adapter;
    private List<MarksManager.Mark> bookmarkList;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_marks);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            TextView actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            actionBarTitle.setText(R.string.marks);
        }
        this.listView = (ListView) findViewById(R.id.listViewMarks);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MarksManager.Mark bookmark = bookmarkList.get(position);
                Intent intent = new Intent(MarksActivity.this, MainActivity.class);
                intent.setData(Uri.parse(bookmark.url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MarksManager.removeMark(MarksActivity.this, position);
                Toast.makeText(MarksActivity.this, R.string.del_mark, Toast.LENGTH_SHORT).show();
                loadMarks();
                return true;
            }
        });
    }
    private void loadMarks() {
        List<MarksManager.Mark> allBookmarks = MarksManager.getMarks(this);
        bookmarkList = new ArrayList<MarksManager.Mark>();

        for (int i = 0; i < allBookmarks.size(); i++) {
            MarksManager.Mark b = allBookmarks.get(i);
            bookmarkList.add(b);
        }

        adapter = new MarkAdapter(this, bookmarkList);
        listView.setAdapter(adapter);
    }
    private static class MarkAdapter extends BaseAdapter {
        private Context context;
        private List<MarksManager.Mark> list;

        public MarkAdapter(Context context, List<MarksManager.Mark> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            MarksManager.Mark item = list.get(position);
            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);

            text1.setText(item.title);
            text2.setText(item.url);

            return convertView;
        }
    }
}
