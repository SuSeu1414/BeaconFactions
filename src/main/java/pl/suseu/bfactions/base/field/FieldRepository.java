package pl.suseu.bfactions.base.field;

import pl.suseu.bfactions.BFactions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FieldRepository {

    private final BFactions plugin;

    private final Map<UUID, Field> fields = new ConcurrentHashMap<>();

    public FieldRepository(BFactions plugin) {
        this.plugin = plugin;
    }

    public Field getField(UUID uuid) {
        return this.fields.get(uuid);
    }

    public void addField(Field field) {
        this.fields.put(field.getUuid(), field);
    }

    public void removeField(Field field) {
        this.fields.remove(field.getUuid());
    }

    /**
     * @return a copy of regions set
     */
    public Set<Field> getFields() {
        return new HashSet<>(this.fields.values());
    }

}
