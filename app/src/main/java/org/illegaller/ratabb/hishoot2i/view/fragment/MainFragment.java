package org.illegaller.ratabb.hishoot2i.view.fragment;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.illegaller.ratabb.hishoot2i.R;
import org.illegaller.ratabb.hishoot2i.di.compenent.AppComponent;
import org.illegaller.ratabb.hishoot2i.di.compenent.DaggerMainFragmentComponent;
import org.illegaller.ratabb.hishoot2i.di.module.MainFragmentModule;
import org.illegaller.ratabb.hishoot2i.events.EventMainPerform;
import org.illegaller.ratabb.hishoot2i.events.EventMainSetImage;
import org.illegaller.ratabb.hishoot2i.events.EventProgressBar;
import org.illegaller.ratabb.hishoot2i.events.EventTools;
import org.illegaller.ratabb.hishoot2i.model.DataImagePath;
import org.illegaller.ratabb.hishoot2i.model.ImageReceive;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.presenter.MainFragmentPresenter;
import org.illegaller.ratabb.hishoot2i.view.common.BaseFragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;

public class MainFragment extends BaseFragment implements MainFragmentView {
    private static final String KEY_IMAGE_RECEIVE = "image_receive";
    private static final String KEY_TEMPLATE_FRAGMENT = "key_template_fragment";
    @Bind(R.id.mainImageView) ImageView mImageView;
    @Inject MainFragmentPresenter mPresenter;
    @Nullable @InjectExtra(KEY_IMAGE_RECEIVE) ImageReceive mImageReceive;
    @InjectExtra(KEY_TEMPLATE_FRAGMENT) Template mTemplate;

    private String pathImageSS1;
    private String pathImageSS2;
    private String pathImageBg;
    private DataImagePath mDataImagePath;

    public MainFragment() {
    }

    public static MainFragment newInstance(@Nullable ImageReceive imageReceive, @NonNull Template template) {
        Bundle args = new Bundle();
        if (imageReceive != null) args.putParcelable(KEY_IMAGE_RECEIVE, imageReceive);
        args.putParcelable(KEY_TEMPLATE_FRAGMENT, template);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override protected void setupComponent(AppComponent component) {
        DaggerMainFragmentComponent.builder()
                .mainFragmentModule(new MainFragmentModule())
                .build().inject(this);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dart.inject(this, getArguments());
        EventBus.getDefault().register(this);
        handleImageReceive();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater,
                                                 @Nullable ViewGroup container,
                                                 @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
    }

    @Override public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe public void onEvent(EventMainPerform e) {
        if (e.flag) perform();
    }

    @Subscribe public void onEvent(EventMainSetImage e) {
        switch (e.whatImage) {
            case SS1:
                pathImageSS1 = e.path;
                break;
            case SS2:
                pathImageSS2 = e.path;
                break;
            case BG:
                pathImageBg = e.path;
                break;
            default:
                break;
        }
        EventBus.getDefault().post(new EventTools(true));
        perform();
    }

    public DataImagePath getDataImagePath() {
        return mDataImagePath;
    }

    private void perform() {
        mDataImagePath = new DataImagePath(pathImageSS1, pathImageSS2, pathImageBg);
        mPresenter.performImageProcess(mTemplate, mDataImagePath);
    }

    @Override public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
    }

    private void handleImageReceive() {
        if (mImageReceive == null) return;
        if (mImageReceive.isBackground) pathImageBg = mImageReceive.imagePath;
        else pathImageSS1 = mImageReceive.imagePath;
    }

    @Override public void showProgress() {
        EventBus.getDefault().post(new EventProgressBar(true));
    }

    @Override public void hideProgress() {
        EventBus.getDefault().post(new EventProgressBar(false));
    }

    @Override public void setImagePreview(final Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    @Override public void showMessage(final String message) {// TODO: ?
        Toast.makeText(context(), message, Toast.LENGTH_SHORT).show();
    }
}
