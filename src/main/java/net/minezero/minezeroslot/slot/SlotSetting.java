package net.minezero.minezeroslot.slot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

import static net.minezero.minezeroslot.MineZeroSlot.*;

public class SlotSetting {

    public static SlotData getSlotData(String slot) {

        SlotData data = new SlotData();
        File slotFile = new File(plugin.getDataFolder().getPath() + "/slots/" + slot + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(slotFile);

        data.type = config.getBoolean("type");

        data.flag = true;

        data.winflag = false;

        data.winkey = "name";

        data.signnum = 0;

        data.reel1 = config.getStringList("reel1");
        data.reel2 = config.getStringList("reel2");
        data.reel3 = config.getStringList("reel3");

        data.coinamount = config.getInt("coin");

        data.spindelay = config.getInt("spindelay");

        data.stopcount = config.getInt("stopcount");

        data.reelstop1 = config.getInt("reelstop1");
        data.reelstop2 = config.getInt("reelstop2");
        data.reelstop3 = config.getInt("reelstop3");

        data.onespinsounds = config.getStringList("onespinsound");
        data.spinsounds = config.getStringList("spinsound");
        data.losesounds = config.getStringList("losesound");

        data.defaultstock = config.getDouble("stock.default");
        data.raise = config.getDouble("stock.raise");
        data.stock = config.getDouble("stock.nowstock");

        data.spincount = config.getInt("spincount");

        data.framesound = config.getBoolean("framesound");

        data.framevisible = config.getBoolean("framevisible");

        data.chance = config.getInt("chance");

        data.probs.add(data.chance);

        for (String key : config.getConfigurationSection("win").getKeys(false)) {

            data.win_name.add(key);

            try {
                data.win_message.put(key, config.getString("win." + key + ".message"));
            } catch (NullPointerException ignore) {
            }

            try {
                data.win_playermessage.put(key, config.getString("win." + key + ".playermessage"));
            } catch (NullPointerException ignore) {
            }

            data.win_symbolcustom.put(key, config.getBoolean("win." + key + ".symbolcustom"));

            if (data.type) {
                data.win_symbols.put(key, config.getStringList("win." + key + ".symbols"));
            } else {
                data.win_symbol3.put(key, config.getIntegerList("win." + key + ".symbol3"));
            }

            data.win_chance.put(key, config.getInt("win." + key + ".chance"));

            data.probs.add(data.win_chance.get(key));

            data.allProb += data.win_chance.get(key);

            data.win_sounds.put(key, (List<String>) config.getList("win." + key + ".winsound"));

            data.win_pot.put(key, config.getBoolean("win." + key + ".pot"));

            try {
                data.win_actions.put(key, (List<String>) config.getList("win." + key + ".actions"));
            } catch (NullPointerException ignore) {
            }

            try {
                data.win_commands.put(key, (List<String>) config.getList("win." + key + ".commands"));
            } catch (NullPointerException ignore) {
            }

        }

        data.allProb += data.chance;

        return data;
    }

//    public static SlotData getFrameLocation(String slot) {
//
//        File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
//        FileConfiguration locConfig = YamlConfiguration.loadConfiguration(locationFile);
//
//        SlotData data = new SlotData();
//
//        data.frameLocationX1 = locConfig.getDouble("frame." + slot + ".0.x");
//        data.frameLocationY1 = locConfig.getDouble("frame." + slot + ".0.y");
//        data.frameLocationZ1 = locConfig.getDouble("frame." + slot + ".0.z");
//        data.frameWorld1 = locConfig.getString("frame." + slot + ".0.world");
//
//        data.frameLocationX2 = locConfig.getDouble("frame." + slot + ".1.x");
//        data.frameLocationY2 = locConfig.getDouble("frame." + slot + ".1.y");
//        data.frameLocationZ2 = locConfig.getDouble("frame." + slot + ".1.z");
//        data.frameWorld2 = locConfig.getString("frame." + slot + ".1.world");
//
//        data.frameLocationX3 = locConfig.getDouble("frame." + slot + ".2.x");
//        data.frameLocationY3 = locConfig.getDouble("frame." + slot + ".2.y");
//        data.frameLocationZ3 = locConfig.getDouble("frame." + slot + ".2.z");
//        data.frameWorld3 = locConfig.getString("frame." + slot + ".2.world");
//
//        return data;
//    }

//    public static SlotData getInputLocation(String slot) {
//
//        File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
//        FileConfiguration locConfig = YamlConfiguration.loadConfiguration(locationFile);
//
//        SlotData data = new SlotData();
//
//        data.coinInputLocationX = locConfig.getDouble("input." + slot + ".x");
//        data.coinInputLocationY = locConfig.getDouble("input." + slot + ".y");
//        data.coinInputLocationZ = locConfig.getDouble("input." + slot + ".z");
//        data.coinInputWorld = locConfig.getString("input." + slot + ".world");
//
//        return data;
//    }

//    public static SlotData getSignLocation(String slot) {
//
//        File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
//        FileConfiguration locConfig = YamlConfiguration.loadConfiguration(locationFile);
//
//        SlotData data = new SlotData();
//
//        data.signLocationX = locConfig.getDouble("sign." + slot + ".x");
//        data.signLocationY = locConfig.getDouble("sign." + slot + ".y");
//        data.signLocationZ = locConfig.getDouble("sign." + slot + ".z");
//        data.signWorld = locConfig.getString("sign." + slot + ".world");
//
//        return data;
//    }
}
