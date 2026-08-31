package org.CreadoresProgram.RetroCreaBrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;

import org.CreadoresProgram.WebViewCREA.WebViewCreaClient;

import java.util.ArrayList;
import java.util.List;

public class UserAgentManager {
    private static final String PREF_NAME = "RetroCreaUserAgentPrefs";
    private static final String KEY_SELECTED_INDEX = "selected_ua_index";

    public static class UserAgentItem {
        public final String displayName;
        public final boolean isRemote;
        public final WebViewCreaClient.LocalUserAgents localUa;
        public final WebViewCreaClient.RemoteUserAgentsIds remoteUa;

        public UserAgentItem(String displayName, WebViewCreaClient.LocalUserAgents localUa) {
            this.displayName = displayName;
            this.isRemote = false;
            this.localUa = localUa;
            this.remoteUa = null;
        }

        public UserAgentItem(String displayName, WebViewCreaClient.RemoteUserAgentsIds remoteUa) {
            this.displayName = displayName;
            this.isRemote = true;
            this.remoteUa = remoteUa;
            this.localUa = null;
        }

        public String resolveUserAgent(WebView webView, WebViewCreaClient creaClient) {
            if (isRemote && creaClient != null) {
                return creaClient.getUserAgent(webView, remoteUa);
            } else if (localUa != null) {
                return localUa.toString();
            }
            return webView.getSettings().getUserAgentString();
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public static List<UserAgentItem> getUserAgents() {
        List<UserAgentItem> list = new ArrayList<UserAgentItem>();

        for (WebViewCreaClient.LocalUserAgents l : WebViewCreaClient.LocalUserAgents.values()) {
            list.add(new UserAgentItem("[Local] " + l.name(), l));
        }

        for (WebViewCreaClient.RemoteUserAgentsIds r : WebViewCreaClient.RemoteUserAgentsIds.values()) {
            list.add(new UserAgentItem("[Remoto] " + r.name(), r));
        }

        return list;
    }

    public static int getSelectedUserAgentIndex(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SELECTED_INDEX, 0);
    }

    public static void setSelectedUserAgentIndex(Context context, int index) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_SELECTED_INDEX, index).apply();
    }

    public static void applySelectedUserAgent(Context context, WebView webView, WebViewCreaClient creaClient) {
        if (webView == null) return;
        List<UserAgentItem> list = getUserAgents();
        int index = getSelectedUserAgentIndex(context);
        if (index >= 0 && index < list.size()) {
            UserAgentItem item = list.get(index);
            String uaString = item.resolveUserAgent(webView, creaClient);
            webView.getSettings().setUserAgentString(uaString);
        }
    }

    public static void applyUserAgentAtIndex(Context context, WebView webView, WebViewCreaClient creaClient, int index) {
        List<UserAgentItem> list = getUserAgents();
        if (index >= 0 && index < list.size()) {
            setSelectedUserAgentIndex(context, index);
            UserAgentItem item = list.get(index);
            String uaString = item.resolveUserAgent(webView, creaClient);
            webView.getSettings().setUserAgentString(uaString);
        }
    }
}