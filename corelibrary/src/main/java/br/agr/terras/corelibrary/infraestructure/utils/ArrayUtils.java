package br.agr.terras.corelibrary.infraestructure.utils;

import android.util.SparseArray;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 21/11/16.
 */

public class ArrayUtils {

    public static <T> List<T> sparseArrayToList(SparseArray<T> sparseArray) {
        if (sparseArray == null) return null;
        List<T> arrayList = new ArrayList<T>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static <T> T[] removeDuplicates(T[] arr, Class<T> clazz){
        int end = arr.length;
        for (int i = 0; i < end; i++) {
            for (int j = i + 1; j < end; j++) {
                if (arr[i].equals(arr[j])) {
                    arr[j] = arr[end-1];
                    end--;
                    j--;
                }
            }
        }
        T[] whitelist = (T[]) Array.newInstance(clazz, end);
        System.arraycopy(arr, 0, whitelist, 0, end);
        return whitelist;
    }

    public static <T> List<T> removeDuplicatesFromList(List<T> list, Class<T> clazz){
        T[] array = listToArray(list, clazz);
        array = removeDuplicates(array, clazz);
        return arrayToList(array, clazz);
    }

    public static <T> T[] listToArray(List<T> list, Class<T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        array = list.toArray(array);
        return array;
    }

    public static <T> List<T> arrayToList(T[]array, Class<T> clazz){
        List<T> list = new ArrayList<>();
        for (T t : array)
            list.add(t);
        return list;
    }
}
