package com.example.lamos.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lamos.coolweather.R;
import com.example.lamos.coolweather.view.WeatherActivity;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends LoginBaseActivity {
    private static final String TAG = "LoginActivity";

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button tourist;
    private Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountEdit = (EditText)findViewById(R.id.account);
        passwordEdit = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String user = null;

                if ((user = check(account,password))!= null){
                    Log.i(TAG, "onClick: user= " + user);

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                    editor.putBoolean("login", true);
                    editor.putString("username",user);
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, WeatherActivity.class);
                    startActivity(intent);

                }else {
                    Toast.makeText(LoginActivity.this, "account or password is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tourist = (Button)findViewById(R.id.tourist);
        tourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });

        register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private String check(String account, String password){
        List<User> users = DataSupport.findAll(User.class);

        for (User user : users){
            if (user.getAccount().equals(account) && user.getPassword().equals(password)){
                return user.getName();
            }
        }

        return null;
    }
}
