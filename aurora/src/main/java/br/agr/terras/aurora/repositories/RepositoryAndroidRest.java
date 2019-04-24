package br.agr.terras.aurora.repositories;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import br.agr.terras.aurora.entities.EntityAndroid;
import br.agr.terras.aurora.entities.EntityWeb;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.WhereCondition.StringCondition;

public abstract class RepositoryAndroidRest<W extends EntityWeb, A extends EntityAndroid> implements RepositoryRest<W, A>, Repository<A> {
    private AbstractDao<A, String> dao;

    protected Class<A> clazz;

    public AbstractDao<A, String> getDao() {
        return dao;
    }

    public RepositoryAndroidRest(AbstractDao<A, String> dao) {
        this.dao = dao;
        configureClass();
    }

    private void configureClass() {
        try {
            if (getClass().getGenericSuperclass() instanceof ParameterizedType)
                clazz = (Class<A>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        } catch (ClassCastException ignored) {
        }
    }

    public A novo(String uuid) {
        try {
            Constructor[] ctors = clazz.getDeclaredConstructors();
            Constructor ctor = null;
            for (int i = 0; i < ctors.length; i++) {
                ctor = ctors[i];
                if (ctor.getGenericParameterTypes().length == 0)
                    break;
            }
            if (ctor != null) {
                ctor.setAccessible(true);
                A a = (A) ctor.newInstance();
                a.setUuid(uuid);
                return a;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void salvarSemTransaction(A entityAndroid) {
        save(entityAndroid);
    }

    public void save(A entityAndroid) {
        entityAndroid.setModified(new Date());
        entityAndroid.setSincronizado(false);
        entityAndroid.setPre_excluido(false);
        dao.insertOrReplace(entityAndroid);
        Log.i("RepositoryAndroidRest", "saved entityAndroid " + entityAndroid.getClass().getName() + " " + entityAndroid.getUuid());
    }

    public void update(A entityAndroid) {
        entityAndroid.setModified(new Date());
        entityAndroid.setSincronizado(false);
        dao.update(entityAndroid);
    }

    public A get(String id) {
        return dao.load(id);

    }

    public A findByUuid(String uuid) {
        A entity = null;
        List<A> list = findAll();
        for (A a : list) {
            if (a.getUuid().equals(uuid)) {
                entity = a;
            }
        }
        return entity;
    }

    public List<A> findAll() {
        return dao.loadAll();
    }

    public void saveMultiple(List<A> entitiesAndroids) {
        for (A entityAndroid : entitiesAndroids) {
            entityAndroid.setSincronizado(false);
            entityAndroid.setPre_excluido(false);
        }
        dao.insertOrReplaceInTx(entitiesAndroids);
    }

    public void saveMultipleRest(List<A> entitiesAndroids) {
        dao.insertOrReplaceInTx(entitiesAndroids);
    }

    @Override
    public void updateAllToDeleteRest() {
        List<A> entitiesAndroids = findAllWebs();
        for (A entityAndroid : entitiesAndroids) {
            entityAndroid.setPre_excluido(true);
        }
        dao.insertOrReplaceInTx(entitiesAndroids);
    }

    @Override
    public void updateRestCallback(A entityAndroid) {
        entityAndroid.setSincronizado(true);
        entityAndroid.setPre_excluido(false);
        dao.update(entityAndroid);

    }

    public void saveRestCallback(A entityAndroid) {
        entityAndroid.setSincronizado(true);
        entityAndroid.setPre_excluido(false);
        dao.insertOrReplace(entityAndroid);
    }


    @Override
    public void deleteAllExcluidosRest() {
        StringCondition sc = new StringCondition("pre_excluido = 1");
        dao.deleteInTx(getDao().queryBuilder()
                .where(sc)
                .list());
    }

    @Override
    public List<A> findAllNaoExcluidos() {
        StringCondition sc = new StringCondition("(pre_excluido = 0 or pre_excluido IS NULL)");
        return getDao().queryBuilder()
                .where(sc)
                .list();
    }


    public long count() {
        return dao.count();
    }


    @Override
    public void deleteRestCallback(A entityAndroid) {
        dao.delete(entityAndroid);
    }


    public List<A> findAllWebs() {
        StringCondition sc = new StringCondition("uuid IS NOT NULL");
        return getDao().queryBuilder()
                .where(sc)
                .list();
    }

    public A getByUuId(String id) {
        StringCondition sc = new StringCondition("uuid = '" + id + "'");
        List<A> list = getDao().queryBuilder()
                .where(sc)
                .list();

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public A getLastWebId() {

        StringCondition sc = new StringCondition("pre_excluido = 0 and sincronizado = 1");
        List<A> list = getDao().queryBuilder()
                .where(sc).orderRaw("uuid desc")
                .list();

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public A getLastModified() {

        StringCondition sc = new StringCondition("pre_excluido = 0 and sincronizado = 1");
        List<A> list = getDao().queryBuilder()
                .where(sc).orderRaw("uuid desc")
                .list();

        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


    @Override
    public List<A> findAllNaoExcluidosWebs() {
        StringCondition sc3 = new StringCondition("(pre_excluido = 0 or pre_excluido IS NULL)");
        return getDao().queryBuilder()
                .where(sc3)
                .list();
    }

    public long size() {
        return getDao().queryBuilder().buildCount().count();
    }

    @Override
    public List<A> findAllNaoSincronizadosUpdate() {

        StringCondition sc = new StringCondition("(pre_excluido = 0 or pre_excluido IS NULL)");
        return getDao().queryBuilder()
                .where(sc).list();
    }

    @Override
    public List<A> findAllNaoSincronizadosSave() {
        StringCondition sc1 = new StringCondition("(pre_excluido = 0 or pre_excluido IS NULL)");
        StringCondition sc2 = new StringCondition("sincronizado = 0");
        return getDao().queryBuilder()
                .where(sc1, sc2).list();
    }

    public void atualizarObjeto(AtualizacaoThread atualizacaoThread) {
        atualizacaoThread.podeAtualizar();
    }

    public void iniciarEdicaoDados() {

    }

    public void finalizarEdicaoDados() {

    }

    public void atualizarThreadAtual() {

    }

    public abstract void cancelarEdicaoDeDados();

    public interface AtualizacaoThread {
        void podeAtualizar();
    }
}
