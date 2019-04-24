package br.agr.terras.materialdroid.utils.filter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface FilterListener<T extends FilterModel> {
    void onFiltersSelected(@NotNull ArrayList<T> var1);
    void onNothingSelected();
    void onFilterSelected(T var1);
    void onFilterDeselected(T var1);
}
