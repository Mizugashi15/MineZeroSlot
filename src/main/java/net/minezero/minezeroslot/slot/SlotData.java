package net.minezero.minezeroslot.slot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SlotData {

    boolean flag = true;

    boolean winflag = false;

    List<String> reel1 = new ArrayList<>();
    List<String> reel2 = new ArrayList<>();
    List<String> reel3 = new ArrayList<>();

    int coinamount = 0;

    int spindelay = 0;

    int stopcount = 0;

    int reelstop1 = 0;
    int reelstop2 = 0;
    int reelstop3 = 0;

    List<String> onespinsounds = new ArrayList<>();
    List<String> spinsounds = new ArrayList<>();
    List<String> losesounds = new ArrayList<>();

    double defaultstock = 0;

    double stock = 0;

    double raise = 0;

    int chance = 0;

    List<String> win_name = new ArrayList<>();

    ConcurrentHashMap<String, String> win_string = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, String> win_message = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, List<String>> win_symbols = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, Integer> win_chance = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, List<String>> win_sounds = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, Boolean> win_pot = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, List<String>> win_actions = new ConcurrentHashMap<>();

    ConcurrentHashMap<String, List<String>> win_commands = new ConcurrentHashMap<>();

    String winkey = "name";

    List<Integer> probs = new ArrayList<>();

    int allProb = 0;

    double frameLocationX1 = 0;
    double frameLocationY1 = 0;
    double frameLocationZ1 = 0;
    String frameWorld1 = "world";

    double frameLocationX2 = 0;
    double frameLocationY2 = 0;
    double frameLocationZ2 = 0;
    String frameWorld2 = "world";

    double frameLocationX3 = 0;
    double frameLocationY3 = 0;
    double frameLocationZ3 = 0;
    String frameWorld3 = "world";

    double coinInputLocationX = 0;
    double coinInputLocationY = 0;
    double coinInputLocationZ = 0;
    String coinInputWorld = "world";

    double signLocationX = 0;
    double signLocationY = 0;
    double signLocationZ = 0;
    String signWorld = "world";

}
