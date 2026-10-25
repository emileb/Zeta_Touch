package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class Ww2giLauncher extends RazeBaseLauncher
{
    Ww2giLauncher()
    {
        SUB_DIR = "WW2GI";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", RAZE_GAME_NAM, WEAPON_WHEEL_NBR,
                new String[]{"WW2GI.GRP"}, R.drawable.ww2gi, "World War II GI", "Copy WW2GI.GRP to:", "Put your WW2GI files here.txt");
        sg.setExtraArgs("-gamegrp WW2GI.GRP");

        // Platoon Leader is a WW2GI addon in the same folder
        sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + "platoon", ".", RAZE_GAME_NAM, WEAPON_WHEEL_NBR,
                new String[]{"PLATOONL.DAT", "PLATOONL.DEF"}, R.drawable.ww2gi, "Platoon Leader", "Copy PLATOONL.DAT and PLATOONL.DEF to:",
                "Put your WW2GI files here.txt");
        sg.setExtraArgs("-gamegrp PLATOONL.DAT");

        addAddonsDir(engine, RAZE_GAME_NAM, availableSubGames, new String[]{""});

        super.updateSubGames(engine, availableSubGames);
    }
}
