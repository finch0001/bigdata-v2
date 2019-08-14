package com.yee.bigdata.common.collection;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A utility class that can convert a HashMap of Properties into a colon separated string,
 * and can take the same format of string and convert it to a HashMap of Properties.
 */
public class StringableMap extends HashMap<String, String> {

    public StringableMap(String s) {
        String[] parts = s.split(":", 2);
        // read that many chars
        int numElements = Integer.parseInt(parts[0]);
        s = parts[1];
        for (int i = 0; i < numElements; i++) {
            // Get the key String.
            parts = s.split(":", 2);
            int len = Integer.parseInt(parts[0]);
            String key = "";     // Default is now an empty string.

            if (len > 0) key = parts[1].substring(0, len);
                // Please check the toString() method of this class.
                // null has -1 as length and empty String has 0.
            else if (len < 0) {
                key = null;
                len = 0;         // Set 0 to 'len' for null-valued key
                // since only len exists for null-valued key from the given String "s".
            }

            // Get the value String for the key
            parts = parts[1].substring(len).split(":", 2);
            len = Integer.parseInt(parts[0]);
            String value = "";

            if (len > 0) value = parts[1].substring(0, len);
            else if (len < 0) {
                value = null;
                len = 0;        // Set 0 to 'len' since only len exists.
            }

            // Put the entry into the HashMap<String, String>.
            put(key, value);

            // Move to the next substring to process.
            s = parts[1].substring(len);
        }
    }

    public StringableMap(Map<String, String> m) {
        super(m);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(size());
        buf.append(':');
        if (size() > 0) {
            for (Map.Entry<String, String> entry : entrySet()) {
                // Append the key String to the output StringBuilder.
                // Note that null is saved as -1 in length, and that empty String as 0.
                int length = (entry.getKey() == null) ? -1 : entry.getKey().length();
                buf.append(length);
                buf.append(':');
                if (length > 0) buf.append(entry.getKey());

                // Append the value String to the output StringBuilder.
                // Note that null is saved as -1 in length, and that empty String as 0.
                length = (entry.getValue() == null) ? -1 : entry.getValue().length();
                buf.append(length);
                buf.append(':');
                if (length > 0) {
                    buf.append(entry.getValue());
                }
            }
        }
        return buf.toString();
    }

    public Properties toProperties() {
        Properties props = new Properties();
        props.putAll(this);
        return props;
    }
}
