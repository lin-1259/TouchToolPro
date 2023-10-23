package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.databinding.FloatManualChoiceExecuteBinding;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class ManualChoiceFloatView extends FrameLayout implements FloatViewInterface {
    private final ItemSelectCallback callback;

    public ManualChoiceFloatView(@NonNull Context context, ArrayList<String> items, ItemSelectCallback callback) {
        super(context);
        this.callback = callback;

        FloatManualChoiceExecuteBinding binding = FloatManualChoiceExecuteBinding.inflate(LayoutInflater.from(context), this, true);
        ManualChoiceRecyclerViewAdapter adapter = new ManualChoiceRecyclerViewAdapter(this, items);
        binding.recyclerView.setAdapter(adapter);

        binding.closeButton.setOnClickListener(v -> {
            selectItem(-1);
            dismiss();
        });
    }

    public void selectItem(int index) {
        callback.onSelected(index);
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setGravity(FloatGravity.CENTER, 0, 0)
                .setTag(ManualChoiceFloatView.class.getName())
                .setAlwaysShow(true)
                .setAnimator(null)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ManualChoiceFloatView.class.getName());
    }

    public interface ItemSelectCallback {
        void onSelected(int index);
    }
}
