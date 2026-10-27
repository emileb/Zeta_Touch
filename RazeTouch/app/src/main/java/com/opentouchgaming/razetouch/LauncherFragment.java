package com.opentouchgaming.razetouch;

import static com.opentouchgaming.androidcore.DebugLog.Level.D;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Pair;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.EngineOptionsInterface;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.ServerAPI;
import com.opentouchgaming.androidcore.Utils;
import com.opentouchgaming.androidcore.common.LaunchIntent;
import com.opentouchgaming.androidcore.common.MainFragment;

import java.io.File;


public class LauncherFragment extends MainFragment
{
    static DebugLog log;

    static
    {
        log = new DebugLog(DebugLog.Module.GAMEFRAGMENT, "LauncherFragment");
    }

    DukeLauncher dukeLauncher;
    SwLauncher swLauncher;
    BloodLauncher bloodLauncher;
    RedneckLauncher redneckLauncher;
    NamLauncher namLauncher;
    Ww2giLauncher ww2giLauncher;
    PowerslaveLauncher powerslaveLauncher;
    IonFuryLauncher ionfuryLauncher;
    EDuke32Launcher edukeLauncher;
    AWOLLauncher awolLauncher;
    AMCLauncher amcLauncher;
    NBloodLauncher nbloodLauncher;
    RedNukemLauncher rednukemLauncher;

    public LauncherFragment()
    {
        super();
        log.log(D, "New instant created!");
        noLicCheck = true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dukeLauncher = new DukeLauncher();
        swLauncher = new SwLauncher();
        bloodLauncher = new BloodLauncher();
        redneckLauncher = new RedneckLauncher();
        namLauncher = new NamLauncher();
        ww2giLauncher = new Ww2giLauncher();
        powerslaveLauncher = new PowerslaveLauncher();
        ionfuryLauncher = new IonFuryLauncher();
        edukeLauncher = new EDuke32Launcher();
        awolLauncher = new AWOLLauncher();
        amcLauncher = new AMCLauncher();
        nbloodLauncher = new NBloodLauncher();
        rednukemLauncher = new RedNukemLauncher();
    }


    public void setLauncher()
    {
        switch (AppInfo.currentEngine.engine)
        {
            case RAZE_DUKE:
                launcher = dukeLauncher;
                break;
            case RAZE_SW:
                launcher = swLauncher;
                break;
            case RAZE_BLOOD:
                launcher = bloodLauncher;
                break;
            case RAZE_REDNECK:
                launcher = redneckLauncher;
                break;
            case RAZE_NAM:
                launcher = namLauncher;
                break;
            case RAZE_WW2GI:
                launcher = ww2giLauncher;
                break;
            case RAZE_POWERSLAVE:
                launcher = powerslaveLauncher;
                break;
            case EDUKE32_IONFURY:
                launcher = ionfuryLauncher;
                break;
            case EDUKE32:
                launcher = edukeLauncher;
                break;
            case EDUKE32_AWOL:
                launcher = awolLauncher;
                break;
            case EDUKE32_AMC:
                launcher = amcLauncher;
                break;
            case EDUKE32_NBLOOD:
                launcher = nbloodLauncher;
                break;
            case EDUKE32_REDNUKEM:
                launcher = rednukemLauncher;
                break;
        }
    }


    public void launchGame(final GameEngine engine, boolean download, final String multiplayerArgs)
    {
        final String rootPath = launcher.getRunDirectory(selectedSubGame);

        // Check if needed to download
        if (selectedSubGame.getDownloadFilename() != null)
        {
            // Refresh the game list when download is complete
            ServerAPI.Callback cb = new ServerAPI.Callback()
            {
                @Override
                public void callback(boolean complete)
                {
                    refreshSubGames();
                    selectSubGame(engineData.selectedSubGamePos);
                }
            };

            Utils.showDownloadDialog(getActivity(),
                                     "Download " + selectedSubGame.getDownloadFilename() + "?",
                                     selectedSubGame.getDownloadPath(),
                                     selectedSubGame.getDownloadFilename(),
                                     80 * 1024 * 1024,
                                     cb);
            return;
        }

        // Check if not installed yet
        if (selectedSubGame.getName() == null)
        {
            return;
        }

        // Check for engine specific downloads or issues
        if (download)
        {
            if (launcher.checkForDownloads(getActivity(), AppInfo.currentEngine, selectedSubGame))
                return;
        }

        // Build args
        EngineOptionsInterface.RunInfo runInfo = engine.engineOptions.getRunInfo(selectedVersion);

        String args = engine.args + " ";
        args += runInfo.args + " ";

        Utils.copyAsset(getActivity(), "raze.pk3", AppInfo.getResFiles());
        Utils.copyAsset(getActivity(), "raze_1.5.pk3", AppInfo.getResFiles());
        Utils.copyAsset(getActivity(), "raze_dev.pk3", AppInfo.getResFiles());

        File sf2 = new File(AppInfo.getAppDirectory() + "/NBLOOD/soundfont.sf2");
        if (!sf2.exists())
        {
            Utils.copyAsset(getActivity(), "raze.sf2", AppInfo.getResFiles());
            Utils.copyAsset(getActivity(), "raze.sf2", AppInfo.getAppDirectory() + "/EDUKE32", "soundfont.sf2");
            Utils.copyAsset(getActivity(), "raze.sf2", AppInfo.getAppDirectory() + "/NBLOOD", "soundfont.sf2");
        }

        // Duke 64's widescreen HUD, found by the engine in its user folder
        if (engine.engine == GameEngine.Engine.EDUKE32_REDNUKEM)
            Utils.copyAsset(getActivity(), "dn64widescreen.pk3", AppInfo.getUserFiles() + "/rednukem/duke64");

        if (selectedSubGame.isRunFromHere())
        {
            args += " -secondary_path " + selectedSubGame.getFullPath();
        }
        else
        {
            if (launcher.getSecondaryDirectory() != null)
                args += " -secondary_path " + launcher.getSecondaryDirectory();
        }

        // Data folder for the engine to use instead of the CWD, which can't be a SAF folder
        if (engine.engine == GameEngine.Engine.EDUKE32 || engine.engine == GameEngine.Engine.EDUKE32_NBLOOD ||
            engine.engine == GameEngine.Engine.EDUKE32_REDNUKEM)
            args += " -game_path " + (selectedSubGame.isRunFromHere() ? selectedSubGame.getFullPath() : selectedSubGame.getRootPath());

        args += argsFinal;

        // Save history
        engineData.addArgsHistory();

        // Create Intent. All the Raze/EDuke32-family engines are SDL2 (app2012).
        Intent intent;

        if (runInfo.sdlVersion == 3)
        {
            intent = new Intent(getActivity(), org.libsdl.app3000.SDLActivity.class);
        }
        else
        {
            intent = new Intent(getActivity(), org.libsdl.app2012.SDLActivity.class);
        }

        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.putExtra("load_libs", engine.loadLibs[selectedVersion]);
        intent.putExtra("log_filename", engine.getLogFilename());
        intent.putExtra("game_path", rootPath);
        intent.putExtra("args", args);

        Pair<String, String> quickCommandPaths = launcher.getQuickCommandsDirectory(selectedSubGame);
        int wheelNbr = selectedSubGame.getWheelNbr();
        LaunchIntent.populateIntent(getActivity(), intent, engine.engineOptions, runInfo, selectedSubGame.getGameType(), wheelNbr, quickCommandPaths);

        log.log(D, "Intent = " + intent);

        startActivity(intent);
    }

    public View getExtraOptions()
    {
        return null;
    }
}
