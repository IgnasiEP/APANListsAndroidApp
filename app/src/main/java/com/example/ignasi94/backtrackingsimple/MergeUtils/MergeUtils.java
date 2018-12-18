package com.example.ignasi94.backtrackingsimple.MergeUtils;

import com.example.ignasi94.backtrackingsimple.Estructuras.Dog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Grafo.DogGraf.TupleDog;
import com.example.ignasi94.backtrackingsimple.Estructuras.Volunteer;

import java.util.ArrayList;

public final class MergeUtils {
    public static void MergeByIdCage(Dog[] data) {
        if (data.length <= 1) return;               // Base case: just 1 elt

        Dog[] a = new Dog[data.length / 2];        // Split array into two
        Dog[] b = new Dog[data.length - a.length]; //   halves, a and b
        for (int i = 0; i < data.length; i++) {
            if (i < a.length) a[i] = data[i];
            else b[i - a.length] = data[i];
        }

        MergeByIdCage(a);                              // Recursively sort first
        MergeByIdCage(b);                              //   and second half.

        int ai = 0;                                // Merge halves: ai, bi
        int bi = 0;                                //   track position in
        while (ai + bi < data.length) {             //   in each half.
            if (bi >= b.length || (ai < a.length && a[ai].idCage < b[bi].idCage)) {
                data[ai + bi] = a[ai]; // (copy element of first array over)
                ai++;
            } else {
                data[ai + bi] = b[bi]; // (copy element of second array over)
                bi++;
            }
        }
    }

    public static void MergeBySizeElements(ArrayList<ArrayList<Dog>> data) {
        if (data.size() <= 1) return;               // Base case: just 1 elt

        ArrayList<ArrayList<Dog>> a = new ArrayList<ArrayList<Dog>>();        // Split array into two
        ArrayList<ArrayList<Dog>> b = new ArrayList<ArrayList<Dog>>(); //   halves, a and b
        for (int i = 0; i < data.size(); i++) {
            if (i < (data.size()/2)) a.add((ArrayList<Dog>)data.get(i).clone());
            else b.add((ArrayList<Dog>)data.get(i).clone());
        }

        MergeBySizeElements(a);                              // Recursively sort first
        MergeBySizeElements(b);                              //   and second half.

        int ai = 0;                                // Merge halves: ai, bi
        int bi = 0;                                //   track position in
        while (ai + bi < data.size()) {             //   in each half.
            if (bi >= b.size() || (ai < a.size() && a.get(ai).size() > b.get(bi).size())) {
                data.remove(ai+bi);
                data.add(ai + bi,a.get(ai)); // (copy element of first array over)
                ai++;
            } else {
                data.remove(ai+bi);
                data.add(ai + bi,b.get(bi)); // (copy element of second array over)
                bi++;
            }
        }
    }

    public static void MergeByFavouriteDogsSize(ArrayList<Volunteer> data) {
        if (data.size() <= 1) return;               // Base case: just 1 elt

        ArrayList<Volunteer> a = new ArrayList<Volunteer>();        // Split array into two
        ArrayList<Volunteer> b = new ArrayList<Volunteer>(); //   halves, a and b
        for (int i = 0; i < data.size(); i++) {
            if (i < (data.size()/2)) a.add(data.get(i));
            else b.add(data.get(i));
        }

        MergeByFavouriteDogsSize(a);                              // Recursively sort first
        MergeByFavouriteDogsSize(b);                              //   and second half.

        int ai = 0;                                // Merge halves: ai, bi
        int bi = 0;                                //   track position in
        while (ai + bi < data.size()) {             //   in each half.
            if (bi >= b.size() || (ai < a.size() && a.get(ai).favouriteDogs.size() < b.get(bi).favouriteDogs.size())) {
                data.remove(ai+bi);
                data.add(ai + bi,a.get(ai)); // (copy element of first array over)
                ai++;
            } else {
                data.remove(ai+bi);
                data.add(ai + bi,b.get(bi)); // (copy element of second array over)
                bi++;
            }
        }
    }
}
