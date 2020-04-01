package pl.suseu.bfactions.util;

import java.util.Set;

public class StringUtil {

    public boolean setContainsIgnoreCase(Set<String> l, String s) {
        for (String string : l) {
            if (string.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
