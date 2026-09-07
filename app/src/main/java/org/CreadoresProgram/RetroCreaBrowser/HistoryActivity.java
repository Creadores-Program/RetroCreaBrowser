package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends Activity {

    private ImageView actionBarIcon;
    private ListView listView;
    private Button btnClearHistory;
    private HistoryAdapter adapter;
    private List<HistoryManager.HistoryItem> historyList = new ArrayList<HistoryManager.HistoryItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_history);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            TextView actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            if (actionBarTitle != null) {
                actionBarTitle.setText(R.string.history);
            }
        }

        this.listView = (ListView) findViewById(R.id.listViewHistory);
        this.btnClearHistory = (Button) findViewById(R.id.btnClearHistory);

        TextView emptyView = (TextView) findViewById(R.id.emptyViewHistory);
        this.listView.setEmptyView(emptyView);

        adapter = new HistoryAdapter(this, historyList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryManager.HistoryItem item = historyList.get(position);
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                intent.setData(Uri.parse(item.url));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryManager.removeHistoryItem(HistoryActivity.this, position);
                Toast.makeText(HistoryActivity.this, R.string.del_history_item, Toast.LENGTH_SHORT).show();
                loadHistory();
                return true;
            }
        });

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!historyList.isEmpty()) {
                    HistoryManager.clearAllHistory(HistoryActivity.this);
                    Toast.makeText(HistoryActivity.this, R.string.history_cleared, Toast.LENGTH_SHORT).show();
                    loadHistory();
                }
            }
        });

        loadHistory();
    }

    private void loadHistory() {
        List<HistoryManager.HistoryItem> allHistory = HistoryManager.getHistory(this);
        historyList.clear();
        if (allHistory != null) {
            historyList.addAll(allHistory);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private static class HistoryAdapter extends BaseAdapter {
        private Context context;
        private List<HistoryManager.HistoryItem> list;
        private LayoutInflater inflater;

        static class ViewHolder {
            TextView textTitle;
            TextView textUrl;
            TextView textDate;
        }

        public HistoryAdapter(Context context, List<HistoryManager.HistoryItem> list) {
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
                holder.textTitle = (TextView) convertView.findViewById(android.R.id.text1);
                holder.textUrl = (TextView) convertView.findViewById(android.R.id.text2);
                
                holder.textDate = holder.textUrl; 
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HistoryManager.HistoryItem item = list.get(position);
            holder.textTitle.setText(item.title);

            CharSequence formattedDate = DateFormat.format("dd/MM/yyyy HH:mm", new Date(item.timestamp));
            holder.textUrl.setText(item.url + "\n" + formattedDate);

            return convertView;
        }
    }
}
