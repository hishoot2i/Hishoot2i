package org.illegaller.ratabb.hishoot2i.di;

import android.support.annotation.NonNull;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.illegaller.ratabb.hishoot2i.model.template.Template;
import org.illegaller.ratabb.hishoot2i.utils.Utils;
import rx.Observable;

public class TemplateManager {
  public static final String NO_FAV = "no_fav";
  @Inject TemplateProvider templateProvider;

  @Inject public TemplateManager() {
  }

  public Observable<List<Template>> getTemplateList(@NonNull final String favListId) {
    return Observable.create((Observable.OnSubscribe<List<Template>>) subscriber -> {
      try {
        templateProvider.provideTemplate();
        final List<Template> templateList = templateProvider.asList();
        final List<Template> result = new ArrayList<>();
        if (!favListId.equalsIgnoreCase(NO_FAV) && !Utils.isEmpty(favListId)) {
          final String[] arrayIdFav = Utils.stringToArray(favListId);
          for (String templateId : arrayIdFav) {
            result.addAll(Stream.of(templateList)
                .filter(template -> templateId.equalsIgnoreCase(template.id))
                .sorted(TemplateProvider.TEMPLATE_NAME_COMPARATOR)
                .collect(Collectors.toList()));
          }
        } else if (!Utils.isEmpty(favListId)) {
          result.addAll(Stream.of(templateList)
              .sorted(TemplateProvider.TEMPLATE_NAME_COMPARATOR)
              .collect(Collectors.toList()));
        }
        /* Template Default always on top list*/
        final Template DEFAULT = templateProvider.getTemplateDefault();
        final boolean containsDefault = result.contains(DEFAULT);
        if (containsDefault) {
          result.remove(DEFAULT);
          result.add(0, DEFAULT);
        }
        subscriber.onNext(result);
        subscriber.onCompleted();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    });
  }

  public Template getTemplateById(String templateId) {
    return templateProvider.findById(templateId);
  }
}
