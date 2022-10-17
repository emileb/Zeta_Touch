package com.opentouchgaming.razetouch.engineoptions

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.view.View
import android.view.ViewGroup
import androidx.arch.core.util.Function
import com.opentouchgaming.androidcore.AppInfo
import com.opentouchgaming.androidcore.EngineOptionsInterface
import com.opentouchgaming.androidcore.GameEngine
import com.opentouchgaming.androidcore.ui.widgets.SpinnerWidget
import com.opentouchgaming.androidcore.ui.widgets.SwitchWidget
import com.opentouchgaming.razetouch.R
import com.opentouchgaming.razetouch.databinding.DialogOptionsRazeBinding
import java.io.File

class EngineOptionsRaze : EngineOptionsInterface
{

    lateinit var binding: DialogOptionsRazeBinding

    lateinit var dialog: Dialog

    var version: Int = 0


    val MODERN_RENDERER_GL2 = 0
    val MODERN_RENDERER_GL3 = 1

    // Modern Software features
    val MODERN_RENDERER_PREFIX = "raze_renderer_backend_modern"
    val MODERN_RENDERER_DEFAULT = MODERN_RENDERER_GL2

    // Modern GLES 2 features
    val MODERN_MAP_BUFFER_PREFIX = "raze_use_mapped_buffer"
    val MODERN_MAP_BUFFER_DEFAULT = true

    val MODERN_FORCE_V100_PREFIX = "raze_force_v100"
    val MODERN_FORCE_V100_DEFAULT = false

    val MODERN_MEDIUM_GLSL_PREFIX = "raze_medium_glsl"
    val MODERN_MEDIUM_GLSL_DEFAULT = false

    val USE_POLYMOST_PREFIX = "raze_use_polymost"
    val USE_POLYMOST_DEFAULT = true

    fun showAlert(activity: Activity, title: String, message: String, function: () -> (Unit))
    {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            function.invoke()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun showDialog(activity: Activity, engine: GameEngine, version: Int, update: Function<Int, Void>?)
    {

        this.version = version

        binding = DialogOptionsRazeBinding.inflate(activity.layoutInflater)

        dialog = Dialog(activity, R.style.MyDialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setTitle("Raze options")
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)


        binding.deleteCfgButton.setOnClickListener {
            val file = "/raze/config/raze.ini"
            val file2 = "/raze_dev/config/raze.ini"

            showAlert(activity, "Delete config file", "Delete Raze config file?\n(${file})") {
                File(file).delete()
                File(file2).delete()
            }
        }

        dialog.setOnDismissListener {
            update!!.apply(this.version)
        }

        selectVersion(activity, version)

        dialog.show()
    }

    fun selectVersion(activity: Activity, version: Int)
    {

        this.version = version

        // These gotta match the definition at the top
        val items = arrayOf<Pair<String, View?>>(Pair("GLES 2.0+", binding.modernGl2OptionsLayout), Pair("GLES 3.2", null))

        // Create and Setup the backend spinner options
        SpinnerWidget(activity,
                binding.modernRendererSpinner.root,
                "Rendering backend",
                "Select rendering backend used. Will affect performance and features.",
                items,
                MODERN_RENDERER_PREFIX,
                MODERN_RENDERER_DEFAULT,
                R.drawable.setting_gpu)

        SwitchWidget(activity,
                binding.usePolymostSwitch.root,
                "Use Polymost renderer",
                "Use the original Polymost renderer, disable to use the Raze3D renderer",
                USE_POLYMOST_PREFIX,
                USE_POLYMOST_DEFAULT)

        // GLES 2.0
        SwitchWidget(activity,
                binding.modernUseMappedBuffersSwitch.root,
                "Use mapped GL buffer",
                "Change vertex buffer loading. Can change performance so test it on/off.",
                MODERN_MAP_BUFFER_PREFIX,
                MODERN_MAP_BUFFER_DEFAULT)

        SwitchWidget(activity,
                binding.modernForceGlslV100Switch.root,
                "Force GLES2 shader compliance",
                "Enable if crash on new game. For very old devices.",
                MODERN_FORCE_V100_PREFIX,
                MODERN_FORCE_V100_DEFAULT)

        SwitchWidget(activity,
                binding.modernGlslPrecisionMedSwitch.root,
                "Use 'medium' GLSL precision",
                "May increase performance. Disable if geometry appears to shake or wobble.",
                MODERN_MEDIUM_GLSL_PREFIX,
                MODERN_MEDIUM_GLSL_DEFAULT)

    }

    override fun getRunInfo(version: Int): EngineOptionsInterface.RunInfo
    {
        var info = EngineOptionsInterface.RunInfo()

        var args = ""
        var glVersion = 3

        val renderer = SpinnerWidget.fetchValue(AppInfo.getContext(), MODERN_RENDERER_PREFIX, MODERN_RENDERER_DEFAULT)

        args += "-width  \$W -height \$H  +set vid_rendermode 4 +set gl_pipeline_depth 4 "

        if (renderer == MODERN_RENDERER_GL2)
        {
            args += " -gles2_renderer +set vid_preferbackend 3 +set gl_customshader 0 +set gl_sort_textures 1 "
        }
        else if (renderer == MODERN_RENDERER_GL3)
        {
            args += " +set vid_preferbackend 0 "
        }

        if (SwitchWidget.fetchValue(AppInfo.getContext(), USE_POLYMOST_PREFIX, USE_POLYMOST_DEFAULT)) args += " +set vid_renderer 0 " // Polymost
        else args += " +set vid_renderer 1 " // New renderer

        if (SwitchWidget.fetchValue(AppInfo.getContext(), MODERN_MEDIUM_GLSL_PREFIX, MODERN_MEDIUM_GLSL_DEFAULT)) args += " +set gles_glsl_precision 1 "

        if (SwitchWidget.fetchValue(AppInfo.getContext(), MODERN_MAP_BUFFER_PREFIX, MODERN_MAP_BUFFER_DEFAULT)) args += " +set gles_use_mapped_buffer 1 "

        if (SwitchWidget.fetchValue(AppInfo.getContext(), MODERN_FORCE_V100_PREFIX, MODERN_FORCE_V100_DEFAULT)) args += " +set gles_force_glsl_v100 1 "

        // Acts strange, disable for now
        args += " +set showendoom 0 "

        info.glesVersion = glVersion
        info.args = args

        return info
    }

    override fun hasMultiplayer(): Boolean
    {
        return false
    }

    override fun launchMultiplayer(activity: Activity?,
                                   engine: GameEngine?,
                                   version: Int,
                                   mainArgs: String?,
                                   callback: EngineOptionsInterface.MultiplayerCallback?)
    {

    }
}