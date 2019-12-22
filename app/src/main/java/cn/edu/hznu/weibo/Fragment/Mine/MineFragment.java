package cn.edu.hznu.weibo.Fragment.Mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.edu.hznu.weibo.Bean.UserInfo;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.MainActivity;
import cn.edu.hznu.weibo.R;
import cn.edu.hznu.weibo.Utils.UI.BottomDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MineFragment extends BaseFragment {
    private static final String TAG = "MineFragment";
    public static final int UPDATE_TEXT = 0;
    private String info;
    private UserInfo userInfo;
    private ImageView setting;
    private ImageView avatar;
    private TextView nickName;
    private TextView intro;
    private TextView num;
    private Handler handler;
    public MineFragment() {
    }

    public MineFragment(String info) {
        this.info = info;
    }

    public void getInfo() {
        Gson gson = new Gson();
        userInfo = gson.fromJson(this.info, UserInfo.class);
        sendQueryInfoRequest();
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case UPDATE_TEXT:
                        Gson gson = new Gson();
                        userInfo =gson.fromJson(msg.getData().get("info").toString(),UserInfo.class);
                        nickName.setText(userInfo.getNickName());
                        Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/"+userInfo.getImg()).into(avatar);
                        if(TextUtils.isEmpty(userInfo.getIntroduce())){
                            intro.setText("简介:暂无简介");
                        }else{
                            intro.setText("简介:"+userInfo.getIntroduce());
                        }
                        num.setText(String.valueOf(userInfo.getNum()));
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected int setContentView() {
        return R.layout.mine_layout;
    }

    @Override
    protected void lazyLoad() {
        setting = (ImageView) super.findViewById(R.id.setting);
        avatar=(ImageView)super.findViewById(R.id.avatar);
        nickName=(TextView)super.findViewById(R.id.nickName);
        intro=(TextView)super.findViewById(R.id.user_intro);
        num=(TextView)super.findViewById(R.id.weiboNum);
        getInfo();
        final LinearLayout toHome=(LinearLayout)super.findViewById(R.id.toHome);
        toHome.setOnClickListener(v -> {
            final MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setAndroidNativeLightStatusBar(getActivity(), false);
            mainActivity.setFragmentSkipInterface(viewPager -> {
                viewPager.setCurrentItem(1);
            });
            mainActivity.skipToFragment();
            mainActivity.findViewById(R.id.nav_bar).findViewById(R.id.home).performClick();
        });
        setting.setOnClickListener((v -> {
            BottomDialog.showSettingDialog(getActivity());
        }));
        avatar.setOnClickListener(v -> {
            BottomDialog.showPhotoDialog(getActivity());
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
