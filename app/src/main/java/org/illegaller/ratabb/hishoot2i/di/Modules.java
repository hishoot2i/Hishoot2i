package org.illegaller.ratabb.hishoot2i.di;

import org.illegaller.ratabb.hishoot2i.HishootApplication;

public class Modules {
   public static Object[] list(final HishootApplication app) {
        return new Object[]{
                new ApplicationModule(app)
        };
    }
}
