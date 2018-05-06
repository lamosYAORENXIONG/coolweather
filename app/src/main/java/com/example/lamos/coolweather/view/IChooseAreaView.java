package com.example.lamos.coolweather.view;

/**
 * Created by lamos on 4/17/18.
 */

public interface IChooseAreaView {
    void showProgressDialog();
    void closeProgressDialog();
    void updatetitle(String title,int buttonstate);
    void updatelist();
    void updatecurrentLevel(int level);
}
