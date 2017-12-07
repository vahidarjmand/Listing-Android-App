package tmediaa.ir.ahamdian.model;

import com.google.gson.JsonArray;

/**
 * Created by tmediaa on 9/20/2017.
 */

public class OrderItem {
    private int id;
    private String title;
    private String desc;
    private String url;
    private String date;
    private String cat_name;
    private JsonArray attachments;
    private boolean own_mode;
    private String status;

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

    public JsonArray getAttachments() {
        return attachments;
    }

    public void setAttachments(JsonArray attachments) {
        this.attachments = attachments;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public boolean isOwn_mode() {
        return own_mode;
    }

    public void setOwn_mode(boolean own_mode) {
        this.own_mode = own_mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
