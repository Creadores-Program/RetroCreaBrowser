package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ExtensionsActivity extends Activity {

    private ListView listView;
    private ExtensionAdapter adapter;
    private List<ExtensionManager.Extension> extensionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        }
        
        setContentView(R.layout.layout_config);
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.top_bar);
            TextView actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            actionBarTitle.setText(R.string.exts);
        }

        this.listView = (ListView) findViewById(R.id.listViewExts);
        TextView emptyView = (TextView) findViewById(R.id.emptyViewExts);
        this.listView.setEmptyView(emptyView);

        extensionsList = ExtensionManager.getExtensions(this);
        adapter = new ExtensionAdapter(this, extensionsList);
        this.listView.setAdapter(adapter);

        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final ExtensionManager.Extension ext = extensionsList.get(position);
                ExtensionManager.removeExtension(ExtensionsActivity.this, position);
                extensionsList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(ExtensionsActivity.this, R.string.deleted_ext, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Button viewExts = (Button) findViewById(R.id.btnSearchExts);
        viewExts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExtensionsActivity.this, MainActivity.class);
                //intent.setData(Uri.parse(URL));
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private class ExtensionAdapter extends BaseAdapter {
        private final Context context;
        private final List<ExtensionManager.Extension> list;
        private final LayoutInflater inflater;

        public ExtensionAdapter(Context context, List<ExtensionManager.Extension> list) {
            this.context = context;
            this.list = list;
            this.inflater = LayoutInflater.from(context);
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

        private class ViewHolder {
            TextView txtName;
            TextView txtCode;
            CheckBox checkEnabled;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_ext, parent, false);
                holder = new ViewHolder();
                holder.txtName = (TextView) convertView.findViewById(R.id.textExtName);
                holder.txtCode = (TextView) convertView.findViewById(R.id.textExtCode);
                holder.checkEnabled = (CheckBox) convertView.findViewById(R.id.checkExtEnabled);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ExtensionManager.Extension ext = list.get(position);
            holder.txtName.setText(ext.name);
            holder.txtCode.setText(ext.scriptCode);

            holder.checkEnabled.setOnCheckedChangeListener(null);
            holder.checkEnabled.setChecked(ext.enabled);

            holder.checkEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ext.enabled = isChecked;
                    ExtensionManager.toggleExtension(context, position, isChecked);
                }
            });

            return convertView;
        }
    }
}