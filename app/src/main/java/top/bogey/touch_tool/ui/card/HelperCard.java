package top.bogey.touch_tool.ui.card;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.WidgetHelperBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.ResultCallback;

public class HelperCard extends MaterialCardView {
    private final WidgetHelperBinding binding;

    private ResultCallback copyCallback;
    private ResultCallback removeCallback;
    private ResultCallback enableCallback;

    private boolean needDelete = false;

    public HelperCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        context = getContext();

        setCardElevation(DisplayUtils.dp2px(context, 5));
        setStrokeWidth(0);
        setCardBackgroundColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0));

        binding = WidgetHelperBinding.inflate(LayoutInflater.from(context), this, true);
        binding.copyButton.setOnClickListener(v -> {
            if (copyCallback != null) copyCallback.onResult(true);
        });

        binding.removeButton.setOnClickListener(v -> {
            if (needDelete) {
                binding.removeButton.setChecked(false);
                needDelete = false;
                if (removeCallback != null) removeCallback.onResult(true);
            } else {
                binding.removeButton.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    binding.removeButton.setChecked(false);
                    needDelete = false;
                }, 500);
            }
        });

        binding.title.addOnCheckedChangeListener((button, isChecked) -> {
            if (enableCallback != null) enableCallback.onResult(isChecked);
        });

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HelperCard);
        String title = array.getString(R.styleable.HelperCard_title);
        if (title == null || title.isEmpty()) binding.title.setVisibility(GONE);
        else binding.title.setText(title);
        array.recycle();
    }

    public void setCopyCallback(ResultCallback copyCallback) {
        this.copyCallback = copyCallback;
    }

    public void setRemoveCallback(ResultCallback removeCallback) {
        this.removeCallback = removeCallback;
    }

    public void setEnableCallback(ResultCallback enableCallback) {
        this.enableCallback = enableCallback;
    }

    public void setEnable(boolean enable) {
        binding.title.setChecked(enable);
    }

    @Override
    public void addView(View child) {
        if (binding == null) super.addView(child);
        else binding.subCardBox.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (binding == null) super.addView(child, index);
        else binding.subCardBox.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (binding == null) super.addView(child, width, height);
        else binding.subCardBox.addView(child, width, height);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (binding == null) super.addView(child, params);
        else binding.subCardBox.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (binding == null) super.addView(child, index, params);
        else binding.subCardBox.addView(child, index, params);
    }
}
