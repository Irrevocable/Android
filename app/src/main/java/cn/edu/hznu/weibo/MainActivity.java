package cn.edu.hznu.weibo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import cn.edu.hznu.weibo.Bean.Operation;
import cn.edu.hznu.weibo.Fragment.Home.HomeFragment;
import cn.edu.hznu.weibo.Fragment.Home.IntroFragment;
import cn.edu.hznu.weibo.Fragment.Mine.MineFragment;
import cn.edu.hznu.weibo.Fragment.NoScrollViewPager;
import cn.edu.hznu.weibo.Fragment.TabFragmentPagerAdapter;
import cn.edu.hznu.weibo.Fragment.Weibo.WeiBoFragment;
import cn.edu.hznu.weibo.Utils.UI.StatusBarUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    public static final int QUERY_SUCCESS = 0;
    public static Gson gson = new Gson();
    public static Operation operation;
    private Handler handler;
    private ImageView mine;
    private ImageView home;
    private ImageView weibo;
    private NoScrollViewPager viewPager;
    private List<Fragment> list;
    private TabFragmentPagerAdapter adapter;
    private FragmentSkipInterface mFragmentSkipInterface;
    private String userInfo;


    public void setFragmentSkipInterface(FragmentSkipInterface fragmentSkipInterface) {
        mFragmentSkipInterface = fragmentSkipInterface;
    }

    /**
     * Fragment跳转
     */
    public void skipToFragment() {
        if (mFragmentSkipInterface != null) {
            mFragmentSkipInterface.gotoFragment(viewPager);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendQueryListsRequest();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case QUERY_SUCCESS:
                        operation = gson.fromJson(msg.getData().get("list").toString(), Operation.class);
                        System.out.println(operation);
                        //把Fragment添加到List集合里面
                        list = new ArrayList<>();
                        list.add(new WeiBoFragment());
                        list.add(new HomeFragment(userInfo));
                        list.add(new MineFragment(userInfo));
                        list.add(new IntroFragment(userInfo));
                        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), list);
                        adapter.notifyDataSetChanged();
                        viewPager.setAdapter(adapter);
                        viewPager.setCurrentItem(1);//显示第一个页面
                        break;
                }
                return false;
            }
        });
        InitView();
        StatusBarUtils.with(this).init();//图片沉浸式
        //设置导航栏点击事件
        mine.setOnClickListener(this);
        home.setOnClickListener(this);
        weibo.setOnClickListener(this);
//        viewPager.addOnPageChangeListener(new MyPagerChangeListener());
    }

    /**
     * 初始化控件
     */
    private void InitView() {
        Log.d(TAG, getIntent().getStringExtra("info"));
        userInfo = getIntent().getStringExtra("info");
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
                setAndroidNativeLightStatusBar(MainActivity.this, true);
                mine.setImageResource(R.drawable.mines);
                home.setImageResource(R.drawable.home);
                weibo.setImageResource(R.drawable.weibo);
                viewPager.setCurrentItem(2);
                break;
            case R.id.home:
                setAndroidNativeLightStatusBar(MainActivity.this, false);
                home.setImageResource(R.drawable.homes);
                mine.setImageResource(R.drawable.mine);
                weibo.setImageResource(R.drawable.weibo);
                viewPager.setCurrentItem(1);
                break;
            case R.id.weibo:
                setAndroidNativeLightStatusBar(MainActivity.this, true);
                weibo.setImageResource(R.drawable.weibos);
                home.setImageResource(R.drawable.home);
                mine.setImageResource(R.drawable.mine);
                viewPager.setCurrentItem(0);
                break;

        }
    }

    public void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public interface FragmentSkipInterface {
        /**
         * ViewPager中子Fragment之间跳转的实现方法
         */
        void gotoFragment(NoScrollViewPager viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    final Bitmap photo = intent.getParcelableExtra("data");
                    //给头像设置你相机拍的照片
                    MineFragment mineFragment = (MineFragment) adapter.getItem(2);
                    ImageView avatar = mineFragment.getView().findViewById(R.id.avatar);
                    avatar.setImageBitmap(photo);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    assert intent != null;
                    Uri uri = intent.getData();
                    cropPhoto(uri);//裁剪图片
                }
                break;
            case 3:
                Bundle bundle = intent.getExtras();

                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    //设置到ImageView上
                    MineFragment mineFragment = (MineFragment) adapter.getItem(2);
                    ImageView avatar = mineFragment.getView().findViewById(R.id.avatar);
                    avatar.setImageBitmap(image);
                    //也可以进行一些保存、压缩等操作后上传
                    String path = saveImage("userHeader", image);
                    File file = new File(path);
                    //可以做上传文件操作
                }
                break;
        }
    }

    private String saveImage(String name, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    private void sendQueryListsRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", "List").build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/deal")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message msg = new Message();
                    msg.what = QUERY_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putString("list", responseData);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Log.d("list", responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
