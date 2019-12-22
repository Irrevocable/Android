package cn.edu.hznu.weibo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.edu.hznu.weibo.Bean.WeiBo;
import cn.edu.hznu.weibo.Utils.HtmlFromUtils;

public class InfoAdapter extends ArrayAdapter<WeiBo> {
    private int resourceId;

    public InfoAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<WeiBo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WeiBo weiBo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//        View view;
//        ViewHolder viewHolder;
//        TextView content = (TextView) view.findViewById(R.id.weibo_content);
//        ImageView image = (ImageView) view.findViewById(R.id.weibo_img);
//        if (convertView == null) {
//            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
//            viewHolder=new ViewHolder();
//            viewHolder.avatar=(ImageView) view.findViewById(R.id.avatarImg);
//            viewHolder.nickName=(TextView) view.findViewById(R.id.username);
//            viewHolder.create_time=(TextView)view.findViewById(R.id.time);
//            viewHolder.transmitIcon=(ImageView)view.findViewById(R.id.transmit);
//            viewHolder.commentIcon=(ImageView)view.findViewById(R.id.comment);
//            viewHolder.zanIcon=(ImageView)view.findViewById(R.id.zan);
//            viewHolder.collectIcon=(ImageView)view.findViewById(R.id.collect);
//            view.setTag(viewHolder);
//        } else {
//            view = convertView;
//            viewHolder=(ViewHolder)view.getTag();
//        }
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
        }else{
            content.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(weiBo.getImage())) {
            Glide.with(getContext()).load("http://10.0.2.2:8080/weibo/" + weiBo.getImage())
                    .apply(requestOptions)
                    .into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        return view;
    }

//        class ViewHolder {
//            TextView nickName;
//            ImageView avatar;
//            TextView create_time;
//            TextView favors;//点赞数
//            TextView transmit;//是否转发
//            ImageView transmitIcon;
//            ImageView commentIcon;
//            ImageView zanIcon;
//            ImageView collectIcon;
//        }
}
