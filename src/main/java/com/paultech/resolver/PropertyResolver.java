package com.paultech.resolver;

import com.paultech.annotation.AfterInjection;
import com.paultech.annotation.FieldNamingPolicy;
import com.paultech.annotation.PropertyName;
import com.paultech.annotation.PropertySource;
import com.paultech.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Properties;
import java.util.Set;

public class PropertyResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyResolver.class);
    public <T> T getProperty(Class<T> propertySourceClass) throws Exception {
        if (propertySourceClass.isAnnotationPresent(PropertySource.class)) {
            T propertySourceBean = propertySourceClass.newInstance();

            Properties properties = readProperties(propertySourceClass);

            injectPropertyValue(propertySourceClass, propertySourceBean, properties);
            invokeAfterInjection(propertySourceClass, propertySourceBean, properties);

            return propertySourceBean;
        }
        return null;
    }

    private <T> Properties readProperties(Class<T> propertySourceClass) {
        PropertySource propertySource = propertySourceClass.getDeclaredAnnotation(PropertySource.class);
        String propertyPath = new File(propertySource.value()).getAbsolutePath();
        return readPropertyFromPath(propertyPath);
    }

    private <T> void injectPropertyValue(Class<T> propertySourceClass, T propertySourceBean, Properties properties) throws IllegalAccessException {
        PropertySource propertySource = propertySourceClass.getDeclaredAnnotation(PropertySource.class);
        FieldNamingPolicy fieldNamingPolicy = propertySource.fieldNamingPolicy();

        Field[] declaredFields = propertySourceClass.getDeclaredFields();
        Set<String> propertyNames = properties.stringPropertyNames();
        for (Field declaredField : declaredFields) {

            String fieldName = declaredField.getName();

            boolean isAssignedFieldName = false;
            if (declaredField.isAnnotationPresent(PropertyName.class)) {
                PropertyName propertyNameAnnotation = declaredField.getDeclaredAnnotation(PropertyName.class);
                fieldName = propertyNameAnnotation.value();
                isAssignedFieldName = true;
            }


            for (String propertyName : propertyNames) {
                String convertedPropertyName = propertyName;

                if (!isAssignedFieldName) {
                    convertedPropertyName = convertPropertyName(propertyName, fieldNamingPolicy);
                }

                if (fieldName.equals(convertedPropertyName)) {
                    if (!declaredField.isAccessible()) {
                        declaredField.setAccessible(true);
                    }
                    declaredField.set(propertySourceBean, properties.getProperty(propertyName));
                    break;
                }
            }
        }
    }

    private <T> void invokeAfterInjection(Class<T> propertySourceClass, T propertySourceBean, Properties properties) throws InvocationTargetException, IllegalAccessException {
        Method[] declaredMethods = propertySourceClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(AfterInjection.class)) {

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                if (method.getParameterCount() == 0) {
                    method.invoke(propertySourceBean);
                } else if (method.getParameterCount() == 1) {
                    Parameter parameter = method.getParameters()[0];
                    if (parameter.getType().isAssignableFrom(Properties.class)) {
                        method.invoke(propertySourceBean, properties);
                    }
                }

            }
        }
    }

    private String convertPropertyName(String propertyName, FieldNamingPolicy fieldNamingPolicy) {
        String result = null;
        switch (fieldNamingPolicy) {
            case DASH_SEPARATED_TO_CAMEL_CASE:
                result = StringUtil.convertStringToCamelCase(propertyName, "-");
                break;
            case DOT_SEPARATED_TO_CAMEL_CASE:
                result = StringUtil.convertStringToCamelCase(propertyName, "\\.");
                break;
            case EXACTLY_MATCH:
                result = propertyName;
                break;
        }
        return result;
    }

    private Properties readPropertyFromPath(String path) {
        Properties properties = new Properties();
        try {
            FileReader fileReader = new FileReader(new File(path));
            properties.load(fileReader);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return properties;
    }
}
