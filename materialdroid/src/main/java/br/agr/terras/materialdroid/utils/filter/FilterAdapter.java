package br.agr.terras.materialdroid.utils.filter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public abstract class FilterAdapter<T extends FilterModel> {
    @NotNull
    private final List<T> items;

    @NotNull
    public abstract FilterItem createView(int var1, T var2);

    @NotNull
    public List<T> getItems() {
        return this.items;
    }

    public FilterAdapter(@NotNull List items) {
        super();
        Intrinsics.checkParameterIsNotNull(items, "items");
        this.items = items;
    }
}
