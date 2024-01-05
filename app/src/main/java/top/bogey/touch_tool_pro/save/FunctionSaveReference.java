package top.bogey.touch_tool_pro.save;

import com.tencent.mmkv.MMKV;

import java.util.HashSet;

import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class FunctionSaveReference extends SaveReference<Function> {
    private final HashSet<String> tags = new HashSet<>();
    private FunctionPinsAction action;

    public FunctionSaveReference(MMKV mmkv, String saveId) {
        super(mmkv, saveId);
    }

    public FunctionSaveReference(MMKV mmkv, Function save) {
        super(mmkv, save.getId(), save);
    }

    @Override
    public Function getOrigin() {
        try {
            return (Function) GsonUtils.getAsObject(mmkv.decodeString(saveId), FunctionContext.class, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void set(Function save) {
        super.set(save);
        tags.clear();
        if (save.getTags() != null) {
            tags.addAll(save.getTags());
        }
        action = save.getAction();
    }

    public boolean existTag(String tag) {
        getTags();
        if (tags.contains(tag)) return true;
        if (tag == null || tag.isEmpty() || SaveRepository.NO_TAG.equals(tag)) {
            return tags.isEmpty();
        }
        return false;
    }

    public HashSet<String> getTags() {
        if (tags.isEmpty()) {
            Function function = get();
            if (function == null) return tags;
            if (function.getTags() != null) {
                tags.addAll(function.getTags());
            }
        }
        return tags;
    }

    public FunctionPinsAction getAction() {
        if (action == null) {
            Function function = get();
            action = function.getAction();
        }
        return action;
    }
}
