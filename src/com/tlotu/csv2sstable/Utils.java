package com.tlotu.csv2sstable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 6/22/2015
 * Time: 11:04 AM
 */
public class Utils {
    private static final SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ"); //2012-02-06 20:09:08.486-05
    private static final SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ"); //2012-02-06 20:09:08-05

    public static String join(Collection<?> c) {
        return join(c, ",");
    }

    public static String join(Collection<?> c, String delimiter) {
        boolean first = true;
        String out = "";
        for (Object o : c) {
            if (first) {
                first = false;
            } else {
                out += delimiter + " ";
            }
            out += (o != null) ? o.toString() : "null";
        }
        return out;
    }

    public static Object convert(ColumnType type, String input) {
        if (input == null || input.length() == 0) {
            return null;
        }
        try {
            switch (type) {
                case Int:
                    return Integer.parseInt(input);
                case Float:
                    return Float.parseFloat(input);
                case BigInt:
                    return Long.parseLong(input);
                case Double:
                    return Double.parseDouble(input);
                case Boolean:
                    return Boolean.parseBoolean(input);
                case Timestamp:
                    input += "00"; //handle weird pg formats
                    try {
                        return DATE_FORMAT_1.parse(input);
                    } catch (ParseException ignored) {
                    }
                    try {
                        return DATE_FORMAT_2.parse(input);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static <T> T[] fill(T[] array, T value) {
        for (int i = 0; i < array.length; i++) {
            array[i] = value;
        }
        return array;
    }

    public static String[] split(String line, String delimiter) {
        String[] in = line.split(delimiter, -1);
        for (int j = 0; j < in.length; j++) {
            in[j] = in[j].trim();
        }
        return in;
    }

    public static <E extends Enum<E>> E valueOfIgnoreCase(Class<E> enumClass, String value) {
        for (E v : enumClass.getEnumConstants())
            if (v.name().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
