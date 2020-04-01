package pl.suseu.bfactions.util;

import java.util.List;

public class StringArrayUtil {

    public static boolean containsIgnoreCase(List<String> l, String s) {
        for (String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
