package cn.edu.hznu.weibo.Fragment.Mine;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import androidx.annotation.NonNull;
import cn.edu.hznu.weibo.Bean.UserInfo;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.MainActivity;
import cn.edu.hznu.weibo.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IntroFragment extends BaseFragment {
    public static final int QUERY_SUCCESS=0;
    public static final int REVISE_SUCCESS=1;
    public static Gson gson=new Gson();
    private String info;
    private UserInfo userInfo;
    private EditText intro_input;
    private TextView finish;
    private Handler handler;

    public IntroFragment() { }

    public IntroFragment(String info) {
        this.info = info;
    }

    public void getInfo() {
        userInfo = gson.fromJson(this.info, UserInfo.class);
        SendQueryIntroRequest();
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case QUERY_SUCCESS:
                        UserInfo info=gson.fromJson(msg.getData().get("data").toString(),UserInfo.class);
                        if (!TextUtils.isEmpty(info.getIntroduce())) {
                            intro_input.setText(info.getIntroduce());
                        }
                        intro_input.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                finish.setTextColor(Color.parseColor("#333333"));
                                finish.setOnClickListener(v -> {
                                    String intro = intro_input.getText().toString();
                                    sendReviseIntroRequest();//发送修改简介请求
                                    intro_input.setText(intro);
                                });
                            }
                        });
                        break;
                    case REVISE_SUCCESS:
                        backToHome();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected int setContentView() {
        return R.layout.intro_layout;
    }

    @Override
    protected void lazyLoad() {
        final TextView back = (TextView) super.findViewById(R.id.backToHome);
        finish = (TextView) super.findViewById(R.id.edit_intro);
        getInfo();
        intro_input = (EditText) super.findViewById(R.id.intro_input);

        back.setOnClickListener((v -> {
            intro_input.setText(userInfo.getIntroduce());
            backToHome();
        }));
    }

    private void backToHome() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setAndroidNativeLightStatusBar(getActivity(), false);
        mainActivity.findViewById(R.id.nav_bar).setVisibility(View.VISIBLE);
        mainActivity.setFragmentSkipInterface(viewPager -> {
            viewPager.setCurrentItem(1);
        });
        mainActivity.skipToFragment();
        return;
    }
    private void SendQueryIntroRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", "queryIntro")
                            .add("uid",String.valueOf(userInfo.getUid()))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/deal")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message message=new Message();
                    message.what=QUERY_SUCCESS;
                    Bundle bundle=new Bundle();
                    bundle.putString("data",responseData);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void sendReviseIntroRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", "reviseIntro")
                            .add("uid", String.valueOf(userInfo.getUid()))
                            .add("intro", intro_input.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/deal")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if(responseData.equals("success")){
                        Message message=new Message();
                        message.what=REVISE_SUCCESS;
                        handler.sendMessage(message);
                    }
                    Log.d(TAG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}