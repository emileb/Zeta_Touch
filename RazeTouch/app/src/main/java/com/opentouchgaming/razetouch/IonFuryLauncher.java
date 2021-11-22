package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class IonFuryLauncher extends RazeBaseLauncher
{
    IonFuryLauncher()
    {
        SUB_DIR = "IONFURY";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", RAZE_GAME_IONFURY, WEAPON_WHEEL_NBR, new String[]{"fury.grp","fury.grpinfo","fury.def"}, R.drawable.dn3d, "Ion Fury",
                        "Copy fury.grp, fury.grpinfo, fury.def to:", "Put your Ion Fury files here.txt");

        sg.setExtraArgs("-grp fury.grp -h fury.def");

        addAddonsDir(engine, RAZE_GAME_IONFURY, availableSubGames, new String[]{});

        super.updateSubGames(engine, availableSubGames);
    }

}
