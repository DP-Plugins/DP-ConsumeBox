package com.blueearthcat.dpcb.events;

import com.blueearthcat.dpcb.ConsumeBox;
import com.blueearthcat.dpcb.box.GiftBox;
import com.blueearthcat.dpcb.functions.DPCBFunction;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Quadruple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.blueearthcat.dpcb.box.enums.BoxType.*;
import static com.blueearthcat.dpcb.functions.DPCBFunction.*;

public class DPCBEvent implements Listener {
    private static ConsumeBox plugin = ConsumeBox.getInstance();
    private static String prefix = plugin.data.getPrefix();
    private static DLang lang = plugin.data.getLang();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        if (e.getItem() == null) return;
        ItemStack item = e.getItem();
        if (NBT.hasTagKey(item, "dpcb_coupon")) {
            String name = NBT.getStringTag(item, "dpcb_coupon");
            e.setCancelled(true);
            if (!isBoxExist(name)) {
                e.getPlayer().sendMessage(prefix + lang.get("box_not_exists"));
                return;
            }
            GiftBox box = DPCBFunction.getBox(name);
            if (box.getType() == ERROR) {
                e.getPlayer().sendMessage(prefix + lang.get("box_wrong_type"));
                return;
            }
            givePrize(e.getPlayer(), name, item);

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!(e.getInventory().getHolder() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory().getHolder();
        if (!inv.isValidHandler(plugin)) return;
        switch (inv.getChannel()) {
            case 0:
                if (e.getCurrentItem() == null) return;
                if (NBT.hasTagKey(e.getCurrentItem(), "current") || NBT.hasTagKey(e.getCurrentItem(), "pane")) {
                    e.setCancelled(true);
                    return;
                }
                if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
                    e.setCancelled(true);
                    ItemStack[] currentPageItems = inv.getContents();
                    inv.setPageContent(inv.getCurrentPage(), currentPageItems);
                    inv.prevPage();
                    DPCBFunction.updateCurrentPage(inv);
                    inv.update();
                    return;
                }
                if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
                    e.setCancelled(true);
                    ItemStack[] currentPageItems = inv.getContents();
                    inv.setPageContent(inv.getCurrentPage(), currentPageItems);
                    inv.nextPage();
                    DPCBFunction.updateCurrentPage(inv);
                    inv.update();
                    return;
                }
                return;
            case 1: //coupon
                if (e.getClickedInventory() == null) return;
                if (e.getSlot() != 13 && e.getClickedInventory().getType() != InventoryType.PLAYER)
                    e.setCancelled(true);
                return;
            case 2: //list
                if (e.getCurrentItem() == null) return;
                e.setCancelled(true);
                if (NBT.hasTagKey(e.getCurrentItem(), "dpcb_coupon")) {
                    String name = NBT.getStringTag(e.getCurrentItem(), "dpcb_coupon");
                    DPCBFunction.giveGiftBox(p, name, p, true);
                }
                return;
            case 3: //select
                e.setCancelled(true);
                if (e.getClickedInventory() == null) return;
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
                ItemStack item = e.getCurrentItem();
                if (NBT.hasTagKey(item, "ban")) return;
                if (inv.getObj() == null) return;
                Quadruple<ItemStack[], String, ItemStack, Integer> datas = (Quadruple<ItemStack[], String, ItemStack, Integer>) inv.getObj();

                if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
                    e.setCancelled(true);
                    ItemStack[] currentPageItems = inv.getContents();
                    inv.setPageContent(inv.getCurrentPage(), currentPageItems);
                    inv.prevPage();
                    updateCurrentPage2(inv, datas.getB(), datas.getD());
                    inv.update();
                    return;
                }
                if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
                    e.setCancelled(true);
                    ItemStack[] currentPageItems = inv.getContents();
                    inv.setPageContent(inv.getCurrentPage(), currentPageItems);
                    inv.nextPage();
                    updateCurrentPage2(inv, datas.getB(), datas.getD());
                    inv.update();
                    return;
                }
                int page = NBT.getIntegerTag(item, "dpcb_number") / 45;
                int slot = NBT.getIntegerTag(item, "dpcb_number") - 45 * page;
                if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
                    if (NBT.hasTagKey(item, "dpcb_number")) {
                        inv.setCurrentPage(page);
                        inv.setItem(slot, item);
                        item.setType(Material.AIR);
                        datas.setD(datas.getD() + 1);
                        e.getClickedInventory().setItem(e.getSlot(), null);
                        updateCurrentPage2(inv, datas.getB(), datas.getD());
                        inv.setPageContent(inv.getCurrentPage(), inv.getContents());
                    }
                } else {
                    if (NBT.hasTagKey(item, "dpcb_number")) {
                        if (datas.getD() == 0) {
                            p.sendMessage(prefix + lang.get("box_max_selected"));
                            return;
                        }
                        p.getInventory().addItem(item);
                        inv.setCurrentPage(page);
                        inv.setItem(e.getSlot(), getSelectedItem());
                        System.out.println("Clicked Slot : " + e.getSlot());
                        datas.setD(datas.getD() - 1);
                        updateCurrentPage2(inv, datas.getB(), datas.getD());
                        inv.setPageContent(inv.getCurrentPage(), inv.getContents());
                    }
                    if (NBT.hasTagKey(item, "dpcb_select")) {
                        if (datas.getD() != 0) {
                            p.sendMessage(prefix + lang.get("box_not_selected"));
                            return;
                        }
                        ItemStack[] backup = datas.getA();
                        for (ItemStack bi : backup) { //backup에서 쿠폰 1개 제거
                            if (bi == null) continue;
                            if (NBT.hasTagKey(bi, "dpcb_coupon") && NBT.getStringTag(bi, "dpcb_coupon").equalsIgnoreCase(datas.getB())) {
                                bi.setAmount(bi.getAmount() - 1);
                            }
                        }
                        ItemStack[] items = p.getInventory().getStorageContents().clone();
                        for (int i = 0; i < items.length; i++) { //선택 시작 ~ 선택 중에 있던 인벤토리 아이템 필터
                            if (items[i] == null) continue;
                            if (NBT.hasTagKey(items[i], "dpcb_number")) {
                                items[i] = NBT.removeTag(items[i], "dpcb_number");
                            }
                            if (NBT.hasTagKey(items[i], "ban")) {
                                items[i] = null;
                            }
                        }
                        Inventory pinv = Bukkit.createInventory(null, 36);
                        pinv.setContents(backup);
                        for (ItemStack sel : items) { //선택된 아이템 지급
                            if (sel == null) continue;
                            pinv.addItem(sel);
                        }
                        p.getInventory().setStorageContents(pinv.getStorageContents());
                        datas.setA(null);
                        p.closeInventory();
                        p.sendMessage(prefix + datas.getB() + lang.get("box_select_give"));
                    }

                    return;

                }

            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory().getHolder();
        Player p = (Player) e.getPlayer();
        if (!inv.isValidHandler(plugin)) return;
        if (inv.getChannel() == 0) {// item edit mode save
            ItemStack[] currentPageItems = inv.getContents();
            inv.setPageContent(inv.getCurrentPage(), currentPageItems);
            DPCBFunction.saveBoxItems((Player) e.getPlayer(), (String) inv.getObj(), inv);
        }
        if (inv.getChannel() == 1) {
            DPCBFunction.saveCouponItem((Player) e.getPlayer(), (String) inv.getObj(), inv);
        }
        if (inv.getChannel() == 3) {
            if (inv.getObj() == null) return;
            Quadruple<ItemStack[], String, ItemStack, Integer> datas = (Quadruple<ItemStack[], String, ItemStack, Integer>) inv.getObj();
            if (datas.getA() != null) {

                for (int i = 0; i < p.getInventory().getStorageContents().length; i++) {
                    if (p.getInventory().getStorageContents()[i] == null) continue;// was using clone
                    p.getInventory().setItem(i, null);
                }
                for (int i = 0; i < datas.getA().length; i++) {
                    p.getInventory().setItem(i, datas.getA()[i]);
                }
                p.sendMessage(prefix + lang.get("box_selected_cancel"));
            }
        }
    }
}
