package cn.edu.hznu.weibo.Bean;

import java.io.Serializable;
import java.util.Arrays;

public class Operation implements Serializable {
    private int[] favors;
    private int[] collects;

    public Operation() {
    }

    public Operation(int[] favors, int[] collects) {
        this.favors = favors;
        this.collects = collects;
    }

    public int[] getFavors() {
        return favors;
    }

    public void setFavors(int[] favors) {
        this.favors = favors;
    }

    public int[] getCollects() {
        return collects;
    }

    public void setCollects(int[] collects) {
        this.collects = collects;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "favors=" + Arrays.toString(favors) +
                ", collects=" + Arrays.toString(collects) +
                '}';
    }
}
