package top.bogey.touch_tool_pro.service;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.ui.setting.SettingView;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class PlayFloatViewTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        int type = SettingSave.getInstance().getPlayViewVisibleType();
        Tile tile = getQsTile();
        tile.setState(type == 0 ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        String title = switch (type) {
            case 1 -> getString(R.string.task_setting_play_view_visible_after_unlock);
            case 2 -> getString(R.string.task_setting_play_view_visible_all_time);
            default -> getString(R.string.task_setting_play_view_visible_none);
        };
        tile.setLabel(title);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        int type = SettingSave.getInstance().getPlayViewVisibleType();
        type = (type + 1) % 3;
        SettingSave.getInstance().setPlayViewVisibleType(type);
        Tile tile = getQsTile();
        tile.setState(type == 0 ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        String title = switch (type) {
            case 1 -> getString(R.string.task_setting_play_view_visible_after_unlock);
            case 2 -> getString(R.string.task_setting_play_view_visible_all_time);
            default -> getString(R.string.task_setting_play_view_visible_none);
        };
        tile.setLabel(title);
        tile.updateTile();

        SettingView.resetSwitchState();
        WorldState.getInstance().showManualActionDialog(SettingSave.getInstance().isPlayViewVisible(this));
    }
}
