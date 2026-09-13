package org.CreadoresProgram.RetroCreaBrowser.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.CreadoresProgram.RetroCreaBrowser.R;
import org.CreadoresProgram.WebViewCREA.network.NetClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RBDownloadManager {

    public static void processDownload(Context context, NetClient netClient, String url,
        String userAgent, String contentDisposition, String mimeType) {
        
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        boolean isHttps = url != null && url.toLowerCase().startsWith("https://");

        if (isHttps && netClient != null && isOkHttpClientAvailable(netClient)) {
            try {
                downloadWithOkHttp(context, netClient.getOkClient(), url, userAgent, fileName, mimeType);
            } catch (Throwable t) {
                downloadWithDownloadManager(context, url, userAgent, fileName, mimeType);
            }
        } else {
            downloadWithDownloadManager(context, url, userAgent, fileName, mimeType);
        }
    }

    private static boolean isOkHttpClientAvailable(NetClient netClient) {
        try {
            return netClient.getOkClient() != null;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void downloadWithOkHttp(final Context context, OkHttpClient client, final String url,
        final String userAgent, final String fileName, final String mimeType) {

        Request.Builder builder = new Request.Builder().url(url);

        if (userAgent != null && !userAgent.isEmpty()) {
            builder.header("User-Agent", userAgent);
        }

        String cookies = getCookiesCompat(context, url);
        if (cookies != null && !cookies.isEmpty()) {
            builder.header("Cookie", cookies);
        }

        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadWithDownloadManager(context, url, userAgent, fileName, mimeType);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    response.close();
                    downloadWithDownloadManager(context, url, userAgent, fileName, mimeType);
                    return;
                }

                ResponseBody body = response.body();
                if (body == null) {
                    response.close();
                    downloadWithDownloadManager(context, url, userAgent, fileName, mimeType);
                    return;
                }

                try {
                    File path = getDownloadsDirectoryCompat();
                    if (!path.exists()) path.mkdirs();
                    File file = new File(path, fileName);

                    InputStream input = body.byteStream();
                    FileOutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        output.write(buffer, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    body.close();

                    showToast(context, context.getString(R.string.done_downl, fileName));
                } catch (Exception e) {
                    response.close();
                    downloadWithDownloadManager(context, url, userAgent, fileName, mimeType);
                }
            }
        });
    }

    private static void downloadWithDownloadManager(Context context, String url, String userAgent,
        String fileName, String mimeType) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            if (userAgent != null && !userAgent.isEmpty()) {
                request.addRequestHeader("User-Agent", userAgent);
            }

            String cookies = getCookiesCompat(context, url);
            if (cookies != null && !cookies.isEmpty()) {
                request.addRequestHeader("Cookie", cookies);
            }

            if (mimeType != null && !mimeType.isEmpty()) {
                request.setMimeType(mimeType);
            }

            setNotificationVisibilityCompat(request);

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
            } else {
                showToast(context, context.getString(R.string.error_downl));
            }
        } catch (Exception e) {
            showToast(context, context.getString(R.string.error_downl));
        }
    }

    @SuppressWarnings("deprecation")
    private static void setNotificationVisibilityCompat(DownloadManager.Request request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        } else {
            request.setShowRunningNotification(true);
        }
    }

    @SuppressWarnings("deprecation")
    private static String getCookiesCompat(Context context, String url) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(context);
                CookieSyncManager.getInstance().sync();
            }
            return CookieManager.getInstance().getCookie(url);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private static File getDownloadsDirectoryCompat() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    private static void showToast(final Context context, final String msg) {
        if (context == null) return;
        
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}