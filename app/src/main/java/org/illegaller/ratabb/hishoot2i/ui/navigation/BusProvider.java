package org.illegaller.ratabb.hishoot2i.ui.navigation;

import com.squareup.otto.Bus;

public class BusProvider {
    private static final Bus BUS = new Bus();

    private BusProvider() {// No instances.
    }

    public static Bus getInstance() {
        return BUS;
    }
}
