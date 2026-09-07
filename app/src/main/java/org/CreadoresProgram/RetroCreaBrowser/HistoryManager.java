package org.CreadoresProgram.RetroCreaBrowser;

import android.os.Build;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String PREF_NAME = "history_pref";
    private static final String KEY_HISTORY = "history_list";

    public static class HistoryItem {
        public String title;
        public String url;
        public long timestamp;

        public HistoryItem(String title, String url, long timestamp) {
            this.title = title;
            this.url = url;
            this.timestamp = timestamp;
        }
    }

    public static List<HistoryItem> getHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String rawData = prefs.getString(KEY_HISTORY, null);
        List<HistoryItem> list = new ArrayList<HistoryItem>();

        if (rawData != null && !TextUtils.isEmpty(rawData)) {
            String[] items = rawData.split(";");
            for (String item : items) {
                String[] parts = item.split("\\|");
                if (parts.length == 3) {
                    try {
                        long time = Long.parseLong(parts[2]);
                        list.add(new HistoryItem(parts[0], parts[1], time));
                    } catch (NumberFormatException e) {}
                }
            }
        }
        return list;
    }

    public static void saveHistory(Context context, List<HistoryItem> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            HistoryItem item = list.get(i);
            String safeTitle = item.title.replace("|", " ").replace(";", " ");
            String safeUrl = item.url.replace("|", " ").replace(";", " ");
            
            sb.append(safeTitle).append("|")
              .append(safeUrl).append("|")
              .append(item.timestamp);

            if (i < list.size() - 1) {
                sb.append(";");
            }
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_HISTORY, sb.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static void addHistory(Context context, String title, String url) {
        List<HistoryItem> list = getHistory(context);
        list.add(0, new HistoryItem(title, url, System.currentTimeMillis()));
        saveHistory(context, list);
    }

    public static void removeHistoryItem(Context context, int index) {
        List<HistoryItem> list = getHistory(context);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveHistory(context, list);
        }
    }

    public static void clearAllHistory(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_HISTORY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
