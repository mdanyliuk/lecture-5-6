package task2;

import org.apache.commons.beanutils.PropertyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Task2 {

    public static <T>T loadFromProperties(Class<T> cls, Path propertiesPath) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException, InstantiationException {

        T instance = null;
        instance = cls.getConstructor().newInstance();

        Properties properties;
        try (InputStream input = Files.newInputStream(propertiesPath)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return instance;
        }

        for (Field field : cls.getDeclaredFields()) {
            String propertyNameInClass = field.getName();
            String propertyNameInFile = propertyNameInClass;
            String propertyFormat = "";
            if (field.isAnnotationPresent(Property.class)) {
                Property property = field.getAnnotation(Property.class);
                if (!"".equals(property.name())) {
                    propertyNameInFile = property.name();
                }
                if (!"".equals(property.format())) {
                    propertyFormat = property.format();
                }
            }
            String propertyValue = properties.getProperty(propertyNameInFile);
            if (propertyValue != null) {
                Class propertyClass = PropertyUtils.getPropertyDescriptor(instance, propertyNameInClass).getPropertyType();
                switch (propertyClass.getName()) {
                    case "Integer", "int" ->
                            PropertyUtils.setProperty(instance, propertyNameInClass, Integer.parseInt(propertyValue));
                    case "java.lang.String" -> PropertyUtils.setProperty(instance, propertyNameInClass, propertyValue);
                    case "java.time.Instant" -> {
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(propertyFormat);
                        LocalDateTime localDateTime = LocalDateTime.parse(propertyValue, dateTimeFormatter);
                        PropertyUtils.setProperty(instance, propertyNameInClass, localDateTime.toInstant(ZoneOffset.UTC));
                    }
                    default -> throw new RuntimeException("Unknown type " + propertyClass.getName());
                }
            }
        }

        return instance;
    }

}