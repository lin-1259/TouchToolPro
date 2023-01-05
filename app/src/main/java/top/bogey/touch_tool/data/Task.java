package top.bogey.touch_tool.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.start.StartAction;

public class Task implements Parcelable {
    private final String id;
    private final HashSet<BaseAction> actions = new HashSet<>();

    private final long createTime;
    private String tag;

    private String title;
    private String des;

    public Task() {
        id = UUID.randomUUID().toString();
        createTime = System.currentTimeMillis();
    }

    protected Task(Parcel in) {
        id = in.readString();
        ArrayList<BaseAction> actionArrayList = new ArrayList<>();
        in.readTypedList(actionArrayList, BaseAction.CREATOR);
        actions.addAll(actionArrayList);
        createTime = in.readLong();
        tag = in.readString();
        title = in.readString();
        des = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public BaseAction getActionById(String id) {
        for (BaseAction action : actions) {
            if (action.getId().equals(id)) return action;
        }
        return null;
    }

    public StartAction getStartAction(Class<? extends StartAction> startActionClass) {
        for (BaseAction action : actions) {
            if (action.getClass().isInstance(startActionClass)) {
                return (StartAction) action;
            }
        }
        return null;
    }

    public void addAction(BaseAction action) {
        actions.add(action);
    }

    public void removeAction(BaseAction action) {
        for (BaseAction baseAction : actions) {
            if (baseAction.getId().equals(action.getId())) {
                actions.remove(baseAction);
                break;
            }
        }
    }

    public String getId() {
        return id;
    }

    public HashSet<BaseAction> getActions() {
        return actions;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(new ArrayList<>(actions));
        dest.writeLong(createTime);
        dest.writeString(tag);
        dest.writeString(title);
        dest.writeString(des);
    }
}
