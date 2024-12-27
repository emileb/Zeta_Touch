package com.opentouchgaming.razetouch;

import static com.opentouchgaming.androidcore.DebugLog.Level.D;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class NBloodLauncher extends RazeBaseLauncher
{
    NBloodLauncher()
    {
        SUB_DIR = "NBLOOD";
        //SUB_DIR_TC = "AMCTC";

        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
        Utils.mkdirs(AppInfo.getContext(), AppInfo.getUserFiles() + "/nblood/", null);
    }

    @Override
    public String getRunDirectory(SubGame subGame)
    {
        if (subGame.isRunFromHere()) // Check if we are chaning the run directory
        {
            log.log(D, "Running from: " + subGame.getFullPath());
            return subGame.getFullPath();
        }
        else
        {
            return getRunDirectory();
        }
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", "", RAZE_GAME_NBLOOD, WEAPON_WHEEL_NBR,
                new String[]{"BLOOD.RFF"}, R.drawable.nblood, "BLOOD", "Copy BLOOD files to:", "Put your BLOOD files here.txt");

        addAddonsDir(engine, "", RAZE_GAME_EDUKE32, availableSubGames, new String[]{"mods", "autoload"});

        super.updateSubGames(engine, availableSubGames);
    }

}
