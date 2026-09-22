package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ConfigActivity extends Activity {
    public static final int THEME_SYSTEM = 0;
    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;
    public static final String KEY_THEME = "theme";
    public static final String KEY_HOME = "homepage";
    private static final String REPO_FDR = "https://creadores-program.github.io/CreaProFDR/repo";
    private static final String REPO_GH = "https://github.com/Creadores-Program/RetroCreaBrowser";

    private List<SearchEngineManager.Engine> enginesList;
    private LinearLayout containerEngines;

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
            if (actionBarTitle != null) {
                actionBarTitle.setText(R.string.config);
            }
        }
        final ConfigManager configM = new ConfigManager(this);

        // dark mode
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

        // search engines
        containerEngines = (LinearLayout) findViewById(R.id.containerEngines);
        Button btnAdd = (Button) findViewById(R.id.btnAddEngine);
        enginesList = SearchEngineManager.getEngines(this);

        renderEngines();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEngineDialog();
            }
        });

        // home page
        final EditText etHomeUrl = (EditText) findViewById(R.id.etHomeUrl);
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

        // f-droid repo
        Button btnFDR = (Button) findViewById(R.id.btnFDR);
        btnFDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
                intent.setData(Uri.parse(REPO_FDR));
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        // github
        Button btnGh = (Button) findViewById(R.id.btnGh);
        btnGh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
                intent.setData(Uri.parse(REPO_GH));
                intent.setAction(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void renderEngines() {
        if (containerEngines == null) return;
        containerEngines.removeAllViews();

        int p = dpToPx(10);
        for (int i = 0; i < enginesList.size(); i++) {
            final int position = i;
            final SearchEngineManager.Engine engine = enginesList.get(i);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(p, p, p, p);
            itemLayout.setClickable(true);

            TextView tvName = new TextView(this);
            tvName.setTextSize(16);
            tvName.setText(engine.name);

            TextView tvUrl = new TextView(this);
            tvUrl.setTextSize(12);
            tvUrl.setText(engine.searchUrl);

            itemLayout.addView(tvName);
            itemLayout.addView(tvUrl);

            itemLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (enginesList.size() <= 1) {
                        Toast.makeText(ConfigActivity.this, 
                            R.string.limit_delete_search_engine, 
                            Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    enginesList.remove(position);
                    SearchEngineManager.saveEngines(ConfigActivity.this, enginesList);
                    renderEngines();
                    Toast.makeText(ConfigActivity.this, R.string.search_engine_deleted, Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            containerEngines.addView(itemLayout);
        }
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
                renderEngines();
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
}
