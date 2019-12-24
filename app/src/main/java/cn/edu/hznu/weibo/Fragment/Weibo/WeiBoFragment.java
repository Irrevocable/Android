package cn.edu.hznu.weibo.Fragment.Weibo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.edu.hznu.weibo.Bean.WeiBo;
import cn.edu.hznu.weibo.EditorActivity;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.Fragment.Home.HomeFragment;
import cn.edu.hznu.weibo.InfoAdapter;
import cn.edu.hznu.weibo.QueryActivity;
import cn.edu.hznu.weibo.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WeiBoFragment extends BaseFragment {
    public static final int QUERY_SUCCESS = 0;
    public static Gson gson = new Gson();
    private static final String TAG = "weibo";
    private List<WeiBo> weiBoList = new ArrayList<>();//全部
    private List<WeiBo> weiBos=new ArrayList<>();//部分
    private ListView listView;
    private SwipeRefreshLayout srfl;
    private Handler handler;
    private InfoAdapter adapter;
    private int total;
    private int index;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        index=0;
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case QUERY_SUCCESS:
                        //查询数据库所有微博数据然后加载进去
                        weiBoList = gson.fromJson(msg.getData().get("message").toString(), new TypeToken<List<WeiBo>>() {
                        }.getType());
                        total=weiBoList.size();
                        weiBos.clear();
                        //先查6个然后再根据滚动来
                        if(weiBoList.size()!=0){
                            for(int i=0;i<6;i++){
                                index=i;
                                weiBos.add(weiBoList.get(i));
                            }
                        }
                        srfl.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        break;
                    case -1:
                        selectAllWeiBoRequest();
//                        Collections.shuffle(weiBos);
//                        adapter.notifyDataSetChanged();
//                        srfl.setRefreshing(false);
                }
                return false;
            }
        });
    }

    @Override
    protected int setContentView() {
        return R.layout.weibo_layout;
    }

    @Override
    protected void lazyLoad() {
        selectAllWeiBoRequest();
        srfl=(SwipeRefreshLayout)super.findViewById(R.id.srfl);
        srfl.setSize(SwipeRefreshLayout.DEFAULT);
        //设置手势滑动监听器
        srfl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //发送一个延时1秒的handler信息
                handler.sendEmptyMessageDelayed(-1,1000);
            }
        });
        listView = (ListView) super.findViewById(R.id.weibo_listView);
        adapter = new InfoAdapter(getContext(), R.layout.list_item, weiBos);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    //当不滚动时候
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //判断是否是最底部
                        if(view.getLastVisiblePosition()==(view.getCount())-1){
                            for (int i=1;i<4;i++){
                                System.out.println(index);
                                if(index==total-1){
                                    break;
                                }else{
                                    index+=1;
                                    weiBos.add(weiBoList.get(index));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        final ImageView write = (ImageView) super.findViewById(R.id.weibo_write);
        write.setOnClickListener(v -> {
            showPopupMenu(write);
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            WeiBo weiBo = weiBoList.get(position);

        });
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.weibo_query:
                        intent = new Intent(getActivity(), QueryActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.weibo_new:
                        intent = new Intent(getActivity(), EditorActivity.class);
                        intent.putExtra("name", HomeFragment.userInfo.getNickName());
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
//                Toast.makeText(getContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }

    private void selectAllWeiBoRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", "queryAll").build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/list")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    message.what = QUERY_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putString("message", responseData);
                    message.setData(bundle);
                    handler.sendMessage(message);
//                    Log.d("weiboList", responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
