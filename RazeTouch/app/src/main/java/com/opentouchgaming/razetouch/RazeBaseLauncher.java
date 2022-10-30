package com.opentouchgaming.razetouch;

import static com.opentouchgaming.androidcore.DebugLog.Level.D;

import android.app.Activity;

import androidx.core.util.Pair;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.Utils;
import com.opentouchgaming.androidcore.common.GameLauncherInterface;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Emile on 09/09/2018.
 */

public class RazeBaseLauncher implements GameLauncherInterface
{
    static DebugLog log;

    static
    {
        log = new DebugLog(DebugLog.Module.CONTROLS, "RazeBaseLauncher");
    }

    final int WEAPON_WHEEL_NBR = 10;
    final int RAZE_GAME_DUKE = 100;
    final int RAZE_GAME_BLOOD = 101;
    final int RAZE_GAME_SW = 102;
    final int RAZE_GAME_RR = 103;
    final int RAZE_GAME_NAM = 104;
    final int RAZE_GAME_PS = 105;
    final int RAZE_GAME_IONFURY = 106;
    final int RAZE_GAME_EDUKE32 = 107;
    final int RAZE_GAME_AWOL = 108;
    String SUB_DIR = null;

    @Override
    public String getRunDirectory()
    {
        return AppInfo.getAppDirectory() + "/" + SUB_DIR;
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
    public String getSecondaryDirectory()
    {
        String secFolder = AppInfo.getAppSecDirectory();
        if (secFolder != null)
            return secFolder + "/" + SUB_DIR;
        else
            return null;
    }

    @Override
    public Pair<String, String> getQuickCommandsDirectory(SubGame subGame)
    {
        String commonPath = AppInfo.getUserFiles() + "/QC";
        String modPath = commonPath + "/" + SUB_DIR;
        return new Pair<>(commonPath, modPath);
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {
        for (SubGame game : availableSubGames)
        {
            game.load(AppInfo.getContext());
        }
    }

    private String quote(String s)
    {
        if (s.contains(" "))
        {
            return "\"" + s + "\"";
        }
        else
            return s;
    }

    public void addAddonsDir(GameEngine engine, int gameType, ArrayList<SubGame> availableSubGames, String[] ignore)
    {
        addAddonsDir(engine, "/addons", gameType, availableSubGames, ignore);
    }

    public void addAddonsDir(GameEngine engine, String addonsDir, int gameType, ArrayList<SubGame> availableSubGames, String[] ignore)
    {
        String addons = addonsDir;
        ArrayList<File> files = Utils.listFiles(new String[]{getRunDirectory() + addons, getSecondaryDirectory() + addons});

        if (files != null)
        {
            for (File f : files)
            {
                String dirName = f.getName().toLowerCase();
                String pathName = f.getParent();
                if (f.isDirectory())
                {
                    boolean skip = false;
                    for (String i : ignore)
                    {
                        if (dirName.contentEquals(i))
                            skip = true;
                    }

                    if (skip)
                        continue;

                    String pathInfo = f.getAbsolutePath();
                    String fileInfo = Utils.filesInfoString(pathInfo, null, 3);

                    SubGame subgame = new SubGame(SUB_DIR + dirName, dirName, dirName, pathName, gameType, R.drawable.raze, pathInfo, fileInfo,
                            WEAPON_WHEEL_NBR);

                    if (gameType == RAZE_GAME_EDUKE32 || gameType == RAZE_GAME_IONFURY)
                        subgame.setExtraArgs("-game_dir " + quote(dirName));
                    else
                        subgame.setExtraArgs("-game_dir " + quote(pathInfo));

                    availableSubGames.add(subgame);
                }
            }
        }
    }

    public String getArgs(GameEngine engine, SubGame subGame)
    {
        return "";
    }

    @Override
    public boolean checkForDownloads(Activity activity, GameEngine engine, SubGame subGame)
    {
        return false;
    }
}
