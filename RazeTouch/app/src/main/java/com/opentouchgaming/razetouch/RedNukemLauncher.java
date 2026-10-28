package com.opentouchgaming.razetouch;

import static com.opentouchgaming.androidcore.DebugLog.Level.D;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class RedNukemLauncher extends RazeBaseLauncher
{
    RedNukemLauncher()
    {
        SUB_DIR = "REDNUKEM";

        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
        Utils.mkdirs(AppInfo.getContext(), AppInfo.getUserFiles() + "/rednukem/", null);
        Utils.mkdirs(AppInfo.getContext(), AppInfo.getUserFiles() + "/rednukem/again/", null);
        Utils.mkdirs(AppInfo.getContext(), AppInfo.getUserFiles() + "/rednukem/duke64/", null);
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", "", RAZE_GAME_REDNUKEM, WEAPON_WHEEL_NBR,
                new String[]{"redneck.grp"}, R.drawable.redneck, "Redneck Rampage", "Copy REDNECK.GRP to:", "Put your REDNECK.GRP files here.txt");

        // Rides Again is standalone and also has a REDNECK.GRP, so run it from its own folder
        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".again", "again", RAZE_GAME_REDNUKEM,
                WEAPON_WHEEL_NBR, new String[]{"again/redneck.grp", "again/redint.mve"}, R.drawable.redneck, "Redneck Rampage: Rides Again",
                "Copy your Rides Again files to:", "Put your Rides Again files here.txt");
        sg.setRunFromHere(true);
        sg.setExtraArgs("-user_subdir again");

        // Duke 64 runs from its ROM, whatever the file is named
        String rom = "duke64/duke64.z64";
        for (File f : Utils.listFiles(new String[]{getRunDirectory() + "/duke64", getSecondaryDirectory() != null ? getSecondaryDirectory() + "/duke64" : null}))
        {
            String name = f.getName().toLowerCase();
            if (name.endsWith(".z64") || name.endsWith(".n64") || name.endsWith(".v64"))
            {
                rom = "duke64/" + f.getName();
                break;
            }
        }

        sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".duke64", "duke64", RAZE_GAME_DUKE64, WEAPON_WHEEL_NBR,
                new String[]{rom}, R.drawable.rednukem, "Duke Nukem 64", "Copy your Duke Nukem 64 ROM (.z64, .n64 or .v64) to:", "Put your Duke 64 ROM here.txt");
        sg.setRunFromHere(true);
        sg.setExtraArgs("-user_subdir duke64");

        addAddonsDir(engine, "", RAZE_GAME_REDNUKEM, availableSubGames, new String[]{"mods", "autoload", "again", "duke64"});

        super.updateSubGames(engine, availableSubGames);
    }
}
