package org.CreadoresProgram.RetroCreaBrowser.download;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.CreadoresProgram.RetroCreaBrowser.R;
import org.CreadoresProgram.WebViewCREA.network.NetClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RBDownloadManager {

    public static void processDownload(Context context, NetClient netClient, String url,
        String userAgent, String contentDisposition, String mimeType) {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);

        downloadWithHttpURLConnection(context, url, userAgent, fileName);
    }

    private static void downloadWithHttpURLConnection(final Context context, final String urlStr,
        final String userAgent, final String fileName) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (userAgent != null) conn.setRequestProperty("User-Agent", userAgent);
                    String cookies = CookieManager.getInstance().getCookie(urlStr);
                    if (cookies != null) conn.setRequestProperty("Cookie", cookies);

                    conn.connect();
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) return false;

                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (!path.exists()) path.mkdirs();
                    File file = new File(path, fileName);

                    InputStream input = conn.getInputStream();
                    FileOutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                showToast(context, success ? context.getString(R.string.done_downl, fileName) : context.getString(R.string.error_downl));
            }
        }.execute();
    }

    private static void showToast(final Context context, final String msg) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}