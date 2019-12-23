package cn.edu.hznu.weibo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import cn.edu.hznu.weibo.Bean.Operation;
import cn.edu.hznu.weibo.Bean.WeiBo;
import cn.edu.hznu.weibo.Utils.HtmlFromUtils;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoAdapter extends ArrayAdapter<WeiBo> {
    public static final int SUCCESS=0;
    public static Gson gson=new Gson();
    private int resourceId;
    private Operation operation;
    private Handler handler;
    public InfoAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<WeiBo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        operation = MainActivity.operation;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WeiBo weiBo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ImageView avatar = (ImageView) view.findViewById(R.id.avatarImg);
        TextView nickName = (TextView) view.findViewById(R.id.username);
        TextView create_time = (TextView) view.findViewById(R.id.time);
        TextView content = (TextView) view.findViewById(R.id.weibo_content);
        ImageView image = (ImageView) view.findViewById(R.id.weibo_img);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.loading).override(1000, 500).fitCenter();
        if (!TextUtils.isEmpty(weiBo.getImg())) {
            Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/" + weiBo.getImg())
                    .apply(requestOptions)
                    .into(avatar);
        } else {
            avatar.setVisibility(View.GONE);
        }
        nickName.setText(weiBo.getNickName());
        create_time.setText(weiBo.getCreate_time());
        //富文本处理
        if (!TextUtils.isEmpty(weiBo.getContent())) {
            HtmlFromUtils.setTextFromHtml((Activity) getContext(), content, weiBo.getContent());
        } else {
            content.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(weiBo.getImage())) {
            Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/" + weiBo.getImage())
                    .apply(requestOptions)
                    .into(image);
        } else {
            image.setVisibility(View.GONE);
        }

        ImageView transmitIcon = (ImageView) view.findViewById(R.id.transmit);
        transmitIcon.setOnClickListener(v -> {
            System.out.println(weiBo.getWid());
        });
        ImageView commentIcon = (ImageView) view.findViewById(R.id.comment);
        commentIcon.setOnClickListener(v -> {
            System.out.println("comment" + weiBo.getUid());
        });
        ImageView zanIcon = (ImageView) view.findViewById(R.id.zan);
        TextView favors = (TextView) view.findViewById(R.id.favorNum);
        if (Arrays.binarySearch(operation.getFavors(), weiBo.getWid()) >= 0) {
            zanIcon.setImageResource(R.drawable.zans);
        }
        if (weiBo.getFavors() == 0) {
            favors.setText("赞");
        } else {
            favors.setText(String.valueOf(weiBo.getFavors()));
        }
        zanIcon.setOnClickListener(v -> {
            if (zanIcon.getDrawable() == null) {
                favorOrCollectRequest("favors",String.valueOf(weiBo.getWid()));
                zanIcon.setImageResource(R.drawable.zans);
                favors.setText(String.valueOf(weiBo.getFavors()+1));
                weiBo.setFavors(weiBo.getFavors()+1);
            } else {
                if (zanIcon.getDrawable().getCurrent().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.zan).getConstantState())) {
                    favorOrCollectRequest("favors",String.valueOf(weiBo.getWid()));
                    zanIcon.setImageResource(R.drawable.zans);
                    favors.setText(String.valueOf(weiBo.getFavors()+1));
                    weiBo.setFavors(weiBo.getFavors()+1);
                } else {
                    favorOrCollectRequest("favor",String.valueOf(weiBo.getWid()));
                    zanIcon.setImageResource(R.drawable.zan);
                    if(weiBo.getFavors()-1==0){
                        favors.setText("赞");
                        weiBo.setFavors(weiBo.getFavors()-1);
                    }else{
                        favors.setText(String.valueOf(weiBo.getFavors()-1));
                        weiBo.setFavors(weiBo.getFavors()-1);
                    }
                }
            }
        });

        ImageView collectIcon = (ImageView) view.findViewById(R.id.collect);
        if (Arrays.binarySearch(operation.getCollects(), weiBo.getWid()) >= 0) {
            collectIcon.setImageResource(R.drawable.collects);
        }
        collectIcon.setOnClickListener(v -> {
            if (collectIcon.getDrawable() == null) {
                favorOrCollectRequest("collects",String.valueOf(weiBo.getWid()));
                collectIcon.setImageResource(R.drawable.collects);
            } else {
                if (collectIcon.getDrawable().getCurrent().getConstantState().equals(ContextCompat.getDrawable(getContext(), R.drawable.collect).getConstantState())) {
                    favorOrCollectRequest("collects",String.valueOf(weiBo.getWid()));
                    collectIcon.setImageResource(R.drawable.collects);
                } else {
                    favorOrCollectRequest("collect",String.valueOf(weiBo.getWid()));
                    collectIcon.setImageResource(R.drawable.collect);
                }
            }
        });
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case SUCCESS:
                        MainActivity.operation=gson.fromJson(msg.getData().get("list").toString(), Operation.class);
                        Log.d("operation", operation.toString());
                        if (Arrays.binarySearch(operation.getFavors(), weiBo.getWid()) >= 0) {
                            zanIcon.setImageResource(R.drawable.zans);
                        }
                        if (Arrays.binarySearch(operation.getCollects(), weiBo.getWid()) >= 0) {
                            collectIcon.setImageResource(R.drawable.collects);
                        }
                        break;
                }
                return  false;
            }
        });
        return view;
    }

    private void favorOrCollectRequest(String oper,String wid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder().add("oper", oper)
                            .add("wid",wid).build();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2:8080/weibo/deal")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message msg=new Message();
                    msg.what=SUCCESS;
                    Bundle bundle=new Bundle();
                    bundle.putString("list",responseData);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Log.d("oper", responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
