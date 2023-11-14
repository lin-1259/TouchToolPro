package top.bogey.touch_tool_pro.bean.base;

import com.google.gson.JsonObject;

import java.util.UUID;

import top.bogey.touch_tool_pro.utils.GsonUtils;

public abstract class IdentityInfo {
    protected String title;
    protected String description;
    private String uid;
    private String id;

    public IdentityInfo() {
        uid = UUID.randomUUID().toString();
        id = UUID.randomUUID().toString();
    }

    public IdentityInfo(JsonObject jsonObject) {
        uid = GsonUtils.getAsString(jsonObject, "uid", UUID.randomUUID().toString());
        id = GsonUtils.getAsString(jsonObject, "id", UUID.randomUUID().toString());
        title = GsonUtils.getAsString(jsonObject, "title", null);
        description = GsonUtils.getAsString(jsonObject, "description", null);
    }

    public abstract IdentityInfo copy();

    public abstract void newInfo();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullDescription() {
        if (getDescription() == null) return getTitle();
        return getTitle() + " - " + getDescription();
    }

    public String getValidDescription() {
        if (getDescription() == null) return getTitle();
        return getDescription();
    }
}
