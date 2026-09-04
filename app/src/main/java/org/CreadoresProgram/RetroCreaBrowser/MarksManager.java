package org.CreadoresProgram.RetroCreaBrowser;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class MarksManager {
    private static final String PREF_NAME = "bookmarks_pref";
    private static final String KEY_BOOKMARKS = "bookmarks_list";

    public static class Mark {
        public String title;
        public String url;

        public Mark(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }

    public static List<Mark> getMarks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String rawData = prefs.getString(KEY_BOOKMARKS, null);
        List<Mark> list = new ArrayList<Mark>();

        if (rawData != null && rawData.trim().length() > 0) {
            String[] items = rawData.split(";");
            for (String item : items) {
                String[] parts = item.split("\\|");
                if (parts.length == 2) {
                    list.add(new Mark(parts[0], parts[1]));
                }
            }
        }
        return list;
    }

    public static void saveMarks(Context context, List<Mark> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Mark b = list.get(i);
            sb.append(b.title).append("|").append(b.url);
            if (i < list.size() - 1) sb.append(";");
        }
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_BOOKMARKS, sb.toString());
        editor.commit();
    }

    public static void addMark(Context context, Mark bookmark) {
        List<Mark> list = getMarks(context);
        list.add(bookmark);
        saveMarks(context, list);
    }

    public static void removeMark(Context context, int index) {
        List<Mark> list = getMarks(context);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveMarks(context, list);
        }
    }
}
