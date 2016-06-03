package org.illegaller.ratabb.hishoot2i.view.fragment.historyview;

import java.util.List;
import org.illegaller.ratabb.hishoot2i.view.common.Mvp;

public interface HistoryFragmentView extends Mvp.View {
  void setList(List<String> list);

  void showProgress(boolean isShow);
}
