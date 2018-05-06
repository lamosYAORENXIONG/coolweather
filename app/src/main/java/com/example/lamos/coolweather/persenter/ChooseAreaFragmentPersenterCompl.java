package com.example.lamos.coolweather.persenter;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lamos.coolweather.view.ChooseAreaFragment;
import com.example.lamos.coolweather.view.IChooseAreaView;
import com.example.lamos.db.City;
import com.example.lamos.db.County;
import com.example.lamos.db.Province;
import com.example.lamos.util.HttpUtil;
import com.example.lamos.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by lamos on 2018/5/1.
 */

public class ChooseAreaFragmentPersenterCompl implements IChooseAreaFragmentPersenter {
    private static final String TAG = "ChooseAreaFragmentPerse";

    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;

    private IChooseAreaView iChooseAreaView;
    public ChooseAreaFragmentPersenterCompl(IChooseAreaView i){
        iChooseAreaView = i;

    }

    @Override
    public void queryProvinces() {
//        titleText.setText("中国");
//        backButton.setVisibility(View.GONE);
        iChooseAreaView.updatetitle("中国",View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
//            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
//            currentLevel = ChooseAreaFragment.LEVEL_PROVINCE;
            iChooseAreaView.updatelist();
            iChooseAreaView.updatecurrentLevel(ChooseAreaFragment.LEVEL_PROVINCE);

        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    @Override
    public void queryCities() {
//        titleText.setText(selectedProvince.getProvinceName());
//        backButton.setVisibility(View.VISIBLE);
        iChooseAreaView.updatetitle(selectedProvince.getProvinceName(),View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        //cityList = DataSupport.findAll(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
//            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
//            currentLevel = ChooseAreaFragment.LEVEL_CITY;
            iChooseAreaView.updatelist();
            iChooseAreaView.updatecurrentLevel(ChooseAreaFragment.LEVEL_CITY);

        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    @Override
    public void queryCounties() {
//        titleText.setText(selectedCity.getCityName());
//        backButton.setVisibility(View.VISIBLE);
        iChooseAreaView.updatetitle(selectedCity.getCityName(),View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
//            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
//            currentLevel = ChooseAreaFragment.LEVEL_COUNTY;
            iChooseAreaView.updatelist();
            iChooseAreaView.updatecurrentLevel(ChooseAreaFragment.LEVEL_COUNTY);

        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/"+ cityCode;
            queryFromServer(address, "county" );
        }
    }

    @Override
    public void queryFromServer(String address, final String type) {
        iChooseAreaView.showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iChooseAreaView.closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());//
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());//
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iChooseAreaView.closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void selectedProvince(int selection) {
        selectedProvince = provinceList.get(selection);
    }

    @Override
    public void selectedCity(int selection) {
        selectedCity = cityList.get(selection);
    }


    public List<County> getCountyList() {
        return countyList;
    }

    public List<String> getDataList() {
        return dataList;
    }


    private FragmentActivity getActivity(){
        return ((ChooseAreaFragment)iChooseAreaView).getActivity();
    }
}
