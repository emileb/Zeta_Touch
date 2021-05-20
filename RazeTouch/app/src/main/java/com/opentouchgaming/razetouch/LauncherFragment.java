package com.opentouchgaming.razetouch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.util.Pair;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.AppSettings;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.EngineOptionsInterface;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.ServerAPI;
import com.opentouchgaming.androidcore.Utils;
import com.opentouchgaming.androidcore.common.MainFragment;

import static com.opentouchgaming.androidcore.DebugLog.Level.D;


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
    PowerslaveLauncher powerslaveLauncher;

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
        powerslaveLauncher = new PowerslaveLauncher();
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
            case RAZE_POWERSLAVE:
                launcher = powerslaveLauncher;
                break;
        }
    }


    public void launchGame(final GameEngine engine, boolean download, final String multiplayerArgs)
    {
        final String rootPath = launcher.getRunDirectory();

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

            Utils.showDownloadDialog(getActivity(), "Download " + selectedSubGame.getDownloadFilename() + "?", selectedSubGame.getDownloadPath(), selectedSubGame.getDownloadFilename(), 80 * 1024 * 1024, cb);
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
            if(launcher.checkForDownloads(getActivity(), AppInfo.currentEngine, selectedSubGame))
                return;
        }

        // Build args
        String args = engine.args + " ";

        int gles_version = 1;
        boolean useGL4ES = false;
        int audioFreq = 0;
        int audiosamples = 0;

        EngineOptionsInterface.RunInfo runInfo = null;

        if (engine.engineOptions != null)
        {
            runInfo =  engine.engineOptions.getRunInfo(selectedVersion);

            args += runInfo.args + " ";

            // Use gles2 touch controls if anything other than gles 1
            gles_version = runInfo.glesVersion;
            useGL4ES = runInfo.useGL4ES;

            audioFreq = engine.engineOptions.audioOverrideFreq();
            audiosamples = engine.engineOptions.audioOverrideSamples();
        }

        Utils.copyAsset(getActivity(), "raze.pk3", AppInfo.getAppDirectory() + "/res/");
        Utils.copyAsset(getActivity(), "raze.sf2", AppInfo.getAppDirectory() + "/res/");

        args += argsFinal;

        // Save history
        engineData.addArgsHistory();

        Intent intent = new Intent(getActivity(), org.libsdl.app2012.SDLActivity.class);

        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        int res_div = AppSettings.getIntOption(getActivity(), "res_div", 1);

        int wheelNbr = selectedSubGame.getWheelNbr();

        Pair<String,String> quickCommandPaths = launcher.getQuickCommandsDirectory(selectedSubGame);

        intent.putExtra("app", AppInfo.app.name());
        intent.putExtra("game_type", selectedSubGame.getGameType());
        intent.putExtra("gles_version", 3);
        intent.putExtra("use_gl4es", false);
        intent.putExtra("framebuffer_width", runInfo.frameBufferWidth);
        intent.putExtra("framebuffer_height", runInfo.frameBufferHeight);
        intent.putExtra("wheel_nbr", wheelNbr);
        intent.putExtra("audio_freq", audioFreq);
        intent.putExtra("audio_samples", audiosamples);
        intent.putExtra("res_div", res_div);
        intent.putExtra("load_libs", engine.loadLibs[selectedVersion]);
        intent.putExtra("log_filename",  AppInfo.currentEngine.getLogFilename());
        intent.putExtra("game_path", rootPath);
        intent.putExtra("user_files", AppInfo.getUserFiles());
        intent.putExtra("args", args);
        intent.putExtra("quick_command_main_path", quickCommandPaths.first);
        intent.putExtra("quick_command_mod_path", quickCommandPaths.second);

        log.log(D,"Intent = " + intent);

        startActivity(intent);
    }

    public View getExtraOptions()
    {
        return null;
    }
}
