package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

public class ConfigActivity extends Activity {
    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;
    public static final String KEY_THEME = "theme";
    public static final String KEY_HOME = "homepage";

    private List<SearchEngineManager.Engine> enginesList;
    private EngineAdapter adapter;
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
            actionBarTitle.setText(R.string.config);
        }
        final ConfigManager configM = new ConfigManager(this);
        //f-droid repo, github link

        //dark mode:
        int selectedTheme = configM.getInt(KEY_THEME, THEME_SYSTEM);
        RadioGroup rgTheme = (RadioGroup) findViewById(R.id.rgTheme);
        if (selectedTheme == THEME_LIGHT) {
            rgTheme.check(R.id.rbLight);
        } else if (selectedTheme == THEME_DARK) {
            rgTheme.check(R.id.rbDark);
        } else {
            rgTheme.check(R.id.rbSystem);
        }
        rgTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId){
                int newTheme = THEME_SYSTEM;

                if (checkedId == R.id.rbLight) {
                    newTheme = THEME_LIGHT;
                } else if (checkedId == R.id.rbDark) {
                    newTheme = THEME_DARK;
                }

                if (newTheme != configM.getInt(KEY_THEME, THEME_SYSTEM)) {
                    configM.setInt(KEY_THEME, newTheme);
                }
            }
        });
        //search engines
        listView = (ListView) findViewById(R.id.listViewEngines);
        Button btnAdd = (Button) findViewById(R.id.btnAddEngine);
        enginesList = SearchEngineManager.getEngines(this);
        adapter = new EngineAdapter(this, enginesList);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if (enginesList.size() <= 1) {
                    Toast.makeText(ConfigActivity.this, 
                        R.string.limit_delete_search_engine, 
                        Toast.LENGTH_SHORT).show();
                    return true;
                }

                enginesList.remove(position);
                SearchEngineManager.saveEngines(ConfigActivity.this, enginesList);
                adapter.notifyDataSetChanged();
                Toast.makeText(ConfigActivity.this, R.string.search_engine_deleted, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEngineDialog();
            }
        });

        //home page
        EditText etHomeUrl = (EditText) findViewById(R.id.etHomeUrl);
        etHomeUrl.setText(configM.getString(KEY_HOME, getString(R.string.home_default)));
        Button btnSave = (Button) findViewById(R.id.btnSaveHome);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUrl = etHomeUrl.getText().toString().trim();

                if (TextUtils.isEmpty(newUrl)) {
                    Toast.makeText(ConfigActivity.this, R.string.no_void_home_page, Toast.LENGTH_SHORT).show();
                    return;
                }

                configM.setString(KEY_HOME, newUrl);

                Toast.makeText(ConfigActivity.this, R.string.updated_home_page, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEngineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.search_engine_add);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int paddingPx = dpToPx(16);
        container.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(R.string.search_engine_need_prefix);
        tvInfo.setTextSize(13);
        tvInfo.setPadding(0, 0, 0, dpToPx(10));
        container.addView(tvInfo);

        final EditText etName = new EditText(this);
        etName.setHint(R.string.search_engine_name_ej);
        container.addView(etName);

        final EditText etUrl = new EditText(this);
        etUrl.setHint(R.string.search_engine_url_ej);
        container.addView(etUrl);

        builder.setView(container);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString().trim();
                String url = etUrl.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) {
                    Toast.makeText(ConfigActivity.this, R.string.complet_create_search_engine, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!url.contains("%s")) {
                    Toast.makeText(ConfigActivity.this, R.string.error_no_prefix_search_engine, Toast.LENGTH_LONG).show();
                    return;
                }

                enginesList.add(new SearchEngineManager.Engine(name, url));
                SearchEngineManager.saveEngines(ConfigActivity.this, enginesList);
                adapter.notifyDataSetChanged();
                Toast.makeText(ConfigActivity.this, R.string.search_engine_added, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private class EngineAdapter extends BaseAdapter {
        private Context context;
        private List<SearchEngineManager.Engine> list;

        public EngineAdapter(Context context, List<SearchEngineManager.Engine> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() { return list.size(); }

        @Override
        public Object getItem(int position) { return list.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LinearLayout itemLayout = new LinearLayout(context);
                itemLayout.setOrientation(LinearLayout.VERTICAL);
                int p = dpToPx(10);
                itemLayout.setPadding(p, p, p, p);

                TextView tvName = new TextView(context);
                tvName.setId(1);
                tvName.setTextSize(16);

                TextView tvUrl = new TextView(context);
                tvUrl.setId(2);
                tvUrl.setTextSize(12);

                itemLayout.addView(tvName);
                itemLayout.addView(tvUrl);
                convertView = itemLayout;
            }

            SearchEngineManager.Engine engine = list.get(position);
            TextView tvName = (TextView) convertView.findViewById(1);
            TextView tvUrl = (TextView) convertView.findViewById(2);

            tvName.setText(engine.name);
            tvUrl.setText(engine.searchUrl);

            return convertView;
        }
    }
}
