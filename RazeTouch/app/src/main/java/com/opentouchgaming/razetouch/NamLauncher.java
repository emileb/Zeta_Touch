package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;

import java.io.File;
import java.util.ArrayList;

public class NamLauncher extends RazeBaseLauncher
{
    NamLauncher()
    {
        SUB_DIR = "NAM";
        new File(getRunDirectory()).mkdirs();
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, "", 0, WEAPON_WHEEL_NBR, new String[]{"NAM.grp","GAME.CON"}, R.drawable.raze,
                        "NAM", "Copy NAM.GRP and GAME.CON to:", "Put your NAM files here.txt");
    }
}
