package net.minezero.minezeroslot.utils;

import org.bukkit.entity.Player;

import static net.minezero.minezeroslot.MineZeroSlot.*;

public class Permission {

    static public boolean isOpPermission(Player player) {

        return player.hasPermission(slotOpPermission);

    }

    static public boolean isUsePermission(Player player) {

        return player.hasPermission(slotUsePermission);

    }
}
