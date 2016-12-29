package com.main.lets.lets.LetsAPI;

import android.view.View;

/**
 * Created by Joe on 12/22/2016.
 */
public abstract class ViewDecorator {
    ViewDecorator mDecorator;

    public ViewDecorator() {

    }

    public ViewDecorator(ViewDecorator d) {
        mDecorator = d;
    }

    public abstract View decorate(View v);

}
