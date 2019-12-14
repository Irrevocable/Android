package cn.edu.hznu.weibo.Bean;

public class Info {
    private String name;
    private int avatarId;
    private String time;
    private String content;
    private int imgId;

    public Info() {
    }

    public Info(String name, int avatarId, String time, String content) {
        this.name = name;
        this.avatarId = avatarId;
        this.time = time;
        this.content = content;
    }

    public Info(String name, int avatarId, String time, String content, int imgId) {
        this.name = name;
        this.avatarId = avatarId;
        this.time = time;
        this.content = content;
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    @Override
    public String toString() {
        return "Info{" +
                "name='" + name + '\'' +
                ", avatarId=" + avatarId +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                ", imgId=" + imgId +
                '}';
    }
}
