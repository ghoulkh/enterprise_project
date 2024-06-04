package com.enterprise.backend.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

/**
 * A custom BeanUtilsBean that only copies properties if the value is not null.
 */
public class NullAwareBeanUtilsBean extends BeanUtilsBean {

    /**
     * Copies the property to the destination object if the value is not null.
     *
     * @param dest  the destination object
     * @param name  the name of the property
     * @param value the value of the property
     * @throws IllegalAccessException    if the caller does not have access to the property accessor method
     * @throws InvocationTargetException if the property accessor method throws an exception
     */
    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value != null) {
            super.copyProperty(dest, name, value);
        }
    }
}