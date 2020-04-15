package pl.suseu.bfactions.base.field.task;

import pl.suseu.bfactions.BFactions;
import pl.suseu.bfactions.base.field.FieldRepository;

public class FieldPassiveDrainTask implements Runnable {

    private final BFactions plugin;
    private final FieldRepository fieldRepository;

    public FieldPassiveDrainTask(BFactions plugin) {
        this.plugin = plugin;
        this.fieldRepository = this.plugin.getFieldRepository();
    }

    @Override
    public void run() {
        fieldRepository.getFields().forEach(field -> {
            field.addEnergy(-field.getGuild().getRegion().getTier().getDrainAmount());
        });
    }
}
