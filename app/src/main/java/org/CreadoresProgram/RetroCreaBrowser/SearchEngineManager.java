package org.CreadoresProgram.RetroCreaBrowser;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class SearchEngineManager {
    private static final String PREF_NAME = "search_engines_pref";
    private static final String KEY_ENGINES = "engines_list";
    private static final String KEY_SELECTED = "selected_engine_index";

    public static class Engine {
        public String name;
        public String searchUrl;

        public Engine(String name, String searchUrl) {
            this.name = name;
            this.searchUrl = searchUrl;
        }
    }

    public static List<Engine> getEngines(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String rawData = prefs.getString(KEY_ENGINES, null);
        List<Engine> list = new ArrayList<Engine>();

        if (rawData == null || rawData.trim().length() == 0) {
            list.add(new Engine("DuckDuckGo Lite", "https://lite.duckduckgo.com/lite/?q=%s"));
            list.add(new Engine("Google", "https://www.google.com/search?q=%s"));
            list.add(new Engine("Bing", "https://www.bing.com/search?q=%s"));
            saveEngines(context, list);
        } else {
            String[] items = rawData.split(";");
            for (String item : items) {
                String[] parts = item.split("\\|");
                if (parts.length == 2) {
                    list.add(new Engine(parts[0], parts[1]));
                }
            }
        }
        return list;
    }

    public static void saveEngines(Context context, List<Engine> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Engine e = list.get(i);
            sb.append(e.name).append("|").append(e.searchUrl);
            if (i < list.size() - 1) sb.append(";");
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ENGINES, sb.toString());
        editor.commit();
    }

    public static int getSelectedEngineIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SELECTED, 0);
    }

    public static void setSelectedEngineIndex(Context context, int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_SELECTED, index);
        editor.commit();
    }
}
