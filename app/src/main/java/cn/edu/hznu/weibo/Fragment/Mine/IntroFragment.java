package cn.edu.hznu.weibo.Fragment.Mine;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

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
    private String info;
    private UserInfo userInfo;
    private EditText intro_input;

    public IntroFragment() {
    }

    public IntroFragment(String info) {
        this.info = info;
    }

    public void getInfo() {
        Gson gson = new Gson();
        userInfo = gson.fromJson(this.info, UserInfo.class);
    }


    @Override
    protected int setContentView() {
        return R.layout.intro_layout;
    }

    @Override
    protected void lazyLoad() {
        getInfo();
        final TextView back = (TextView) super.findViewById(R.id.backToHome);
        final TextView finish = (TextView) super.findViewById(R.id.edit_intro);
        finish.setTextColor(Color.parseColor("#808080"));
        intro_input = (EditText) super.findViewById(R.id.intro_input);
        if (!TextUtils.isEmpty(userInfo.getIntroduce())) {
            intro_input.setText(userInfo.getIntroduce());
        }
        back.setOnClickListener((v -> {
            intro_input.setText(userInfo.getIntroduce());
            backToHome();
        }));
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
                    backToHome();
                    intro_input.setText(intro);
                });
            }
        });
    }

    private void backToHome() {
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setAndroidNativeLightStatusBar(getActivity(), false);
        mainActivity.findViewById(R.id.nav_bar).setVisibility(View.VISIBLE);
        mainActivity.setFragmentSkipInterface(viewPager -> {
            viewPager.setCurrentItem(1);
        });
        mainActivity.skipToFragment();
        onDestroy();
        return;
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
                    Log.d(TAG, responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}