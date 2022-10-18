package com.simplefanc.utils;

import java.util.Collection;

/**
 * 集合工具类
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
