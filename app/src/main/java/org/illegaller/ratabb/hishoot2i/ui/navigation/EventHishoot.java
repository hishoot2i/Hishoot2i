package org.illegaller.ratabb.hishoot2i.ui.navigation;

import org.illegaller.ratabb.hishoot2i.model.DataImagePath;

import android.net.Uri;
import android.support.annotation.Nullable;

public class EventHishoot {

    protected EventHishoot() {
        throw new AssertionError("EventHishoot no construction");
    }

    public static class EventProcessDone {
        public final Uri uri;

        public EventProcessDone(Uri uri) {
            this.uri = uri;
        }
    }

    public static class EventFab {
        @Nullable public final DataImagePath dataPaths;

        public EventFab(@Nullable DataImagePath dataPaths) {
            this.dataPaths = dataPaths;
        }
    }

    public static class EventSetTemplateUse {
        public final String templateID;

        public EventSetTemplateUse(String templateID) {
            this.templateID = templateID;
        }
    }
}
