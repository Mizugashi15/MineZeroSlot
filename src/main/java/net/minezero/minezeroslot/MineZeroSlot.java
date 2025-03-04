package net.minezero.minezeroslot;

import net.minezero.minezeroslot.command.MainCommand;
import net.minezero.minezeroslot.slot.SlotData;
import net.minezero.minezeroslot.slot.SlotEvent;
import net.minezero.minezeroslot.slot.SlotFixtures;
import net.minezero.minezeroslot.slot.SlotSetting;
import net.minezero.minezeroslot.utils.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class MineZeroSlot extends JavaPlugin {

    static public JavaPlugin plugin;
    static public VaultManager vault;
    static public String prefix = "§f[§eCasino§f]§r";
    static public List<String> filenames = new ArrayList<>();
    static public String metadata = "slotbuildwating";
    static public String slotOpPermission = "minezero.admin";
    static public String slotUsePermission = "minezero.slot.use";
    static public HashMap<Player, Integer> placewating = new HashMap<>();
    static public HashMap<String, SlotData> slotdatamap = new HashMap<>();
    static public HashMap<String, List<Location>> framedatamap = new HashMap<>();
    static public HashMap<String, List<ItemFrame>> frames = new HashMap<>();
    static public HashMap<Location, String> inputdatamap = new HashMap<>();
    static public HashMap<String, Location> signdatamap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;
        vault = new VaultManager(plugin);
        Bukkit.getPluginManager().registerEvents(new SlotFixtures(), plugin);
        Bukkit.getPluginManager().registerEvents(new SlotEvent(), plugin);
        Objects.requireNonNull(getCommand("slot")).setExecutor(new MainCommand());
        saveDefaultConfig();

//        try {

            plugin.getDataFolder().mkdir();

            File file = new File(plugin.getDataFolder().getPath() + "/slots");
            file.mkdir();

            for (File f : Objects.requireNonNull(file.listFiles())) {

                if (f.getName().substring(f.getName().lastIndexOf(".") + 1).equalsIgnoreCase("yml")) {
                    filenames.add(f.getName().substring(0, f.getName().indexOf(".")));
                }

                String slotname = f.getName().substring(0, f.getName().indexOf("."));

                slotdatamap.put(slotname, SlotSetting.getSlotData(slotname));

                List<Location> framelocations = new ArrayList<>();
                File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(locationFile);
                Location location;
                if (config.getBoolean("frame." + slotname + ".type")) {
                    for (int i = 0 ; i <= 2 ; i++) {
                        location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + slotname + "." + i + ".world"))), config.getDouble("frame." + slotname + "." + i + ".x"), config.getDouble("frame." + slotname + "." + i + ".y"), config.getDouble("frame." + slotname + "." + i + ".z"));
                        framelocations.add(location);
                    }
                } else {
                    for (int i = 0 ; i <= 8 ; i++) {
                        location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + slotname + "." + i + ".world"))), config.getDouble("frame." + slotname + "." + i + ".x"), config.getDouble("frame." + slotname + "." + i + ".y"), config.getDouble("frame." + slotname + "." + i + ".z"));
                        framelocations.add(location);
                    }
                }

                framedatamap.put(slotname, framelocations);

                List<ItemFrame> itemFrames = new ArrayList<>();

                if (config.getBoolean("frame." + slotname + ".type")) {
                    for (int i = 0 ; i <= 2 ; i++) {
                        for (Entity entity : framelocations.get(i).getChunk().getEntities()) {
                            location = new Location(entity.getWorld(), entity.getLocation().getBlock().getX(), entity.getLocation().getBlock().getY(), entity.getLocation().getBlock().getZ());
                            if (location.equals(framelocations.get(i)) && entity.getType() == EntityType.ITEM_FRAME) {
                                itemFrames.add((ItemFrame) entity);
                            }
                        }
                    }
                } else {
                    for (int i = 0 ; i <= 8 ; i++) {
                        for (Entity entity : framelocations.get(i).getChunk().getEntities()) {
                            location = new Location(entity.getWorld(), entity.getLocation().getBlock().getX(), entity.getLocation().getBlock().getY(), entity.getLocation().getBlock().getZ());
                            if (location.equals(framelocations.get(i)) && entity.getType() == EntityType.ITEM_FRAME) {
                                itemFrames.add((ItemFrame) entity);
                            }
                        }
                    }
                }

                frames.put(slotname, itemFrames);

                location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("input." + slotname + ".world"))), config.getDouble("input." + slotname + ".x"), config.getDouble("input." + slotname + ".y"), config.getDouble("input." + slotname + ".z"));

                inputdatamap.put(location, slotname);

                location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("sign." + slotname + ".world"))), config.getDouble("sign." +slotname + ".x"), config.getDouble("sign." + slotname + ".y"), config.getDouble("sign." + slotname + ".z"));

                signdatamap.put(slotname, location);

            }

//        } catch (NullPointerException ignore) {
//        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
