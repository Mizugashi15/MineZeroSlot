package net.minezero.minezeroslot.utils;

import org.bukkit.entity.Player;

import static net.minezero.minezeroslot.MineZeroSlot.*;

public class Permission {

    static public boolean isOpPermission(Player player) {

        if (player.hasPermission(slotOpPermission)) {
            return true;
        } else {
            return false;
        }

    }

    static public boolean isUsePermission(Player player) {

        if (player.hasPermission(slotUsePermission)) {
            return true;
        } else {
            return false;
        }

    }
}
