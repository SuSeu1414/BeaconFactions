package pl.suseu.bfactions.base.region.event;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.suseu.bfactions.base.region.Region;

public class EntityRegionChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Location from;
    private final Location to;
    private final Entity entity;
    private final Region region;
    private boolean cancelled;

    public EntityRegionChangeEvent(Entity entity, Region region, Location from, Location to) {
        super(true);
        this.entity = entity;
        this.region = region;
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Entity getEntity() {
        return entity;
    }

    public Region getRegion() {
        return region;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
