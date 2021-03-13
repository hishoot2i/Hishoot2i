package org.illegaller.ratabb.hishoot2i.ui.setting

import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.text.format.Formatter.formatShortFileSize
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
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
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.data.pref.SettingPref
import org.illegaller.ratabb.hishoot2i.databinding.FragmentSettingBinding
import org.illegaller.ratabb.hishoot2i.ui.ARG_THEME
import org.illegaller.ratabb.hishoot2i.ui.KEY_REQ_THEME
import org.illegaller.ratabb.hishoot2i.ui.common.registerOpenDocumentTree
import org.illegaller.ratabb.hishoot2i.ui.common.viewObserve
import org.illegaller.ratabb.hishoot2i.ui.setting.SettingFragmentDirections.Companion.actionSettingToThemeChooser
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {
    @Inject
    lateinit var settingPref: SettingPref

    private val viewModel: SettingViewModel by viewModels()

    @RequiresApi(21)
    private val customDir = registerOpenDocumentTree { uri ->
        val file = DocumentFile.fromTreeUri(requireContext(), uri)?.uri?.toFile(requireContext())
        settingPref.customFontPath = file?.absolutePath
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentSettingBinding.bind(view).apply {
            setViewListener()
            setFragmentResultListener(KEY_REQ_THEME) { _, result ->
                val ordinal = result.getInt(ARG_THEME)
                val dayNightMode = DayNightMode.values()[ordinal]
                settingPref.dayNightMode = dayNightMode
                AppCompatDelegate.setDefaultNightMode(dayNightMode.mode)
                updateDayNightUi()
            }
        }
    }

    override fun onDestroyView() {
        clearFragmentResult(KEY_REQ_THEME)
        super.onDestroyView()
    }

    private fun FragmentSettingBinding.setViewListener() {
        settingBottomAppBar.setNavigationOnClickListener {
            it.preventMultipleClick { findNavController().popBackStack() }
        }
        // region Theme
        itemSettingAppThemes.settingDayNight.setOnClickListener {
            it.preventMultipleClick {
                findNavController().navigate(actionSettingToThemeChooser(settingPref.dayNightMode))
            }
        }
        updateDayNightUi()
        // endregion

        // region Badge
        itemSettingBadgeFontPath.settingBadgeSystemFont.apply {
            isChecked = settingPref.systemFontEnable
            setOnCheckedChangeListener { cb, checked ->
                cb.preventMultipleClick {
                    if (settingPref.systemFontEnable != checked) {
                        settingPref.systemFontEnable = checked
                    }
                }
            }
        }
        itemSettingBadgeFontPath.settingBadgeCustomFontDir.apply {
            isEnabled = SDK_INT >= 21
            setOnClickListener {
                it.preventMultipleClick {
                    if (SDK_INT >= 21) customDir.launch(Uri.EMPTY)
                }
            }
        }
        // endregion

        // region Save
        itemSettingSaveOption.settingSaveQuality.apply {
            value = settingPref.saveQuality.toFloat()
            addOnChangeListener { _, value, _ ->
                settingPref.saveQuality = value.toInt()
            }
        }
        handleEnableQuality()
        itemSettingSaveOption.settingSaveFormat.apply {
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
        itemSettingSaveOption.settingSaveNotification.apply {
            isChecked = settingPref.saveNotificationEnable
            setOnCheckedChangeListener { cb, checked ->
                cb.preventMultipleClick {
                    if (settingPref.saveNotificationEnable != checked) {
                        settingPref.saveNotificationEnable = checked
                    }
                }
            }
        }
        // endregion

        // region Cache
        viewObserve(viewModel.diskCacheSize) { updateCacheCount(it) }
        itemSettingCache.settingCacheClear.setOnClickListener {
            it.preventMultipleClick { viewModel.clearDiskCache() }
        }
        // endregion
    }

    private fun FragmentSettingBinding.handleEnableQuality() {
        itemSettingSaveOption.settingSaveQuality.isEnabled =
            settingPref.compressFormat != CompressFormat.PNG
    }

    private fun FragmentSettingBinding.updateCacheCount(diskCacheSize: Long) {
        itemSettingCache.settingCacheCount.apply {
            text = formatShortFileSize(context, diskCacheSize)
        }
    }

    private fun FragmentSettingBinding.updateDayNightUi() {
        val (textId, iconId) = when (settingPref.dayNightMode) {
            LIGHT -> R.string.light to R.drawable.ic_brightness_day
            DARK -> R.string.dark to R.drawable.ic_brightness_night
            SYSTEM -> R.string.follow_system to R.drawable.ic_brightness_auto //
        }
        with(itemSettingAppThemes.settingDayNight) {
            setText(textId)
            setCompoundDrawablesWithIntrinsicBounds(0, 0, iconId, 0)
        }
    }
}
