package org.illegaller.ratabb.hishoot2i.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseActivity

class SettingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initActionBar()
        supportFragmentManager.beginTransaction()
            .replace(R.id.settingContent, SettingFragment())
            .commit()
    }

    private fun initActionBar() {
        setSupportActionBar(findViewById(R.id.settingToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }
}
