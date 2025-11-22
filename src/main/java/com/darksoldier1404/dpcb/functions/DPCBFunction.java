package com.darksoldier1404.dpcb.functions;


import com.darksoldier1404.dpcb.enums.BoxType;
import com.darksoldier1404.dpcb.enums.RandomType;
import com.darksoldier1404.dpcb.obj.Box;
import com.darksoldier1404.dpcb.obj.BoxWeight;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.InventoryUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.darksoldier1404.dpcb.ConsumeBox.plugin;

public class DPCBFunction {

    public static boolean isExistingBox(String name) {
        return plugin.data.containsKey(name);
    }

    public static void createBox(Player p, String name, BoxType type) {
        if (isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cA box with that name already exists.");
        } else {
            Box box = new Box(name, type, RandomType.SIMPLE, new DInventory(name, 54, true, plugin), null, 0);
            plugin.data.put(name, box);
            p.sendMessage(plugin.getPrefix() + "§aConsume Box '" + name + "' of type '" + type.toString() + "' has been created.");
        }
    }

    public static void editItems(Player p, String name) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            box.getInventory().setChannel(1);
            box.openInventory(p);
            p.sendMessage(plugin.getPrefix() + "§aYou are now editing the items of the Consume Box '" + name + "'.");
        }
    }

    public static void saveItems(Player p, DInventory inv) {
        Box box = (Box) inv.getObj();
        inv.applyChanges();
        inv.setChannel(0);
        box.setInventory(inv);
        plugin.data.put(box.getName(), box);
        plugin.data.save(box.getName());
        p.sendMessage(plugin.getPrefix() + "§aItems for the Consume Box '" + box.getName() + "' have been saved.");
    }

    public static ItemStack[] getPaneContent() {
        ItemStack pane = NBT.setStringTag(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "dppc_clickcancel", "true");
        ItemMeta meta = pane.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        ItemStack[] contents = new ItemStack[27];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = pane;
        }
        return contents;
    }

    public static void openDefaultCouponEditor(Player p) {
        DInventory inv = new DInventory("Default Coupon Item", 27, plugin);
        inv.setChannel(101);
        inv.setContents(getPaneContent());
        inv.setItem(13, plugin.getConfig().getItemStack("Settings.DefaultCouponItem"));
        inv.openInventory(p);
    }

    public static void saveDefaultCouponItem(Player p, ItemStack item) {
        plugin.getConfig().set("Settings.DefaultCouponItem", item);
        plugin.saveConfig();
        p.sendMessage(plugin.getPrefix() + "§aDefault coupon item has been saved.");
    }

    public static void openCouponEditor(Player p, String name) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            DInventory inv = new DInventory("Coupon Item - " + name, 27, plugin);
            inv.setChannel(201);
            inv.setObj(box);
            inv.setContents(getPaneContent());
            ItemStack item = box.getCouponItem();
            inv.setItem(13, item);
            inv.openInventory(p);
        }
    }

    public static void saveCouponItem(Player p, DInventory inv) {
        Box box = (Box) inv.getObj();
        box.setCouponItem(inv.getItem(13));
        plugin.data.put(box.getName(), box);
        plugin.data.save(box.getName());
        p.sendMessage(plugin.getPrefix() + "§aCoupon item for the Consume Box '" + box.getName() + "' has been saved.");
    }

    public static void changeBoxType(Player p, String name, BoxType type) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            box.setType(type);
            plugin.data.put(name, box);
            plugin.data.save(name);
            p.sendMessage(plugin.getPrefix() + "§aConsume Box '" + name + "' type has been changed to '" + type.toString() + "'.");
        }
    }

    public static List<ItemStack> getRandomItems(Box box, int amount) {
        DInventory inv = box.getInventory().clone();
        if (box.getRandomType() == RandomType.SIMPLE) {
            List<ItemStack> items = inv.getAllPageItems().stream().filter(item -> item != null && item.getType() != Material.AIR).toList();
            if (items.isEmpty()) {
                return new ArrayList<>();
            }
            List<ItemStack> randomItems = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                int randomIndex = (int) (Math.random() * items.size());
                randomItems.add(items.get(randomIndex).clone());
            }
            return randomItems;
        } else {
            List<DInventory.PageItemSet> allItems = inv.getAllPageItemSets();
            List<Integer> weights = new ArrayList<>();
            int totalWeight = 0;
            for (DInventory.PageItemSet pis : allItems) {
                int weight = box.findWeight(pis.getPage(), pis.getSlot());
                weights.add(weight);
                totalWeight += weight;
            }
            List<ItemStack> selectedItems = new ArrayList<>();
            while (selectedItems.size() < amount) {
                int rand = new Random().nextInt(totalWeight);
                int sum = 0;
                for (int i = 0; i < allItems.size(); i++) {
                    sum += weights.get(i);
                    if (rand < sum) {
                        selectedItems.add(allItems.get(i).getItem().clone());
                        break;
                    }
                }
            }
            return selectedItems;
        }
    }

    public static void applyDefaultPageTools(DInventory inv) {
        int currentPage = inv.getCurrentPage();
        int pages = inv.getPages();
        ItemStack[] defaultPageTools = new ItemStack[9];
        ItemStack pane = NBT.setStringTag(new ItemStack(org.bukkit.Material.BLACK_STAINED_GLASS_PANE), "dppc_clickcancel", "true");
        ItemMeta meta = pane.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);
        ItemStack nextPage = NBT.setStringTag(new ItemStack(org.bukkit.Material.ARROW), "dppc_clickcancel", "true");
        nextPage = NBT.setStringTag(nextPage, "dppc_nextpage", "true");
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName("§aNext Page (" + (currentPage + 1) + "/" + (pages + 1) + ")");
        nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        nextPage.setItemMeta(nextMeta);
        ItemStack prevPage = NBT.setStringTag(new ItemStack(org.bukkit.Material.ARROW), "dppc_clickcancel", "true");
        prevPage = NBT.setStringTag(prevPage, "dppc_prevpage", "true");
        ItemMeta prevMeta = prevPage.getItemMeta();
        prevMeta.setDisplayName("§aPrevious Page (" + (currentPage + 1) + "/" + (pages + 1) + ")");
        prevMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        prevPage.setItemMeta(prevMeta);
        ItemStack confirmButton = NBT.setStringTag(new ItemStack(org.bukkit.Material.GREEN_WOOL), "dpcb_confirm", "true");
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");
        confirmMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        confirmButton.setItemMeta(confirmMeta);
        defaultPageTools[0] = pane; // Slot 0
        defaultPageTools[1] = prevPage; // Slot 1
        defaultPageTools[2] = pane; // Slot 2
        defaultPageTools[3] = pane; // Slot 3
        defaultPageTools[4] = confirmButton; // Slot 4
        defaultPageTools[5] = pane; // Slot 5
        defaultPageTools[6] = pane; // Slot 6
        defaultPageTools[7] = nextPage; // Slot 7
        defaultPageTools[8] = pane; // Slot 8
        inv.setPageTools(defaultPageTools);
    }

    public static void setMaxPage(Player p, String name, int page) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            DInventory inv = box.getInventory();
            inv.setPages(page);
            box.setInventory(inv);
            plugin.data.put(name, box);
            plugin.data.save(name);
            p.sendMessage(plugin.getPrefix() + "§aMaximum page for the Consume Box '" + name + "' has been set to " + page + ".");
        }
    }

    public static void setRewardAmount(Player p, String name, int amount) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            box.setRewardAmount(amount);
            plugin.data.put(name, box);
            plugin.data.save(name);
            p.sendMessage(plugin.getPrefix() + "§aReward amount for the Consume Box '" + name + "' has been set to " + amount + ".");
        }
    }

    public static void giveRandomReward(Player p, String name) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        }
        Box box = plugin.data.get(name);
        ItemStack coupon = p.getInventory().getItemInMainHand();
        if (!isValidCoupon(coupon, box.getName())) {
            p.sendMessage(plugin.getPrefix() + "§cYou are not holding a valid coupon for this Consume Box.");
            return;
        }
        int amount = box.getRewardAmount();
        if (amount == 0) {
            p.sendMessage(plugin.getPrefix() + "§cThe reward amount for the Consume Box '" + name + "' is set to 0. Please set a valid reward amount.");
            return;
        }
        List<ItemStack> rewards = getRandomItems(box, amount);
        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), rewards)) {
            p.sendMessage(plugin.getPrefix() + "§cYou do not have enough inventory space to receive the rewards.");
            p.sendMessage(plugin.getPrefix() + "§cYou need at least " + amount + " empty slot(s).");
            return;
        }
        for (ItemStack reward : rewards) {
            if (reward != null) {
                p.getInventory().addItem(reward);
            }
        }
        coupon.setAmount(coupon.getAmount() - 1);
        p.sendMessage(plugin.getPrefix() + "§aYou have received " + amount + " random item(s) from the Consume Box '" + name + "'.");
    }

    public static void giveGiftReward(Player p, String name) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        }
        Box box = plugin.data.get(name);
        ItemStack coupon = p.getInventory().getItemInMainHand();
        if (!isValidCoupon(coupon, box.getName())) {
            p.sendMessage(plugin.getPrefix() + "§cYou are not holding a valid coupon for this Consume Box.");
            return;
        }
        List<ItemStack> rewards = box.getInventory().getAllPageItems();
        int amount = rewards.size();
        if (amount == 0) {
            p.sendMessage(plugin.getPrefix() + "§cThe reward amount for the Consume Box '" + name + "' is set to 0. Please set a valid reward amount.");
            return;
        }
        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), rewards)) {
            p.sendMessage(plugin.getPrefix() + "§cYou do not have enough inventory space to receive the rewards.");
            p.sendMessage(plugin.getPrefix() + "§cYou need at least " + amount + " empty slot(s).");
            return;
        }
        for (ItemStack reward : rewards) {
            if (reward != null) {
                p.getInventory().addItem(reward);
            }
        }
        coupon.setAmount(coupon.getAmount() - 1);
        p.sendMessage(plugin.getPrefix() + "§aYou have received all items from the Consume Box '" + name + "'.");
    }

    public static void openSelectedItemEditor(Player p) {
        DInventory inv = new DInventory("Selected Item Editor", 27, plugin);
        inv.setChannel(301);
        inv.setContents(getPaneContent());
        inv.setItem(13, plugin.getConfig().getItemStack("Settings.SelectedItem"));
        inv.openInventory(p);
    }

    public static void saveSelectedItem(Player p, DInventory inv) {
        ItemStack selectedItem = inv.getItem(13);
        plugin.getConfig().set("Settings.SelectedItem", selectedItem);
        plugin.saveConfig();
        p.sendMessage(plugin.getPrefix() + "§aSelected item has been saved.");
    }

    public static void giveSelectedReward(Player p, DInventory inv) {
        if (inv.getObj() == null || !(inv.getObj() instanceof Box)) {
            p.sendMessage(plugin.getPrefix() + "§cInvalid Consume Box.");
            return;
        }
        Box box = (Box) inv.getObj();
        ItemStack coupon = p.getInventory().getItemInMainHand();
        if (!isValidCoupon(coupon, box.getName())) {
            p.sendMessage(plugin.getPrefix() + "§cYou are not holding a valid coupon for this Consume Box.");
            return;
        }
        List<ItemStack> selectedItems = new ArrayList<>();
        for (ItemStack item : inv.getAllPageItems()) {
            if (NBT.getItemStackTag(item, "dpcb_selected") != null) {
                selectedItems.add(NBT.getItemStackTag(item, "dpcb_selected"));
            }
        }
        if (selectedItems.isEmpty()) {
            p.sendMessage(plugin.getPrefix() + "§cNo selected items found in the Consume Box.");
            return;
        }
        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), selectedItems)) {
            p.sendMessage(plugin.getPrefix() + "§cYou do not have enough inventory space to receive the rewards.");
            p.sendMessage(plugin.getPrefix() + "§cYou need at least " + selectedItems.size() + " empty slot(s).");
            return;
        }
        coupon.setAmount(coupon.getAmount() - 1);
        for (ItemStack reward : selectedItems) {
            if (reward != null) {
                p.getInventory().addItem(reward);
            }
        }
        p.closeInventory();
        p.sendMessage(plugin.getPrefix() + "§aYou have received " + selectedItems.size() + " selected item(s) from the Consume Box.");
    }

    public static boolean isValidCoupon(ItemStack item, String boxName) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        if (!isExistingBox(boxName)) {
            return false;
        }
        return NBT.hasTagKey(item, "dpcb_name") && NBT.getStringTag(item, "dpcb_name").equals(boxName);
    }

    public static void handleCouponUse(Player p, ItemStack item) {
        String name = NBT.getStringTag(item, "dpcb_name");
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
            return;
        }
        Box box = plugin.data.get(name);
        if (box.getType() == BoxType.SELECT) {
            box.openSelectInventory(p);
        } else if (box.getType() == BoxType.RANDOM) {
            giveRandomReward(p, name);
        } else if (box.getType() == BoxType.GIFT) {
            giveGiftReward(p, name);
        }
    }

    public static void giveCoupon(Player p, String name, OfflinePlayer target, int amount) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
            return;
        }
        if (!target.isOnline()) {
            p.sendMessage(plugin.getPrefix() + "§cThe target player is not online.");
            return;
        }
        if (amount <= 0) {
            p.sendMessage(plugin.getPrefix() + "§cAmount must be greater than 0.");
            return;
        }
        Box box = plugin.data.get(name);
        ItemStack couponItem = box.getCouponItem();
        if (couponItem == null) {
            couponItem = plugin.getConfig().getItemStack("Settings.DefaultCouponItem");
        }
        if (couponItem == null || couponItem.getType().isAir()) {
            p.sendMessage(plugin.getPrefix() + "§cNo coupon item is set for this Consume Box. also the default coupon item is not set.");
            return;
        }
        ItemStack coupon = couponItem.clone();
        coupon.setAmount(amount);
        coupon = NBT.setStringTag(coupon, "dpcb_name", name);
        Player targetPlayer = (Player) target;
        if (!InventoryUtils.hasEnoughSpace(targetPlayer.getInventory().getStorageContents(), coupon)) {
            p.sendMessage(plugin.getPrefix() + "§cThe target player does not have enough inventory space to receive the coupons.");
            return;
        }
        targetPlayer.getInventory().addItem(coupon);
        p.sendMessage(plugin.getPrefix() + "§aYou have given " + amount + " coupon(s) of the Consume Box '" + name + "' to " + target.getName() + ".");
        targetPlayer.sendMessage(plugin.getPrefix() + "§aYou have received " + amount + " coupon(s) for the Consume Box '" + name + "' from " + p.getName() + ".");
    }

    public static void removeBox(Player p, String name) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            plugin.data.delete(name);
            p.sendMessage(plugin.getPrefix() + "§aConsume Box '" + name + "' has been removed.");
        }
    }

    public static ItemStack getSelectedItem() {
        return plugin.getConfig().getItemStack("Settings.SelectedItem");
    }

    public static void setWeightWithChat(Player p, DInventory inv, DInventory.PageItemSet pis) {
        plugin.currentWeightSet.put(p.getUniqueId(), Tuple.of(inv, pis));
        p.sendMessage(plugin.getPrefix() + "§aPlease enter the weight for the page: " + pis.getPage() + ", slot: " + pis.getSlot() + " in the chat.");
        p.sendMessage(plugin.getPrefix() + "§aType §e0 §ato remove the weight.");
        p.closeInventory();
    }

    public static void applyWeightSet(Player p, String sWeight) {
        if (!plugin.currentWeightSet.containsKey(p.getUniqueId())) {
            p.sendMessage(plugin.getPrefix() + "§cNo weight setting in progress.");
            return;
        }
        int weight;
        try {
            weight = Integer.parseInt(sWeight);
            if (weight < 0) {
                p.sendMessage(plugin.getPrefix() + "§cWeight must be a non-negative integer.");
                return;
            }
        } catch (NumberFormatException e) {
            p.sendMessage(plugin.getPrefix() + "§cInvalid weight. Please enter a valid non-negative integer.");
            return;
        }
        Tuple<DInventory, DInventory.PageItemSet> tuple = plugin.currentWeightSet.get(p.getUniqueId());
        DInventory inv = tuple.getA();
        DInventory.PageItemSet pis = tuple.getB();
        ItemStack item = pis.getItem();
        if (item == null || item.getType().isAir()) {
            p.sendMessage(plugin.getPrefix() + "§cInvalid item for weight setting.");
            plugin.currentWeightSet.remove(p.getUniqueId());
            return;
        }
        plugin.currentWeightSet.remove(p.getUniqueId());
        Box box = (Box) inv.getObj();
        BoxWeight bw = box.findBoxWeight(pis.getPage(), pis.getSlot());
        box.getWeightList().remove(bw);
        if (weight == 0) {
            plugin.data.put(box.getName(), box);
            plugin.data.save(box.getName());
            p.sendMessage(plugin.getPrefix() + "§aWeight for page: " + pis.getPage() + ", slot: " + pis.getSlot() + " has been removed.");
        } else {
            bw.setWeight(weight);
            box.getWeightList().add(bw);
            plugin.data.put(box.getName(), box);
            plugin.data.save(box.getName());
            p.sendMessage(plugin.getPrefix() + "§aWeight for page: " + pis.getPage() + ", slot: " + pis.getSlot() + " has been set to " + weight + ".");
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> box.openWeightEditInventory(p, inv.getCurrentPage()), 1L);
    }

    public static void updateChanceLore(DInventory inv) {
        Box box = (Box) inv.getObj();
        if (box == null) return;
        inv.applyAllItemChanges((pis -> {
            ItemStack item = pis.getItem();
            if (item == null || item.getType().isAir()) {
                return pis;
            }
            int totalWeight = 0;
            for (DInventory.PageItemSet pageItemSet : inv.getAllPageItemSets()) {
                totalWeight += box.findWeight(pageItemSet.getPage(), pageItemSet.getSlot());
            }
            int weight = box.findWeight(pis.getPage(), pis.getSlot());
            List<String> lore = item.getItemMeta() != null && item.getItemMeta().getLore() != null ? item.getItemMeta().getLore() : new ArrayList<>();
            if (weight > 0) {
                lore.add("§7Weight: §e" + weight);
                double chance = (double) weight / (double) totalWeight * 100.0;
                lore.add("§7Chance: §e" + String.format("%.2f", chance) + "%");
            }
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
            pis.setItem(item);
            return pis;
        }));
        inv.update();
    }

    public static void setRandomType(Player p, String name, RandomType randomType) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            box.setRandomType(randomType);
            plugin.data.put(name, box);
            plugin.data.save(name);
            p.sendMessage(plugin.getPrefix() + "§aConsume Box '" + name + "' random type has been changed to '" + randomType.toString() + "'.");
        }
    }

    public static void openWeightEditor(Player p, String name) {
        if (!isExistingBox(name)) {
            p.sendMessage(plugin.getPrefix() + "§cNo box found with that name.");
        } else {
            Box box = plugin.data.get(name);
            box.openWeightEditInventory(p, 0);
            p.sendMessage(plugin.getPrefix() + "§aYou are now editing the weights of the Consume Box '" + name + "'.");
        }
    }

    public static void listBoxes(Player p) {
        if (plugin.data.isEmpty()) {
            p.sendMessage(plugin.getPrefix() + "§eNo Consume Boxes available.");
            return;
        }
        p.sendMessage(plugin.getPrefix() + "§aAvailable Consume Boxes:");
        for (String boxName : plugin.data.keySet()) {
            Box box = plugin.data.get(boxName);
            p.sendMessage("§e- " + boxName + " §7(Type: " + box.getType().toString() + ", RandomType: " + box.getRandomType().toString() + ", RewardAmount: " + box.getRewardAmount() + ", CouponItem: " + (box.getCouponItem() != null ? "Set" : "Not Set") + ", Pages: " + (box.getInventory() != null ? box.getInventory().getPages() : 0) + ", Total Items: " + (box.getInventory() != null ? box.getInventory().getAllPageItems().stream().filter(item -> item != null && item.getType() != Material.AIR).count() : 0) + ")");
        }
    }
}
