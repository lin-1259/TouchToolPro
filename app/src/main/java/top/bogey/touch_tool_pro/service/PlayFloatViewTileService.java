package top.bogey.touch_tool_pro.service;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import top.bogey.touch_tool_pro.ui.setting.SettingView;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class PlayFloatViewTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        boolean visible = SettingSave.getInstance().isPlayViewVisible();
        Tile tile = getQsTile();
        tile.setState(visible ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        boolean visible = !SettingSave.getInstance().isPlayViewVisible();
        SettingSave.getInstance().setPlayViewVisible(visible);
        Tile tile = getQsTile();
        tile.setState(visible ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();

        SettingView.resetSwitchState();
        WorldState.getInstance().showManualActionDialog(visible);
    }
}
