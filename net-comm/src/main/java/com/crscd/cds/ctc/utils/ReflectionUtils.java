package com.crscd.cds.ctc.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author zhaole
 * @date 2022-04-04
 */
public class ReflectionUtils {
    public static List<Field> getAllFieldsList(Class<?> cls) {
        Stack<Class<?>> classes = new Stack<Class<?>>();
        List<Field> allFields = new ArrayList<Field>();

        for(Class<?> currentClass = cls; currentClass != null; currentClass = currentClass.getSuperclass()) {
            classes.push(currentClass);
        }

        while (!classes.isEmpty()) {
            Class<?> currentClass = classes.pop();

            Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
        }

        return allFields;
    }
}
