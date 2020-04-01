package pl.suseu.bfactions.util;

import java.util.Set;

public class StringArrayUtil {

    public static boolean containsIgnoreCase(Set<String> l, String s) {
        for (String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
