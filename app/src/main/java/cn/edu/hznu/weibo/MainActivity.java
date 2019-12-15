package cn.edu.hznu.weibo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import cn.edu.hznu.weibo.Fragment.Home.HomeFragment;
import cn.edu.hznu.weibo.Fragment.Mine.MineFragment;
import cn.edu.hznu.weibo.Fragment.NoScrollViewPager;
import cn.edu.hznu.weibo.Fragment.TabFragmentPagerAdapter;
import cn.edu.hznu.weibo.Fragment.Weibo.WeiBoFragment;
import cn.edu.hznu.weibo.UI.StatusBarUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mine;
    private ImageView home;
    private ImageView weibo;
    private NoScrollViewPager viewPager;
    private List<Fragment> list;
    private TabFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        StatusBarUtils.with(this).init();//图片沉浸式
        //设置导航栏点击事件
        mine.setOnClickListener(this);
        home.setOnClickListener(this);
        weibo.setOnClickListener(this);
//        viewPager.addOnPageChangeListener(new MyPagerChangeListener());
        //把Fragment添加到List集合里面
        list = new ArrayList<>();
        list.add(new WeiBoFragment());
        list.add(new HomeFragment());
        list.add(new MineFragment());
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);//显示第一个页面
    }

    /**
     * 初始化控件
     */
    private void InitView() {
        mine = (ImageView) findViewById(R.id.mine);
        home = (ImageView) findViewById(R.id.home);
        home.setImageResource(R.drawable.homes);
        weibo = (ImageView) findViewById(R.id.weibo);
        viewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine:
                setAndroidNativeLightStatusBar(MainActivity.this,true);
                mine.setImageResource(R.drawable.mines);
                home.setImageResource(R.drawable.home);
                weibo.setImageResource(R.drawable.weibo);
                viewPager.setCurrentItem(2);
                break;
            case R.id.home:
                setAndroidNativeLightStatusBar(MainActivity.this,false);
                home.setImageResource(R.drawable.homes);
                mine.setImageResource(R.drawable.mine);
                weibo.setImageResource(R.drawable.weibo);
                viewPager.setCurrentItem(1);
                break;
            case R.id.weibo:
                setAndroidNativeLightStatusBar(MainActivity.this,true);
                weibo.setImageResource(R.drawable.weibos);
                home.setImageResource(R.drawable.home);
                mine.setImageResource(R.drawable.mine);
                viewPager.setCurrentItem(0);
                break;

        }
    }
    private void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
