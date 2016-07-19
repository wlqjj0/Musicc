package com.ui.my;

import android.app.Application;

/**
 * Created by wl on 2016/7/9.
 */
public class Mucc extends Application {
    public String myMu,nam;
    public int duc;
    public String getMyMu() {
        return myMu;
    }

    public void setMyMu(String s) {
        myMu = s;
    }

    public int getDuc() {
        return duc;
    }

    public void setDuc(int duc) {
        this.duc = duc;
    }

    public String getNam() {
        return nam;
    }

    public void setNam(String nam) {
        this.nam = nam;
    }
}
