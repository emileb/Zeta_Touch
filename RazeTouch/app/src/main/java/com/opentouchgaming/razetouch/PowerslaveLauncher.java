package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class PowerslaveLauncher extends RazeBaseLauncher
{
    PowerslaveLauncher()
    {
        SUB_DIR = "PS";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", RAZE_GAME_PS, WEAPON_WHEEL_NBR,
                new String[]{"STUFF.DAT"}, R.drawable.ps, "Powerslave", "Copy STUFF.DAT to:", "Put your Powerslave files here.txt");

        addAddonsDir(engine, RAZE_GAME_PS, availableSubGames, new String[]{""});

        super.updateSubGames(engine, availableSubGames);
    }
}
