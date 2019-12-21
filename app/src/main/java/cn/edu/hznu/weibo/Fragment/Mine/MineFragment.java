package cn.edu.hznu.weibo.Fragment.Mine;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.R;

public class MineFragment extends BaseFragment {
    private static final String TAG="MineFragment";

    @Override
    protected int setContentView() {
        return R.layout.mine_layout;
    }

    @Override
    protected void lazyLoad() {
        final ImageView setting=(ImageView)super.findViewById(R.id.setting);
        setting.setOnClickListener((v->{
            Log.d(TAG, "设置被点击了 ");
        }));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
