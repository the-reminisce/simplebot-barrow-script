package me.remie.xeros.barrows;

import simple.api.actions.SimpleItemActions;
import simple.api.coords.WorldArea;
import simple.api.coords.WorldPoint;
import simple.api.events.ChatMessageEvent;
import simple.api.listeners.SimpleMessageListener;
import simple.api.queries.SimpleEntityQuery;
import simple.api.script.Category;
import simple.api.script.Script;
import simple.api.script.ScriptManifest;
import simple.api.script.interfaces.SimplePaintable;
import simple.api.wrappers.SimpleGroundItem;
import simple.api.wrappers.SimpleItem;
import simple.api.wrappers.SimpleNpc;

import java.awt.*;

/**
 * Created by Seth on October 10/15/2021, 2021 at 2:26 PM
 *
 * @author Seth Davis <sethdavis321@gmail.com>
 * @Discord Reminisce#1707
 */
@ScriptManifest(author = "Reminisce", name = "RBarrows - Xeros", category = Category.MONEYMAKING, version = "1.0",
        description = "Does Barrows", discord = "Reminisce#1707", servers = { "Xeros"})
public class BarrowsScript extends Script implements SimplePaintable, SimpleMessageListener {

    public String status;
    public long startTime;

    public Hill targetHill = Hill.KARIL;

    public static final WorldArea HOME_AREA = new WorldArea(
            new WorldPoint(3072, 3521, 0), new WorldPoint(3072, 3464, 0),
            new WorldPoint(3137, 3474, 0), new WorldPoint(3137, 3521, 0));

    public static final WorldArea BARROWS_AREA = new WorldArea(
            new WorldPoint(3545, 3320, 0),
            new WorldPoint(3585, 3267, 0));

    public static final String[] lootNames = { "Karil's", "Resource box", "Bolt rack", "Clue scroll"};

    @Override
    public boolean onExecute() {
        startTime = System.currentTimeMillis();
        status = "Waiting to start...";
        ctx.log("Thanks for using %s!", getName());
        return true;
    }

    private boolean presetLoaded = false;

    @Override
    public void onProcess() {
        if (HOME_AREA.containsPoint(ctx.players.getLocal())) {
            if (!presetLoaded) {
                loadPreset();
                return;
            }
            ctx.teleporter.teleportStringPath("Minigames", "Barrows");
            return;
        }
        if (BARROWS_AREA.containsPoint(ctx.players.getLocal())) {
            if (presetLoaded) {
                presetLoaded = false;
            }

            if (ctx.pathing.distanceTo(targetHill.stepTile) > 8) {
                ctx.pathing.step(targetHill.stepTile);
                ctx.sleep(650);
                return;
            }

            if (ctx.inventory.populate().filterContains("Prayer potion").isEmpty() && ctx.prayers.points() < 20) {
                ctx.magic.castHomeTeleport();
                return;
            }

            if (!ctx.inventory.populate().filter("Vial").isEmpty()) {
                ctx.inventory.next().interact(SimpleItemActions.DROP);
                ctx.sleep(750);
            }

            if (!ctx.groundItems.populate().filterContains(lootNames).filterWithin(targetHill.stepTile, 9).isEmpty()) {
                SimpleGroundItem item = ctx.groundItems.nearest().next();
                if (item != null && ctx.inventory.canPickupItem(item)) {
                    status("Looting " + item.getName());
                    item.interact();
                    return;
                }
            }

            if (!ctx.prayers.prayerActive(targetHill.protectionPrayer)) {
                ctx.prayers.prayer(targetHill.protectionPrayer, true);
                ctx.sleep(750);
            }
            if (!ctx.players.getLocal().inCombat() || ctx.players.getLocal().getInteracting() == null) {
                SimpleNpc fm = npcs().filter((n) -> n.getInteracting() != null && n.getInteracting().equals(ctx.players.getLocal()) && n.inCombat()).nearest().next();
                SimpleNpc npc = fm != null ? fm : npcs().nearest().next();
                if (npc == null) {
                    ctx.prayers.disableAll();
                    return;
                }
                status("Attacking " + npc.getName());
                npc.interact("attack");
                ctx.onCondition(() -> ctx.players.getLocal().inCombat(), 250, 12);
            } else {
                handleDrinkingPrayer();
            }
        } else {
            ctx.magic.castHomeTeleport();
        }
    }

    @Override
    public void onTerminate() {

    }

    /**
     * Drinks prayer potions when your prayer points drop below a certain amount
     */
    private void handleDrinkingPrayer() {
        if (ctx.prayers.points() > 20) {
            if (!ctx.prayers.prayerActive(targetHill.boostPrayer)) {
                ctx.prayers.prayer(targetHill.boostPrayer, true);
                ctx.sleep(750);
            }
        }
        if (ctx.prayers.points() <= 20) {
            final SimpleItem potion = ctx.inventory.populate().filterContains("Prayer potion").next();
            final int cached = ctx.prayers.points();
            status("Drinking prayer");
            if (potion != null && potion.interact("drink")) {
                ctx.onCondition(() -> ctx.prayers.points() > cached, 250, 12);
            }
        }
    }

    private void loadPreset() {
        ctx.menuActions.clickButton(19076);
        if (ctx.onCondition(() -> ctx.client.getOpenInterfaceId() == 21553, 250, 10)) {
            ctx.menuActions.sendAction(1701, -1, -1, 21593);//CHANGE HERE '21593' - Preset #2 id
            ctx.sleep(750, 1500);
            ctx.menuActions.clickButton(21566);
            ctx.onCondition(() -> ctx.client.getOpenInterfaceId() == -1, 250, 10);
        }
    }

    public final SimpleEntityQuery<SimpleNpc> npcs() {
        return ctx.npcs.populate().filter(targetHill.npcName).filter((n) -> {
            if (n == null) {
                return false;
            }
            if (n.getId() == 10) return false;
            if (n.getLocation().distanceTo(ctx.players.getLocal().getLocation()) > 15) {
                return false;
            }
            if (n.inCombat() && n.getInteracting() != null && !n.getInteracting().equals(ctx.players.getLocal())) {
                return false;
            }
            if (n.isDead()) {
                return false;
            }
            return true;
        });
    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(5, 2, 192, 58);
        g.setColor(Color.decode("#ea411c"));
        g.drawRect(5, 2, 192, 58);
        g.drawLine(8, 24, 194, 24);

        g.setColor(Color.decode("#e0ad01"));
        g.drawString("RBarrows                             v. " + "0.1", 12, 20);
        g.drawString("Time: " + ctx.paint.formatTime(System.currentTimeMillis() - startTime), 14, 42);
        g.drawString("Status: " + "", 14, 56);
    }

    @Override
    public void onChatMessage(ChatMessageEvent event) {
        //(0) []: You have successfully loaded the barrows set. <col=7a2100>(CTRL + R)
        if (event.getMessageType() == 0 && event.getSender().equals("")) {
            if (event.getMessage().contains("You have successfully loaded the")) {
                presetLoaded = true;
            }
        }
    }

    private void status(String status) { // Set's our script's status
        this.status = status;
    }

}
