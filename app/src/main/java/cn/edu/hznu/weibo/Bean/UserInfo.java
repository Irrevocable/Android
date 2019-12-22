package cn.edu.hznu.weibo.Bean;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private int uid;
    private String nickName;
    private String img;
    private String introduce;
    private int num;

    public UserInfo() {
    }

    public UserInfo(int uid, String nickName, String img, String introduce, int num) {
        this.uid = uid;
        this.nickName = nickName;
        this.img = img;
        this.introduce = introduce;
        this.num = num;
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

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid=" + uid +
                ", nickName='" + nickName + '\'' +
                ", img='" + img + '\'' +
                ", introduce='" + introduce + '\'' +
                ", num=" + num +
                '}';
    }
}
