package pet.sansa.gestaobovina;

import java.util.List;

import io.realm.Realm;
import pet.sansa.gestaobovina.entities.Boi;

public class BoiRepository {
    private Realm realm;

    public BoiRepository(){
        realm = Realm.getDefaultInstance();
    }

    public List<Boi> findAllBois(){
        return realm.where(Boi.class).findAll();
    }

    public void saveBoi(Boi boi){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(boi);
        realm.commitTransaction();
    }

    public void saveBois(List<Boi> bois){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(bois);
        realm.commitTransaction();
    }

    public void removeBoi(Boi boi){
        if (boi.isManaged()) {
            realm.beginTransaction();
            boi.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public Boi findById(String uuid) {
        return realm.where(Boi.class).equalTo("id", uuid).findFirst();
    }
}
