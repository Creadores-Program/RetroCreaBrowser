package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Application;
import org.conscrypt.Conscrypt;

import java.security.Security;
import java.util.List;
import java.util.ArrayList;

import org.CreadoresProgram.RetroCreaBrowser.MainActivity;

public class RetroCreaBrowserApp extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    }
}
