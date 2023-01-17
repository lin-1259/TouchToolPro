package top.bogey.touch_tool.ui.task_blueprint;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.sidesheet.SideSheetDialog;

import top.bogey.touch_tool.databinding.ViewCardListBinding;

public class ActionSideSheetDialog extends SideSheetDialog {

    public ActionSideSheetDialog(@NonNull Context context, CardLayoutView cardLayoutView) {
        super(context);
        ViewCardListBinding binding = ViewCardListBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

        ActionTreeAdapter adapter = new ActionTreeAdapter(cardLayoutView, new TreeNodeManager());
        binding.actionBox.setAdapter(adapter);
    }
}
