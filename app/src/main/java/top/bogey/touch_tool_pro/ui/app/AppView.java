package top.bogey.touch_tool_pro.ui.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.databinding.ViewAppBinding;
import top.bogey.touch_tool_pro.utils.ResultCallback;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

public class AppView extends BottomSheetDialogFragment {
    private final HashMap<String, ArrayList<String>> packages;
    private final PinSubType mode;
    private ResultCallback callback;

    private CharSequence searchText = "";
    private boolean showSystem = false;

    public AppView(HashMap<String, ArrayList<String>> packages, PinSubType mode, ResultCallback callback) {
        this.packages = packages;
        this.mode = mode;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewAppBinding binding = ViewAppBinding.inflate(inflater, container, false);
        boolean single = mode == PinSubType.SINGLE || mode == PinSubType.SINGLE_ACTIVITY || mode == PinSubType.SINGLE_ALL_ACTIVITY;
        boolean all = mode == PinSubType.SINGLE_ALL_ACTIVITY || mode == PinSubType.MULTI_ALL_ACTIVITY;
        boolean withActivity = mode != PinSubType.SINGLE && mode != PinSubType.MULTI;
        AppRecyclerViewAdapter adapter = new AppRecyclerViewAdapter(packages, result -> {
            if (single) {
                if (callback != null) {
                    callback.onResult(result);
                    callback = null;
                    dismiss();
                }
            }
        }, single, all, withActivity);
        binding.appIconBox.setAdapter(adapter);
        adapter.refreshApps(WorldState.getInstance().findPackageList(requireContext(), showSystem, searchText, single));

        binding.exchangeButton.setOnClickListener(v -> {
            showSystem = !showSystem;
            adapter.refreshApps(WorldState.getInstance().findPackageList(requireContext(), showSystem, searchText, single));
        });

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                searchText = s;
                adapter.refreshApps(WorldState.getInstance().findPackageList(requireContext(), showSystem, searchText, single));
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (callback != null) callback.onResult(true);
    }
}
