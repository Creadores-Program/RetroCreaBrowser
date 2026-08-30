package org.CreadoresProgram.RetroCreaBrowser.browserconfig;

import android.content.Context;

import okhttp3.TlsVersion;
import okhttp3.ConnectionSpec;
import okhttp3.Cache;

import java.util.Arrays;
import java.io.File;

import org.CreadoresProgram.WebViewCREA.network.NetClient;

public class SetConfigOkClient {
  public static void configOkClient(NetClient client, Context context){
      File cacheDir = new File(context.getCacheDir(), "patch_cache");
      client.setOkClient(client.getOkClient().newBuilder()
          .connectionSpecs(Arrays.asList(
              new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                  .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
                  .supportsTlsExtensions(true)
                  .build(),
              new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                  .supportsTlsExtensions(true)
                  .build()
          ))
          .cache(new Cache(cacheDir, 30 * 1024 * 1024))
          .build());
  }
}
