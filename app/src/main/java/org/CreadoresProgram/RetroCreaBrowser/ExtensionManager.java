package org.CreadoresProgram.RetroCreaBrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import org.CreadoresProgram.RetroCreaBrowser.utils.AssetUtils;
import java.util.ArrayList;
import java.util.List;

public class ExtensionManager {
    private static final String PREF_NAME = "extensions_pref";
    private static final String KEY_EXTENSIONS = "extensions_list";

    public static class Extension {
        public String name;
        public String scriptCode;
        public boolean enabled;

        public Extension(String name, String scriptCode, boolean enabled) {
            this.name = name;
            this.scriptCode = scriptCode;
            this.enabled = enabled;
        }
    }

    public static List<Extension> getExtensions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String rawData = prefs.getString(KEY_EXTENSIONS, null);
        List<Extension> list = new ArrayList<Extension>();

        if (rawData == null || TextUtils.isEmpty(rawData)) {
            list.add(new Extension("Force Dark", AssetUtils.readAssetAsString(context.getAssets(), "forceDarkExt.js"), false));
            list.add(new Extension("Block Popups", AssetUtils.readAssetAsString(context.getAssets(), "blockPopups.js"), false));
            saveExtensions(context, list);
        } else {
            String[] items = rawData.split("###");
            for (String item : items) {
                String[] parts = item.split(":::");
                if (parts.length == 3) {
                    boolean isEnabled = Boolean.parseBoolean(parts[2]);
                    list.add(new Extension(parts[0], parts[1], isEnabled));
                }
            }
        }
        return list;
    }

    public static void saveExtensions(Context context, List<Extension> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Extension ext = list.get(i);
            sb.append(ext.name).append(":::").append(ext.scriptCode).append(":::").append(ext.enabled);
            if (i < list.size() - 1) {
                sb.append("###");
            }
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_EXTENSIONS, sb.toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static void addExtension(Context context, String name, String scriptCode) {
        List<Extension> list = getExtensions(context);
        list.add(new Extension(name, scriptCode, true));
        saveExtensions(context, list);
    }

    public static void removeExtension(Context context, int index) {
        List<Extension> list = getExtensions(context);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveExtensions(context, list);
        }
    }
    public static void toggleExtension(Context context, int index, boolean enabled) {
        List<Extension> list = getExtensions(context);
        if (index >= 0 && index < list.size()) {
            list.get(index).enabled = enabled;
            saveExtensions(context, list);
        }
    }
}