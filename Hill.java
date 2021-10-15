package me.remie.xeros.barrows;

import simple.api.coords.WorldPoint;
import simple.api.filters.SimplePrayers;

/**
 * Created by Seth on October 10/15/2021, 2021 at 2:46 PM
 *
 * @author Seth Davis <sethdavis321@gmail.com>
 * @Discord Reminisce#1707
 */
public enum Hill {

    AHRIM("Ahrim the Blighted", SimplePrayers.Prayers.PROTECT_FROM_MAGIC, SimplePrayers.Prayers.PIETY, new WorldPoint(3564, 3289, 0)),
    DHAROK("Dharok the Wretched", SimplePrayers.Prayers.PROTECT_FROM_MELEE, SimplePrayers.Prayers.PIETY, new WorldPoint(3574, 3299, 0)),
    GUTHAN("Guthan the Infested", SimplePrayers.Prayers.PROTECT_FROM_MELEE, SimplePrayers.Prayers.PIETY, new WorldPoint(3577, 3281, 0)),
    KARIL("Karil the Tainted", SimplePrayers.Prayers.PROTECT_FROM_MISSILES, SimplePrayers.Prayers.PIETY, new WorldPoint(3565, 3275, 0)),
    TORAG("Torag the Corrupted", SimplePrayers.Prayers.PROTECT_FROM_MELEE, SimplePrayers.Prayers.PIETY, new WorldPoint(3555, 3282, 0)),
    VERAC("Verac the Defiled", SimplePrayers.Prayers.PROTECT_FROM_MELEE, SimplePrayers.Prayers.PIETY, new WorldPoint(3558, 3298, 0)),
    ;

    public final String npcName;
    public final SimplePrayers.Prayers protectionPrayer, boostPrayer;
    public final WorldPoint stepTile;

    Hill(final String npcName, final SimplePrayers.Prayers protectionPrayer, final SimplePrayers.Prayers boostPrayer, final WorldPoint stepTile) {
        this.npcName = npcName;
        this.protectionPrayer = protectionPrayer;
        this.boostPrayer = boostPrayer;
        this.stepTile = stepTile;
    }

}
