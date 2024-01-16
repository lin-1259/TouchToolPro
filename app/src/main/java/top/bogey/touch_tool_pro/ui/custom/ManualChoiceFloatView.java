package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.databinding.FloatManualChoiceExecuteBinding;
import top.bogey.touch_tool_pro.databinding.FloatManualChoiceExecuteItemBinding;
import top.bogey.touch_tool_pro.ui.picker.FloatBaseCallback;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewHelper;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class ManualChoiceFloatView extends FrameLayout implements FloatViewInterface {
    private final ItemSelectCallback callback;

    public ManualChoiceFloatView(@NonNull Context context, ArrayList<ChoiceInfo> items, ItemSelectCallback callback) {
        super(context);
        this.callback = callback;

        FloatManualChoiceExecuteBinding binding = FloatManualChoiceExecuteBinding.inflate(LayoutInflater.from(context), this, true);
        for (ChoiceInfo item : items) {
            FloatManualChoiceExecuteItemBinding itemBinding = FloatManualChoiceExecuteItemBinding.inflate(LayoutInflater.from(context), binding.flexBox, true);
            itemBinding.icon.setImageBitmap(item.image);
            itemBinding.titleText.setText(item.title);
            itemBinding.getRoot().setOnClickListener(v -> callback.onSelected(binding.flexBox.indexOfChild(v)));
        }

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
        Point position = SettingSave.getInstance().getChoiceViewPosition();
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setGravity(FloatGravity.CENTER, position.x, position.y)
                .setTag(ManualChoiceFloatView.class.getName())
                .setAlwaysShow(true)
                .setAnimator(null)
                .setCallback(new FloatCallback())
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ManualChoiceFloatView.class.getName());
    }

    public interface ItemSelectCallback {
        void onSelected(int index);
    }


    private class FloatCallback extends FloatBaseCallback {
        @Override
        public void onDragEnd() {
            FloatViewHelper helper = EasyFloat.getHelper(ManualChoiceFloatView.class.getName());
            if (helper == null) return;
            Point position = helper.getConfigPosition();
            SettingSave.getInstance().setChoiceViewPosition(new Point(position.x, position.y));
        }

        @Override
        public void onShow(String tag) {

        }

        @Override
        public void onDismiss() {

        }
    }

    public static class ChoiceInfo{
        public String title;
        public Bitmap image;

        public ChoiceInfo(String title, Bitmap image) {
            this.title = title;
            this.image = image;
        }
    }
}
