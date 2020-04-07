package pl.suseu.bfactions.base.region.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.suseu.bfactions.base.region.Region;
import pl.suseu.bfactions.base.user.User;

public class PlayerMoveInRegionEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Location from;
    private final Location to;
    private final Player player;
    private final User user;
    private final Region region;
    private boolean cancelled;

    public PlayerMoveInRegionEvent(Player player, User user, Region region, Location from, Location to) {
        this.player = player;
        this.user = user;
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

    public Player getPlayer() {
        return player;
    }

    public User getUser() {
        return user;
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
