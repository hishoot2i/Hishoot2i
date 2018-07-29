package org.illegaller.ratabb.hishoot2i.ui.template

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.illegaller.ratabb.hishoot2i.R
import org.illegaller.ratabb.hishoot2i.ui.common.BaseActivity
import org.illegaller.ratabb.hishoot2i.ui.common.widget.BadgeTabLayout
import org.illegaller.ratabb.hishoot2i.ui.common.widget.NoScrollViewPager
import org.illegaller.ratabb.hishoot2i.ui.setting.SettingActivity
import org.illegaller.ratabb.hishoot2i.ui.template.fragment.favorite.FavoriteFragment
import org.illegaller.ratabb.hishoot2i.ui.template.fragment.installed.InstalledFragment
import rbb.hishoot2i.common.ext.actionGetContentWith
import rbb.hishoot2i.common.ext.preventMultipleClick
import rbb.hishoot2i.common.ext.toFile
import rbb.hishoot2i.template.Template
import timber.log.Timber
import javax.inject.Inject

class TemplateManagerActivity : BaseActivity(), TemplateManagerView {
    @Inject
    lateinit var presenter: TemplateManagerPresenter
    //
    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: BadgeTabLayout
    private lateinit var viewPager: NoScrollViewPager
    private lateinit var addHtz: FloatingActionButton
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_manager)
        toolbar = findViewById(R.id.templateManagerToolbar)
        tabLayout = findViewById(R.id.templateManagerTabLayout)
        viewPager = findViewById(R.id.templateManagerViewPager)
        addHtz = findViewById(R.id.templateManagerAddHtz)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        presenter.attachView(this)
        initView()

        handleDataUri(intent)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.template_manager, menu) //
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_setting) SettingActivity.start(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("reqCode:$requestCode resCode:$resultCode data:$data")
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_HTZ_PICK) {
            handleDataUri(data)
        }
    }

    private fun handleDataUri(data: Intent?) {
        data?.data?.let { uri: Uri ->
            presenter.importHtz(uri.toFile(this))
        }
    }

    private fun initView() {
        viewPager.adapter = TemplatePagerAdapter(supportFragmentManager)

        with(tabLayout) {
            setupWithViewPager(viewPager)
            with(POSITION_INSTALLED).apply {
                titleView.text = getString(R.string.installed)
                iconView.setImageResource(R.drawable.ic_installed_black_24)
            }.build()

            with(POSITION_FAVORITE).apply {
                titleView.text = getString(R.string.favorite)
                iconView.setImageResource(R.drawable.ic_favorite_black_24dp)
            }.build()
        }
        addHtz.setOnClickListener {
            it.preventMultipleClick {
                startActivityForResult(actionGetContentWith(type = "*/*"), REQ_HTZ_PICK)
            }
        }
    }

    fun updateTabBadge(position: Int, count: Int) {
        tabLayout.with(position).badgeCount = count
    }

    override fun onSuccessImportHtz(htz: Template.VersionHtz) {
        // TODO
        Timber.d("onSuccessImportHtz: $htz")
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }

    inner class TemplatePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val data = arrayOf(POSITION_INSTALLED, POSITION_FAVORITE)
        override fun getItem(position: Int): Fragment = when (position) {
            POSITION_INSTALLED -> InstalledFragment()
            POSITION_FAVORITE -> FavoriteFragment()
            else -> throw IllegalStateException("TemplatePagerAdapter unknown position:$position")
        }

        override fun getCount(): Int = data.size
    }

    companion object {
        private const val REQ_HTZ_PICK = 0x123
        const val POSITION_INSTALLED = 0
        const val POSITION_FAVORITE = 1
        @JvmStatic
        fun start(context: Context) {
            ActivityOptionsCompat.makeCustomAnimation(
                context,
                R.anim.window_enter,
                R.anim.window_exit
            ).let {
                context.startActivity(
                    Intent(context, TemplateManagerActivity::class.java),
                    it.toBundle()
                )
            }
        }
    }
}