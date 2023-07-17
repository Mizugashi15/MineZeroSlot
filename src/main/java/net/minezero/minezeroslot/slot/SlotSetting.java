package net.minezero.minezeroslot.slot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

import static net.minezero.minezeroslot.MineZeroSlot.*;

public class SlotSetting {

    public static SlotData getSlotData( String slot) {

        SlotData data = new SlotData();
        File slotFile = new File(plugin.getDataFolder().getPath() + "/slots/" + slot + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(slotFile);

        data.flag = true;

        data.winflag = false;

        data.winkey = "name";

        data.reel1.add((ItemStack) config.getList("reel1"));
        data.reel2.add((ItemStack) config.getList("reel2"));
        data.reel3.add((ItemStack) config.getList("reel3"));

        data.coinamount = config.getInt("coin");

        data.spindelay = config.getInt("spindelay");

        data.stopcount = config.getInt("stopcount");

        data.reelstop1 = config.getInt("reelstop1");
        data.reelstop2 = config.getInt("reelstop2");
        data.reelstop3 = config.getInt("reelstop3");

        data.onespinsounds.add(config.getStringList("onespinsound").toString());
        data.spinsounds.add(config.getStringList("spinsound").toString());
        data.losesounds.add(config.getStringList("losesound").toString());

        data.stock = config.getDouble("stock.default");
        data.raise = config.getDouble("stock.raise");

        data.chance = config.getInt("chance");

        data.probs.add(data.chance);
        try {
            for (String key : config.getConfigurationSection("win").getKeys(false)) {

                data.win_name.add(key);

                data.win_string.put(key, config.getString("win." + key + ".name"));

                data.win_message.put(key, config.getString("win." + key + ".message"));

                data.win_symbols.put(key, (List<ItemStack>) config.getList("win." + key + ".symbols"));

                data.win_chance.put(key, config.getInt("win." + key + ".chance"));

                data.probs.add(data.win_chance.get(key));

                data.allProb += data.win_chance.get(key);

                data.win_sounds.put(key, (List<String>) config.getList("win." + key + ".winsound"));

                data.win_pot.put(key, config.getBoolean("win." + key + ".pot"));

                data.win_actions.put(key, (List<String>) config.getList("win." + key + ".actions"));

                data.win_commands.put(key, (List<String>) config.getList("win." + key + ".commands"));

            }
        } catch (NullPointerException ignore){
        }

        data.allProb += data.chance;

        return data;
    }

//    public static void registerSlotData(String slot) {
//
//        SlotData data = getSlotData(slot);
//
//        slotdatamap.put(slot, data);
//
//    }

    public static SlotData getFrameLocation(String slot) {

        File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
        FileConfiguration locConfig = YamlConfiguration.loadConfiguration(locationFile);

        SlotData data = new SlotData();

        data.frameLocationX1 = locConfig.getDouble("frame." + slot + ".0.x");
        data.frameLocationY1 = locConfig.getDouble("frame." + slot + ".0.y");
        data.frameLocationZ1 = locConfig.getDouble("frame." + slot + ".0.z");
        data.frameWorld1 = locConfig.getString("frame." + slot + ".0.world");

        data.frameLocationX2 = locConfig.getDouble("frame." + slot + ".1.x");
        data.frameLocationY2 = locConfig.getDouble("frame." + slot + ".1.y");
        data.frameLocationZ2 = locConfig.getDouble("frame." + slot + ".1.z");
        data.frameWorld2 = locConfig.getString("frame." + slot + ".1.world");

        data.frameLocationX3 = locConfig.getDouble("frame." + slot + ".2.x");
        data.frameLocationY3 = locConfig.getDouble("frame." + slot + ".2.y");
        data.frameLocationZ3 = locConfig.getDouble("frame." + slot + ".2.z");
        data.frameWorld3 = locConfig.getString("frame." + slot + ".2.world");

        return data;
    }

    public static SlotData getInputLocation(String slot) {

        File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
        FileConfiguration locConfig = YamlConfiguration.loadConfiguration(locationFile);

        SlotData data = new SlotData();

        data.coinInputLocationX = locConfig.getDouble("input." + slot + ".x");
        data.coinInputLocationY = locConfig.getDouble("input." + slot + ".y");
        data.coinInputLocationZ = locConfig.getDouble("input." + slot + ".z");
        data.coinInputWorld = locConfig.getString("input." + slot + ".world");

        return data;
    }

    public static SlotData getSignLocation(String slot) {

        File locationFile = new File(plugin.getDataFolder().getPath() + "/location.yml");
        FileConfiguration locConfig = YamlConfiguration.loadConfiguration(locationFile);

        SlotData data = new SlotData();

        data.signLocationX = locConfig.getDouble("sign." + slot + ".x");
        data.signLocationY = locConfig.getDouble("sign." + slot + ".y");
        data.signLocationZ = locConfig.getDouble("sign." + slot + ".z");
        data.signWorld = locConfig.getString("sign." + slot + ".world");

        return data;
    }
}
