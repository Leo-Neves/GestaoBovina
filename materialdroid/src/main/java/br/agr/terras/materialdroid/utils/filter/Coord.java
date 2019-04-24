package br.agr.terras.materialdroid.utils.filter;

import java.io.Serializable;

public class Coord implements Serializable {
    private int x;
    private int y;

    public int getX() {
        return this.x;
    }

    public void setX(int var1) {
        this.x = var1;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int var1) {
        this.y = var1;
    }

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }
}