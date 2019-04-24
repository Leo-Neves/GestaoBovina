package pet.sansa.gestaobovina;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import br.agr.terras.aurora.AURORA;
import br.agr.terras.aurora.log.Logger;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    private static InicioActivity activity;
    private boolean isRealmIniciado;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = this;
        AURORA.init(this);
        Realm.init(this);
        configurarRealm();
        configurarLogger();
        configurarFileProvider();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    private void configurarRealm() {
        if (!isRealmIniciado) {
            try {
                Realm.init(this);
                Realm.setDefaultConfiguration(
                        new RealmConfiguration.Builder()
                            .deleteRealmIfMigrationNeeded()
                            .build()
                );
            } catch (Exception e) {
            } finally {
                isRealmIniciado = true;
            }
        }
    }

    public static Context getContext() {
        return context;
    }

    public static InicioActivity getActivity() {
        return activity;
    }

    public static String string(int id) {
        return context.getString(id);
    }

    public static void setActivity(InicioActivity activity) {
        MyApplication.activity = activity;
    }

    private void configurarLogger() {
        Logger.init("Terras").hideThreadInfo().methodCount(0);
        Logger.i("Init Terras");
    }

    private void configurarFileProvider() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}
