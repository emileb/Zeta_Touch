package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;

import java.io.File;
import java.util.ArrayList;

public class SwLauncher extends RazeBaseLauncher
{
    SwLauncher()
    {
        SUB_DIR = "SW";
        new File(getRunDirectory()).mkdirs();
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, "", 0, WEAPON_WHEEL_NBR, new String[]{"sw.grp"}, R.drawable.raze,
                        "Shadow Warrior", "Copy SW.GRP to:", "Put your SW.GRP files here.txt");
    }
}
