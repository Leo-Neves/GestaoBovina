package br.agr.terras.materialdroid.utils.filter;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public interface FilterModel extends Serializable {
    @NotNull
    String getText();
}
