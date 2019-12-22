package cn.edu.hznu.weibo.Bean;

import java.io.Serializable;

public class WeiBo implements Serializable {
    private int wid;
    private int uid;
    private String nickName;
    private String img;
    private String create_time;
    private String content;
    private String image;
    private int favors;
    private int transmit;

    public WeiBo() {
        super();
    }

    public WeiBo(int wid, int uid, String nickName, String img, String create_time, String content, String image,
                 int favors, int transmit) {
        super();
        this.wid = wid;
        this.uid = uid;
        this.nickName = nickName;
        this.img = img;
        this.create_time = create_time;
        this.content = content;
        this.image = image;
        this.favors = favors;
        this.transmit = transmit;
    }

    public int getWid() {
        return wid;
    }
    public void setWid(int wid) {
        this.wid = wid;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public String getCreate_time() {
        return create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public int getFavors() {
        return favors;
    }
    public void setFavors(int favors) {
        this.favors = favors;
    }

    public int getTransmit() {
        return transmit;
    }

    public void setTransmit(int transmit) {
        this.transmit = transmit;
    }

    @Override
    public String toString() {
        return "WeiBo{" +
                "wid=" + wid +
                ", uid=" + uid +
                ", nickName='" + nickName + '\'' +
                ", img='" + img + '\'' +
                ", create_time=" + create_time +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", favors=" + favors +
                ", transmit=" + transmit +
                '}';
    }
}