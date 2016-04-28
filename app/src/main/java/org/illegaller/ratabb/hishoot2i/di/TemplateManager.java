package org.illegaller.ratabb.hishoot2i.di;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.SimpleSchedulers;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import rx.Observable;
import rx.Subscriber;

public class TemplateManager {
  public static final String NO_FAV = "no_fav";
  private final TemplateProvider templateProvider;
  private final SimpleSchedulers schedulers;

  public TemplateManager(TemplateProvider templateProvider, SimpleSchedulers schedulers) {
    this.templateProvider = templateProvider;
    this.schedulers = schedulers;
  }

  public Observable<List<Template>> getTemplateList(final String favListId) {
    return Observable.create(new Observable.OnSubscribe<List<Template>>() {
      @Override public void call(Subscriber<? super List<Template>> subscriber) {
        templateProvider.provideTemplate();
        final List<Template> templateList = templateProvider.asList();
        if (favListId.equalsIgnoreCase(NO_FAV)) {
          subscriber.onNext(templateList);
          subscriber.onCompleted();
        } else {
          List<Template> result = new ArrayList<>();
          if (Utils.isEmpty(favListId)) {
            subscriber.onNext(result);
            subscriber.onCompleted();
          }
          String[] arrayIdFav = Utils.stringToArray(favListId);
          for (String s : arrayIdFav)
            for (Template t : templateList)
              if (s.equalsIgnoreCase(t.id)) result.add(t);
          final Template DEFAULT = templateProvider.getDEFAULT();
          final boolean containsDefault = result.contains(DEFAULT);
          if (containsDefault) result.remove(DEFAULT);
          Collections.sort(result, TemplateProvider.TEMPLATE_NAME_COMPARATOR);
          if (containsDefault) result.add(0, DEFAULT);
          subscriber.onNext(result);
          subscriber.onCompleted();
        }
      }
    }).subscribeOn(schedulers.backgroundThread()).observeOn(schedulers.mainThread());
  }

  public Template getTemplateById(String templateId) {
    return templateProvider.findById(templateId);
  }
}
