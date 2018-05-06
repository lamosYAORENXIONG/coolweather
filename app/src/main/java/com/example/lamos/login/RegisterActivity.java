package com.example.lamos.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lamos.coolweather.R;

import org.litepal.tablemanager.Connector;

public class RegisterActivity extends AppCompatActivity {
    private EditText account;
    private EditText password;
    private EditText name;
    private EditText mail;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Connector.getDatabase();

        account = (EditText)findViewById(R.id.user_account);
        password = (EditText)findViewById(R.id.user_password);
        name = (EditText)findViewById(R.id.user_name);
        mail = (EditText)findViewById(R.id.user_mail);

        register = (Button)findViewById(R.id.user_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_account = account.getText().toString();
                String user_passwork = password.getText().toString();
                String user_name = name.getText().toString();
                String user_mail = mail.getText().toString();

                User user = new User();
                user.setAccount(user_account);
                user.setPassword(user_passwork);
                user.setName(user_name);
                user.setMail(user_mail);
                user.save();

                RegisterActivity.this.finish();
            }
        });
    }
}
