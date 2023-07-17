package net.minezero.minezeroslot.slot;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;

import static net.minezero.minezeroslot.MineZeroSlot.*;
import static net.minezero.minezeroslot.command.MainCommand.*;
import static net.minezero.minezeroslot.utils.Permission.*;

public class SlotFixtures implements Listener {

    @EventHandler
    void onPlaceFixtures(BlockPlaceEvent event) throws IOException {

        if (!isOpPermission(event.getPlayer())) {
            return;
        }

        if (event.getPlayer().getMetadata(metadata).isEmpty()) {
            return;
        }

        boolean buildwating = event.getPlayer().getMetadata(metadata).get(0).asBoolean();

        if (!buildwating) {
            return;
        }

        File file = new File(plugin.getDataFolder().getPath() + "/location.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);


        if (event.getBlock().getType().equals(Material.JUKEBOX)) {

            if (placewating.get(event.getPlayer()) != 3) {
                return;
            }

            event.getPlayer().sendMessage(prefix + " §aオークの看板を設置してください");
            config.set("input." + slotname + ".x", event.getBlock().getState().getX());
            config.set("input." + slotname + ".y", event.getBlock().getState().getY());
            config.set("input." + slotname + ".z", event.getBlock().getState().getZ());
            config.set("input." + slotname + ".world", event.getBlock().getWorld().getUID().toString());
            config.save(file);
            placewating.put(event.getPlayer(), 4);
            return;
        }

    }

    @EventHandler
    void placeSign(SignChangeEvent event) throws IOException {

        if (!placewating.containsKey(event.getPlayer())) {
            return;
        }

        Sign sign = (Sign) event.getBlock().getState();

            if (placewating.get(event.getPlayer()) != 4) {
                return;
            }

            File file = new File(plugin.getDataFolder().getPath() + "/location.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);


            sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE, 0.0);

            config.set("sign." + slotname + ".x", event.getBlock().getState().getX());
            config.set("sign." + slotname + ".y", event.getBlock().getState().getY());
            config.set("sign." + slotname + ".z", event.getBlock().getState().getZ());
            config.set("sign." + slotname + ".world", event.getBlock().getWorld().getUID().toString());
            config.save(file);

            placewating.remove(event.getPlayer());
            event.getPlayer().setMetadata(metadata, new FixedMetadataValue(plugin, false));

            event.getPlayer().sendMessage(prefix + " §a§l保存しました");
    }

    @EventHandler
    void frameplace(HangingPlaceEvent event) throws IOException {

        if (event.getEntity().getType() != EntityType.ITEM_FRAME) {
            return;
        }

        if (event.getPlayer() == null) {
            return;
        }

        if (!placewating.containsKey(event.getPlayer())) {
            return;
        }

        File file = new File(plugin.getDataFolder().getPath() + "/location.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        switch (placewating.get(event.getPlayer())) {
            case 0 -> {

                if (event.getEntity().getType() == EntityType.ITEM_FRAME) {

                    config.set("frame." + slotname + ".0.x", event.getEntity().getLocation().getX());
                    config.set("frame." + slotname + ".0.y", event.getEntity().getLocation().getY());
                    config.set("frame." + slotname + ".0.z", event.getEntity().getLocation().getZ());
                    config.set("frame." + slotname + ".0.world", event.getEntity().getWorld().getUID().toString());
                    config.save(file);
                    event.getPlayer().sendMessage(prefix + " §a§l保存しました");
                    placewating.put(event.getPlayer(), 1);
                }

            }

            case 1 -> {

                if (event.getEntity().getType() == EntityType.ITEM_FRAME) {

                    config.set("frame." + slotname + ".1.x", event.getEntity().getLocation().getX());
                    config.set("frame." + slotname + ".1.y", event.getEntity().getLocation().getY());
                    config.set("frame." + slotname + ".1.z", event.getEntity().getLocation().getZ());
                    config.set("frame." + slotname + ".1.world", event.getBlock().getWorld().getUID().toString());
                    config.save(file);
                    event.getPlayer().sendMessage(prefix + " §a§l保存しました");
                    placewating.put(event.getPlayer(), 2);
                }

            }

            case 2 -> {

                if (event.getEntity().getType() == EntityType.ITEM_FRAME) {

                    config.set("frame." + slotname + ".2.x", event.getEntity().getLocation().getX());
                    config.set("frame." + slotname + ".2.y", event.getEntity().getLocation().getY());
                    config.set("frame." + slotname + ".2.z", event.getEntity().getLocation().getZ());
                    config.set("frame." + slotname + ".2.world", event.getBlock().getWorld().getUID().toString());
                    config.save(file);
                    event.getPlayer().sendMessage(prefix + " §a§l保存しました");
                    placewating.put(event.getPlayer(), 3);
                    event.getPlayer().sendMessage(prefix + " §aジュークボックスを設置してください");
                }
            }
            default -> event.getPlayer().sendMessage(prefix + " §a§lakan");
        }
    }
}




