package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;

import java.io.File;
import java.util.ArrayList;

public class PowerslaveLauncher extends RazeBaseLauncher
{
    PowerslaveLauncher()
    {
        SUB_DIR = "PS";
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        new File(getRunDirectory()).mkdirs();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, "", 0, WEAPON_WHEEL_NBR, new String[]{"STUFF.DAT"}, R.drawable.quake1,
                        "Powerslave", "Copy STUFF.DAT to:", "Put your Powerslave files here.txt");
    }
}
