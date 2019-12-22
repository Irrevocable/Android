package cn.edu.hznu.weibo.Fragment.Home;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import cn.edu.hznu.weibo.Bean.UserInfo;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.R;

public class HomeFragment extends BaseFragment {
    private String info;
    private UserInfo userInfo;
    public HomeFragment(String info) {
        this.info = info;
    }
    public void getInfo(){
        Gson gson=new Gson();
        userInfo =gson.fromJson(this.info,UserInfo.class);
    }
    @Override
    protected int setContentView() {
        return R.layout.home_layout;
    }

    @Override
    protected void lazyLoad() {
        getInfo();
        final TextView nickName=(TextView)super.findViewById(R.id.nickName);
        final ImageView avatar=(ImageView)super.findViewById(R.id.avatar);
        final TextView intro=(TextView)super.findViewById(R.id.user_intro);
        nickName.setText(this.userInfo.getNickName());
        Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/"+this.userInfo.getImg()).into(avatar);
        if(TextUtils.isEmpty(userInfo.getIntroduce())){
            intro.setText("简介:暂无简介");
        }else{
            intro.setText("简介:"+userInfo.getIntroduce());
        }
    }
}
