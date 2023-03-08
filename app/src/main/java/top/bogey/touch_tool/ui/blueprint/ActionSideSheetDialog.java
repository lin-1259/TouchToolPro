package top.bogey.touch_tool.ui.blueprint;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.sidesheet.SideSheetDialog;

import top.bogey.touch_tool.databinding.ViewCardListBinding;

public class ActionSideSheetDialog extends SideSheetDialog {

    public ActionSideSheetDialog(@NonNull Context context, RecyclerView.Adapter<?> adapter) {
        super(context);
        ViewCardListBinding binding = ViewCardListBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
        binding.actionBox.setAdapter(adapter);
    }
}
