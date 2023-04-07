package top.bogey.touch_tool;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.View;

import top.bogey.touch_tool.ui.BaseActivity;
import top.bogey.touch_tool.ui.picker.PackagePickerFloatPreview;
import top.bogey.touch_tool.ui.setting.SettingView;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

public class PackageInfoTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) {
            BaseActivity activity = MainApplication.getInstance().getActivity();
            if (activity != null) {
                View view = EasyFloat.getView(PackagePickerFloatPreview.class.getName());
                if (view == null) {
                    new PackagePickerFloatPreview(activity).show();
                } else {
                    EasyFloat.dismiss(PackagePickerFloatPreview.class.getName());
                }
            }
        }
        updateTile();
        SettingView.resetSwitchState();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    private void updateTile() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        Tile tile = getQsTile();
        if (service != null && service.isServiceEnabled()) {
            View view = EasyFloat.getView(PackagePickerFloatPreview.class.getName());
            tile.setState(view != null ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        } else tile.setState(Tile.STATE_UNAVAILABLE);
        tile.updateTile();
    }
}
