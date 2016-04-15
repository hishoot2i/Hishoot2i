package org.illegaller.ratabb.hishoot2i.presenter;

import org.illegaller.ratabb.hishoot2i.view.common.IVew;

public interface IPresenter<V extends IVew> {

    void attachView(V view);

    void detachView();

}
