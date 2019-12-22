package cn.edu.hznu.weibo.Fragment.Home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class HomeFragment extends BaseFragment {
    private static final String TAG="HomeFragment";
    public static final int UPDATE_TEXT = 0;
    private String info;
    private UserInfo userInfo;
    private TextView nickName;
    private TextView intro;
    private ImageView avatar;
    private Handler handler;
    private MainActivity mainActivity;
    public HomeFragment() {
    }

    public HomeFragment(String info) {
        this.info = info;
    }
    public void getInfo(){
        Gson gson=new Gson();
        userInfo =gson.fromJson(info,UserInfo.class);
        sendQueryInfoRequest();
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case UPDATE_TEXT:
                        Gson gson=new Gson();
                        userInfo =gson.fromJson(msg.getData().get("info").toString(),UserInfo.class);
                        intro.setText(userInfo.getIntroduce());
                        nickName.setText(userInfo.getNickName());
                        Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/" + userInfo.getImg()).into(avatar);
                        if (TextUtils.isEmpty(userInfo.getIntroduce())) {
                            intro.setText("简介:暂无简介");
                        } else {
                            intro.setText("简介:" + userInfo.getIntroduce());
                        }
                }
                return false;
            }
        });
    }


    @Override
    protected int setContentView() {
        return R.layout.home_layout;
    }

    @Override
    protected void lazyLoad() {
        nickName=(TextView)super.findViewById(R.id.nickName);
        avatar=(ImageView)super.findViewById(R.id.avatar);
        intro=(TextView)super.findViewById(R.id.user_intro);
        getInfo();
        intro.setOnClickListener((v->{
            final MainActivity mainActivity=(MainActivity)getActivity();
            mainActivity.setAndroidNativeLightStatusBar(getActivity(),true);
            mainActivity.findViewById(R.id.nav_bar).setVisibility(View.GONE);
            mainActivity.setFragmentSkipInterface(viewPager -> viewPager.setCurrentItem(3));
            mainActivity.skipToFragment();
        }));
    }

    private void sendQueryInfoRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", "queryIntro")
                            .add("uid", String.valueOf(userInfo.getUid()))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/deal")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d(TAG, responseData);
                    Message message=new Message();
                    message.what=UPDATE_TEXT;
                    Bundle bundle = new Bundle();
                    bundle.putString("info", responseData);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
