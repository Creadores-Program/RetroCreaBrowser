package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.CookieManager;
import android.webkit.WebIconDatabase;
import android.widget.TextView;
import android.widget.ImageView;

import org.CreadoresProgram.WebViewCREA.WebViewCreaClient;
import org.CreadoresProgram.RetroCreaBrowser.browserconfig.SetConfigOkClient;

public class MainActivity extends Activity{
    private WebView webView;
    private WebViewCreaClient creaClient;
    private TextView actionBarTitle;
    private ImageView actionBarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        this.webView = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            this.actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        }
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                updateTitle(title);
            }
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                updateIcon(icon);
            }
        });
        this.creaClient = new WebViewCreaClient();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            SetConfigOkClient.configOkClient(creaClient.getNetClient());
        }
        webView.setWebViewClient(creaClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
            webSettings.setDatabasePath(getApplicationContext().getDir("LocalStorageOld", Context.MODE_PRIVATE).getPath());
            webSettings.setAppCachePath(getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
            webSettings.setAppCacheEnabled(true);
            webView.setDrawingCacheEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setAllowContentAccess(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(false);
            webSettings.setAllowUniversalAccessFromFileURLs(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webSettings.setDisabledActionModeMenuItems(WebSettings.MENU_ITEM_NONE);
        }
        webSettings.setUserAgentString(creaClient.getUserAgent(webView, WebViewCreaClient.UserAgentsIds.WEBVIEW_MOBILE));
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        webView.setBackgroundColor(Color.BLACK);
        creaClient.loadUrl(webView, "https://lite.duckduckgo.com/lite/");
    }

    private void updateTitle(String title){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            updateActionBarTitle(title);
        } else if (actionBarTitle != null) {
            actionBarTitle.setText(title);
        }
    }

    private void updateIcon(Bitmap icon){
        if(icon == null){
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            updateActionBarIcon(icon);
        }else if(actionBarIcon != null){
            actionBarIcon.setImageBitmap(icon);
        }
    }

    private void updateActionBarTitle(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void updateActionBarIcon(Bitmap icon){
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setIcon(new BitmapDrawable(getResources(), icon));
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(this.webView != null){
            webView.pauseTimers();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                webView.onPause();
            }else{
              try {
                  Class.forName("android.webkit.WebView")
                      .getMethod("onPause")
                      .invoke(webView);
              } catch (Exception e) {
                  e.printStackTrace();
              }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(this.webView != null){
            webView.resumeTimers();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
              webView.onResume();
            }else{
              try {
                  Class.forName("android.webkit.WebView")
                      .getMethod("onResume")
                      .invoke(webView);
              } catch (Exception e) {
                  e.printStackTrace();
              }
            }
        }
    }
    @Override
    protected void onDestroy() {
        webView.post(new Runnable(){
            @Override
            public void run(){
                webView.destroy();
                webView = null;
            }
        });
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            WebIconDatabase.getInstance().close();
        }
        super.onDestroy();
    }
}
