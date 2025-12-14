package com.darksoldier1404.dpcb.events;

import com.darksoldier1404.dpcb.functions.DPCBFunction;
import com.darksoldier1404.dpcb.obj.Box;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.events.dinventory.*;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static com.darksoldier1404.dpcb.ConsumeBox.plugin;

public class DPCBEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (plugin.currentWeightSet.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            DPCBFunction.applyWeightSet(p, e.getMessage());
        }
    }

    @EventHandler
    public void onInventoryClick(DInventoryClickEvent e) {
        DInventory inv = e.getDInventory();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(3)) {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                Box box = (Box) inv.getObj();
                int maxRewards = box.getRewardAmount();
                if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER)
                    return;
                ItemStack item = e.getCurrentItem();
                if (item == null || item.getType().isAir()) {
                    return;
                }
                if (NBT.hasTagKey(item, "dpcb_confirm")) {
                    DPCBFunction.giveSelectedReward((Player) e.getWhoClicked(), inv);
                    return;
                }
                if (NBT.hasTagKey(item, "dppc_clickcancel")) return;
                if (NBT.getItemStackTag(item, "dpcb_selected") != null) {
                    ItemStack original = NBT.getItemStackTag(item, "dpcb_selected");
                    e.setCurrentItem(original);
                    inv.applyChanges();
                    inv.update();
                    plugin.currentSelectedCount.put(p.getUniqueId(), plugin.currentSelectedCount.getOrDefault(p.getUniqueId(), 0) - 1);
                    return;
                }
                if (plugin.currentSelectedCount.getOrDefault(p.getUniqueId(), 0) >= maxRewards) {
                    p.sendMessage(plugin.getPrefix() + "§cYou have reached the maximum number of selections for this box!");
                    p.sendMessage(plugin.getPrefix() + "§eClick the §a§lConfirm §eto receive your rewards.");
                    return;
                }
                ItemStack temp = item.clone();
                ItemStack selected = DPCBFunction.getSelectedItem();
                if (selected == null || selected.getType().isAir()) {
                    p.sendMessage(plugin.getPrefix() + "§cThe selected item is not set! Please contact an administrator.");
                    return;
                }
                selected = selected.clone();
                e.setCurrentItem(NBT.setItemStackTag(selected, "dpcb_selected", temp));
                inv.applyChanges();
                inv.update();
                plugin.currentSelectedCount.put(p.getUniqueId(), plugin.currentSelectedCount.getOrDefault(p.getUniqueId(), 0) + 1);
                return;
            }
            if (inv.isValidChannel(2)) { // weight setting
                e.setCancelled(true);
                if (NBT.hasTagKey(e.getCurrentItem(), "dpcb_weight")) {
                    DInventory.PageItemSet pis = e.getPageItemSet();
                    if (pis == null) return;
                    ItemStack item = pis.getItem();
                    if (item == null || item.getType().isAir()) {
                        return;
                    }
                    DPCBFunction.setWeightWithChat((Player) e.getWhoClicked(), inv, pis);
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) {
            return;
        }
        if (NBT.hasTagKey(item, "dpcb_name")) {
            e.setCancelled(true);
            DPCBFunction.handleCouponUse(e.getPlayer(), item);
        }
    }

    @EventHandler
    public void onInventoryOpen(DInventoryOpenEvent e) {
        DInventory inv = e.getDInventory();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(3)) {
                inv.setUseDefaultPageTools(false);
                DPCBFunction.applyDefaultPageTools(inv);
                inv.updatePageTools();
            } else {
                inv.setUseDefaultPageTools(true);
                inv.applyDefaultPageTools();
                inv.updatePageTools();
            }
        }
    }

    @EventHandler
    public void onNextPage(DInventoryNextPageEvent e) {
        DInventory inv = e.getDInventory();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(3)) {
                e.setCancelled(true);
                inv.applyChanges();
                inv.nextPage();
                DPCBFunction.applyDefaultPageTools(inv);
                inv.updatePageTools();
            }
        }
    }

    @EventHandler
    public void onPrevPage(DInventoryPreviousPageEvent e) {
        DInventory inv = e.getDInventory();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(3)) {
                e.setCancelled(true);
                inv.applyChanges();
                inv.prevPage();
                DPCBFunction.applyDefaultPageTools(inv);
                inv.updatePageTools();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(DInventoryCloseEvent e) {
        DInventory inv = e.getDInventory();
        Player p = (Player) e.getPlayer();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(1)) {
                DPCBFunction.saveItems(p, inv);
                return;
            }
            if (inv.isValidChannel(3)) {
                plugin.currentSelectedCount.remove(p.getUniqueId());
                return;
            }
            if (inv.isValidChannel(101)) {
                ItemStack item = inv.getItem(13);
                DPCBFunction.saveDefaultCouponItem(p, item);
                return;
            }
            if (inv.isValidChannel(201)) {
                DPCBFunction.saveCouponItem(p, inv);
                return;
            }
            if (inv.isValidChannel(301)) {
                DPCBFunction.saveSelectedItem(p, inv);
            }
        }
    }
}
