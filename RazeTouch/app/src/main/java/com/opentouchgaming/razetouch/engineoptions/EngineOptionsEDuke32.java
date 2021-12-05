package com.opentouchgaming.razetouch.engineoptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.arch.core.util.Function;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.AppSettings;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.EngineOptionsInterface;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.Utils;
import com.opentouchgaming.androidcore.ui.AudioOverride;
import com.opentouchgaming.androidcore.ui.ResolutionOptionsView;
import com.opentouchgaming.razetouch.R;

import java.io.File;
import java.util.ArrayList;


public class EngineOptionsEDuke32 implements EngineOptionsInterface
{
    static DebugLog log;

    static
    {
        log = new DebugLog(DebugLog.Module.GAMEFRAGMENT, "EngineOptionsEDuke32");
    }

    Dialog dialog;
    AudioOverride audioOverride;

    ResolutionOptionsView resolutionOptionsSoftware;
    ResolutionOptionsView resolutionOptionsGL;

    int renderMode = 0; // 0=soft, 1 = gl2

    final String settingPrefix;

    public EngineOptionsEDuke32(String prefix)
    {
        settingPrefix = prefix;
        audioOverride = new AudioOverride(settingPrefix + "eduke_");
    }

    @Override
    public void showDialog(final Activity activity, GameEngine engine, int version, Function<Integer, Void> update)
    {
        loadSettings();

        dialog = new Dialog(activity, R.style.MyDialog);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setTitle("EDuke32 options");
        dialog.setContentView(R.layout.dialog_options_eduke);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        View softResolutionLayout = dialog.findViewById(R.id.soft_resolution);
        View glResolutionLayout = dialog.findViewById(R.id.gl_resolution);

        resolutionOptionsSoftware = new ResolutionOptionsView(activity, softResolutionLayout, settingPrefix + "eduke_sw_");
        resolutionOptionsGL = new ResolutionOptionsView(activity, glResolutionLayout, settingPrefix + "eduke_gl_");

        // Handles audio override
        audioOverride.linkUI(activity, dialog);

        final RadioButton gl2Radio = dialog.findViewById(R.id.gles2_radioButton);
        final RadioButton swRadio = dialog.findViewById(R.id.software_radioButton);

        if (renderMode == 0)
        {
            resolutionOptionsSoftware.setEnabled(true);
            resolutionOptionsGL.setEnabled(false);
            swRadio.setChecked(true);
        }
        else if (renderMode == 2)
        {
            resolutionOptionsSoftware.setEnabled(false);
            resolutionOptionsGL.setEnabled(true);
            gl2Radio.setChecked(true);
        }

        swRadio.setOnCheckedChangeListener((compoundButton, b) ->
                                           {
                                               if (b == true)
                                               {
                                                   resolutionOptionsSoftware.setEnabled(true);
                                                   resolutionOptionsGL.setEnabled(false);
                                               }
                                           });

        gl2Radio.setOnCheckedChangeListener((compoundButton, b) ->
                                            {
                                                if (b == true)
                                                {
                                                    resolutionOptionsSoftware.setEnabled(false);
                                                    resolutionOptionsGL.setEnabled(true);
                                                }
                                            });

        dialog.setOnDismissListener(dialogInterface ->
                                    {
                                        renderMode = swRadio.isChecked() ? 0 : 2;
                                        resolutionOptionsSoftware.save();
                                        saveSettings();
                                    });


        Button delete = dialog.findViewById(R.id.delete_cfg_button);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                {
                    final String cfgRoot = AppInfo.getUserFiles() + "/yq2/";
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                    dialogBuilder.setMessage("Delete all Eduke32 config files?");
                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            //new File(file).delete();
                            log.log(DebugLog.Level.D, "cfgRoot = " + cfgRoot);
                            ArrayList<String> files = new ArrayList<>();
                            Utils.findFiles(new File(cfgRoot), "config.cfg", files);
                            for (String f : files)
                            {
                                log.log(DebugLog.Level.D, "file to delete = " + f);
                                new File(f).delete();
                            }
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                }
            }
        });

        dialog.show();
    }

    private void saveSettings()
    {
        AppSettings.setIntOption(AppInfo.getContext(), settingPrefix + "eduke_ref", renderMode);
    }

    private void loadSettings()
    {
        renderMode = AppSettings.getIntOption(AppInfo.getContext(), settingPrefix + "eduke_ref", 2);
    }


    @Override
    public RunInfo getRunInfo(int version)
    {
        loadSettings();

        RunInfo info = new RunInfo();

        info.args = "";

        info.glesVersion = 2;
        info.useGL4ES = true;

        ResolutionOptionsView.ResolutionOptions optionSW = ResolutionOptionsView.getResOption(settingPrefix + "eduke_sw_");
        ResolutionOptionsView.ResolutionOptions optionGL = ResolutionOptionsView.getResOption(settingPrefix + "eduke_gl_");

        if (renderMode == 0)
            info.args = " -screen_bpp 8 -screen_width " + optionSW.w + "  -screen_height " + optionSW.h + " ";
        else
        {
            info.args = " -screen_bpp 32 -screen_width " + optionGL.w + "  -screen_height " + optionGL.h + " ";
            info.frameBufferWidth = optionGL.w;
            info.frameBufferHeight = optionGL.h;
        }

        return info;
    }

    @Override
    public boolean hasMultiplayer()
    {
        return false;
    }

    @Override
    public void launchMultiplayer(Activity ac, GameEngine engine, int version, String mainArgs, MultiplayerCallback callback)
    {

    }

    @Override
    public int audioOverrideFreq()
    {
        return audioOverride.getFreq();
    }

    @Override
    public int audioOverrideSamples()
    {
        return audioOverride.getSamples();
    }

    @Override
    public int audioOverrideBackend()
    {
        return audioOverride.getBackend();
    }
}
