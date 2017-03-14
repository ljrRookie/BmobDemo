package com.example.bmobdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmobdemo.R;
import com.example.bmobdemo.javabean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by user on 2017/3/13.
 */
public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private TextInputLayout mTilUsername;
    private EditText mUsername;
    private TextInputLayout mTilPassword;
    private EditText mPassword;
    private Button mLogin;
    private TextView mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(LoginActivity.this, "f83c7903132eea0295b25c60662fc548");
        setContentView(R.layout.activity_login);
        initView();

    }

    private void initView() {
        mTilUsername = (TextInputLayout) findViewById(R.id.til_username);
        mUsername = (EditText) findViewById(R.id.username);
        mTilPassword = (TextInputLayout) findViewById(R.id.til_password);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (TextView) findViewById(R.id.register);
    }

    public void login(View view) {
        String userName = mTilUsername.getEditText().getText().toString().trim();
        String passWord = mTilPassword.getEditText().getText().toString().trim();
        BmobUser.loginByAccount(userName, passWord, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    Log.i("smile", "用户登陆成功");
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: ");
                }
            }
        });

    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    public void findPassword(View view) {
        Toast.makeText(this, "蠢得像猪一样还想找密码？", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RegisterActivity.REGIST_SUCCESS) {
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            mUsername.setText(username);
            mPassword.setText(password);
        }
    }
}
