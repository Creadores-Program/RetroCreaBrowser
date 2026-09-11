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
import android.view.Window;
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
    private List<MarksManager.Mark> bookmarkList = new ArrayList<MarksManager.Mark>();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        }
        setContentView(R.layout.layout_marks);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.top_bar);
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            TextView actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            actionBarTitle.setText(R.string.marks);
        }
        this.listView = (ListView) findViewById(R.id.listViewMarks);
        TextView emptyView = (TextView) findViewById(R.id.emptyView);
        this.listView.setEmptyView(emptyView);
        adapter = new MarkAdapter(this, bookmarkList);
        listView.setAdapter(adapter);
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
        loadMarks();
    }
    private void loadMarks() {
        List<MarksManager.Mark> allBookmarks = MarksManager.getMarks(this);
        bookmarkList.clear();
        bookmarkList.addAll(allBookmarks);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    private static class MarkAdapter extends BaseAdapter {
        private Context context;
        private List<MarksManager.Mark> list;
        private LayoutInflater inflater;

        static class ViewHolder {
            TextView text1;
            TextView text2;
        }

        public MarkAdapter(Context context, List<MarksManager.Mark> list) {
            this.context = context;
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
                holder = new ViewHolder();
                holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MarksManager.Mark item = list.get(position);
            holder.text1.setText(item.title);
            holder.text2.setText(item.url);

            return convertView;
        }
    }
}
