package pl.suseu.bfactions.placeholder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Placeholders {

    private final Map<String, String> placeholders = new ConcurrentHashMap<>();

    public void setPlaceholder(String placeholder, String value) {
        this.placeholders.put(placeholder, value);
    }

    public String getPlaceholder(String placeholder) {
        if (!this.placeholders.containsKey(placeholder)) {
            return "";
        }
        return this.placeholders.get(placeholder);
    }

}
