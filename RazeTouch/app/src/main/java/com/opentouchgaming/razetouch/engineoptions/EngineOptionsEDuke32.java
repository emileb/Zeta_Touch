package com.opentouchgaming.razetouch.engineoptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.arch.core.util.Function;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.AppSettings;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.EngineOptionsInterface;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.androidcore.Utils;
import com.opentouchgaming.androidcore.ui.widgets.AudioOverrideWidget;
import com.opentouchgaming.androidcore.ui.widgets.ResolutionOptionsWidget;
import com.opentouchgaming.razetouch.R;

import java.io.File;
import java.util.ArrayList;


public class EngineOptionsEDuke32 implements EngineOptionsInterface
{
    static DebugLog log;

    static Pair<String, Integer>[] cacheSizes = new Pair[]{new Pair("Default", 0), new Pair("128 MB", 128), new Pair("256 MB", 256), new Pair("384 MB", 384),
            new Pair("512 MB", 512), new Pair("768 MB", 768)};

    static
    {
        log = new DebugLog(DebugLog.Module.GAMEFRAGMENT, "EngineOptionsEDuke32");
    }

    final String settingPrefix;
    final String userFilesDir;
    Dialog dialog;
    AudioOverrideWidget audioOverride;
    ResolutionOptionsWidget resolutionOptionsSoftware;
    ResolutionOptionsWidget resolutionOptionsGL;
    CheckBox autoloadCheckbox;
    int renderMode = 0; // 0=soft, 1 = gl2

    public EngineOptionsEDuke32(String settingPrefix, String userFilesDir)
    {
        this.settingPrefix = settingPrefix;
        this.userFilesDir = userFilesDir;
        audioOverride = new AudioOverrideWidget(settingPrefix + "eduke_");
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

        resolutionOptionsSoftware = new ResolutionOptionsWidget(activity, softResolutionLayout, settingPrefix + "eduke_sw_");
        resolutionOptionsGL = new ResolutionOptionsWidget(activity, glResolutionLayout, settingPrefix + "eduke_gl_");

        autoloadCheckbox = dialog.findViewById(R.id.autoload_checkBox);

        // Autoload vheckbox
        autoloadCheckbox.setChecked(AppSettings.getBoolOption(AppInfo.getContext(), "eduke32_autoload_" + settingPrefix, false));
        autoloadCheckbox.setOnCheckedChangeListener(
                (compoundButton, b) -> AppSettings.setBoolOption(AppInfo.getContext(), "eduke32_autoload_" + settingPrefix, b));

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


        Spinner spinner = dialog.findViewById(R.id.cache_size_spinner);

        String[] cacheSizesLable = new String[cacheSizes.length];
        for (int n = 0; n < cacheSizes.length; n++)
        {
            cacheSizesLable[n] = cacheSizes[n].first;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, cacheSizesLable);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(AppSettings.getIntOption(activity, "eduke32_cachesize_" + settingPrefix, 0));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                AppSettings.setIntOption(activity, "eduke32_cachesize_" + settingPrefix, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        dialog.setOnDismissListener(dialogInterface ->
        {
            renderMode = swRadio.isChecked() ? 0 : 2;
            resolutionOptionsSoftware.save();
            resolutionOptionsGL.save();
            saveSettings();
        });


        Button delete = dialog.findViewById(R.id.delete_cfg_button);
        delete.setOnClickListener(view ->
        {
            {
                final String cfgRoot = AppInfo.getUserFiles() + "/" + userFilesDir;
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
                        Utils.findFiles(new File(cfgRoot), "eduke32.cfg", files);
                        Utils.findFiles(new File(cfgRoot), "settings.cfg", files);
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

        ResolutionOptionsWidget.ResolutionOptions optionSW = ResolutionOptionsWidget.getResOption(settingPrefix + "eduke_sw_");
        ResolutionOptionsWidget.ResolutionOptions optionGL = ResolutionOptionsWidget.getResOption(settingPrefix + "eduke_gl_");

        boolean autoload = AppSettings.getBoolOption(AppInfo.getContext(), "eduke32_autoload_" + settingPrefix, false);

        if (!autoload)
            info.args += " -noautoload ";

        int cacheSize = AppSettings.getIntOption(AppInfo.getContext(), "eduke32_cachesize_" + settingPrefix, 0);
        int cacheSizeMB = cacheSizes[cacheSize].second;

        if (cacheSize > 0)
            info.args += " -cachesize " + cacheSizeMB * 1024 + " "; // -cachesize is in KB

        if (renderMode == 0)
        {
            info.args += " -screen_bpp 8 -screen_width " + optionSW.w + "  -screen_height " + optionSW.h + " ";
            info.frameBufferWidth = optionSW.w;
            info.frameBufferHeight = optionSW.h;
        }
        else
        {
            info.args += " -screen_bpp 32 -screen_width " + optionGL.w + "  -screen_height " + optionGL.h + " ";
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
