package top.bogey.touch_tool_pro.save;

import com.tencent.mmkv.MMKV;

import top.bogey.touch_tool_pro.utils.GsonUtils;

public abstract class SaveReference<T> {
    protected final MMKV mmkv;
    protected final String saveId;

    private T save;
    private long lastUseTime;

    public SaveReference(MMKV mmkv, String saveId) {
        this.mmkv = mmkv;
        this.saveId = saveId;
    }

    public SaveReference(MMKV mmkv, String saveId, T save) {
        this.mmkv = mmkv;
        this.saveId = saveId;
        this.save = save;
        mmkv.encode(saveId, GsonUtils.toJson(save));
    }

    public T get() {
        if (save == null) {
            save = getOrigin();
        }
        lastUseTime = System.currentTimeMillis();
        return save;
    }

    public abstract T getOrigin();

    public void set(T save) {
        this.save = save;
        mmkv.encode(saveId, GsonUtils.toJson(save));
    }

    public void remove() {
        mmkv.remove(saveId);
    }

    public void check() {
        long current = System.currentTimeMillis();
        if (current - lastUseTime > 10 * 60 * 1000) {
            save = null;
        }
    }

}
