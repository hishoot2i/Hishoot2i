package org.illegaller.ratabb.hishoot2i.di;

import android.support.annotation.NonNull;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import rx.Observable;

import static org.illegaller.ratabb.hishoot2i.utils.Utils.isEmpty;
import static org.illegaller.ratabb.hishoot2i.utils.Utils.stringToArray;

public class TemplateManager {
  public static final String NO_FAV = "no_fav";
  @Inject TemplateProvider mTemplateProvider;

  @Inject TemplateManager() {
  }

  public Observable<List<Template>> getTemplateList(@NonNull final String favListId) {
    return Observable.create(subscriber -> {
      try {
        mTemplateProvider.provideTemplate();
        final List<Template> source = mTemplateProvider.asList();
        final List<Template> result = new ArrayList<>();
        if (!isEmpty(favListId)) {
          if (!favListId.equalsIgnoreCase(NO_FAV)) {
            /* template Fav fragment */
            for (String templateId : stringToArray(favListId)) {
              result.addAll(Stream.of(source)
                  .filter(template -> templateId.equalsIgnoreCase(template.id))
                  .collect(Collectors.toList()));
            }
          } else {
            result.addAll(Stream.of(source).collect(Collectors.toList()));
          }
        }
        subscriber.onNext(sortedAndDefaultOnTop(result));
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }

  /**
   * Template Default always on top list
   */
  private List<Template> sortedAndDefaultOnTop(List<Template> list) {
    final List<Template> result = new ArrayList<>();
    result.addAll(Stream.of(list)
        .sorted((t0, t1) -> t0.name.compareTo(t1.name))
        .collect(Collectors.toList()));
    final Template templateDefault = mTemplateProvider.getTemplateDefault();
    if (result.contains(templateDefault)) {
      result.remove(templateDefault);
      result.add(0, templateDefault);
    }
    return result;
  }

  public Template getTemplateById(String templateId) {
    return mTemplateProvider.findById(templateId);
  }
}
