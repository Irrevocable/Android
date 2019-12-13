package cn.edu.hznu.weibo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int LOGIN_FAIL = 0;
    public static final int LOGIN_SUCCESS = 1;
    public static final int SEND_SUCCESS = 2;
    public static final int SEND_FAIL = 3;
    public static final int LOGIN_ERROR=4;
    //账号密码登录界面
    private LinearLayout accountLayout;
    private EditText user;
    private EditText pwd;
    private Button loginBtn;
    private Button msgBtn;
    //短信验证码登录界面
    private LinearLayout msgLayout;
    private EditText phone;
    private EditText validateCode;
    private TextView getMsg;
    private Button msgLoginBtn;
    private Button accountBtn;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏字体
        setContentView(R.layout.activity_login);
        accountLayout = findViewById(R.id.account_layout);
        msgLayout = findViewById(R.id.msg_layout);
        user = findViewById(R.id.user_input);
        pwd = findViewById(R.id.pwd_input);
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(LoginActivity.this);
        msgBtn = findViewById(R.id.msg_btn);
        msgBtn.setOnClickListener(this);
        phone = findViewById(R.id.phone_input);
        validateCode = findViewById(R.id.validateCode);
        getMsg = findViewById(R.id.getMsg);
        getMsg.setOnClickListener(this);
        msgLoginBtn = findViewById(R.id.msgLogin_btn);
        msgLoginBtn.setOnClickListener(this);
        accountBtn = findViewById(R.id.account_btn);
        accountBtn.setOnClickListener(this);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Toast toast;
                switch (msg.what) {
                    case LOGIN_FAIL:
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                        alertDialog.setIcon(R.drawable.error)
                                .setTitle("错误")
                                .setMessage("用户名或密码错误!")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    user.setText("");
                                    pwd.setText("");
                                    user.setFocusable(true);
                                    user.setFocusableInTouchMode(true);
                                    user.requestFocus();
                                    user.findFocus();
                                })
                                .setCancelable(true)
                                .create();
                        alertDialog.show();
                        break;
                    case LOGIN_SUCCESS:
                        toast=Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT);
                        toast.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 1500);
                        break;
                    case SEND_SUCCESS:
                        toast=Toast.makeText(LoginActivity.this, "验证码已发送，请注意查看手机短信.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        break;
                    case SEND_FAIL:
                        AlertDialog.Builder sendDialog = new AlertDialog.Builder(LoginActivity.this);
                        sendDialog.setIcon(R.drawable.error)
                                .setTitle("错误")
                                .setMessage((String) msg.getData().get("message"))
                                .setPositiveButton("确定", (dialog, which) -> {
                                    phone.setText("");
                                    validateCode.setText("");
                                    phone.setFocusable(true);
                                    phone.setFocusableInTouchMode(true);
                                    phone.requestFocus();
                                    phone.findFocus();
                                })
                                .setCancelable(true)
                                .create();
                        sendDialog.show();
                        break;
                    case LOGIN_ERROR:
                        AlertDialog.Builder loginDialog = new AlertDialog.Builder(LoginActivity.this);
                        loginDialog.setIcon(R.drawable.error)
                                .setTitle("错误")
                                .setMessage("请重新输入正确的验证码!")
                                .setPositiveButton("确定", (dialog, which) -> {
                                    validateCode.setText("");
                                })
                                .setCancelable(true)
                                .create();
                        loginDialog.show();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        String number = phone.getText().toString();
        switch (v.getId()) {
            case R.id.login_btn:
                //发送http请求到servlet
                String username = user.getText().toString();
                String password = pwd.getText().toString();
                AlertDialog.Builder accountDialog = new AlertDialog.Builder(LoginActivity.this);
                if (username.equals("") || password.equals("")) {
                    accountDialog.setIcon(R.drawable.error)
                            .setTitle("错误")
                            .setMessage("用户名或密码不能为空!")
                            .setPositiveButton("确定", (dialog, which) -> {
                            })
                            .setCancelable(true)
                            .create();
                    accountDialog.show();
                } else if (!username.matches(Patterns.EMAIL_ADDRESS.pattern()) && (username.length() != 11 || !username.matches(Patterns.PHONE.pattern()))) {
                    accountDialog.setIcon(R.drawable.tip)
                            .setTitle("提示")
                            .setMessage("用户名必须为邮箱或手机号!")
                            .setPositiveButton("确定", (dialog, which) -> {
                                user.setText("");
                                pwd.setText("");
                                user.setFocusable(true);
                                user.setFocusableInTouchMode(true);
                                user.requestFocus();
                                user.findFocus();
                            })
                            .setCancelable(true)
                            .create();
                    accountDialog.show();
                } else {
                    sendLoginRequest();
                }
                break;
            case R.id.msg_btn:
                msgLayout.setVisibility(View.VISIBLE);
                accountLayout.setVisibility(View.GONE);
                break;
            case R.id.getMsg:
                AlertDialog.Builder phoneDialog = new AlertDialog.Builder(LoginActivity.this);
                if (number.equals("")) {
                    phoneDialog.setIcon(R.drawable.error)
                            .setTitle("错误")
                            .setMessage("手机号不能为空!")
                            .setPositiveButton("确定", (dialog, which) -> {
                            })
                            .setCancelable(true)
                            .create();
                    phoneDialog.show();
                } else if (number.length() != 11 || !number.matches(Patterns.PHONE.pattern())) {
                    phoneDialog.setIcon(R.drawable.tip)
                            .setTitle("提示")
                            .setMessage("请输入正确的手机号!")
                            .setPositiveButton("确定", (dialog, which) -> {
                                phone.setText("");
                            })
                            .setCancelable(true)
                            .create();
                    phoneDialog.show();
                } else {
                    sendMsgRequest();
                    new CountDownTimer(60 * 1000 - 1, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            getMsg.setClickable(false);
                            getMsg.setText(millisUntilFinished / 1000 + "秒后可以重新获取");
                            getMsg.setTextColor(Color.GRAY);
                        }

                        @Override
                        public void onFinish() {
                            getMsg.setClickable(true);
                            getMsg.setText("重新获取验证码");
                            getMsg.setTextColor(Color.parseColor("#1e90ff"));
                            cancel();
                        }
                    }.start();
                }
                break;
            case R.id.msgLogin_btn:
                String code = validateCode.getText().toString();
                //文本框为空
                if (number.equals("") || code.equals("")) {
                    AlertDialog.Builder msgDialog = new AlertDialog.Builder(LoginActivity.this);
                    msgDialog.setIcon(R.drawable.error)
                            .setTitle("错误")
                            .setMessage("手机号和验证码均不能为空!")
                            .setPositiveButton("确定", (dialog, which) -> {
                            })
                            .setCancelable(true)
                            .create();
                    msgDialog.show();
                } else {
                    //发送请求
                    sendPhoneLoginRequest();
                }
                break;
            case R.id.account_btn:
                accountLayout.setVisibility(View.VISIBLE);
                msgLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void sendPhoneLoginRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("phone", phone.getText().toString())
                            .add("msg", validateCode.getText().toString()).build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("msgLogin", responseData);
                    Message message = new Message();
                    if(responseData.equals("success")){
                        message.what=LOGIN_SUCCESS;
                        handler.sendMessage(message);
                    }else{
                        message.what=LOGIN_ERROR;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendMsgRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("phone", phone.getText().toString())
                            .add("flag", "2").build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/msg")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("msgSend", responseData);
                    Message message = new Message();
                    if (responseData.equals("OK")) {
                        //验证码发送成功
                        message.what = SEND_SUCCESS;
                        handler.sendMessage(message);
                    } else {
                        message.what = SEND_FAIL;
                        Bundle bundle = new Bundle();
                        bundle.putString("message", responseData);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendLoginRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("user", user.getText().toString())
                            .add("pwd", pwd.getText().toString()).build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/login")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("accountLogin", responseData);
                    Message message = new Message();
                    if (responseData.equals("success")) {
                        //进入MainActivity
                        message.what = LOGIN_SUCCESS;
                        handler.sendMessage(message);
                    } else {
                        //找不到该用户
                        message.what = LOGIN_FAIL;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
