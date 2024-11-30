package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;

import java.io.File;
import java.util.ArrayList;

public class AMCLauncher extends RazeBaseLauncher
{
    AMCLauncher()
    {
        SUB_DIR = "AMC";
        new File(getRunDirectory()).mkdirs();
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", "", RAZE_GAME_AMC, 4,
                new String[]{"amcart.grp"}, R.drawable.amctc, "AMC", "Go to www.opentouchgaming.com to download the AMC data files",
                "Put your AMC files here.txt");

        super.updateSubGames(engine, availableSubGames);
    }
}
