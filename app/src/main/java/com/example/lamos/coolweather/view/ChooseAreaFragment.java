package com.example.lamos.coolweather.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lamos.coolweather.MainActivity;
import com.example.lamos.coolweather.R;
import com.example.lamos.coolweather.persenter.ChooseAreaFragmentPersenterCompl;

/**
 * Created by lamos on 2018/3/17.
 */

public class ChooseAreaFragment extends Fragment implements IChooseAreaView {
    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private int currentLevel;
    private ChooseAreaFragmentPersenterCompl chooseAreaFragmentPersenterCompl = new ChooseAreaFragmentPersenterCompl(this);



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);

        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_buton);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, chooseAreaFragmentPersenterCompl.getDataList());
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (currentLevel == LEVEL_PROVINCE){
                    chooseAreaFragmentPersenterCompl.selectedProvince(i);
                    chooseAreaFragmentPersenterCompl.queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    chooseAreaFragmentPersenterCompl.selectedCity(i);
                    chooseAreaFragmentPersenterCompl.queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = chooseAreaFragmentPersenterCompl.getCountyList().get(i).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId );
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        //activity.drawerLayout.closeDrawers();
                        activity.getSwipeRefresh().setRefreshing(true);
                        activity.getQueryPersenterCompl().requestWeather(weatherId);
                    }


                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY){
                    chooseAreaFragmentPersenterCompl.queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    chooseAreaFragmentPersenterCompl.queryProvinces();
                }
            }
        });

        
        chooseAreaFragmentPersenterCompl.queryProvinces();
    }

    //initialize IChooseAreaView
    @Override
    public void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void updatetitle(String title, int buttonstate) {
        titleText.setText(title);
        backButton.setVisibility(buttonstate);
    }

    @Override
    public void updatelist() {
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }

    @Override
    public void updatecurrentLevel(int level) {
        currentLevel = level;
    }
}
