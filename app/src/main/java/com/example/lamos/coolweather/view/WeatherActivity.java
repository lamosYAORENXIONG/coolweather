package com.example.lamos.coolweather.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lamos.coolweather.R;
import com.example.lamos.coolweather.persenter.WeatherActivityPersenterCompl;
import com.example.lamos.gson.Weather;
import com.example.lamos.login.LoginActivity;
import com.example.lamos.login.User;
import com.example.lamos.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WeatherActivity extends AppCompatActivity implements IWeatherActivityView {
    private static final String TAG = "WeatherActivity";

    //init view
    private ImageView bingPicImg;//danger public
    private SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;


    //init data
    private String weatherId;
    private String responseText;
    SharedPreferences prefs = null;//在这里初始化会没有point
    private WeatherActivityPersenterCompl queryPersenterCompl = new WeatherActivityPersenterCompl(this);

    //login view init
    private FrameLayout login_container;
    private Button login_button;
    private CircleImageView userimage;

    private WeatherFragment fragment;
    private float state;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);

        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        login_container = (FrameLayout)findViewById(R.id.login_container);

//        一种解决View滑动冲突猜想
//        swipeRefresh.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (fragment.getWeatherLayout().getScrollY() == 10){
//                    swipeRefresh.setScrollState(false);
//                }else {
//                    swipeRefresh.setScrollState(true);
//                }
//
//                return false;
//            }
//        });

        login();
        init();

}

    public DrawerLayout getDrawerlayout(){
        return drawerLayout;
    }


    // IWeatherActivityView init
    @Override
    public void login() {
        Boolean loginstate = prefs.getBoolean("login",false);

        if (!loginstate){
            View view = getLayoutInflater().inflate(R.layout.no_login, null);
            login_container.addView(view);

            login_button = (Button)findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WeatherActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            View view = getLayoutInflater().inflate(R.layout.ok_login,null);
            NavigationView navigationView = (NavigationView)view;
            RelativeLayout nav_handler = (RelativeLayout) navigationView.inflateHeaderView(R.layout.nav_handler); //    <!--app:headerLayout="@layout/nav_handler"使用代码插入不用用这条-->

            login_container.addView(view);

            //String username = getIntent().getStringExtra("user"); 第二次启动无法触发
            String username = prefs.getString("username", null); //第二次启动无法触发
            User muser = null;
            //List<User> users = DataSupport.where("name = ?", username).find(User.class);
            List<User> users = DataSupport.findAll(User.class);
            for (User user : users){
                if (user.getName().equals(username)){
                    muser =  user;
                }
            }


            if (muser != null){
                TextView name = (TextView)nav_handler.findViewById(R.id.username);
                name.setText(muser.getName());
                TextView mail = (TextView)nav_handler.findViewById(R.id.usermail);
                mail.setText(muser.getMail());}


            navigationView = (NavigationView)findViewById(R.id.nav_call);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.login_out:
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putBoolean("login", false);

                            Intent intent = new Intent(WeatherActivity.this, LoginActivity.class);
                            startActivity(intent);
                            WeatherActivity.this.finish();
                            break;

                        case R.id.login_call:
                            Intent share = new Intent();
                            share.setAction("com.example.lamos.shareweather.SHARE");
                            startActivity(share);
                            break;

                        default:
                            break;
                    }
                    return false;
                }
            });

            //change user picture
            userimage = (CircleImageView) nav_handler.findViewById(R.id.icon_image);
            userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        openAlbum();
                }
            });
        }
    }

    @Override
    public void init() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                weatherId = prefs.getString("weatherId", null);
                queryPersenterCompl.requestWeather(weatherId);
            }
        });


        //load Bing Picture
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            queryPersenterCompl.loadBingPic();
        }

        //custom theme
        if (Build.VERSION.SDK_INT >= 21){
            View decorView =getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //init WeatherFragment
        String weatherId = getIntent().getStringExtra("weather_id");
        if (weatherId == null){
            responseText = prefs.getString("weather",null);
            Weather weather = Utility.handleWeatherResponse(responseText);
            loadfragment(weather);
        }else {
            queryPersenterCompl.requestWeather(weatherId);
        }

    }


    //
    public void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        queryPersenterCompl.handleImageOnKitKat(data);
                    }else {
                        queryPersenterCompl.handleImageBeforeKitKat(data);
                    }
                }
        }
    }

    @Override
    public void loadfragment(Weather weather) {
        if (fragment == null){
            fragment = new WeatherFragment(weather);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.weather_fragment_container,fragment)
                    .commit();
        }else fragment.showWeatherInfo(weather);

    }

    public WeatherFragment getFragment() {
        return fragment;
    }

    public CircleImageView getUserimage() {
        return userimage;
    }

    public ImageView getBingPicImg() {
        return bingPicImg;
    }

    public SwipeRefreshLayout getSwipeRefresh() {
        return swipeRefresh;
    }

    public WeatherActivityPersenterCompl getQueryPersenterCompl() {
        return queryPersenterCompl;
    }

    public void setSwipeRefreshState(float a){
        boolean b = false;
        state = a;
        if (state < 100.0){
            b = true;
            Log.i(TAG, "setSwipeRefreshState: " + b);
        }else {
            b = false;
            Log.i(TAG, "setSwipeRefreshState: " + b);
        }
        swipeRefresh.setEnabled(b);
        Log.i(TAG, "setSwipeRefreshState: do" + " " + b );
    }

}
