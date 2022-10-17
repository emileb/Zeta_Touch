package com.opentouchgaming.razetouch;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;

import java.io.File;
import java.util.ArrayList;

public class BloodLauncher extends RazeBaseLauncher
{
    BloodLauncher()
    {
        SUB_DIR = "BLOOD";
        new File(getRunDirectory()).mkdirs();
        Utils.mkdirs(AppInfo.getContext(), getRunDirectory() + "/mods/", "Put your mods files here.txt");
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        log.log(DebugLog.Level.D, "updateSubGames");

        availableSubGames.clear();

        SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR + ".", ".", RAZE_GAME_BLOOD, WEAPON_WHEEL_NBR,
                new String[]{"blood.ini"}, R.drawable.blood, "BLOOD", "Copy Blood files (BLOOD.RFF, tilesXXX.art, blood.ini) to:",
                "Put your Blood files here.txt");

        SubGame sg = SubGame.addGame(availableSubGames, getRunDirectory(), getSecondaryDirectory(), SUB_DIR, "addons/cryptic", 0, WEAPON_WHEEL_NBR,
                new String[]{"addons/cryptic/CP01.MAP", "addons/cryptic/cryptic.ini"}, R.drawable.raze, "BLOOD: Cryptic Passage",
                "Copy your Cryptic Passage files to:", "Put your Cryptic Passage files here.txt");

        sg.setExtraArgs(" -cryptic -game_dir " + sg.getRootPath() + "/addons/cryptic");

        addAddonsDir(engine, RAZE_GAME_BLOOD, availableSubGames, new String[]{"cryptic"});

        super.updateSubGames(engine, availableSubGames);
    }
}
