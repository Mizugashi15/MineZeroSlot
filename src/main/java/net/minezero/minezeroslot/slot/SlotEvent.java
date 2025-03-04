package net.minezero.minezeroslot.slot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minezero.minezeroslot.MineZeroSlot.*;
import static org.bukkit.Bukkit.*;

public class SlotEvent implements Listener {

    String line1 = "§e§m=======|§r §6§lMoney §e§m|=======";
    String line2 = "§3§m=======|§r §6§lMoney §3§m|=======";
    String line3 = "§f§m=======|§r §6§lMoney §f§m|=======";
    String line4 = "§d§m=======|§r §6§lMoney §d§m|=======";
    String line5 = "§c§m=======|§r §6§lMoney §c§m|=======";
    String line6 = "§f§m=======|§r §6§lCount §f§m|=======";
    String line7 = "§3§m=======|§r §6§lCount §3§m|=======";
    String line8 = "§c§m=======|§r §6§lCount §c§m|=======";
    String line9 = "§e§m=======|§r §6§lCount §e§m|=======";
    String line10 = "§d§m=======|§r §6§lCount §d§m|=======";

    @EventHandler
    void spin(PlayerInteractEvent event) throws IOException {

//        try {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if (inputdatamap.containsKey(event.getClickedBlock().getState().getLocation())) {

//                    if (isUsePermission(event.getPlayer())) {
//                        event.getPlayer().sendMessage(prefix + " §c権限がありません！");
//                        return;
//                    }

                    String slotname = inputdatamap.get(event.getClickedBlock().getState().getLocation());
                    Player player = event.getPlayer();

                    if (!slotdatamap.get(slotname).flag) {
                        return;
                    }

                    if (!player.getInventory().getItemInMainHand().hasItemMeta()) {
                        return;
                    }

                    if (!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                        player.sendMessage(prefix + " §cコインを持ってください！");
                        return;
                    }

                    if (!hasEnoughCoins(player, slotname)) {
                        player.sendMessage(prefix + " §cコインが" + slotdatamap.get(slotname).coinamount + "§c枚必要です！");
                        return;
                    }

                    if (slotdatamap.get(slotname).type) {

                        slotdatamap.get(slotname).flag = false;
                        removeCoins(player, slotname);
                        updateSign(slotname);
                        raiseSign(slotname);

                        if (slotdatamap.get(slotname).onespinsounds != null) {
                            for (String s : slotdatamap.get(slotname).onespinsounds) {
                                String[] sound = s.split("-");
                                player.getWorld().playSound(framedatamap.get(slotname).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                            }
                        }
                        List<ItemStack>[] symbols = judge(slotname);

                        if (slotdatamap.get(slotname).winflag) {
                            if (!slotdatamap.get(slotname).kakuteispinsounds.get(slotdatamap.get(slotname).winkey).isEmpty()) {
                                for (String s : slotdatamap.get(slotname).kakuteispinsounds.get(slotdatamap.get(slotname).winkey)) {
                                    String[] sound = s.split("-");
                                    player.getWorld().playSound(framedatamap.get(slotname).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                }
                            }
                        }

                        int stop1;
                        int stop2;
                        int stop3;

                        if (slotdatamap.get(slotname).winflag) {
                            if (!slotdatamap.get(slotname).kakuteispinwait.get(slotdatamap.get(slotname).winkey).describeConstable().isEmpty()) {
                                stop1 = slotdatamap.get(slotname).kakuteispinwait.get(slotdatamap.get(slotname).winkey) + slotdatamap.get(slotname).kakuteispin1.get(slotdatamap.get(slotname).winkey)-1;
                                stop2 = slotdatamap.get(slotname).kakuteispinwait.get(slotdatamap.get(slotname).winkey) + slotdatamap.get(slotname).kakuteispin2.get(slotdatamap.get(slotname).winkey)-1;
                                stop3 = slotdatamap.get(slotname).kakuteispinwait.get(slotdatamap.get(slotname).winkey) + slotdatamap.get(slotname).kakuteispin3.get(slotdatamap.get(slotname).winkey)-1;
                            } else {
                                stop1 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop1 - 1;
                                stop2 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop2 - 1;
                                stop3 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop3 - 1;
                            }
                        } else {
                            stop1 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop1 - 1;
                            stop2 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop2 - 1;
                            stop3 = slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop3 - 1;
                        }

                        List<ItemStack> reel1 = symbols[0];
                        List<ItemStack> reel2 = symbols[1];
                        List<ItemStack> reel3 = symbols[2];

                        ItemFrame frame1 = frames.get(slotname).get(0);
                        ItemFrame frame2 = frames.get(slotname).get(1);
                        ItemFrame frame3 = frames.get(slotname).get(2);

                        if (!slotdatamap.get(slotname).framesound) {
                            if (!frame1.isSilent()) frame1.setSilent(true);
                            if (!frame2.isSilent()) frame2.setSilent(true);
                            if (!frame3.isSilent()) frame3.setSilent(true);
                        } else {
                            if (frame1.isSilent()) frame1.setSilent(false);
                            if (frame2.isSilent()) frame2.setSilent(false);
                            if (frame3.isSilent()) frame3.setSilent(false);
                        }

                        if (!slotdatamap.get(slotname).framevisible) {
                            if (!frame1.isVisible()) frame1.setVisible(true);
                            if (!frame2.isVisible()) frame2.setVisible(true);
                            if (!frame3.isVisible()) frame3.setVisible(true);
                        } else {
                            if (frame1.isVisible()) frame1.setVisible(false);
                            if (frame2.isVisible()) frame2.setVisible(false);
                            if (frame3.isVisible()) frame3.setVisible(false);
                        }

                        int max = Math.max(stop1, Math.max(stop2, stop3));

                        BukkitScheduler scheduler = getServer().getScheduler();

                        long delay = slotdatamap.get(slotname).spindelay;

                        AtomicBoolean stopsound1 = new AtomicBoolean(true);
                        AtomicBoolean stopsound2 = new AtomicBoolean(true);
                        AtomicBoolean stopsound3 = new AtomicBoolean(true);

                        for (int i = 0; i < max; i++) {

                            if (i > 0) {
                                delay += slotdatamap.get(slotname).spindelay;
                            }

                            if (stop1 != 0) {
                                stop1--;
                            }
                            if (stop2 != 0) {
                                stop2--;
                            }
                            if (stop3 != 0) {
                                stop3--;
                            }
                            int finalStop1 = stop1;
                            int finalStop2 = stop2;
                            int finalStop3 = stop3;

                            scheduler.scheduleSyncDelayedTask(plugin, () -> {

                                if (slotdatamap.get(slotname).spinsounds != null) {
                                    for (String s : slotdatamap.get(slotname).spinsounds) {
                                        String[] sound = s.split("-");
                                        player.getWorld().playSound(frame2.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                    }
                                }
                                colorSign(slotname);

                                if (finalStop1 != 0) {
                                    frame1.setItem(reel1.get(finalStop1));
                                }
                                if (finalStop2 != 0) {
                                    frame2.setItem(reel2.get(finalStop2));
                                }
                                if (finalStop3 != 0) {
                                    frame3.setItem(reel3.get(finalStop3));
                                }

                                if (stopsound1.get()) {
                                    if (finalStop1 == 0) {
                                        if (slotdatamap.get(slotname).winflag) {
                                            if (!slotdatamap.get(slotname).stop1_sounds.get(slotdatamap.get(slotname).winkey).isEmpty()) {
                                                for (String s : slotdatamap.get(slotname).stop1_sounds.get(slotdatamap.get(slotname).winkey)) {
                                                    String[] sound = s.split("-");
                                                    player.getWorld().playSound(frame2.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                                }
                                                stopsound1.set(false);
                                            }
                                        }
                                    }
                                }

                                if (stopsound2.get()) {
                                    if (finalStop2 == 0) {
                                        if (slotdatamap.get(slotname).winflag) {
                                            if (!slotdatamap.get(slotname).stop2_sounds.get(slotdatamap.get(slotname).winkey).isEmpty()) {
                                                for (String s : slotdatamap.get(slotname).stop2_sounds.get(slotdatamap.get(slotname).winkey)) {
                                                    String[] sound = s.split("-");
                                                    player.getWorld().playSound(frame2.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                                }
                                                stopsound2.set(false);
                                            }
                                        }
                                    }
                                }
                                if (stopsound3.get()) {
                                    if (finalStop3 == 0) {
                                        if (slotdatamap.get(slotname).winflag) {
                                            if (!slotdatamap.get(slotname).stop3_sounds.get(slotdatamap.get(slotname).winkey).isEmpty()) {
                                                for (String s : slotdatamap.get(slotname).stop3_sounds.get(slotdatamap.get(slotname).winkey)) {
                                                    String[] sound = s.split("-");
                                                    player.getWorld().playSound(frame2.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                                }
                                                stopsound3.set(false);
                                            }
                                        }
                                    }
                                }
                            }, delay);
                        }
                        scheduler.scheduleSyncDelayedTask(plugin, () -> {
                            try {
                                spinend(slotname, player);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }, delay);
                    } else {

                        slotdatamap.get(slotname).flag = false;
                        removeCoins(player, slotname);
                        updateSign(slotname);
                        raiseSign(slotname);

                        if (slotdatamap.get(slotname).onespinsounds != null) {
                            if (slotdatamap.get(slotname).winflag) {
                                if (!slotdatamap.get(slotname).kakuteispinsounds.get(slotdatamap.get(slotname).winkey).isEmpty()) {
                                    for (String s : slotdatamap.get(slotname).kakuteispinsounds.get(slotdatamap.get(slotname).winkey)) {
                                        String[] sound = s.split("-");
                                        player.getWorld().playSound(framedatamap.get(slotname).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                    }
                                }
                            }
                            for (String s : slotdatamap.get(slotname).onespinsounds) {
                                String[] sound = s.split("-");
                                player.getWorld().playSound(framedatamap.get(slotname).get(4).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                            }

                            List<ItemStack>[] symbols = judge(slotname);
                            int stop1 = (slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop1) * (slotdatamap.get(slotname).reel1.size())+2;
                            int stop2 = (slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop2) * (slotdatamap.get(slotname).reel2.size())+2;
                            int stop3 = (slotdatamap.get(slotname).stopcount + slotdatamap.get(slotname).reelstop3) * (slotdatamap.get(slotname).reel3.size())+2;
                            int count1 = 0;
                            int count2 = 0;
                            int count3 = 0;
                            List<ItemStack> reel1 = symbols[0];
                            List<ItemStack> reel2 = symbols[1];
                            List<ItemStack> reel3 = symbols[2];
                            ItemFrame frame1 = frames.get(slotname).get(0);
                            ItemFrame frame2 = frames.get(slotname).get(1);
                            ItemFrame frame3 = frames.get(slotname).get(2);
                            ItemFrame frame4 = frames.get(slotname).get(3);
                            ItemFrame frame5 = frames.get(slotname).get(4);
                            ItemFrame frame6 = frames.get(slotname).get(5);
                            ItemFrame frame7 = frames.get(slotname).get(6);
                            ItemFrame frame8 = frames.get(slotname).get(7);
                            ItemFrame frame9 = frames.get(slotname).get(8);

                            if (slotdatamap.get(slotname).framesound) {
                                if (frame1.isSilent()) frame1.setSilent(false);
                                if (frame2.isSilent()) frame2.setSilent(false);
                                if (frame3.isSilent()) frame3.setSilent(false);
                                if (frame4.isSilent()) frame4.setSilent(false);
                                if (frame5.isSilent()) frame5.setSilent(false);
                                if (frame6.isSilent()) frame6.setSilent(false);
                                if (frame7.isSilent()) frame7.setSilent(false);
                                if (frame8.isSilent()) frame8.setSilent(false);
                                if (frame9.isSilent()) frame9.setSilent(false);
                            } else {
                                if (!frame1.isSilent()) frame1.setSilent(true);
                                if (!frame2.isSilent()) frame2.setSilent(true);
                                if (!frame3.isSilent()) frame3.setSilent(true);
                                if (!frame4.isSilent()) frame4.setSilent(true);
                                if (!frame5.isSilent()) frame5.setSilent(true);
                                if (!frame6.isSilent()) frame6.setSilent(true);
                                if (!frame7.isSilent()) frame7.setSilent(true);
                                if (!frame8.isSilent()) frame8.setSilent(true);
                                if (!frame9.isSilent()) frame9.setSilent(true);
                            }

                            if (!slotdatamap.get(slotname).framevisible) {
                                if (!frame1.isVisible()) frame1.setVisible(true);
                                if (!frame2.isVisible()) frame2.setVisible(true);
                                if (!frame3.isVisible()) frame3.setVisible(true);
                                if (!frame4.isVisible()) frame4.setVisible(true);
                                if (!frame5.isVisible()) frame5.setVisible(true);
                                if (!frame6.isVisible()) frame6.setVisible(true);
                                if (!frame7.isVisible()) frame7.setVisible(true);
                                if (!frame8.isVisible()) frame8.setVisible(true);
                                if (!frame9.isVisible()) frame9.setVisible(true);
                            } else {
                                if (frame1.isVisible()) frame1.setVisible(false);
                                if (frame2.isVisible()) frame2.setVisible(false);
                                if (frame3.isVisible()) frame3.setVisible(false);
                                if (frame4.isVisible()) frame4.setVisible(false);
                                if (frame5.isVisible()) frame5.setVisible(false);
                                if (frame6.isVisible()) frame6.setVisible(false);
                                if (frame7.isVisible()) frame7.setVisible(false);
                                if (frame8.isVisible()) frame8.setVisible(false);
                                if (frame9.isVisible()) frame9.setVisible(false);
                            }

                            if (slotdatamap.get(slotname).winflag) {

                                int i = new Random().nextInt(3);
                                int j = new Random().nextInt(2);
                                stop1 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(0)+i;
                                Bukkit.getServer().broadcastMessage(slotdatamap.get(slotname).winkey);
                                switch (i) {
                                    case 0:
                                        if (j == 0) {
                                            stop2 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(1);
                                            stop3 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(2);
                                        } else {
                                            stop2 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(1)+1;
                                            stop3 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(2)+2;
                                        }
                                        break;
                                    case 1:
                                        stop2 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(1)+1;
                                        stop3 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(2)+1;
                                        break;
                                    case 2:
                                        if (j == 1) {
                                            stop2 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(1)+2;
                                            stop3 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(2)+2;
                                        } else {
                                            stop2 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(1)+1;
                                            stop3 += slotdatamap.get(slotname).win_symbol3.get(slotdatamap.get(slotname).winkey).get(2);
                                        }
                                        break;
                                    default:
                                        Bukkit.getServer().broadcastMessage(prefix + " §c予期せぬエラーです");
                                }

                                stop2 += slotdatamap.get(slotname).reel2.size();
                                stop3 += slotdatamap.get(slotname).reel3.size();

                            } else {
                                int random;
                                random = new Random().nextInt(slotdatamap.get(slotname).reel1.size());
                                stop1 += random;
                                int i1 = random;
                                random = new Random().nextInt(slotdatamap.get(slotname).reel2.size());
                                stop2 += random;
                                int i2 = random;
                                random = new Random().nextInt(slotdatamap.get(slotname).reel3.size());
                                int i3 = random;
                                List<Integer> stops = new ArrayList<>();
                                boolean stop3bool = true;
                                stops.add(i1);
                                stops.add(i2);
                                stops.add(i3);

                                Bukkit.broadcastMessage(i1 + "/" + i2 + "/" + i3);

                                Bukkit.broadcastMessage("負け");

                                do {
                                    Bukkit.broadcastMessage("なんで");
                                    if (slotdatamap.get(slotname).win_symbol3.contains(stops)) {
                                        stop3bool = false;
                                    }
                                    random = new Random().nextInt(slotdatamap.get(slotname).reel3.size());
                                    for (String s : slotdatamap.get(slotname).win_name) {

                                        Bukkit.broadcastMessage("やねん");

                                        if (i1 == slotdatamap.get(slotname).win_symbol3.get(s).get(0) && i2 == slotdatamap.get(slotname).win_symbol3.get(s).get(1)) {
                                            if (random == slotdatamap.get(slotname).win_symbol3.get(s).get(2)) {
                                                break;
                                            }
                                            i3 = random;
                                            stop3bool = false;
                                            break;
                                        } else if (i1 == slotdatamap.get(slotname).win_symbol3.get(s).get(0) && i2 == slotdatamap.get(slotname).win_symbol3.get(s).get(1)+1) {
                                            if (random == slotdatamap.get(slotname).win_symbol3.get(s).get(2)+2) {
                                                break;
                                            }
                                            i3 = random;
                                            stop3bool = false;
                                            break;
                                        } else if (i1 == slotdatamap.get(slotname).win_symbol3.get(s).get(0)+1 && i2 == slotdatamap.get(slotname).win_symbol3.get(s).get(1)+1) {
                                            if (random == slotdatamap.get(slotname).win_symbol3.get(s).get(2)+1) {
                                                break;
                                            }
                                            i3 = random;
                                            stop3bool = false;
                                            break;
                                        } else if (i1 == slotdatamap.get(slotname).win_symbol3.get(s).get(0)+2 && i2 == slotdatamap.get(slotname).win_symbol3.get(s).get(1)+1) {
                                            if (random == slotdatamap.get(slotname).win_symbol3.get(s).get(2)) {
                                                break;
                                            }
                                            i3 = random;
                                            stop3bool = false;
                                            break;
                                        } else if (i1 == slotdatamap.get(slotname).win_symbol3.get(s).get(0)+2 && i2 == slotdatamap.get(slotname).win_symbol3.get(s).get(1)+2) {
                                            if (random == slotdatamap.get(slotname).win_symbol3.get(s).get(2)+2) {
                                                break;
                                            }
                                            i3 = random;
                                            stop3bool = false;
                                            break;
                                        } else {
                                            i3 = random;
                                            stop3bool = false;
                                            break;
                                        }
                                    }
                                } while (stop3bool);
                                stop3 += i3;
                            }

                            int max = Math.max(stop1, Math.max(stop2, stop3));
                            BukkitScheduler scheduler = getServer().getScheduler();
                            long delay = slotdatamap.get(slotname).spindelay;
                            int finalreel1 = 0;
                            int finalreel2 = 1;
                            int finalreel3 = 2;
                            int finalStop = stop1;
                            int finalStop4 = stop2;
                            int finalStop5 = stop3;

                            for (int i = 0; i < max; i++) {

                                if (i > 0) {
                                    delay += slotdatamap.get(slotname).spindelay;
                                }

                                if (stop1 != count1) {
                                    count1++;
                                }
                                if (stop2 != count2) {
                                    count2++;
                                }
                                if (stop3 != count3) {
                                    count3++;
                                }
                                int finalStop1 = count1;
                                int finalStop2 = count2;
                                int finalStop3 = count3;

                                if (i > 1) {
                                    if (i >= slotdatamap.get(slotname).reel1.size()) {
                                        finalreel1++;
                                    } else {
                                        finalreel1 = i;
                                    }
                                    if (i >= slotdatamap.get(slotname).reel2.size() + 1) {
                                        finalreel2++;
                                    } else {
                                        finalreel2 = i - 1;
                                    }
                                    if (i >= slotdatamap.get(slotname).reel3.size() + 2) {
                                        finalreel3++;
                                    } else {
                                        finalreel3 = i - 2;
                                    }
                                } else if (i == 1){
                                    finalreel1 = 1;
                                    finalreel2 = 0;
                                    finalreel3 = slotdatamap.get(slotname).reel3.size()-1;
                                }

                                if (finalreel1 == slotdatamap.get(slotname).reel1.size()) {
                                    finalreel1 = 0;
                                }
                                if (finalreel2 == slotdatamap.get(slotname).reel2.size()) {
                                    finalreel2 = 0;
                                }
                                if (finalreel3 == slotdatamap.get(slotname).reel3.size()) {
                                    finalreel3 = 0;
                                }

                                int finalReel = finalreel1;
                                int finalReel1 = finalreel2;
                                int finalReel2 = finalreel3;

                                scheduler.scheduleSyncDelayedTask(plugin, () -> {

                                    if (slotdatamap.get(slotname).spinsounds != null) {
                                        for (String s : slotdatamap.get(slotname).spinsounds) {
                                            String[] sound = s.split("-");
                                            player.getWorld().playSound(frame5.getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
                                        }
                                    }
                                    colorSign(slotname);

                                    if (finalStop1 != finalStop) {
//                                        Bukkit.getServer().broadcastMessage(finalStop1 + " / " + finalStop);
                                        frame1.setItem(reel1.get(finalReel));
                                        frame2.setItem(reel1.get(finalReel1));
                                        frame3.setItem(reel1.get(finalReel2));
                                    }
                                    if (finalStop2 != finalStop4) {
//                                        Bukkit.getServer().broadcastMessage(finalStop2 + " + " + finalStop4);
                                        frame4.setItem(reel2.get(finalReel));
                                        frame5.setItem(reel2.get(finalReel1));
                                        frame6.setItem(reel2.get(finalReel2));
                                    }
                                    if (finalStop3 != finalStop5) {
//                                        Bukkit.getServer().broadcastMessage(finalStop3 + " - " + finalStop5);
                                        frame7.setItem(reel3.get(finalReel));
                                        frame8.setItem(reel3.get(finalReel1));
                                        frame9.setItem(reel3.get(finalReel2));
                                    }

                                }, delay);
                            }
                            scheduler.scheduleSyncDelayedTask(plugin, () -> {
                                try {
                                    spinend(slotname, player);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }, delay);
                        }
                    }

                }
            }
    }

    private void spinend(String slotname, Player player) throws IOException {
        if (slotdatamap.get(slotname).winflag) {
            Sign sign = (Sign) signdatamap.get(slotname).getBlock().getState();
            double money = sign.getPersistentDataContainer().get(new NamespacedKey(plugin, slotname + "-moneypot"), PersistentDataType.DOUBLE);
            win(slotname, slotdatamap.get(slotname).winkey, money, player);
        } else {
            lose(slotname, player);
        }
        slotdatamap.get(slotname).flag = true;
    }

    private List<ItemStack>[] judge(String s) {

        Random random = new Random();

        double prob = random.nextDouble(slotdatamap.get(s).allProb);

        double plusprob = slotdatamap.get(s).probs.get(0);

        if (slotdatamap.get(s).type) {
            if (plusprob >= prob) {
                slotdatamap.get(s).winflag = false;
                slotdatamap.get(s).winkey = null;
                return isLose1(s);
            }

            for (String s1 : slotdatamap.get(s).win_name) {

                plusprob += slotdatamap.get(s).win_chance.get(s1);

                if (plusprob >= prob) {
                    slotdatamap.get(s).winflag = true;
                    return isWin1(s, s1);
                }
            }
        } else {
            if (plusprob >= prob) {
                slotdatamap.get(s).winflag = false;
                return reel3_3("false", s);
            }
            for (String s1 : slotdatamap.get(s).win_name) {

                plusprob += slotdatamap.get(s).win_chance.get(s1);

                if (plusprob >= prob) {
                    slotdatamap.get(s).winflag = true;
                    Bukkit.getServer().broadcastMessage("§b"+s1);
                    return reel3_3(s1, s);
                }
            }
        }
        return null;
    }

    private List<ItemStack>[] isLose1(String s) {

        Random random = new Random();
        int reel2 = 0;
        int reel3;
        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();

        String[] s1;

        for (int i = 0 ; i < slotdatamap.get(s).stopcount ; i++) {

            s1 = slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);

            reel2 = random.nextInt(slotdatamap.get(s).reel2.size());
            s1 = slotdatamap.get(s).reel2.get(reel2).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);

            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
            } while (reel2 == reel3);
            s1 = slotdatamap.get(s).reel3.get(reel3).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);

        }
        return new List[]{reel1(item1, s), reel2(item2, s), reel3(item3, s, reel2)};
    }

    private List<ItemStack>[] isWin1(String slot, String s) {

        Random random = new Random();
        int reel2 = 0;
        int reel3;
        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();
        String[] s1;
        int stopcount;

        if (!slotdatamap.get(slot).kakuteispinwait.get(s).describeConstable().isEmpty()) {
            stopcount = slotdatamap.get(slot).kakuteispinwait.get(s);
        } else stopcount = slotdatamap.get(slot).stopcount;

        for (int i = 0 ; i < stopcount ; i++) {

            s1 = slotdatamap.get(slot).reel1.get(random.nextInt(slotdatamap.get(slot).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);

            reel2 = random.nextInt(slotdatamap.get(slot).reel2.size());
            s1 = slotdatamap.get(slot).reel2.get(reel2).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);
            do {
                reel3 = random.nextInt(slotdatamap.get(slot).reel3.size());
            } while (reel2 == reel3);
            s1 = slotdatamap.get(slot).reel3.get(reel3).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);
        }

        if (slotdatamap.get(slot).win_symbolcustom.get(s)) {
            s1 = slotdatamap.get(slot).win_symbols.get(s).get(0).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.set(1, item);

            s1 = slotdatamap.get(slot).win_symbols.get(s).get(1).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.set(1, item);

            s1 = slotdatamap.get(slot).win_symbols.get(s).get(2).split("-");
            item = new ItemStack(Material.valueOf(s1[0]));
            itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.set(1, item);
        } else {
            s1 = slotdatamap.get(slot).reel1.get(random.nextInt(slotdatamap.get(slot).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);

            item1.set(1, item);
            item2.set(1, item);
            item3.set(1, item);
        }

        slotdatamap.get(slot).winkey = s;
        return new List[]{reel1(item1, slot), reel2(item2, slot), reel3(item3, slot, reel2)};
    }

    private List<ItemStack> reel1(List<ItemStack> items, String s) {

        Random random = new Random();
        String[] s1;
        int stopcount;

        if (slotdatamap.get(s).kakuteispin1.isEmpty()) {
            stopcount = slotdatamap.get(s).reelstop1;
        } else stopcount = slotdatamap.get(s).kakuteispin1.get(slotdatamap.get(s).winkey);

        for (int i = 0 ; i < stopcount ; i++) {
            s1 = slotdatamap.get(s).reel1.get(random.nextInt(slotdatamap.get(s).reel1.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            items.add(item);
        }
        return items;
    }

    private List<ItemStack> reel2(List<ItemStack> items , String s) {

        Random random = new Random();
        String[] s1;
        int stopcount;

        if (slotdatamap.get(s).kakuteispin1.isEmpty()) {
            stopcount = slotdatamap.get(s).reelstop2;
        } else stopcount = slotdatamap.get(s).kakuteispin2.get(slotdatamap.get(s).winkey);

        for (int i = 0 ; i < stopcount ; i++) {
            s1 = slotdatamap.get(s).reel2.get(random.nextInt(slotdatamap.get(s).reel2.size())).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            items.add(item);
        }
        return items;
    }

    private List<ItemStack> reel3(List<ItemStack> items , String s , int n) {

        Random random = new Random();
        String[] s1;
        int reel3;
        int stopcount;

        if (slotdatamap.get(s).kakuteispin1.isEmpty()) {
            stopcount = slotdatamap.get(s).reelstop3;
        } else stopcount = slotdatamap.get(s).kakuteispin3.get(slotdatamap.get(s).winkey);

        for (int i = 0 ; i < stopcount ; i++) {
            do {
                reel3 = random.nextInt(slotdatamap.get(s).reel3.size());
                s1 = slotdatamap.get(s).reel3.get(random.nextInt(slotdatamap.get(s).reel3.size())).split("-");
                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
                item.setItemMeta(itemMeta);
                items.add(item);
            } while (n == reel3);
        }
        return items;
    }

    private List<ItemStack>[] reel3_3(String win, String s) {

        List<ItemStack> item1 = new ArrayList<>();
        List<ItemStack> item2 = new ArrayList<>();
        List<ItemStack> item3 = new ArrayList<>();
        String s1[];
        int j = 0;

        for (int i = 0 ; i < slotdatamap.get(s).reel1.size() ; i++) {
            s1 = slotdatamap.get(s).reel1.get(i).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item1.add(item);
        }
        for (int i = 0 ; i < slotdatamap.get(s).reel2.size() ; i++) {
            s1 = slotdatamap.get(s).reel2.get(i).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item2.add(item);
        }
        for (int i = 0 ; i < slotdatamap.get(s).reel3.size() ; i++) {
            s1 = slotdatamap.get(s).reel3.get(i).split("-");
            ItemStack item = new ItemStack(Material.valueOf(s1[0]));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
            item.setItemMeta(itemMeta);
            item3.add(item);
        }

//        for (int i = 0 ; i < slotdatamap.get(s).stopcount + slotdatamap.get(s).reelstop1 + slotdatamap.get(s).reel1.size() ; i++) {
//
//            if (i > slotdatamap.get(s).reel1.size()) {
//                s1 = slotdatamap.get(s).reel1.get(j).split("-");
//                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
//                ItemMeta itemMeta = item.getItemMeta();
//                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
//                item.setItemMeta(itemMeta);
//                item1.add(item);
//                j++;
//            } else {
//                s1 = slotdatamap.get(s).reel1.get(i).split("-");
//                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
//                ItemMeta itemMeta = item.getItemMeta();
//                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
//                item.setItemMeta(itemMeta);
//                item1.add(item);
//            }
//            if (j == slotdatamap.get(s).reel1.size()) {
//                j = 0;
//            }
//        }
//
//        for (int i = 0 ; i < slotdatamap.get(s).stopcount + slotdatamap.get(s).reelstop2 + slotdatamap.get(s).reel2.size() ; i++) {
//
//            if (i > slotdatamap.get(s).reel2.size()) {
//                s1 = slotdatamap.get(s).reel2.get(j).split("-");
//                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
//                ItemMeta itemMeta = item.getItemMeta();
//                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
//                item.setItemMeta(itemMeta);
//                item2.add(item);
//                j++;
//            } else {
//                s1 = slotdatamap.get(s).reel2.get(i).split("-");
//                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
//                ItemMeta itemMeta = item.getItemMeta();
//                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
//                item.setItemMeta(itemMeta);
//                item2.add(item);
//            }
//            if (j == slotdatamap.get(s).reel2.size()) {
//                j = 0;
//            }
//        }
//
//        for (int i = 0 ; i < slotdatamap.get(s).stopcount + slotdatamap.get(s).reelstop3 + slotdatamap.get(s).reel3.size() ; i++) {
//
//            if (i > slotdatamap.get(s).reel3.size()) {
//                s1 = slotdatamap.get(s).reel3.get(j).split("-");
//                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
//                ItemMeta itemMeta = item.getItemMeta();
//                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
//                item.setItemMeta(itemMeta);
//                item3.add(item);
//                j++;
//            } else {
//                s1 = slotdatamap.get(s).reel3.get(i).split("-");
//                ItemStack item = new ItemStack(Material.valueOf(s1[0]));
//                ItemMeta itemMeta = item.getItemMeta();
//                itemMeta.setCustomModelData(Integer.valueOf(s1[1]));
//                item.setItemMeta(itemMeta);
//                item3.add(item);
//            }
//            if (j == slotdatamap.get(s).reel3.size()) {
//                j = 0;
//            }
//        }
        if (!win.equalsIgnoreCase("false")) {
            slotdatamap.get(s).winkey = win;
        }
        return new List[] {item1, item2, item3};
    }

    private void lose(String s, Player player) {

        player.sendMessage(prefix + " §c何も獲得できませんでした...");
        if (slotdatamap.get(s).losesounds != null) {
            for (String s1 : slotdatamap.get(s).losesounds) {
                String[] sound = s1.split("-");
                player.getWorld().playSound(framedatamap.get(s).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            }
        }
    }

    private void win(String slot, String s, double money, Player player) throws IOException {

        try {
            for (String s1 : slotdatamap.get(slot).win_actions.get(s)) {

                double action = Double.parseDouble(s1.substring(s1.lastIndexOf(":") + 1));
                if (s1.contains("MULTI")) {
                    money = money * action;
                }
                if (s1.contains("RAISE")) {
                    money = money + action;
                }
            }
        } catch (NullPointerException ignore) {
        }

        try {
            for (String s1 : slotdatamap.get(slot).win_commands.get(s)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s1.replace("%player%", player.getName()).replace("%money%", String.valueOf(money)).replace("%spin%", String.valueOf(slotdatamap.get(slot).spincount)));
            }
        } catch (NullPointerException ignore) {
        }

        try {
            Bukkit.getServer().broadcastMessage(slotdatamap.get(slot).win_message.get(s).replace("%player%", player.getName()).replace("%money%", String.valueOf(money)).replace("&", "§").replace("%spin%", String.valueOf(slotdatamap.get(slot).spincount)));
        } catch (NullPointerException ignore) {
        }

        try {
            player.sendMessage(slotdatamap.get(slot).win_playermessage.get(s).replace("%player%", player.getName()).replace("%money%", String.valueOf(money)).replace("&", "§").replace("%spin%", String.valueOf(slotdatamap.get(slot).spincount)));
        } catch (NullPointerException ignore) {
        }

        if (slotdatamap.get(slot).win_sounds.get(s) != null) {
            for (String s1 : slotdatamap.get(slot).win_sounds.get(s)) {
                String[] sound = s1.split("-");
                player.getWorld().playSound(framedatamap.get(slot).get(1).getBlock().getLocation(), sound[0], Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
            }
        }

        if (slotdatamap.get(slot).win_pot.get(s)) {
            File file = new File(plugin.getDataFolder().getPath() + "/slots/" + slot + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("stock.nowstock", slotdatamap.get(slot).defaultstock);
            config.set("spincount", 0);
            config.save(file);
            vault.deposit(player, money);
            Sign sign = (Sign) signdatamap.get(slot).getBlock().getState();
            sign.getSide(Side.FRONT).setLine(1, "§e§l" + slotdatamap.get(slot).defaultstock);
            sign.getSide(Side.FRONT).setLine(2, "§f§l" + 0);
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slot + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(slot).defaultstock);
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin, slot + "-spincount"), PersistentDataType.INTEGER, 0);
            sign.update(true);
        }

    }

    public boolean hasEnoughCoins(Player player, String s) {
        int remaining = slotdatamap.get(s).coinamount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                if (remaining == 0 || item.getAmount() >= remaining) {
                    return true;
                }
                remaining -= item.getAmount();
            }
        }

        return false;
    }

    private void removeCoins(Player player, String s) {
        int remaining = slotdatamap.get(s).coinamount;
        ItemStack[] invContents = player.getInventory().getContents();
        for (int i = 0; i < invContents.length; i++) {
            ItemStack item = invContents[i];
            if (item != null && item.getItemMeta().getDisplayName().contains("§Ｃ§Ａ§Ｓ§Ｉ§Ｎ§Ｏ")) {
                int amount = item.getAmount();
                if (amount > remaining) {
                    item.setAmount(amount - remaining);
                    break;
                } else if (amount == remaining) {
                    item.setType(Material.AIR);
                    break;
                }

                item.setType(Material.AIR);
                remaining -= amount;
                if (remaining == 0) {
                    break;
                }

                invContents[i] = item;
            }
        }
        player.getInventory().setContents(invContents);
    }

    private void raiseSign(String s) throws IOException {

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();
        double moneypot = sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE);
        int spincount = sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-spincount"), PersistentDataType.INTEGER);
        File file = new File(plugin.getDataFolder().getPath() + "/slots/" + s + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        moneypot += slotdatamap.get(s).raise;

        spincount++;
        config.set("stock.nowstock", moneypot);
        config.set("spincount", spincount);
        config.save(file);
        sign.getSide(Side.FRONT).setLine(1, "§e§l" + moneypot);
        sign.getSide(Side.FRONT).setLine(2, "§f§l" + spincount);
        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, s + "-moneypot"), PersistentDataType.DOUBLE, moneypot);
        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, s + "-spincount"), PersistentDataType.INTEGER, spincount);
        sign.update();
    }

    private void updateSign(String s) {

        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        if (sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE) == null) {
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin , s + "-moneypot"), PersistentDataType.DOUBLE, slotdatamap.get(s).stock);
        }

        if (sign.getPersistentDataContainer().get(new NamespacedKey(plugin , s + "-spincount"), PersistentDataType.INTEGER) == null) {
            sign.getPersistentDataContainer().set(new NamespacedKey(plugin , s + "-spincount"), PersistentDataType.INTEGER, slotdatamap.get(s).spincount);
        }

        if (!sign.isWaxed()) {
            sign.setWaxed(true);
        }
        sign.update(true);
    }

    private void colorSign(String s) {

        slotdatamap.get(s).signnum++;
        Sign sign = (Sign) signdatamap.get(s).getBlock().getState();

        if (slotdatamap.get(s).signnum == 0 || sign.getSide(Side.FRONT).getLine(0).isEmpty()) {
            sign.getSide(Side.FRONT).setLine(0, line1.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line6.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 1) {
            sign.getSide(Side.FRONT).setLine(0, line2.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line7.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 2) {
            sign.getSide(Side.FRONT).setLine(0, line3.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line8.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 3) {
            sign.getSide(Side.FRONT).setLine(0, line4.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line9.replace("a", ""));
        }
        if (slotdatamap.get(s).signnum == 4) {
            sign.getSide(Side.FRONT).setLine(0, line5.replace("a", ""));
            sign.getSide(Side.FRONT).setLine(3, line10.replace("a", ""));
            slotdatamap.get(s).signnum = 0;
        }

        sign.update(true);
    }
}
