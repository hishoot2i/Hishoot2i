package org.illegaller.ratabb.hishoot2i.presenter;

import org.illegaller.ratabb.hishoot2i.HishootApplication;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.model.tray.TrayManager;
import org.illegaller.ratabb.hishoot2i.utils.HishootProcess;
import org.illegaller.ratabb.hishoot2i.view.fragment.MainFragmentView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

public class MainFragmentPresenter implements IPresenter<MainFragmentView>, HishootProcess.Callback {

    private Handler mHandler;
    private MainFragmentView mView;
    private ProcessTask mTask;

    @Override public void attachView(MainFragmentView view) {
        this.mView = view;
        this.mView.hideProgress();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override public void detachView() {
        if (this.mTask != null) {
            this.mTask.cancel(true);
            this.mTask = null;
        }
        this.mView = null;
        this.mHandler = null;
    }

    public void performImageProcess(@NonNull Template template, DataImagePath dataImagePath) {
        final Context context = this.mView.context();
        if (context != null) {
            this.mTask = new ProcessTask(context, template, dataImagePath);
            this.mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }


    @Override public void startingImage(long startTime) {
        if (this.mView != null) this.mHandler.post(new Runnable() {
            @Override public void run() {
                mView.showProgress();
            }
        });
    }

    @Override public void failedImage(final String text, final String extra) {
        if (this.mView != null) this.mHandler.post(new Runnable() {
            @Override public void run() {
                mView.hideProgress();
                mView.showMessage(text + "\n" + extra);
            }
        });
    }

    @Override public void doneImage(final Bitmap result) {
        if (this.mView != null) this.mHandler.post(new Runnable() {
            @Override public void run() {
                mView.hideProgress();
                mView.setImagePreview(result);
            }
        });
    }

    @Override public void doneService(Bitmap result, Uri uri) {        //no-op
    }

    /////////////////////////
    class ProcessTask extends AsyncTask<Void, Void, Void> {
        private final TrayManager mTrayManager;
        private final Template template;
        private final DataImagePath dataImagePath;
        private final Context mContext;

        ProcessTask(Context context, Template template, DataImagePath dataImagePath) {
            this.mContext = context;
            this.mTrayManager = HishootApplication.get(mContext).getComponent().trayManager();
            this.template = template;
            this.dataImagePath = dataImagePath;
        }

        @Override protected Void doInBackground(Void... voids) {
            final HishootProcess mProcess = new HishootProcess(mContext, template,
                    mTrayManager.getSsDoubleEnableTray().get(), mTrayManager.getBgColorEnableTray().get(),
                    mTrayManager.getBgImageBlurEnableTray().get(), mTrayManager.getBadgeEnableTray().get(),
                    mTrayManager.getGlareEnableTray().get(), mTrayManager.getShadowEnableTray().get(),
                    mTrayManager.getBgColorIntTray().get(), mTrayManager.getBgImageBlurRadiusTray().get(),
                    mTrayManager.getBadgeColorTray().get(), mTrayManager.getBadgeTextTray().get(),
                    mTrayManager.getBadgeSizeTray().get(), MainFragmentPresenter.this);
            mProcess.process(dataImagePath, false);
            return null;
        }
    }
}
