package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.CookieManager;
import android.webkit.WebIconDatabase;
import android.webkit.WebResourceRequest;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.CreadoresProgram.WebViewCREA.WebViewCreaClient;
import org.CreadoresProgram.RetroCreaBrowser.browserconfig.SetConfigOkClient;

public class MainActivity extends Activity {
    private WebView webView;
    private WebViewCreaClient creaClient;
    private RelativeLayout actionBar;
    private TextView actionBarTitle;
    private ImageView actionBarIcon;
    private Drawable originalActionBarTheme;
    private Drawable originalXmlBackground;
    private int originalStatusBarTheme;

    private static final String SCHEME_COLOR_PREFIX = "app-color://";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        this.webView = (WebView) findViewById(R.id.webview);
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            this.actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            this.actionBar = (RelativeLayout) actionBarTitle.getParent();
            originalXmlBackground = actionBar.getBackground();
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        }
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                this.originalActionBarTheme = createHoloLayerDrawable(Color.parseColor("#33B5E5"));
            } else {
                this.originalActionBarTheme = new ColorDrawable(Color.parseColor("#1A1A1A"));
            }
        }
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.originalStatusBarTheme = getWindow().getStatusBarColor();
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

        this.creaClient = new WebViewCreaClient(){
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith(SCHEME_COLOR_PREFIX)) {
                    applyDynamicColor(url.substring(SCHEME_COLOR_PREFIX.length()));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url != null && url.startsWith(SCHEME_COLOR_PREFIX)) {
                    applyDynamicColor(url.substring(SCHEME_COLOR_PREFIX.length()));
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String jsScript = "javascript:(function() {" +
                    "try {" +
                    "   var color = '';" +
                    "   var meta = document.querySelector('meta[name=\"theme-color\"]');" +
                    "   if (meta && meta.content) {" +
                    "       color = meta.content;" +
                    "   } else if (document.body) {" +
                    "       var style = document.defaultView ? document.defaultView.getComputedStyle(document.body, null) : null;" +
                    "       if (style && style.backgroundColor) {" +
                    "           color = style.backgroundColor;" +
                    "       } else if (document.body.style && document.body.style.backgroundColor) {" +
                    "           color = document.body.style.backgroundColor;" +
                    "       }" +
                    "   }" +
                    "   if (color && color !== 'rgba(0, 0, 0, 0)' && color !== 'transparent') {" +
                    "       window.location.href = '" + SCHEME_COLOR_PREFIX + "' + encodeURIComponent(color);" +
                    "   } else {" +
                    "       window.location.href = '" + SCHEME_COLOR_PREFIX + "default';" +
                    "   }" +
                    "} catch(e) {" +
                    "   window.location.href = '" + SCHEME_COLOR_PREFIX + "default';" +
                    "}" +
                    "})()";
                webView.loadUrl(jsScript);
            }
        };

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

        //webSettings.setUserAgentString(creaClient.getUserAgent(webView, WebViewCreaClient.UserAgentsIds.WEBVIEW_MOBILE));
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        creaClient.loadUrl(webView, "https://lite.duckduckgo.com/lite/");
    }

    private void applyDynamicColor(String color){
        try{
            color = java.net.URLDecoder.decode(color, "UTF-8");
            if ("default".equals(color) || color.trim().length() == 0) {
                resetDefaultBar();
                return;
            }
            int parsedColor;
            if (color.startsWith("rgb")) {
                String[] numbers = color.replace("rgb(", "").replace(")", "").split(",");
                int r = Integer.parseInt(numbers[0].trim());
                int g = Integer.parseInt(numbers[1].trim());
                int b = Integer.parseInt(numbers[2].trim());
                parsedColor = Color.rgb(r, g, b);
            } else {
                parsedColor = Color.parseColor(color);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setColorActionBar(parsedColor);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    getWindow().setStatusBarColor(parsedColor);
                }
            } else if (actionBar != null) {
                actionBar.setBackgroundColor(parsedColor);
                boolean isLight = isColorLight(parsedColor);
                int textColor = isLight ? Color.BLACK : Color.WHITE;
                if (actionBarTitle != null) {
                    actionBarTitle.setTextColor(textColor);
                }
            }
        } catch(Exception e){}
    }

    private boolean isColorLight(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return ((r * 299 + g * 587 + b * 114) / 1000.0) >= 128;
    }

    private void setColorActionBar(int color){
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB 
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                actionBar.setBackgroundDrawable(createHoloLayerDrawable(color));
            } else {
                actionBar.setBackgroundDrawable(new ColorDrawable(color));
            }
        }
    }

    private Drawable createHoloLayerDrawable(int accentColor) {
        ColorDrawable baseBackground = new ColorDrawable(Color.parseColor("#1A1A1A"));
        ColorDrawable accentLine = new ColorDrawable(accentColor);

        Drawable[] layers = new Drawable[]{ baseBackground, accentLine };
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int actionBarHeight = 48;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        layerDrawable.setLayerInset(1, 0, actionBarHeight - 4, 0, 0);
        return layerDrawable;
    }

    private void resetDefaultBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            resetDefaultActionBar();
        } else if (actionBar != null) {
            actionBar.setBackgroundDrawable(originalXmlBackground);
            if (actionBarTitle != null) {
                actionBarTitle.setTextColor(Color.WHITE);
            }
        }
    }

    private void resetDefaultActionBar(){
        ActionBar actionBar = getActionBar();
        if (actionBar != null && originalActionBarTheme != null) {
            actionBar.setBackgroundDrawable(originalActionBarTheme);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(originalStatusBarTheme);
        }
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
        } else if(actionBarIcon != null){
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
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(this.webView != null){
            webView.pauseTimers();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                webView.onPause();
            } else {
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
            } else {
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
        if (webView != null) {
            webView.post(new Runnable(){
                @Override
                public void run(){
                    if (webView != null) {
                        webView.destroy();
                        webView = null;
                    }
                }
            });
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            WebIconDatabase.getInstance().close();
        }
        super.onDestroy();
    }
}
