package org.illegaller.ratabb.hishoot2i.ui.setting

import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.text.format.Formatter.formatShortFileSize
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import common.ext.preventMultipleClick
import common.ext.setOnItemSelected
import common.ext.toFile
import dagger.hilt.android.AndroidEntryPoint
import entity.DayNightMode
import entity.DayNightMode.DARK
import entity.DayNightMode.LIGHT
import entity.DayNightMode.SYSTEM
import entity.mode
import imageloader.ImageLoader
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentSettingBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_THEME
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_THEME
import org.illegaller.ratabb.hishoot2i.ui.common.registerOpenDocumentTree
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {
    @Inject
    lateinit var settingPref: SettingPref

    @Inject
    lateinit var imageLoader: ImageLoader

    private var settingBinding: FragmentSettingBinding? = null

    @RequiresApi(21)
    private val customDir = registerOpenDocumentTree { uri ->
        val file = DocumentFile.fromTreeUri(requireContext(), uri)?.uri?.toFile(requireContext())
        settingPref.customFontPath = file?.absolutePath
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentSettingBinding.bind(view).apply {
            settingBinding = this
            setViewListener(this)
        }
        setFragmentResultListener(KEY_REQ_THEME) { _, result ->
            val ordinal = result.getInt(ARG_THEME)
            val dayNightMode = DayNightMode.values()[ordinal]
            settingPref.dayNightMode = dayNightMode
            AppCompatDelegate.setDefaultNightMode(dayNightMode.mode)
            updateDayNightUi()
        }
    }

    override fun onDestroyView() {
        clearFragmentResult(KEY_REQ_THEME)
        settingBinding?.itemSettingSaveOption?.settingSaveQuality?.clearOnChangeListeners()
        settingBinding = null
        super.onDestroyView()
    }

    private fun setViewListener(binding: FragmentSettingBinding) = with(binding) {
        settingBottomAppBar.setNavigationOnClickListener {
            it.preventMultipleClick { findNavController().popBackStack() }
        }
        // region Theme
        itemSettingAppThemes.settingDayNight.setOnClickListener {
            it.preventMultipleClick {
                findNavController().navigate(
                    SettingFragmentDirections.actionSettingToThemeChooser(
                        settingPref.dayNightMode
                    )
                )
            }
        }
        updateDayNightUi()
        // endregion

        // region Badge
        itemSettingBadgeFontPath.run {
            settingBadgeSystemFont.apply {
                isChecked = settingPref.systemFontEnable
                setOnCheckedChangeListener { cb, checked ->
                    cb.preventMultipleClick {
                        if (settingPref.systemFontEnable != checked) {
                            settingPref.systemFontEnable = checked
                        }
                    }
                }
            }
            settingBadgeCustomFontDir.apply {
                isEnabled = SDK_INT >= 21
                setOnClickListener {
                    it.preventMultipleClick {
                        if (SDK_INT >= 21) {
                            customDir.launch(Uri.EMPTY)
                        }
                    }
                }
            }
        }
        // endregion

        // region Save
        itemSettingSaveOption.run {
            settingSaveQuality.apply {
                value = settingPref.saveQuality.toFloat()
                addOnChangeListener { _, value, _ ->
                    settingPref.saveQuality = value.toInt()
                }
            }
            settingSaveFormat.apply {
                setSelection(settingPref.compressFormat.ordinal, false)
                setOnItemSelected { _, v, position, _ ->
                    v?.preventMultipleClick {
                        if (position != settingPref.compressFormat.ordinal) {
                            settingPref.compressFormat = CompressFormat.values()[position]
                            handleEnableQuality()
                        }
                    }
                }
            }
            handleEnableQuality()
            settingSaveNotification.apply {
                isChecked = settingPref.saveNotificationEnable
                setOnCheckedChangeListener { cb, checked ->
                    cb.preventMultipleClick {
                        if (settingPref.saveNotificationEnable != checked) {
                            settingPref.saveNotificationEnable = checked
                        }
                    }
                }
            }
        }
        // endregion

        // region Cache
        updateCacheCount(itemSettingCache.settingCacheCount)
        itemSettingCache.settingCacheClear.setOnClickListener {
            it.preventMultipleClick {
                imageLoader.clearDiskCache()
                updateCacheCount(itemSettingCache.settingCacheCount)
            }
        }
        // endregion
    }

    private fun handleEnableQuality() {
        settingBinding?.itemSettingSaveOption?.settingSaveQuality?.isEnabled =
            settingPref.compressFormat != CompressFormat.PNG
    }

    private fun updateCacheCount(cacheTextView: TextView) {
        cacheTextView.text = formatShortFileSize(
            requireContext(),
            imageLoader.totalDiskCacheSize()
        )
    }

    private fun updateDayNightUi() {
        val (textId, iconId) = when (settingPref.dayNightMode) {
            LIGHT -> R.string.light to R.drawable.ic_brightness_day
            DARK -> R.string.dark to R.drawable.ic_brightness_night
            SYSTEM -> R.string.follow_system to R.drawable.ic_brightness_auto //
        }
        settingBinding?.itemSettingAppThemes?.settingDayNight?.apply {
            setText(textId)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconId, 0)
        }
    }
}
