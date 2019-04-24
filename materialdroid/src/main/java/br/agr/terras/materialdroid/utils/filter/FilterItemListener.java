package br.agr.terras.materialdroid.utils.filter;

import org.jetbrains.annotations.NotNull;

public interface FilterItemListener {
    void onItemSelected(@NotNull FilterItem var1);

    void onItemDeselected(@NotNull FilterItem var1);

    void onItemRemoved(@NotNull FilterItem var1);
}
