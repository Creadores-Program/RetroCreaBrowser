package org.CreadoresProgram.RetroCreaBrowser;

import android.app.Application;

import java.util.List;
import java.util.ArrayList;

import org.CreadoresProgram.RetroCreaBrowser.MainActivity;

public class RetroCreaBrowserApp extends Application {
    private final List<MainActivity> tabs = new ArrayList<MainActivity>();

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
