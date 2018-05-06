package com.example.lamos.coolweather.persenter;

import com.example.lamos.db.City;
import com.example.lamos.db.County;
import com.example.lamos.db.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lamos on 2018/5/1.
 */

public interface IChooseAreaFragmentPersenter {
    void queryProvinces();
    void queryCities();
    void queryCounties();
    void queryFromServer(String address, String type);
    void selectedProvince(int selection);
    void selectedCity(int selection);

}
