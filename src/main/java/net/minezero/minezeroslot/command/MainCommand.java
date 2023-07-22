package net.minezero.minezeroslot.command;

import net.minezero.minezeroslot.slot.SlotSetting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.minezero.minezeroslot.MineZeroSlot.*;
import static net.minezero.minezeroslot.utils.Permission.*;

public class MainCommand implements CommandExecutor {

    public static String slotname;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!isOpPermission((Player) sender)) {

            sender.sendMessage(prefix + " §c権限がありません！");
            return true;
        }

        if (args.length == 0) {

            sender.sendMessage(prefix);
            sender.sendMessage(" §7/slot §ebuild [スロット名] §7➡ §f[スロット名]の名前でスロットを設置します");
            sender.sendMessage(" §7/slot §eremove [スロット名] §7➡ §f[スロット名]のスロットを撤去します");
            sender.sendMessage(" §7/slot §ereload §7➡ §fスロットデータをリロードします");
            sender.sendMessage(" §7/slot §elist §7➡ §fスロットの一覧を表示します");
            sender.sendMessage(" §7/coin §ebuy [枚数] §7➡ §fコインを[枚数]枚購入します");

            return true;
        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list")) {

                sender.sendMessage(prefix);

                for (String s : filenames) {

                    sender.sendMessage(" §f- §e" + s);

                }
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {

                File file = new File(plugin.getDataFolder().getPath() + "/slots");

                filenames.clear();
                slotdatamap.clear();
                framedatamap.clear();
                inputdatamap.clear();
                signdatamap.clear();

                for (File f : Objects.requireNonNull(file.listFiles())) {

                    if (f.getName().substring(f.getName().lastIndexOf(".") + 1).equalsIgnoreCase("yml")) {
                        filenames.add(f.getName().substring(0, f.getName().indexOf(".")));
                    }

                    String slotname = f.getName().substring(0, f.getName().indexOf("."));

                    slotdatamap.put(slotname, SlotSetting.getSlotData(slotname));

                    List<Location> framelocations = new ArrayList<>();
                    File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
                    FileConfiguration config = YamlConfiguration.loadConfiguration(locationFile);
                    Location location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + slotname + ".0.world"))), config.getDouble("frame." + slotname + ".0.x"), config.getDouble("frame." + slotname + ".0.y"), config.getDouble("frame." + slotname + ".0.z"));
                    framelocations.add(location);
                    location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + slotname + ".1.world"))), config.getDouble("frame." + slotname + ".1.x"), config.getDouble("frame." + slotname + ".1.y"), config.getDouble("frame." + slotname + ".1.z"));
                    framelocations.add(location);
                    location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + slotname + ".2.world"))), config.getDouble("frame." + slotname + ".2.x"), config.getDouble("frame." + slotname + ".2.y"), config.getDouble("frame." + slotname + ".2.z"));
                    framelocations.add(location);

                    framedatamap.put(slotname, framelocations);

                    List<ItemFrame> itemFrames = new ArrayList<>();

                    for (Entity entity : framelocations.get(0).getChunk().getEntities()) {
                        location = new Location(entity.getWorld(), entity.getLocation().getBlock().getX(), entity.getLocation().getBlock().getY(), entity.getLocation().getBlock().getZ());
                        if (location.equals(framelocations.get(0)) && entity.getType() == EntityType.ITEM_FRAME) {
                            itemFrames.add((ItemFrame) entity);
                        }
                    }
                    for (Entity entity : framelocations.get(1).getChunk().getEntities()) {
                        location = new Location(entity.getWorld(), entity.getLocation().getBlock().getX(), entity.getLocation().getBlock().getY(), entity.getLocation().getBlock().getZ());
                        if (location.equals(framelocations.get(1)) && entity.getType() == EntityType.ITEM_FRAME) {
                            itemFrames.add((ItemFrame) entity);
                        }
                    }
                    for (Entity entity : framelocations.get(2).getChunk().getEntities()) {
                        location = new Location(entity.getWorld(), entity.getLocation().getBlock().getX(), entity.getLocation().getBlock().getY(), entity.getLocation().getBlock().getZ());
                        if (location.equals(framelocations.get(2)) && entity.getType() == EntityType.ITEM_FRAME) {
                            itemFrames.add((ItemFrame) entity);
                        }
                    }

                    frames.put(slotname, itemFrames);

                    location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("input." + slotname + ".world"))), config.getDouble("input." + slotname + ".x"), config.getDouble("input." + slotname + ".y"), config.getDouble("input." + slotname + ".z"));

                    inputdatamap.put(location, slotname);

                    location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("sign." + slotname + ".world"))), config.getDouble("sign." +slotname + ".x"), config.getDouble("sign." + slotname + ".y"), config.getDouble("sign." + slotname + ".z"));

                    signdatamap.put(slotname, location);

                }

                sender.sendMessage(prefix + " §areload done");
                return true;
            }
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("build")) {

                slotname = args[1];
                Player player = (Player) sender;

                player.setMetadata(metadata, new FixedMetadataValue(plugin, true));
                placewating.put(player, 0);
                player.sendMessage(prefix + " §a額縁を設置してください");
                player.sendMessage(prefix + " §a置いた順番から1 2 3と番号付けられます");

                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {

                File file = new File(plugin.getDataFolder().getPath() + "/slots/" + args[1] + ".yml");

                if (file.exists()) {

                    file.delete();
                    sender.sendMessage(prefix + " §a" + args[1] + "を削除しました");
                    return true;
                } else {
                    sender.sendMessage(prefix + " §cスロットが見つかりません！");
                    return false;
                }
            }
    }
        sender.sendMessage(prefix + " §c使い方が間違っています");
        return false;
    }

}
