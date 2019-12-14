package cn.edu.hznu.weibo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.edu.hznu.weibo.Bean.Info;

public class InfoAdapter extends ArrayAdapter<Info> {
    private int resourceId;

    public InfoAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<Info> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Info info = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder=new ViewHolder();
            viewHolder.avatarImg=(ImageView) view.findViewById(R.id.avatarImg);
            viewHolder.name=(TextView) view.findViewById(R.id.username);
            viewHolder.time=(TextView)view.findViewById(R.id.time);
            viewHolder.content=(TextView)view.findViewById(R.id.weibo_content);
            viewHolder.img=(ImageView)view.findViewById(R.id.weibo_img);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.avatarImg.setImageResource(info.getAvatarId());
        viewHolder.name.setText(info.getName());
        viewHolder.time.setText(info.getTime());
        viewHolder.content.setText(info.getContent());
        viewHolder.img.setImageResource(info.getImgId());
        return view;
    }
    class ViewHolder{
        ImageView avatarImg;
        TextView name;
        TextView time;
        TextView content;
        ImageView img;
    }
}
