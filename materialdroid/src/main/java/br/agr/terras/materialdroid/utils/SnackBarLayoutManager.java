package br.agr.terras.materialdroid.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import br.agr.terras.materialdroid.childs.SnackBarLayout;

/**
 * Created by leo on 02/06/16.
 */
public class SnackBarLayoutManager {
    private static final int MSG_TIMEOUT = 0;
    private static final int SHORT_DURATION_MS = 1500;
    private static final int LONG_DURATION_MS = 2750;
    private static SnackBarLayoutManager sSnackBarLayoutManager;
    public static SnackBarLayoutManager getInstance() {
        if (sSnackBarLayoutManager == null) {
            sSnackBarLayoutManager = new SnackBarLayoutManager();
        }
        return sSnackBarLayoutManager;
    }
    private final Object mLock;
    private final Handler mHandler;
    private SnackbarRecord mCurrentSnackbar;
    private SnackbarRecord mNextSnackbar;
    private SnackBarLayoutManager() {
        mLock = new Object();
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((SnackbarRecord) message.obj);
                        return true;
                }
                return false;
            }
        });
    }
    public interface Callback {
        void show();
        void dismiss();
    }
    public void show(int duration, Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(callback)) {
                // Means that the callback is already in the queue. We'll just update the duration
                mCurrentSnackbar.duration = duration;
                // If this is the Snackbar currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
                scheduleTimeoutLocked(mCurrentSnackbar);
                return;
            } else if (isNextSnackbar(callback)) {
                // We'll just update the duration
                mNextSnackbar.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextSnackbar = new SnackbarRecord(duration, callback);
            }
            if (mCurrentSnackbar != null) {
                // If the new Snackbar isn't in position 0, we'll cancel the current one and wait
                // in line
                cancelSnackbarLocked(mCurrentSnackbar);
            } else {
                // Otherwise, just show it now
                showNextSnackbarLocked();
            }
        }
    }
    public void dismiss(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(callback)) {
                cancelSnackbarLocked(mCurrentSnackbar);
            }
            if (isNextSnackbar(callback)) {
                cancelSnackbarLocked(mNextSnackbar);
            }
        }
    }
    /**
     * Should be called when a Snackbar is no longer displayed. This is after any exit
     * animation has finished.
     */
    public void onDismissed(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(callback)) {
                // If the callback is from a Snackbar currently show, remove it and show a new one
                mCurrentSnackbar = null;
                if (mNextSnackbar != null) {
                    showNextSnackbarLocked();
                }
            }
        }
    }
    /**
     * Should be called when a Snackbar is being shown. This is after any entrance animation has
     * finished.
     */
    public void onShown(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(callback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }
    public void cancelTimeout(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(callback)) {
                mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
            }
        }
    }
    public void restoreTimeout(Callback callback) {
        synchronized (mLock) {
            if (isCurrentSnackbar(callback)) {
                scheduleTimeoutLocked(mCurrentSnackbar);
            }
        }
    }
    private static class SnackbarRecord {
        private Callback callback;
        private int duration;
        SnackbarRecord(int duration, Callback callback) {
            this.callback = callback;
            this.duration = duration;
        }
    }
    private void showNextSnackbarLocked() {
        if (mNextSnackbar != null) {
            mCurrentSnackbar = mNextSnackbar;
            mCurrentSnackbar.callback.show();
            mNextSnackbar = null;
        }
    }
    private void cancelSnackbarLocked(SnackbarRecord record) {
        record.callback.dismiss();
    }
    private boolean isCurrentSnackbar(Callback callback) {
        return mCurrentSnackbar != null && mCurrentSnackbar.callback == callback;
    }
    private boolean isNextSnackbar(Callback callback) {
        return mNextSnackbar != null && mNextSnackbar.callback == callback;
    }
    private void scheduleTimeoutLocked(SnackbarRecord r) {
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r),
                r.duration == SnackBarLayout.LENGTH_LONG
                        ? LONG_DURATION_MS
                        : SHORT_DURATION_MS);
    }
    private void handleTimeout(SnackbarRecord record) {
        synchronized (mLock) {
            if (mCurrentSnackbar == record || mNextSnackbar == record) {
                cancelSnackbarLocked(record);
            }
        }
    }
}
