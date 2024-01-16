package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.databinding.PinWidgetExecuteBinding;
import top.bogey.touch_tool_pro.service.WorldState;
import top.bogey.touch_tool_pro.ui.app.AppView;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinWidgetExecute extends PinWidget<PinExecute> {
    private final PinWidgetExecuteBinding binding;

    public PinWidgetExecute(@NonNull Context context, ActionCard<?> card, PinView pinView, PinExecute pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetExecuteBinding.inflate(LayoutInflater.from(context), this, true);

        init();
    }

    @Override
    public void initBase() {
        switch (pinObject.getSubType()) {
            case NORMAL -> pinView.getPinViewBox().setVisibility(GONE);
            case ICON -> {
                pinView.getPinViewBox().setVisibility(VISIBLE);
                Bitmap image = pinObject.getImage();
                if (image != null) {
                    binding.icon.setImageBitmap(image);
                    binding.icon.setImageTintList(ColorStateList.valueOf(0));
                }
                HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
                binding.icon.setOnClickListener(v -> new AppView(hashMap, PinSubType.SINGLE, result -> {
                    if (hashMap.isEmpty()) {
                        binding.icon.setImageResource(R.drawable.icon_image);
                        binding.icon.setImageTintList(ColorStateList.valueOf(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0)));
                    } else {
                        for (String packageName : hashMap.keySet()) {
                            PackageInfo info = WorldState.getInstance().getPackage(packageName);
                            Drawable icon = info.applicationInfo.loadIcon(context.getPackageManager());
                            binding.icon.setImageDrawable(icon);
                            binding.icon.setImageTintList(ColorStateList.valueOf(0));
                            pinObject.setImage(icon);
                            break;
                        }
                    }
                }).show(((AppCompatActivity) context).getSupportFragmentManager(), null));
            }
        }
    }

    @Override
    public void initCustom() {

    }
}
