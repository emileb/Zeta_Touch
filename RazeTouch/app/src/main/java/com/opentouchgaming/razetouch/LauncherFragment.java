package com.opentouchgaming.razetouch;

import static com.opentouchgaming.androidcore.DebugLog.Level.D;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.AppSettings;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.EngineOptionsInterface;
import com.opentouchgaming.androidcore.GD;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.ServerAPI;
import com.opentouchgaming.androidcore.SimpleServerAccess;
import com.opentouchgaming.androidcore.Utils;
import com.opentouchgaming.androidcore.common.MainFragment;

import java.io.ByteArrayOutputStream;
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
    PowerslaveLauncher powerslaveLauncher;
    IonFuryLauncher ionfuryLauncher;
    EDuke32Launcher edukeLauncher;
    AWOLLauncher awolLauncher;

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
        ionfuryLauncher = new IonFuryLauncher();
        edukeLauncher = new EDuke32Launcher();
        awolLauncher = new AWOLLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        downloadNewVersion.setOnClickListener(v ->
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://opentouchgaming.com/raze-touch/"));
            startActivity(browserIntent);
        });

        SimpleServerAccess.AccessInfo versionAccess = new SimpleServerAccess.AccessInfo();
        versionAccess.url = "http://opentouchgaming.com/api/version_raze.txt";
        versionAccess.callback = new Consumer<ByteArrayOutputStream>()
        {
            @Override
            public void accept(ByteArrayOutputStream byteArrayOutputStream)
            {
                int version = 0;
                try
                {
                    version = Integer.decode(byteArrayOutputStream.toString());
                }
                catch (Exception e)
                {

                }

                if (version != 0)
                {
                    log.log(DebugLog.Level.D, "FOUND VERSION: " + version);
                    if (version > GD.version)
                    {
                        Toast.makeText(getActivity(), "New version available", Toast.LENGTH_LONG).show();
                        downloadNewVersion.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        new SimpleServerAccess(getContext(), versionAccess);

        return view;
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
            case EDUKE32_IONFURY:
                launcher = ionfuryLauncher;
                break;
            case EDUKE32:
                launcher = edukeLauncher;
                break;
            case EDUKE32_AWOL:
                launcher = awolLauncher;
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

            Utils.showDownloadDialog(getActivity(), "Download " + selectedSubGame.getDownloadFilename() + "?", selectedSubGame.getDownloadPath(),
                    selectedSubGame.getDownloadFilename(), 80 * 1024 * 1024, cb);
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
        String args = engine.args + " ";

        int gles_version = 1;
        boolean useGL4ES = false;
        int audioFreq = 0;
        int audiosamples = 0;
        int audioEngine = 0;

        EngineOptionsInterface.RunInfo runInfo = null;

        if (engine.engineOptions != null)
        {
            runInfo = engine.engineOptions.getRunInfo(selectedVersion);

            args += runInfo.args + " ";

            // Use gles2 touch controls if anything other than gles 1
            gles_version = runInfo.glesVersion;
            useGL4ES = runInfo.useGL4ES;

            audioFreq = engine.engineOptions.audioOverrideFreq();
            audiosamples = engine.engineOptions.audioOverrideSamples();

            int audioDefault = AppSettings.getIntOption(getActivity(), "sdl_audio_backend", 0);
            int audioBackendOverride = engine.engineOptions.audioOverrideBackend();

            if (audioBackendOverride == -1)
            {
                audioEngine = audioDefault;
            }
            else
            {
                audioEngine = audioBackendOverride;
            }
        }

        Utils.copyAsset(getActivity(), "raze.pk3", AppInfo.getResFiles());
        Utils.copyAsset(getActivity(), "raze_dev.pk3", AppInfo.getResFiles());

        File sf2 = new File(AppInfo.getAppDirectory() + "/EDUKE32/soundfont.sf2");
        if (!sf2.exists())
        {
            Utils.copyAsset(getActivity(), "raze.sf2", AppInfo.getResFiles());
            Utils.copyAsset(getActivity(), "raze.sf2", AppInfo.getAppDirectory() + "/EDUKE32", "soundfont.sf2");
        }

        if (selectedSubGame.isRunFromHere())
        {
            args += " -secondary_path " + selectedSubGame.getFullPath();
        }
        else
        {
            if (launcher.getSecondaryDirectory() != null)
                args += " -secondary_path " + launcher.getSecondaryDirectory();
        }

        args += argsFinal;

        // Save history
        engineData.addArgsHistory();

        Intent intent = new Intent(getActivity(), org.libsdl.app2012.SDLActivity.class);

        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        int res_div = AppSettings.getIntOption(getActivity(), "res_div", 1);

        int wheelNbr = selectedSubGame.getWheelNbr();

        Pair<String, String> quickCommandPaths = launcher.getQuickCommandsDirectory(selectedSubGame);

        intent.putExtra("app", AppInfo.app.name());
        intent.putExtra("game_type", selectedSubGame.getGameType());
        intent.putExtra("gles_version", gles_version);
        intent.putExtra("use_gl4es", useGL4ES);
        intent.putExtra("framebuffer_width", runInfo.frameBufferWidth);
        intent.putExtra("framebuffer_height", runInfo.frameBufferHeight);
        intent.putExtra("wheel_nbr", wheelNbr);
        intent.putExtra("audio_freq", audioFreq);
        intent.putExtra("audio_samples", audiosamples);
        intent.putExtra("audio_backend", audioEngine);
        intent.putExtra("res_div", res_div);
        intent.putExtra("load_libs", engine.loadLibs[selectedVersion]);
        intent.putExtra("log_filename", AppInfo.currentEngine.getLogFilename());
        intent.putExtra("game_path", rootPath);
        intent.putExtra("user_files", AppInfo.getUserFiles());
        intent.putExtra("res_files", AppInfo.getResFiles());
        intent.putExtra("args", args);
        intent.putExtra("quick_command_main_path", quickCommandPaths.first);
        intent.putExtra("quick_command_mod_path", quickCommandPaths.second);

        log.log(D, "Intent = " + intent);

        startActivity(intent);
    }

    public View getExtraOptions()
    {
        return null;
    }
}
