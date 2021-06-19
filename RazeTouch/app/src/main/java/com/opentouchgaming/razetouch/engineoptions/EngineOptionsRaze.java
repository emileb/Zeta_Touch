package com.opentouchgaming.razetouch.engineoptions;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.arch.core.util.Function;

import com.opentouchgaming.androidcore.AppInfo;
import com.opentouchgaming.androidcore.AppSettings;
import com.opentouchgaming.androidcore.DebugLog;
import com.opentouchgaming.androidcore.EngineOptionsInterface;
import com.opentouchgaming.androidcore.GameEngine;
import com.opentouchgaming.razetouch.R;

import java.io.File;

/**
 * Created by Emile on 10/12/2017.
 */

public class EngineOptionsRaze implements EngineOptionsInterface
{
    static DebugLog log;

    static
    {
        log = new DebugLog(DebugLog.Module.GAMEFRAGMENT, "EngineOptionsRaze");
    }

    Dialog dialog;
    int version;

    int modernRenderer = 0;
    int modernWidth = 640;
    int modernHeight = 480;
    boolean modernMultithread = false;
    boolean modernGLSLMedium = false;
    boolean modernUseMappedBuffer = true;
    boolean modernForceGLSLv100 = false;

    RadioButton modernSwRadio;
    RadioButton modernGl2Radio;
    RadioButton modernGl3Radio;
    EditText modernWidthEdit;
    EditText modernHeightEdit;

    LinearLayout modernSwOptionsLayout;
    LinearLayout modernGl2OptionsLayout;
    LinearLayout modernGl3OptionsLayout;

    CheckBox modernMultithreadCheck;
    CheckBox modernGLSLMediumCheck;
    CheckBox modernUseMappedBufferCheck;
    CheckBox modernForceGLSLv100Check;

    @Override
    public void showDialog(final Activity activity, GameEngine engine, int version, Function<Integer, Void> update)
    {
        loadSettings(version);

        dialog = new Dialog(activity, R.style.MyDialog);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setTitle("Raze options");
        dialog.setContentView(R.layout.dialog_options_raze);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


        modernSwOptionsLayout = dialog.findViewById(R.id.modern_sw_options_layout);
        modernGl2OptionsLayout = dialog.findViewById(R.id.modern_gl2_options_layout);
        modernGl3OptionsLayout = dialog.findViewById(R.id.modern_gl3_options_layout);

        modernMultithreadCheck = dialog.findViewById(R.id.modern_r_multithreaded);
        modernGLSLMediumCheck = dialog.findViewById(R.id.modern_glslPrecision_med);
        modernUseMappedBufferCheck = dialog.findViewById(R.id.modern_use_mapped_buffers);
        modernForceGLSLv100Check = dialog.findViewById(R.id.modern_force_glsl_v100);

        modernSwRadio = dialog.findViewById(R.id.modern_sw_radioButton);
        modernGl2Radio = dialog.findViewById(R.id.modern_gles2_radioButton);
        modernGl3Radio = dialog.findViewById(R.id.modern_gles3_radioButton);
        modernWidthEdit = dialog.findViewById(R.id.modern_width_editText);
        modernHeightEdit = dialog.findViewById(R.id.modern_height_editText);

        chooseVersion(version);


        modernSwRadio.setOnCheckedChangeListener((buttonView, isChecked) ->
                                                 {
                                                     if (isChecked)
                                                     {
                                                         modernRenderer = 0;
                                                         saveSettings(version);
                                                         chooseVersion(version);
                                                     }
                                                 });

        modernGl2Radio.setOnCheckedChangeListener((buttonView, isChecked) ->
                                                  {
                                                      if (isChecked)
                                                      {
                                                          modernRenderer = 2;
                                                          saveSettings(version);
                                                          chooseVersion(version);
                                                      }
                                                  });

        modernGl3Radio.setOnCheckedChangeListener((buttonView, isChecked) ->
                                                  {
                                                      if (isChecked)
                                                      {
                                                          if (modernRenderer != 3)
                                                          {
                                                              AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                                                              dialogBuilder.setTitle("GLES 3.2 required!");
                                                              dialogBuilder.setMessage(
                                                                      "!! YOU NEED GLES 3.2 FOR GZDOOM TO RUN IN THIS MODE. !!\n\nIf GZDoom 'dev modern' does not start you do not have a compatible device and you must use Software mode.\nEmail support@opentouchgaming.com if you have any questions.");
                                                              AlertDialog dialog = dialogBuilder.create();
                                                              dialog.show();

                                                              // The first time we run GL3 mode, need to override the settings forced by SW mode
                                                              AppSettings.setBoolOption(activity, "gl3_override_screen_settings", true);
                                                          }
                                                          modernRenderer = 3;
                                                          saveSettings(version);
                                                          chooseVersion(version);
                                                      }
                                                  });

        dialog.setOnDismissListener(dialogInterface ->
                                    {
                                        captureUIInfo();
                                        saveSettings(version);
                                        update.apply(version);
                                    });

        Button delete = dialog.findViewById(R.id.delete_cfg_button);

        delete.setOnClickListener(view ->
                                  {
                                      {
                                          final String file = AppInfo.getUserFiles() + "/raze/config/raze.ini";
                                          AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                                          dialogBuilder.setTitle("Delete config file");
                                          dialogBuilder.setMessage("Delete Raze config file? (" + file + ")");
                                          dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which)
                                              {
                                                  new File(file).delete();
                                              }
                                          });
                                          AlertDialog dialog = dialogBuilder.create();
                                          dialog.show();
                                      }
                                  });
        dialog.show();
    }

    private void captureUIInfo()
    {

        if (modernSwRadio.isChecked())
            modernRenderer = 0;
        else if (modernGl2Radio.isChecked())
        {
            modernRenderer = 2;
        }
        else if (modernGl3Radio.isChecked())
        {
            modernRenderer = 3;
        }

        modernWidth = Integer.decode(modernWidthEdit.getText().toString());
        modernHeight = Integer.decode(modernHeightEdit.getText().toString());

        modernMultithread = modernMultithreadCheck.isChecked();
        modernGLSLMedium = modernGLSLMediumCheck.isChecked();
        modernForceGLSLv100 = modernForceGLSLv100Check.isChecked();
        modernUseMappedBuffer = modernUseMappedBufferCheck.isChecked();
    }

    private void chooseVersion(int v)
    {
        version = v;

        loadSettings(version);

        modernSwOptionsLayout.setVisibility((modernRenderer == 0) ? View.VISIBLE : View.GONE);
        modernGl2OptionsLayout.setVisibility((modernRenderer == 2) ? View.VISIBLE : View.GONE);
        modernGl3OptionsLayout.setVisibility((modernRenderer == 3) ? View.VISIBLE : View.GONE);

        modernSwRadio.setChecked(modernRenderer == 0);
        modernGl2Radio.setChecked(modernRenderer == 2);
        modernGl3Radio.setChecked(modernRenderer == 3);

        modernWidthEdit.setText(String.valueOf(modernWidth));
        modernHeightEdit.setText(String.valueOf(modernHeight));

        modernMultithreadCheck.setChecked(modernMultithread);
        modernGLSLMediumCheck.setChecked(modernGLSLMedium);
        modernUseMappedBufferCheck.setChecked(modernUseMappedBuffer);
        modernForceGLSLv100Check.setChecked(modernForceGLSLv100);
    }

    private void saveSettings(int version)
    {
        AppSettings.setBoolOption(AppInfo.getContext(), "gzdoom_modern_multithread" + version, modernMultithread);
        AppSettings.setBoolOption(AppInfo.getContext(), "gzdoom_modern_glsl_med" + version, modernGLSLMedium);
        AppSettings.setBoolOption(AppInfo.getContext(), "gzdoom_modern_use_mapped" + version, modernUseMappedBuffer);
        AppSettings.setBoolOption(AppInfo.getContext(), "gzdoom_modern_force_v100" + version, modernForceGLSLv100);

        AppSettings.setIntOption(AppInfo.getContext(), "gzdoom_modern_renderer" + version, modernRenderer);
        AppSettings.setIntOption(AppInfo.getContext(), "gzdoom_moder_width" + version, modernWidth);
        AppSettings.setIntOption(AppInfo.getContext(), "gzdoom_moder_height" + version, modernHeight);
    }

    private void loadSettings(int version)
    {
        modernRenderer = AppSettings.getIntOption(AppInfo.getContext(), "gzdoom_modern_renderer" + version, 2);
        modernWidth = AppSettings.getIntOption(AppInfo.getContext(), "gzdoom_moder_width" + version, 640);
        modernHeight = AppSettings.getIntOption(AppInfo.getContext(), "gzdoom_moder_height" + version, 480);

        modernMultithread = AppSettings.getBoolOption(AppInfo.getContext(), "gzdoom_modern_multithread" + version, false);
        modernGLSLMedium = AppSettings.getBoolOption(AppInfo.getContext(), "gzdoom_modern_glsl_med" + version, false);
        modernUseMappedBuffer = AppSettings.getBoolOption(AppInfo.getContext(), "gzdoom_modern_use_mapped" + version, true);
        modernForceGLSLv100 = AppSettings.getBoolOption(AppInfo.getContext(), "gzdoom_modern_force_v100" + version, false);
    }

    //return "+set vid_rendermode 1 -width 800 -height 480 +set vid_scale_customwidth 800 +set vid_scale_customheight 480";
    public String getArgs(int version)
    {
        loadSettings(version);
        String ret = "";

        if (modernRenderer == 0) // SW mode
        {
            ret +=
                    "-width " + modernWidth + " -height " + modernHeight + " +set vid_scalemode 5 +set vid_scale_customwidth " + modernWidth + " +set vid_scale_customheight " + modernHeight + " +set gl_pipeline_depth 1  +vid_rendermode 1 +vid_preferbackend 2 ";
            if (modernMultithread)
                ret += " +set r_multithreaded 1 ";
            else
                ret += " +set r_multithreaded 0 ";
        }
        else
        {
            ret += "-width  $W -height $H  +vid_rendermode 4 +vid_preferbackend 0 +set gl_pipeline_depth 4 ";

            // The first time we run GL3 mode, need to override the settings forced by SW mode
            if (AppSettings.getBoolOption(AppInfo.getContext(), "gl3_override_screen_settings", true))
            {
                // Clear it
                AppSettings.setBoolOption(AppInfo.getContext(), "gl3_override_screen_settings", false);
                ret += " +set vid_scalemode 0 ";
            }

            if (modernRenderer == 2)
            {
                ret += " -gles2_renderer +set gl_customshader 0 + set gl_sort_textures 1 ";

                if (modernGLSLMedium)
                    ret += " +set gles_glsl_precision 1 ";

                if (modernUseMappedBuffer)
                    ret += " +set gles_use_mapped_buffer 1 ";

                if (modernForceGLSLv100)
                    ret += " +set gles_force_glsl_v100 1 ";
            }

            // VULKAN
            //ret += " +set vid_preferbackend 1 ";
        }

        return ret;
    }

    public int getGLESVersion(int version)
    {
        loadSettings(version);

        if (modernRenderer == 0)
            return 0;
        else
            return 3;
    }

    public RunInfo getRunInfo(int version)
    {
        loadSettings(version);

        RunInfo info = new RunInfo();

        info.args = getArgs(version);
        info.glesVersion = getGLESVersion(version);

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
        return 0;
    }

    @Override
    public int audioOverrideSamples()
    {
        return 0;
    }
}
