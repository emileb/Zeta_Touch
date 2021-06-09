package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;

import java.io.File;
import java.util.ArrayList;

public class RedneckLauncher extends RazeBaseLauncher
{
    RedneckLauncher()
    {
        SUB_DIR = "REDNECK";
        new File(getRunDirectory()).mkdirs();
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", 0, WEAPON_WHEEL_NBR, new String[]{"redneck.grp"}, R.drawable.redneck,
                        "Redneck Rampage", "Copy REDNECK.GRP to:", "Put your REDNECK.GRP files here.txt");

        addAddonsDir(engine, availableSubGames, new String[]{""});

        super.updateSubGames(engine,availableSubGames);
    }
}
