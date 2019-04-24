package br.agr.terras.materialdroid.utils.calendar;

import android.support.annotation.LayoutRes;
import android.view.View;

import java.lang.reflect.ParameterizedType;

/**
 * Created by leo on 15/06/16.
 */
public abstract class EventRenderer<T extends CalendarEvent> {
    public abstract void render(final View view, final T event);

    @LayoutRes
    public abstract int getEventLayout();

    public Class<T> getRenderType() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
