package cn.edu.hznu.weibo.Fragment.Weibo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.edu.hznu.weibo.Bean.WeiBo;
import cn.edu.hznu.weibo.Fragment.BaseFragment;
import cn.edu.hznu.weibo.InfoAdapter;
import cn.edu.hznu.weibo.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WeiBoFragment extends BaseFragment {
    public static  final int QUERY_SUCCESS=0;
    private static final String TAG="weibo";
    private List<WeiBo> weiBoList =new ArrayList<>();
    private ListView  listView;
    private Handler handler;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case QUERY_SUCCESS:
                        //查询数据库所有微博数据然后加载进去
                        Gson gson=new Gson();
                        List<WeiBo> weiBoList=gson.fromJson(msg.getData().get("message").toString(),new TypeToken<List<WeiBo>>(){}.getType());
                        InfoAdapter adapter = new InfoAdapter(getContext(), R.layout.list_item, weiBoList);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
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
         listView=(ListView) super.findViewById(R.id.weibo_listView);
//        发送数据查询请求
        selectAllWeiBoRequest();
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
                    Log.d("weiboList", responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
