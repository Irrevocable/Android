package cn.edu.hznu.weibo.Fragment.Mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import androidx.annotation.Nullable;
import cn.edu.hznu.weibo.Bean.UserInfo;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.R;

public class MineFragment extends BaseFragment {
    private static final String TAG = "MineFragment";
    private String info;
    private UserInfo userInfo;

    public MineFragment() {
    }

    public MineFragment(String info) {
        this.info = info;
    }

    public void getInfo() {
        Gson gson = new Gson();
        userInfo = gson.fromJson(this.info, UserInfo.class);
    }

    @Override
    protected int setContentView() {
        return R.layout.mine_layout;
    }

    @Override
    protected void lazyLoad() {
        getInfo();
        final ImageView setting = (ImageView) super.findViewById(R.id.setting);
        final ImageView avatar=(ImageView)super.findViewById(R.id.avatar);
        final TextView nickName=(TextView)super.findViewById(R.id.nickName);
        final TextView intro=(TextView)super.findViewById(R.id.user_intro);
        final TextView num=(TextView)super.findViewById(R.id.weiboNum);
        nickName.setText(userInfo.getNickName());
        Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/"+userInfo.getImg()).into(avatar);
        if(TextUtils.isEmpty(userInfo.getIntroduce())){
            intro.setText("简介:暂无简介");
        }else{
            intro.setText("简介:"+userInfo.getIntroduce());
        }
        num.setText(String.valueOf(userInfo.getNum()));
        setting.setOnClickListener((v -> {
            Log.d(TAG, "设置被点击了 ");
        }));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
