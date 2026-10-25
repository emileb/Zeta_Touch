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
import com.opentouchgaming.androidcore.Utils
import com.opentouchgaming.androidcore.ui.widgets.AudioOverrideWidget
import com.opentouchgaming.androidcore.ui.widgets.ResolutionOptionsWidget
import com.opentouchgaming.androidcore.ui.widgets.SpinnerWidget
import com.opentouchgaming.androidcore.ui.widgets.SwitchWidget
import com.opentouchgaming.razetouch.R
import com.opentouchgaming.razetouch.databinding.DialogOptionsEdukeNewBinding
import com.opentouchgaming.saffal.FileSAF
import java.io.File

open class EngineOptionsEDuke32Kt(prefix: String, val userFilesDir: String) : EngineOptionsInterface
{

    lateinit var binding: DialogOptionsEdukeNewBinding

    lateinit var dialog: Dialog
    var audioOverride: AudioOverrideWidget? = null

    var version: Int = 0

    val RENDERER_GL2 = 0
    val RENDERER_SW = 1

    val RENDERER_PREFIX = "eduke32_renderer_backend_$prefix"
    val RENDERER_DEFAULT = RENDERER_GL2

    val RESOLUTION_PREFIX_GL = "eduke32_resolution_gl_$prefix"
    val RESOLUTION_DEFAULT_GL = 0 // 100% screen

    val RESOLUTION_PREFIX_SW = "eduke32_resolution_sw_$prefix"
    val RESOLUTION_DEFAULT_SW = 3 // 25% screen

    val AUTOLOAD_PREFIX = "eduke32_autoload_$prefix"
    val AUTOLOAD_DEFAULT = false

    val CACHE_SIZE_PREFIX = "eduke32_cache_size_$prefix"
    val CACHE_SIZE_DEFAULT = 0

    // Texture memory options, passed with -set so they override the engine's config files and autoexec.cfg
    val TEXTURE_DOWNSIZE_PREFIX = "eduke32_texture_downsize_$prefix"
    val TEXTURE_MAXSIZE_PREFIX = "eduke32_texture_maxsize_$prefix"

    // r_downsize for each choice, null leaves the game's own setting
    val textureDownsizes = arrayOf<Pair<String, Int?>>(Pair("Default", null), Pair("Full", 0), Pair("Half", 1), Pair("Quarter", 2))

    // r_texturemaxsize is a power of two (11 = 2048)
    val textureMaxSizes = arrayOf<Pair<String, Int?>>(Pair("Default", null), Pair("2048", 11), Pair("1024", 10))


    val cacheSizes = arrayOf(
        Pair("Default", 0),
        Pair("128 MB", 128),
        Pair("256 MB", 256),
        Pair("384 MB", 384),
        Pair("512 MB", 512),
        Pair("768 MB", 768),
        Pair("1024 MB", 1024),
        Pair("1.5 GB", 1536),
        Pair("1.75 GB", 1536 + 256)
    )

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

        binding = DialogOptionsEdukeNewBinding.inflate(activity.layoutInflater)

        dialog = Dialog(activity, R.style.DialogEngineSettings)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.setTitle("EDuke32 options - $userFilesDir")
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        selectVersion(activity, version)

        binding.deleteCfgButton.setOnClickListener {

            showAlert(activity, "Delete config file", "Delete EDuke32 config files?\n") {
                val cfgRoot = AppInfo.getUserFiles() + "/" + userFilesDir
                val files = ArrayList<String>()
                Utils.findFiles(FileSAF(cfgRoot), "eduke32.cfg", files)
                Utils.findFiles(FileSAF(cfgRoot), "settings.cfg", files)
                Utils.findFiles(FileSAF(cfgRoot), "nblood.cfg", files)
                Utils.findFiles(FileSAF(cfgRoot), "nblood_cvars.cfg", files)
                for (f in files)
                {
                    File(f).delete()
                }
            }
        }

        dialog.setOnDismissListener {
            update!!.apply(this.version)
        }

        dialog.show()
    }

    fun selectVersion(activity: Activity, version: Int)
    {

        this.version = version

        val items = arrayOf<Pair<String, View?>>(Pair("GLES 2.0", binding.gl2Options), Pair("Software", binding.swOptions))

        // Create and Setup the renderer spinner options
        SpinnerWidget(
            activity,
            binding.rendererSpinner.root,
            "Rendering backend",
            "Select rendering backend used. Will affect performance and features",
            items,
            RENDERER_PREFIX,
            RENDERER_DEFAULT,
            R.drawable.setting_gpu
        )

        val cacheItems = Array<Pair<String, View?>>(cacheSizes.size) { index ->
            Pair(cacheSizes[index].first, null)
        }

        // Create and setup the cache spinner
        SpinnerWidget(
            activity,
            binding.cacheSpinner.root,
            "Cache memory",
            "Size of Cache memory to allocate",
            cacheItems,
            CACHE_SIZE_PREFIX,
            CACHE_SIZE_DEFAULT,
            R.drawable.settings_ram
        )

        ResolutionOptionsWidget(activity, binding.resolutionSelectGl.root, RESOLUTION_PREFIX_GL, RESOLUTION_DEFAULT_GL)

        SpinnerWidget(
            activity,
            binding.textureDownsizeSpinner.root,
            "Hi-res texture size",
            "Lower uses much less GPU memory. Use Half or Quarter if a texture pack crashes",
            Array<Pair<String, View?>>(textureDownsizes.size) { Pair(textureDownsizes[it].first, null) },
            TEXTURE_DOWNSIZE_PREFIX,
            0,
            R.drawable.settings_ram
        )

        SpinnerWidget(
            activity,
            binding.textureMaxsizeSpinner.root,
            "Max texture size",
            "Shrinks only the largest textures to save GPU memory",
            Array<Pair<String, View?>>(textureMaxSizes.size) { Pair(textureMaxSizes[it].first, null) },
            TEXTURE_MAXSIZE_PREFIX,
            0,
            R.drawable.settings_ram
        )
        ResolutionOptionsWidget(activity, binding.resolutionSelectSw.root, RESOLUTION_PREFIX_SW, RESOLUTION_DEFAULT_SW)

        SwitchWidget(
            activity,
            binding.loadAutoloadSwitch.root,
            "Load 'autoload' folder",
            "Automatically load files in the 'autoload' folder",
            AUTOLOAD_PREFIX,
            AUTOLOAD_DEFAULT,
            0
        )
    }

    override fun getRunInfo(version: Int): EngineOptionsInterface.RunInfo
    {
        var info = EngineOptionsInterface.RunInfo()

        info.args = ""
        info.glesVersion = 2
        info.useGL4ES = true

        val renderer = SpinnerWidget.fetchValue(AppInfo.getContext(), RENDERER_PREFIX, RENDERER_DEFAULT)
        val cacheSize = SpinnerWidget.fetchValue(AppInfo.getContext(), CACHE_SIZE_PREFIX, CACHE_SIZE_DEFAULT)
        val cacheSizeMB = cacheSizes[cacheSize].second

        if (renderer == RENDERER_SW)
        {
            val optionSW = ResolutionOptionsWidget.getResOption(RESOLUTION_PREFIX_SW, RESOLUTION_DEFAULT_SW)
            info.args += " -screen_bpp 8 -screen_width " + optionSW.w + "  -screen_height " + optionSW.h + " "
            info.frameBufferWidth = optionSW.w
            info.frameBufferHeight = optionSW.h
            info.maintainAspect = optionSW.maintainAspect
        } else if (renderer == RENDERER_GL2)
        {
            val optionGL = ResolutionOptionsWidget.getResOption(RESOLUTION_PREFIX_GL, RESOLUTION_DEFAULT_GL)
            info.args += " -screen_bpp 32 -screen_width " + optionGL.w + "  -screen_height " + optionGL.h + " "
            info.frameBufferWidth = optionGL.w
            info.frameBufferHeight = optionGL.h
            info.maintainAspect = optionGL.maintainAspect
        }

        if (cacheSize > 0) info.args += " -cachesize " + cacheSizeMB * 1024 + " " // -cachesize is in KB

        if (!SwitchWidget.fetchValue(AppInfo.getContext(), AUTOLOAD_PREFIX, AUTOLOAD_DEFAULT))
        {
            info.args += " -noautoload "
        }

        if (renderer == RENDERER_GL2)
        {
            val downsize = SpinnerWidget.fetchValue(AppInfo.getContext(), TEXTURE_DOWNSIZE_PREFIX, 0).coerceIn(0, textureDownsizes.size - 1)
            textureDownsizes[downsize].second?.let { info.args += " -set r_downsize $it " }

            val maxSize = SpinnerWidget.fetchValue(AppInfo.getContext(), TEXTURE_MAXSIZE_PREFIX, 0).coerceIn(0, textureMaxSizes.size - 1)
            textureMaxSizes[maxSize].second?.let { info.args += " -set r_texturemaxsize $it " }
        }

        return info
    }

    override fun hasMultiplayer(): Boolean
    {
        return false
    }

    override fun launchMultiplayer(
        activity: Activity?, engine: GameEngine?, version: Int, mainArgs: String?, callback: EngineOptionsInterface.MultiplayerCallback?
    )
    {

    }
}