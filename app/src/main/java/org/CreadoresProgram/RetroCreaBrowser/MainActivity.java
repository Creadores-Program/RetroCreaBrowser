package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ComponentCallbacks2;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.webkit.ClientCertRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.webkit.CookieManager;
import android.webkit.WebIconDatabase;
import android.webkit.WebResourceRequest;
import android.webkit.SslErrorHandler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.res.ColorStateList;

import org.CreadoresProgram.WebViewCREA.WebViewCreaClient;
import org.CreadoresProgram.RetroCreaBrowser.browserconfig.SetConfigOkClient;
import org.CreadoresProgram.RetroCreaBrowser.utils.*;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class MainActivity extends Activity {
    private WebView webView;
    private ProgressBar progressBar;
    private WebViewCreaClient creaClient;
    private RelativeLayout actionBar;
    private TextView actionBarTitle;
    private ImageView actionBarIcon;
    private Drawable originalActionBarTheme;
    private Drawable originalXmlBackground;
    private int originalStatusBarTheme;

    private static final String SCHEME_COLOR_PREFIX = "app-color://";
    private static final int MENU_TABS = 1001;
    private String colorExt;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        this.webView = (WebView) findViewById(R.id.webview);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.colorExt = AssetUtils.readAssetAsString(getAssets(), "colorExt.js");
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
            this.actionBarTitle = (TextView) findViewById(R.id.top_bar_title);
            this.actionBar = (RelativeLayout) findViewById(R.id.top_bar_container);
            originalXmlBackground = actionBar.getBackground();
            this.actionBarIcon = (ImageView) findViewById(R.id.top_bar_icon);
            actionBarIcon.setImageResource(R.drawable.ic_launcher);
            WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());

            if (this.actionBar != null) {
                this.actionBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSearchDialog();
                    }
                });
            }
        }
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            setActionBarHomeBtn();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                this.originalActionBarTheme = createHoloLayerDrawable(Color.parseColor("#33B5E5"));
            } else {
                this.originalActionBarTheme = new ColorDrawable(Color.parseColor("#1A1A1A"));
            }
        }
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.originalStatusBarTheme = getWindow().getStatusBarColor();
        }

        this.creaClient = new WebViewCreaClient(){
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith(SCHEME_COLOR_PREFIX)) {
                    applyDynamicColor(url.substring(SCHEME_COLOR_PREFIX.length()));
                    return true;
                }
                if (openInExternalAppIfPossible(url)) {
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
                if (openInExternalAppIfPossible(url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                progressBar.setVisibility(View.VISIBLE);
                progressBar.bringToFront();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                WebViewUtils.evaluateJS(webView, colorExt);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(SslUtils.isCertInGlobalJavaTrustStore(SslUtils.getX509Certificate(error.getCertificate()))){
                    handler.proceed();
                    return;
                }
                //request a user
                handler.cancel();
            }
            @Override
            public void onReceivedClientCertRequest(WebView view, final ClientCertRequest request) {
                SslUtils.ClientCredentials credentials = SslUtils.getClientCredentialsFromProvider(request.getHost(), request.getPort());
                if (credentials != null) {
                    request.proceed(credentials.privateKey, credentials.certificateChain);
                    return;
                }
                KeyChain.choosePrivateKeyAlias(
                    MainActivity.this,
                    new KeyChainAliasCallback() {
                        @Override
                        public void alias(String alias) {
                            if (alias == null) {
                                request.cancel();
                                return;
                            }
                            try {
                                PrivateKey privateKey = KeyChain.getPrivateKey(MainActivity.this, alias);
                                X509Certificate[] chain = KeyChain.getCertificateChain(MainActivity.this, alias);
                                request.proceed(privateKey, chain);
                            } catch (Exception e) {
                                request.cancel();
                            }
                        }
                    },
                    request.getKeyTypes(),
                    request.getPrincipals(),
                    request.getHost(),
                    request.getPort(),
                    null
                );
            }
        };

        creaClient.setWebChromeClient(webView, new WebChromeClient(){
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            SetConfigOkClient.configOkClient(creaClient.getNetClient(), this);
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
            webSettings.setDisplayZoomControls(false);
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

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        webView.setInitialScale(0);
        UserAgentManager.applySelectedUserAgent(this, webView, creaClient);
        Intent intent = getIntent();
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null) {
                String url = data.toString();
                creaClient.loadUrl(webView, url);
                return;
            }
        }
        creaClient.loadUrl(webView, "https://lite.duckduckgo.com/lite/");
    }

    private boolean openInExternalAppIfPossible(String url) {
        if (url == null) return false;

        if (url.startsWith(SCHEME_COLOR_PREFIX) || url.startsWith("javascript:")) {
            return false;
        }

        Intent intent;
        try {
            if (url.startsWith("intent://")) {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
            }
        } catch (Exception e) {
            return false;
        }
        PackageManager pm = getPackageManager();

        Intent genericWebIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        genericWebIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        List<ResolveInfo> genericBrowsers = pm.queryIntentActivities(genericWebIntent, PackageManager.MATCH_DEFAULT_ONLY);

        Set<String> browserPackages = new HashSet<String>();
        Intent httpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")).addCategory(Intent.CATEGORY_BROWSABLE);
        Intent httpsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).addCategory(Intent.CATEGORY_BROWSABLE);
        List<ResolveInfo> httpBrowsers = pm.queryIntentActivities(httpIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (httpBrowsers != null) {
            for (ResolveInfo b : httpBrowsers) {
                if (b.activityInfo != null) browserPackages.add(b.activityInfo.packageName);
            }
        }
        List<ResolveInfo> httpsBrowsers = pm.queryIntentActivities(httpsIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (httpsBrowsers != null) {
            for (ResolveInfo b : httpsBrowsers) {
                if (b.activityInfo != null) browserPackages.add(b.activityInfo.packageName);
            }
        }

        List<ResolveInfo> candidates = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<String> validNativePackages = new ArrayList<String>();

        if (candidates != null) {
            for (ResolveInfo info : candidates) {
                if (info.activityInfo == null) continue;
                    String packageName = info.activityInfo.packageName;

                if (!packageName.equals(getPackageName()) && !browserPackages.contains(packageName) && !packageName.equals("android") && !packageName.startsWith("com.android.internal")) {
                    validNativePackages.add(packageName);
                }
            }
        }

        if (validNativePackages.size() >= 1) {
            if (validNativePackages.size() == 1) {
                intent.setPackage(validNativePackages.get(0));
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null) {
                creaClient.loadUrl(webView, data.toString());
            }
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 10);

        TextView tvEngine = new TextView(this);
        tvEngine.setText("Motor de búsqueda:");
        tvEngine.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvEngine.setPadding(0, 15, 0, 5);
        final List<SearchEngineManager.Engine> engines = SearchEngineManager.getEngines(this);
        List<String> engineNames = new ArrayList<String>();
        for (SearchEngineManager.Engine e : engines) {
            engineNames.add(e.name);
        }

        final Spinner spinnerEngines = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, engineNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEngines.setAdapter(adapter);

        int savedIndex = SearchEngineManager.getSelectedEngineIndex(this);
        if (savedIndex < engines.size()) {
            spinnerEngines.setSelection(savedIndex);
        }

        spinnerEngines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchEngineManager.setSelectedEngineIndex(MainActivity.this, position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        TextView tvUserAgent = new TextView(this);
        tvUserAgent.setText("Agente de Usuario:");
        tvUserAgent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvUserAgent.setPadding(0, 10, 0, 5);
        final List<UserAgentManager.UserAgentItem> userAgents = UserAgentManager.getUserAgents();
        final Spinner spinnerUserAgents = new Spinner(this);
        ArrayAdapter<UserAgentManager.UserAgentItem> adapterUA = new ArrayAdapter<UserAgentManager.UserAgentItem>(this, android.R.layout.simple_spinner_item, userAgents);
        adapterUA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserAgents.setAdapter(adapterUA);
        int savedUaIndex = UserAgentManager.getSelectedUserAgentIndex(this);
        if (savedUaIndex < userAgents.size()) {
            spinnerUserAgents.setSelection(savedUaIndex);
        }
        spinnerUserAgents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UserAgentManager.applyUserAgentAtIndex(MainActivity.this, webView, creaClient, position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        TextView tvInput = new TextView(this);
        tvInput.setText(R.string.url);
        tvInput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvInput.setPadding(0, 10, 0, 5);

        final EditText input = new EditText(this);
        input.setHint("Escribe una URL o búsqueda...");
        if (webView != null && webView.getUrl() != null) {
            input.setText(webView.getUrl());
        }
        input.setSelectAllOnFocus(true);

        layout.addView(tvUserAgent);
        layout.addView(spinnerUserAgents);
        layout.addView(tvEngine);
        layout.addView(spinnerEngines);
        layout.addView(tvInput);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String query = input.getText().toString().trim();
                if (query.length() > 0) {
                    if (query.startsWith("http://") || query.startsWith("https://")) {
                        creaClient.loadUrl(webView, query);
                    } else if(query.startsWith("javascript:")){
                        creaClient.loadUrl(webView, query);
                    } else{
                        if(openInExternalAppIfPossible(query)){
                            return;
                        }
                        int selectedPos = spinnerEngines.getSelectedItemPosition();
                        SearchEngineManager.Engine selectedEngine = engines.get(selectedPos);
                        try {
                            String searchUrl = String.format(selectedEngine.searchUrl, java.net.URLEncoder.encode(query, "UTF-8"));
                            creaClient.loadUrl(webView, searchUrl);
                        } catch (Exception e) {
                            creaClient.loadUrl(webView, selectedEngine.searchUrl.replace("%s", query));
                        }
                    }
                }
            }
        });

        builder.setNeutralButton("Recargar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                creaClient.reload(webView);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);

        builder.show();
    }

    private void setActionBarHomeBtn(){
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
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
        if (progressBar != null) {
            Drawable progressDrawable = progressBar.getProgressDrawable();
            if (progressDrawable != null) {
                progressDrawable.mutate().clearColorFilter();
            }
        }
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
    public String getTitleBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            return getActionBarTitle();
        }else if(actionBarTitle != null){
            return actionBarTitle.getText().toString();
        }
        return null;
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

    private String getActionBarTitle(){
        ActionBar actionBar = getActionBar();
        if (actionBar != null && actionBar.getTitle() != null) {
            return actionBar.getTitle().toString();
        }
        return null;
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
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE){
            uniLowMem();
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        uniLowMem();
    }

    private void uniLowMem(){
        if (webView != null) {
            webView.clearCache(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                webView.freeMemory();
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_TABS, Menu.NONE, R.string.tabs);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            showSearchDialog();
            return true;
        }else if(id == MENU_TABS){
            Intent intent = new Intent(this, TabsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
