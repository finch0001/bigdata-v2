package com.yee.bigdata.common.util;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

public class ServerProperty {

    /**
     * Read a properties file from the given path
     * @param filename The path of the file to read
     */
    public static Properties loadProps(String filename) throws IOException {
        Properties props = new Properties();

        if (filename != null) {
            try (InputStream propStream = Files.newInputStream(Paths.get(filename))) {
                props.load(propStream);
            }
        } else {
            System.out.println("Did not load any properties since the property file is not specified");
        }

        return props;
    }

    /**
     * Converts a Properties object to a Map<String, String>, calling {@link #toString} to ensure all keys and values
     * are Strings.
     */
    public static Map<String, String> propsToStringMap(Properties props) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : props.entrySet())
            result.put(entry.getKey().toString(), entry.getValue().toString());
        return result;
    }

    /**
     * Creates a {@link Properties} from a map
     *
     * @param properties A map of properties to add
     * @return The properties object
     */
    public static Properties mkProperties(final Map<String, String> properties) {
        final Properties result = new Properties();
        for (final Map.Entry<String, String> entry : properties.entrySet()) {
            result.setProperty(entry.getKey(), entry.getValue());
        }
        return result;
    }


}
