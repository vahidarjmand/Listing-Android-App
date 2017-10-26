package tmediaa.ir.ahamdian.model;

import android.support.annotation.Nullable;

/**
 * Created by tmediaa on 9/20/2017.
 */

public class OrderItem {
    private int id;
    private String title;
    private String desc;
    private String url;
    private String date;

    public OrderItem() {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.date = date;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
