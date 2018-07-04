package com.paultech.resolver;

import com.paultech.annotation.FieldNamingPolicy;
import com.paultech.annotation.PropertyName;
import com.paultech.annotation.PropertySource;
import com.paultech.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

public class PropertyResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyResolver.class);
    public <T> T getProperty(Class<T> propertySourceClass) throws Exception {
        if (propertySourceClass.isAnnotationPresent(PropertySource.class)) {
            PropertySource propertySource = propertySourceClass.getDeclaredAnnotation(PropertySource.class);
            String propertyPath = propertySource.value();
            Properties properties = readPropertyFromPath(propertyPath);

            FieldNamingPolicy fieldNamingPolicy = propertySource.fieldNamingPolicy();

            T propertySourceBean = propertySourceClass.newInstance();

            Field[] declaredFields = propertySourceClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {

                String fieldName = declaredField.getName();
                Set<String> propertyNames = properties.stringPropertyNames();
                for (String propertyName : propertyNames) {
                    String convertedPropertyName = propertyName;

                    if (declaredField.isAnnotationPresent(PropertyName.class)) {
                        PropertyName propertyNameAnnotation = declaredField.getDeclaredAnnotation(PropertyName.class);
                        fieldName = propertyNameAnnotation.value();
                    } else {
                        convertedPropertyName = convertPropertyName(propertyName, fieldNamingPolicy);
                    }

                    if (fieldName.equals(convertedPropertyName)) {
                        if (!declaredField.isAccessible()) {
                            declaredField.setAccessible(true);
                        }
                        declaredField.set(propertySourceBean, properties.getProperty(propertyName));
                    }
                }
            }
            return propertySourceBean;
        }
        return null;
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
