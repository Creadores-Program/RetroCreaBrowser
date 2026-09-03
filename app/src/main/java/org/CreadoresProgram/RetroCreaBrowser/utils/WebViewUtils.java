package org.CreadoresProgram.RetroCreaBrowser.utils;

import android.os.Build;
import android.webkit.WebView;

public class WebViewUtils {
  public static void evaluateJS(WebView view, String code){
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
          evaluateJSK(view, code);
      }else{
        view.loadUrl("javascript:" + code);
      }
  }
  private static void evaluateJSK(WebView webview, String code){
      webview.evaluateJavascript(code, null);
  }
}
