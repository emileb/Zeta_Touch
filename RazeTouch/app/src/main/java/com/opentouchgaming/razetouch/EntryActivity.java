package com.opentouchgaming.razetouch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.opentouchgaming.androidcore.AboutDialog;
import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.AppSettings;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.GD;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.ScopedStorage;
import com.opentouchgaming.androidcore.controls.GamepadDefinitions;
import com.opentouchgaming.androidcore.ui.ScopedStorageDialog;
import com.opentouchgaming.androidcore.ui.StorageConfigDialog;
import com.opentouchgaming.androidcore.ui.tutorial.Tutorial;
import com.opentouchgaming.razetouch.engineoptions.EngineOptionsEDuke32_AWOL;
import com.opentouchgaming.razetouch.engineoptions.EngineOptionsEDuke32_Duke;
import com.opentouchgaming.razetouch.engineoptions.EngineOptionsEDuke32_IonFury;
import com.opentouchgaming.razetouch.engineoptions.EngineOptionsRaze;

import java.util.ArrayList;
import java.util.List;


public class EntryActivity extends FragmentActivity
{
    static DebugLog log;

    static
    {
        log = new DebugLog(DebugLog.Module.APP, "EntryActivity");
    }

    static
    {
        Tutorial tut;
        tut = new Tutorial("Using the Custom touch buttons.", "ic_tut_custom");
        tut.addScreen(new Tutorial.Screen("Press the touch settings button.", "", "http://opentouchgaming.com/tutorial/delta/custom_cog.png"));
        tut.addScreen(new Tutorial.Screen("Press the touch options button.", "", "http://opentouchgaming.com/tutorial/delta/custom_sliders.png"));
        tut.addScreen(new Tutorial.Screen("Press the 'Hide/Show' button.", "", "http://opentouchgaming.com/tutorial/delta/custom_hideshow.png"));
        tut.addScreen(new Tutorial.Screen("Enable the 'Show custom' button. This adds a new button to the game touch controls.", "",
                "http://opentouchgaming.com/tutorial/delta/custom_enablecustom.png"));
        tut.addScreen(new Tutorial.Screen("Press the new button to show the custom controls. Press the touch settings button to edit and move them.", "",
                "http://opentouchgaming.com/tutorial/delta/custom_showcustom.png"));
        tut.addScreen(new Tutorial.Screen("To assign them go to 'Options -> Customise Controls' and select the action.", "",
                "http://opentouchgaming.com/tutorial/delta/custom_controloptions.png"));
        tut.addScreen(new Tutorial.Screen("Press a custom button to assign. Slide from the centre of the 'Quad-buttons' up, down, left or right. ", "",
                "http://opentouchgaming.com/tutorial/delta/custom_assign.png"));
        AppInfo.tutorials.add(tut);

        tut = new Tutorial("Using the console/keyboard", "ic_tut_keyboard");
        tut.addScreen(new Tutorial.Screen("Start a new game and press the 'cog' to edit the touch controls", "",
                "http://opentouchgaming.com/tutorial/quad/keyboard_1.png"));
        tut.addScreen(new Tutorial.Screen("Press the 'sliders' button", "", "http://opentouchgaming.com/tutorial/quad/keyboard_2.png"));
        tut.addScreen(new Tutorial.Screen("Press 'Hide/Show buttons'", "", "http://opentouchgaming.com/tutorial/quad/keyboard_3.png"));
        tut.addScreen(new Tutorial.Screen("Enable the Keyboard button", "", "http://opentouchgaming.com/tutorial/quad/keyboard_4.png"));
        tut.addScreen(new Tutorial.Screen("Enable the Console button", "", "http://opentouchgaming.com/tutorial/quad/keyboard_5.png"));
        AppInfo.tutorials.add(tut);

        tut = new Tutorial("Enable the gyroscope", "ic_tut_gyro");
        tut.addScreen(new Tutorial.Screen("Go to the menu of the game and press the 'gyro' button in the top right", "",
                "http://opentouchgaming.com/tutorial/quad/gyro_1.png"));
        tut.addScreen(new Tutorial.Screen("Enable the gyroscope. Remember your device needs to have the gyroscope hardware to enable this feature", "",
                "http://opentouchgaming.com/tutorial/quad/gyro_2.png"));
        AppInfo.tutorials.add(tut);

        tut = new Tutorial("Using Quick Commands", "ic_baseline_star_border");
        tut.addScreen(new Tutorial.Screen("Start a new game and press the 'cog' to edit the touch controls", "",
                "http://opentouchgaming.com/tutorial/quad/quick_cmd_1.png"));
        tut.addScreen(new Tutorial.Screen("Press the 'sliders' button", "", "http://opentouchgaming.com/tutorial/quad/quick_cmd_2.png"));
        tut.addScreen(new Tutorial.Screen("Press 'Hide/Show buttons'", "", "http://opentouchgaming.com/tutorial/quad/quick_cmd_3.png"));
        tut.addScreen(new Tutorial.Screen("Enable 'Quick Commands'", "", "http://opentouchgaming.com/tutorial/quad/quick_cmd_4.png"));
        tut.addScreen(new Tutorial.Screen("Tap the Quick Commands button to show", "", "http://opentouchgaming.com/tutorial/quad/quick_cmd_5.png"));
        tut.addScreen(new Tutorial.Screen("Add new commands by pressing the add button", "", "http://opentouchgaming.com/tutorial/quad/quick_cmd_6.png"));
        AppInfo.tutorials.add(tut);

        AppInfo.gameEngines = new GameEngine[]{new GameEngine(GameEngine.Engine.EDUKE32_AWOL, 0, "A.W.O.L", "duke3d", "", new String[]{"stable"},
                new String[][]{{"touchcontrols", "GL4ES", "openal", "awol"}}, " ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.awol,
                R.drawable.awol_background, 0x0077481d, R.drawable.eduke_button_bg_foucable, EngineOptionsEDuke32_IonFury.class),

                new GameEngine(GameEngine.Engine.EDUKE32_IONFURY, 0, "Ion fury", "duke3d", "", new String[]{"stable", "dev"},
                        new String[][]{{"touchcontrols", "GL4ES", "openal", "eduke32"}, {"touchcontrols", "GL4ES", "openal", "eduke32_dev"}}, " ",
                        GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.ionfury, 0, 0x002006e0, R.drawable.eduke_button_bg_foucable,
                        EngineOptionsEDuke32_AWOL.class),

                new GameEngine(GameEngine.Engine.EDUKE32, 1, "Eduke32", "duke3d", "", new String[]{"stable", "dev"},
                        new String[][]{{"touchcontrols", "GL4ES", "openal", "eduke32"}, {"touchcontrols", "GL4ES", "openal", "eduke32_dev"}}, " ",
                        GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.dn3d_eduke, 0, 0x002f7fad, R.drawable.eduke_button_bg_foucable,
                        EngineOptionsEDuke32_Duke.class),

                new GameEngine(GameEngine.Engine.RAZE_DUKE, 1, "Duke Nukem 3D", "duke3d", "", new String[]{"1.3.1", "dev (1.5.0)"},
                        new String[][]{{"touchcontrols", "openal", "zmusic", "raze"}, {"touchcontrols", "openal", "zmusic", "raze_dev"}},
                        " +set cl_syncinput 1 ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.dn3d, 0, 0x00d1bd10,
                        R.drawable.raze_button_bg_foucable, EngineOptionsRaze.class),

                new GameEngine(GameEngine.Engine.RAZE_SW, 2, "Shadow Warrior", "shadow_warrior", "", new String[]{"1.3.1", "dev (1.5.0)"},
                        new String[][]{{"touchcontrols", "openal", "zmusic", "raze"}, {"touchcontrols", "openal", "zmusic", "raze_dev"}},
                        " +set cl_syncinput 1 ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.sw, 0, 0x00dad9d2,
                        R.drawable.raze_button_bg_foucable, EngineOptionsRaze.class),

                new GameEngine(GameEngine.Engine.RAZE_BLOOD, 3, "Blood", "blood", "", new String[]{"1.3.1", "dev (1.5.0)"},
                        new String[][]{{"touchcontrols", "openal", "zmusic", "raze"}, {"touchcontrols", "openal", "zmusic", "raze_dev"}},
                        " +set cl_syncinput 1 ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.blood, 0, 0x00d20829,
                        R.drawable.raze_button_bg_foucable, EngineOptionsRaze.class),

                new GameEngine(GameEngine.Engine.RAZE_REDNECK, 4, "Redneck Rampage", "redneck", "", new String[]{"1.3.1", "dev (1.5.0)"},
                        new String[][]{{"touchcontrols", "openal", "zmusic", "raze"}, {"touchcontrols", "openal", "zmusic", "raze_dev"}},
                        " +set cl_syncinput 1 ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.redneck, 0, 0x008f6407,
                        R.drawable.raze_button_bg_foucable, EngineOptionsRaze.class),

                new GameEngine(GameEngine.Engine.RAZE_NAM, 5, "NAM", "nam", "", new String[]{"1.3.1", "dev (1.5.0)"},
                        new String[][]{{"touchcontrols", "openal", "zmusic", "raze"}, {"touchcontrols", "openal", "zmusic", "raze_dev"}},
                        " +set cl_syncinput 1 ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.nam, 0, 0x00428747,
                        R.drawable.raze_button_bg_foucable, EngineOptionsRaze.class),

                new GameEngine(GameEngine.Engine.RAZE_POWERSLAVE, 6, "Powerslave", "powerslave", "", new String[]{"1.3.1", "dev (1.5.0)"},
                        new String[][]{{"touchcontrols", "openal", "zmusic", "raze"}, {"touchcontrols", "openal", "zmusic", "raze_dev"}},
                        " +set cl_syncinput 1 ", GamepadDefinitions.getDefinition(AppInfo.Apps.RAZE_TOUCH), R.drawable.ps, 0, 0x0008dfdc,
                        R.drawable.raze_button_bg_foucable, EngineOptionsRaze.class),};

        List<StorageConfigDialog.StorageExamples> examples = new ArrayList<>();
        examples.add(new StorageConfigDialog.StorageExamples("Duke Nukem 3D", "(DUKE3D.GRP):", StorageConfigDialog.PathLocation.BOTH, "/DUKE"));
        examples.add(new StorageConfigDialog.StorageExamples("BLOOD", "(BLOOD.RFF, tilesxxx.art, ...):", StorageConfigDialog.PathLocation.BOTH, "/BLOOD"));
        examples.add(new StorageConfigDialog.StorageExamples("Shadow Warrior", "(SW.GRP):", StorageConfigDialog.PathLocation.BOTH, "/SW"));

        examples.add(new StorageConfigDialog.StorageExamples("Redneck Rampage", "(All files):", StorageConfigDialog.PathLocation.BOTH, "/REDNECK"));
        examples.add(new StorageConfigDialog.StorageExamples("NAM", "(NAM.GRP, GAME.COM, NAM.RTS):", StorageConfigDialog.PathLocation.BOTH, "/NAM"));
        examples.add(new StorageConfigDialog.StorageExamples("Powerslave", "(STUFF.DAT):", StorageConfigDialog.PathLocation.BOTH, "/PS"));

        examples.add(new StorageConfigDialog.StorageExamples("User files", "(config, saves etc):", StorageConfigDialog.PathLocation.PRIM, "/user_files"));

        AppInfo.storageExamples = examples;

        ScopedStorageDialog.Tutorial scopedTutorial = new ScopedStorageDialog.Tutorial();
        scopedTutorial.folder = "Device > OpenTouch > Zeta";
        scopedTutorial.items = new ArrayList<>();
        /*
        scopedTutorial.items.add(new Pair<>(R.drawable.ss_2, "Select where you want your files."));
        scopedTutorial.items.add(new Pair<>(R.drawable.ss_3, "Create (or select) the 'OpenTouch' folder."));
        scopedTutorial.items.add(new Pair<>(R.drawable.ss_4, "Create (or select) the 'Quad' folder."));
        scopedTutorial.items.add(new Pair<>(R.drawable.ss_5, "Check the path is correct and press 'USE THIS FOLDER'"));
        */
        AppInfo.scopedTutorial = scopedTutorial;
    }

    final String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArVTuCs3MUfRpivh5ETTzfgq+pdSHPfvWKKOsqLdyugv37TPWGfjHADzI+Ryst8qdObT9qEfKQXbd5PLC6+Lspl3" +
                       "/N8L+FXJO5tNSzcxDNr/gCXgR/vs+YiRpyuCJMNcuwPHfDIKdBmPaFxQAxSggdzoWfEmTXyaA1S8PZprT1GcOIB1scLUWpXPjzZeOTXwEzD20HWKeR" +
                       "+oG7PzFBAF85clKu5Y2bypoBcmnlpBl3nK2TdNJdESitxjS5CssRp5zBxYQ6BnMfDI1W8n2QCatFb+lAHcnhye/FB8" +
                       "/nA476b2WOw3VBkk5CspXhDNRom6dCMfP1HTxHrH6Q0LFh81SxQIDAQAB";
    final int MY_PERMISSIONS_REQUEST_SD_WRITE = 1;
    LauncherFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_entry);

        AboutDialog.aboutRes = R.raw.about;
        AppInfo.key = key;

        GD.init(getApplicationContext());

        AppSettings.reloadSettings(getApplication());

        AppInfo.setAppInfo(getApplicationContext(), AppInfo.Apps.RAZE_TOUCH, "Zeta Touch", "Zeta", BuildConfig.APPLICATION_ID, "razelogs@opentouchgaming.com",
                false, R.drawable.raze, false);
        AppInfo.showRateButton = false;
        AppInfo.sidePanelImage = R.drawable.side_panel;

        AppInfo.website = "http://opentouchgaming.com/zeta-touch/";
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null)
        {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null)
            {
                mainFragment = (LauncherFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            mainFragment = new LauncherFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mainFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_SD_WRITE);

            log.log(DebugLog.Level.D, "Sending request");

        }
        else
        {
            // Permission has already been granted
            log.log(DebugLog.Level.D, "Permission already granted");
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        log.log(DebugLog.Level.D, "onActivityResult, requestCode = " + requestCode + " resultCode = " + resultCode);

        ScopedStorage.activityResult(this, requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_SD_WRITE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    log.log(DebugLog.Level.D, "Permission granted");
                }
                else
                {
                    log.log(DebugLog.Level.D, "Permission denined");
                }

                return;
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mainFragment.onBackPressed())
        {
            // Fragment ate back button press
            return;
        }
        else
        {
            super.onBackPressed();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        return mainFragment.onGenericMotionEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (mainFragment.onKeyDown(keyCode, event))
        {
            return true;
        }
        else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


}
