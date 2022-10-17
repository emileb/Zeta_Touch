package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class SwLauncher extends RazeBaseLauncher
{
    SwLauncher()
    {
        SUB_DIR = "SW";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", RAZE_GAME_SW, WEAPON_WHEEL_NBR,
                new String[]{"sw.grp"}, R.drawable.sw, "Shadow Warrior", "Copy SW.GRP to:", "Put your SW.GRP files here.txt");

        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + "addons/td", "addons/td", RAZE_GAME_SW,
                WEAPON_WHEEL_NBR, new String[]{"addons/td/td.grp"}, R.drawable.raze, "Shadow Warrior: Twin Dragon", "Copy your TD.GRP file to:",
                "Put your TD.GRP here.txt");
        sg.setExtraArgs("-gamegrp TD.GRP");

        sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + "addons/wt", "addons/wt", RAZE_GAME_SW, WEAPON_WHEEL_NBR,
                new String[]{"addons/wt/wt.grp"}, R.drawable.raze, "Shadow Warrior: Wanton Destruction", "Copy your WT.GRP file to:",
                "Put your WT.GRP here.txt");
        sg.setExtraArgs("-gamegrp WT.GRP");

        addAddonsDir(engine, RAZE_GAME_SW, availableSubGames, new String[]{"td", "wt"});

        super.updateSubGames(engine, availableSubGames);
    }
}
