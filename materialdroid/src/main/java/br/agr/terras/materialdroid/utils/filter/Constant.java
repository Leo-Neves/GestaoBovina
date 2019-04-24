package br.agr.terras.materialdroid.utils.filter;

public final class Constant {
    public static long ANIMATION_DURATION = 400L;
    public static Constant INSTANCE;

    public final long getANIMATION_DURATION() {
        return ANIMATION_DURATION;
    }

    private Constant() {
        INSTANCE = (Constant)this;
        ANIMATION_DURATION = 400L;
    }

    static {
        new Constant();
    }
}
