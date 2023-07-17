package net.minezero.minezeroslot;

import net.minezero.minezeroslot.command.MainCommand;
import net.minezero.minezeroslot.slot.SlotData;
import net.minezero.minezeroslot.slot.SlotEvent;
import net.minezero.minezeroslot.slot.SlotFixtures;
import net.minezero.minezeroslot.slot.SlotSetting;
import net.minezero.minezeroslot.utils.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.*;

public final class MineZeroSlot extends JavaPlugin {

    static public JavaPlugin plugin;
    static public VaultManager vault;
    static public String prefix = "§f[§eMineZero§2Slot§f]§r";
    static public List<String> filenames = new ArrayList<>();
    static public String metadata = "slotbuildwating";
    static public String slotOpPermission = "minezero.slot.op";
    static public String slotUsePermission = "minezero.slot.use";
    static public HashMap<Player, Integer> placewating = new HashMap<>();
    static public HashMap<String, SlotData> slotdatamap = new HashMap<>();
    static public HashMap<String, List<Location>> framedatamap = new HashMap<>();
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

                slotdatamap.put(f.getName(), SlotSetting.getSlotData(f.getName()));

                List<Location> framelocations = new ArrayList<>();
                File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(locationFile);
                Location location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + f.getName() + ".0.world"))), config.getDouble("frame." + f.getName() + ".0.x"), config.getDouble("frame." + f.getName() + ".0.y"), config.getDouble("frame." + f.getName() + ".0.z"));
                framelocations.add(location);
                location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + f.getName() + ".1.world"))), config.getDouble("frame." + f.getName() + ".1.x"), config.getDouble("frame." + f.getName() + ".1.y"), config.getDouble("frame." + f.getName() + ".1.z"));
                framelocations.add(location);
                location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("frame." + f.getName() + ".2.world"))), config.getDouble("frame." + f.getName() + ".2.x"), config.getDouble("frame." + f.getName() + ".2.y"), config.getDouble("frame." + f.getName() + ".2.z"));
                framelocations.add(location);

                framedatamap.put(f.getName(), framelocations);

                location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("input." + f.getName() + ".world"))), config.getDouble("input." + f.getName() + ".x"), config.getDouble("input." + f.getName() + ".y"), config.getDouble("input." + f.getName() + ".z"));

                inputdatamap.put(location, config.getConfigurationSection("input").getKeys(false).toString());

                location = new Location(Bukkit.getWorld(UUID.fromString(config.getString("sign." + f.getName() + ".world"))), config.getDouble("sign." + f.getName() + ".x"), config.getDouble("sign." + f.getName() + ".y"), config.getDouble("sign." + f.getName() + ".z"));

                signdatamap.put(f.getName(), location);

            }

//        } catch (NullPointerException ignore) {
//        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
