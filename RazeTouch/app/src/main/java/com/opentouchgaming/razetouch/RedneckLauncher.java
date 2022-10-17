package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class RedneckLauncher extends RazeBaseLauncher
{
    RedneckLauncher()
    {
        SUB_DIR = "REDNECK";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", RAZE_GAME_RR, WEAPON_WHEEL_NBR,
                new String[]{"redneck.grp"}, R.drawable.redneck, "Redneck Rampage", "Copy REDNECK.GRP to:", "Put your REDNECK.GRP files here.txt");


        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, "addons/AGAIN", 0, WEAPON_WHEEL_NBR,
                new String[]{"addons/again/redneck.grp", "addons/again/redint.mve"}, R.drawable.raze, "Redneck Rampage: Rides Again",
                "Copy your Rides Again files to:", "Put your Rides Again files here.txt");
        sg.setExtraArgs(" -game_dir " + sg.getRootPath() + "/addons/AGAIN");
        // Rides again basically includes all the files and is standalone, need to set this otherwise it will load normal Redneck
        sg.setRunFromHere(true);

        // sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, "addons/ROUTE66", 0, WEAPON_WHEEL_NBR, new
        // String[]{"addons/route66/TILES024.ART", "addons/route66/TILES052.ART"}, R.drawable.raze,
        //        "Redneck Rampage: Route 66", "Copy your Route 66 files to:", "Put your Route 66 files here.txt");
        //sg.setExtraArgs(" -game_dir " + sg.getRootPath() + "/addons/ROUTE66" +  " -route66");

        addAddonsDir(engine, RAZE_GAME_RR, availableSubGames, new String[]{"again"});

        super.updateSubGames(engine, availableSubGames);
    }
}
