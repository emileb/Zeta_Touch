package com.opentouchgaming.razetouch;

import android.app.Activity;

import androidx.core.util.Pair;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.SubGame;
import com.opentouchgaming.androidcore.common.GameLauncherInterface;

import java.util.ArrayList;

/**
 * Created by Emile on 09/09/2018.
 */

public class RazeBaseLauncher implements GameLauncherInterface
{
    static DebugLog log;

    final int WEAPON_WHEEL_NBR = 8;

    static
    {
        log = new DebugLog(DebugLog.Module.CONTROLS, "RazeBaseLauncher");
    }

    String SUB_DIR = null;

    @Override
    public String getRunDirectory()
    {
        return AppInfo.getAppDirectory() + "/" + SUB_DIR;
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
        String commonPath = AppInfo.getUserFiles() + "/" + SUB_DIR;
        String modPath = commonPath + "/" + subGame.getName();
        return new Pair<>(commonPath, modPath);
    }

    @Override
    public void updateSubGames(GameEngine engine, ArrayList<SubGame> availableSubGames)
    {


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

    public String getArgs(GameEngine engine, SubGame subGame)
    {
        return " +set cl_syncinput 1 +set vid_fps 1 ";
    }

    @Override
    public boolean checkForDownloads(Activity activity, GameEngine engine, SubGame subGame)
    {
        return false;
    }
}
