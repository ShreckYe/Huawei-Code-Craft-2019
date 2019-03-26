package com.huawei.common;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    private ListUtils() {
    }

    public static <E> List<E> concatLists(List<E> list1, List<E> list2) {
        ArrayList<E> list = new ArrayList<>(list1.size() + list2.size());
        list.addAll(list1);
        list.addAll(list2);
        return list;
    }
}
