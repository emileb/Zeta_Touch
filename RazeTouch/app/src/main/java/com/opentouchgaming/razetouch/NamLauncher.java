package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class NamLauncher extends RazeBaseLauncher
{
    NamLauncher()
    {
        SUB_DIR = "NAM";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, ".", RAZE_GAME_NAM, WEAPON_WHEEL_NBR, new String[]{"NAM.grp","GAME.CON"}, R.drawable.nam,
                        "NAM", "Copy NAM.GRP and GAME.CON to:", "Put your NAM files here.txt");

        addAddonsDir(engine, RAZE_GAME_NAM, availableSubGames, new String[]{""});

        super.updateSubGames(engine,availableSubGames);
    }
}
