package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;

import java.io.File;
import java.util.ArrayList;

public class AWOLLauncher extends RazeBaseLauncher
{
    AWOLLauncher()
    {
        SUB_DIR = "AWOL";
        new File(getRunDirectory()).mkdirs();
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", "", RAZE_GAME_AWOL, 4,
                new String[]{"awol.grp", "awol.grpinfo", "awol_customize.con", "awol_mods.con"}, R.drawable.awol, "A.W.O.L", "Copy awol.grp, awol.grpinfo, awol_customize.con, awol_mods.con to:",
                "Put your AWOL files here.txt");

        super.updateSubGames(engine, availableSubGames);
    }
}
