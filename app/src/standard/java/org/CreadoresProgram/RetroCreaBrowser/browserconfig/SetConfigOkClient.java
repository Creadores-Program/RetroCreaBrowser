package org.CreadoresProgram.RetroCreaBrowser.browserconfig;

import okhttp3.TlsVersion;
import okhttp3.ConnectionSpec;

import java.util.Arrays;

import org.CreadoresProgram.WebViewCREA.network.NetClient;

public class SetConfigOkClient {
  public static void configOkClient(NetClient client){
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
          .build());
  }
}
