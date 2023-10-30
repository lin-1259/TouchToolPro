package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.databinding.PinWidgetAppBinding;
import top.bogey.touch_tool_pro.databinding.PinWidgetAppItemBinding;
import top.bogey.touch_tool_pro.ui.app.AppView;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinCustomView;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetApp extends PinWidget<PinApplication> {
    private final PinWidgetAppBinding binding;

    public PinWidgetApp(@NonNull Context context, ActionCard<?> card, PinView pinView, PinApplication pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetAppBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.selectAppButton.setOnClickListener(v -> new AppView(pinObject.getApps(), pinObject.getSubType(), result -> refreshApps()).show(((AppCompatActivity) context).getSupportFragmentManager(), null));
        refreshApps();
    }

    @Override
    public void initCustom() {
        binding.spinner.setVisibility(VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        adapter.addAll(context.getResources().getStringArray(R.array.select_app_type));
        binding.spinner.setAdapter(adapter);
        binding.spinner.setSelection(subTypeToIndex(pinObject.getSubType()));
        binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == subTypeToIndex(pinObject.getSubType())) return;
                Pin functionPin = ((PinCustomView) pinView).getFunctionPin();
                functionPin.cleanLinks(card.getFunctionContext());
                functionPin.setValue(new PinApplication(indexToSubType(position)));
                pinView.refreshPinView();
            }
        });
    }

    private int subTypeToIndex(PinSubType subType) {
        return switch (subType) {
            case SINGLE_ACTIVITY -> 1;
            case SINGLE_ALL_ACTIVITY -> 2;
            case MULTI -> 3;
            case MULTI_ACTIVITY -> 4;
            case MULTI_ALL_ACTIVITY -> 5;
            case SHARE_ACTIVITY -> 6;
            default -> 0;
        };
    }

    private PinSubType indexToSubType(int index) {
        return switch (index) {
            case 1 -> PinSubType.SINGLE_ACTIVITY;
            case 2 -> PinSubType.SINGLE_ALL_ACTIVITY;
            case 3 -> PinSubType.MULTI;
            case 4 -> PinSubType.MULTI_ACTIVITY;
            case 5 -> PinSubType.MULTI_ALL_ACTIVITY;
            case 6 -> PinSubType.SHARE_ACTIVITY;
            default -> PinSubType.SINGLE;
        };
    }

    private void refreshApps() {
        binding.iconBox.removeAllViews();

        LinkedHashMap<String, ArrayList<String>> apps = pinObject.getApps();
        Set<String> packages = apps.keySet();
        if (packages.isEmpty()) return;

        PackageManager manager = context.getPackageManager();
        String commonPackage = context.getString(R.string.common_package_name);
        boolean includeCommon = packages.contains(commonPackage);

        int count = 0;
        if (includeCommon) {
            Drawable drawable = context.getApplicationInfo().loadIcon(manager);
            PinWidgetAppItemBinding itemBinding = PinWidgetAppItemBinding.inflate(LayoutInflater.from(context), binding.iconBox, true);
            itemBinding.icon.setImageDrawable(drawable);
            itemBinding.numberBox.setVisibility(GONE);
            count++;

            if (packages.size() == 1) return;
        }

        for (String packageName : packages) {
            if (packageName.equals(commonPackage)) continue;
            PinWidgetAppItemBinding itemBinding = PinWidgetAppItemBinding.inflate(LayoutInflater.from(context), binding.iconBox, true);
            itemBinding.exclude.setVisibility(includeCommon ? VISIBLE : GONE);
            if (packages.size() > 5 && count == 4) {
                itemBinding.icon.setImageResource(R.drawable.icon_more);
                itemBinding.icon.setImageTintList(ColorStateList.valueOf(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0)));
                itemBinding.numberText.setText(String.valueOf(packages.size() - 4));
                break;
            } else {
                ArrayList<String> activities = apps.get(packageName);
                if (activities == null) continue;
                PackageInfo packageInfo = WorldState.getInstance().getPackage(packageName);
                if (packageInfo == null) continue;
                itemBinding.icon.setImageDrawable(packageInfo.applicationInfo.loadIcon(manager));
                itemBinding.numberBox.setVisibility(activities.isEmpty() ? GONE : VISIBLE);
                itemBinding.numberText.setText(String.valueOf(activities.size()));
            }
            count++;
        }
    }
}
