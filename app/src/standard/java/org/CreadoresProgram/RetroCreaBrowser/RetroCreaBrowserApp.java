package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Application;
import org.conscrypt.Conscrypt;

import java.security.Security;
import java.util.List;
import java.util.ArrayList;

import org.CreadoresProgram.RetroCreaBrowser.MainActivity;

public class RetroCreaBrowserApp extends Application {
    private final List<MainActivity> tabs = new ArrayList<MainActivity>();
    @Override
    public void onCreate(){
        super.onCreate();
        Security.insertProviderAt(Conscrypt.newProvider(), 1);
    }
    public void addTab(MainActivity tab){
        tabs.add(tab);
    }

    public void removeTab(MainActivity tab){
        tabs.remove(tab);
    }

    public List<MainActivity> getTabs(){
        return new ArrayList<MainActivity>(tabs);
    }
}
