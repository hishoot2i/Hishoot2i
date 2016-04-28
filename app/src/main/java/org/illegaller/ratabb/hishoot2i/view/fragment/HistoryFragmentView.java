package org.illegaller.ratabb.hishoot2i.view.fragment;

import java.util.List;
import org.illegaller.ratabb.hishoot2i.view.common.IVew;

public interface HistoryFragmentView  extends IVew{
  void setList(List<String> list);
  void showProgress(boolean isShow);
}
