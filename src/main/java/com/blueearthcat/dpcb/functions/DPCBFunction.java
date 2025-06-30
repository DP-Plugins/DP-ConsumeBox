package com.blueearthcat.dpcb.functions;

import com.blueearthcat.dpcb.ConsumeBox;
import com.blueearthcat.dpcb.box.GiftBox;
import com.blueearthcat.dpcb.box.enums.BoxType;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@SuppressWarnings("static-access")
public class DPCBFunction {
    private static final ConsumeBox plugin = ConsumeBox.getInstance();
    private static DLang lang;
    private static String prefix;

    public static void init() {
        plugin.boxes.clear();
        lang = plugin.data.getLang();
        prefix = plugin.data.getPrefix();
        for (YamlConfiguration data : ConfigUtils.loadCustomDataList(plugin, "data")) {
            GiftBox box = new GiftBox().deserialize(data);
            plugin.boxes.put(box.getName(), box);
        }
    }

    public static void createBox(Player p, String name, String type) {
        if (isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_exists"));
            return;
        }
        BoxType boxtype = BoxType.fromString(type);
        if (boxtype == BoxType.ERROR) {
            p.sendMessage(prefix + lang.get("box_wrong_type"));
            return;
        }
        GiftBox box = new GiftBox(name, boxtype);
        plugin.boxes.put(name, box);
        saveBox(box);
        p.sendMessage(prefix + name + lang.get("box_create"));
    }

    public static boolean isBoxExist(String name) {
        return plugin.boxes.containsKey(name);
    }

    @Nullable
    public static GiftBox getBox(String name) {
        return plugin.boxes.get(name);
    }


    public static void saveBox(GiftBox box) {
        ConfigUtils.saveCustomData(plugin, box.serialize(), box.getName(), "data");
    }

    public static void saveAllBox() {
        for (GiftBox b : plugin.boxes.values()) {
            saveBox(b);
        }
    }

    public static void setGiftBoxItem(Player p, String name) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        String title = name + lang.get("box_item_title");
        GiftBox box = getBox(name);
        DInventory inv = new DInventory(title, 54, true, plugin);
        inv.setChannel(0);
        int maxPage = box.getMaxPage();
        inv.setPages(maxPage);
        inv.setPageTools(getPageTools(inv));
        for (int i = 0; i < box.getItems().size(); i++) {
            inv.setPageContent(i, box.getItems().get(i));
        }
        inv.setObj(name);
        inv.update();
        p.openInventory(inv.getInventory());
    }

    public static ItemStack[] getPageTools(DInventory inv) {
        ItemStack prev = NBT.setStringTag(new ItemStack(Material.ARROW), "prev", "true");
        ItemStack next = NBT.setStringTag(new ItemStack(Material.ARROW), "next", "true");
        ItemStack current = NBT.setStringTag(new ItemStack(Material.PAPER), "current", "true");
        ItemStack pane = NBT.setStringTag(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "pane", "true");

        ItemMeta im = prev.getItemMeta();
        im.setDisplayName(lang.get("prev_page"));
        prev.setItemMeta(im);

        im = next.getItemMeta();
        im.setDisplayName(lang.get("next_page"));
        next.setItemMeta(im);

        im = current.getItemMeta();
        im.setDisplayName(lang.get("current_page") + (inv.getCurrentPage() + 1) + "/" + (inv.getPages() + 1));
        current.setItemMeta(im);

        im = pane.getItemMeta();
        im.setDisplayName("§f");
        pane.setItemMeta(im);
        return new ItemStack[]{pane, pane, prev, pane, current, pane, next, pane, pane};
    }

    public static void setGiftBoxType(Player p, String name, String type) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        BoxType boxtype = BoxType.fromString(type);
        if (boxtype == BoxType.ERROR) {
            p.sendMessage(prefix + lang.get("box_wrong_type"));
            return;
        }
        box.setType(boxtype);
        plugin.boxes.put(name, box);
        saveBox(box);
        p.sendMessage(prefix + name + lang.get("box_set_type") + type);
    }

    public static void setCouponItem(Player p, String name) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        String title = name + lang.get("box_coupon_title");
        DInventory inv = new DInventory(title, 27, false, plugin);
        for (int i = 0; i < inv.getSize(); i++) { // fill empty space with glass pane
            inv.setItem(i, inv.getPageTools()[0]);
        }
        inv.setItem(13, box.getCouponItem());
        inv.setChannel(1);
        inv.setObj(name);
        p.openInventory(inv.getInventory());
    }

    public static void saveCouponItem(Player p, String name, DInventory inv) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        box.setCouponItem(NBT.removeTag(inv.getItem(13), "dpcb_coupon"));
        saveBox(box);
        p.sendMessage(prefix + name + lang.get("box_coupon_save"));
    }

    public static void givePrize(Player p, String name, ItemStack cp) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        if (box.getItems() == null || box.getItems().isEmpty()) {
            p.sendMessage(prefix + lang.get("box_no_items"));
            return;
        }
        int drop = box.getDrops();
        List<ItemStack> prizes = new ArrayList<>();
        for (int page = 0; page <= box.getMaxPage(); page++) {
            if (box.getItems().get(page) == null) continue;
            for (int i = 0; i < box.getItems().get(page).length; i++) {
                if (box.getItems().get(page)[i] != null) {
                    ItemStack item = box.getItems().get(page)[i].clone();
                    prizes.add(item);
                }
            }
        }
        if (drop > prizes.size()) {
            p.sendMessage(prefix + lang.get("box_wrong_drop"));
            return;
        }
        switch (box.getType()) {
            case RANDOM:
                doPrizeRandom(p, prizes, cp, drop, name);
                return;
            case GIFT:
                doPrizeGift(p, prizes, cp, name);
                return;
            case SELECT:
                Collections.shuffle(prizes);
                List<ItemStack> select = prizes.subList(0, drop);
                ItemStack[] random = select.toArray(new ItemStack[0]);
                if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), random)) {
                    p.sendMessage(prefix + lang.get("player_has_no_space"));
                    return;
                }
                openSelectGUI(p, name, cp);
                return;
            default:
        }
    }

    public static void doPrizeRandom(Player p, List<ItemStack> prizes, ItemStack cp, int drop, String name) {

        Collections.shuffle(prizes);
        List<ItemStack> selected = prizes.subList(0, drop);
        ItemStack[] randomItem = selected.toArray(new ItemStack[0]);

        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), randomItem)) {
            p.sendMessage(prefix + lang.get("player_has_no_space"));
            return;
        }
        p.getInventory().addItem(randomItem);
        p.sendMessage(prefix + name + lang.get("box_random_give"));
        cp.setAmount(cp.getAmount() - 1);
    }

    public static void doPrizeGift(Player p, List<ItemStack> copied, ItemStack cp, String name) {

        ItemStack[] allItems = copied.toArray(new ItemStack[0]);
        if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), allItems)) {
            p.sendMessage(prefix + lang.get("player_has_no_space"));
            return;
        }
        p.getInventory().addItem(allItems);
        p.sendMessage(prefix + name + lang.get("box_gift_give"));
        cp.setAmount(cp.getAmount() - 1);
    }

    public static void openSelectGUI(Player p, String name, ItemStack cp) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        String title = name + lang.get("box_select_title");
        DInventory inv = new DInventory(title, 54, true, plugin);
        inv.setChannel(3);
        int maxPage = box.getMaxPage();
        inv.setPages(maxPage);
        Map<Integer, ItemStack[]> items = box.clone().getItems();
        for (int page = 0; page < box.getMaxPage() + 1; page++) {
            for (int i = 0; i < items.get(page).length; i++) {
                if (items.get(page)[i] == null) continue;
                items.get(page)[i] = NBT.setIntTag(items.get(page)[i], "dpcb_number", page * 45 + i);
            }
        }
        for (int i = 0; i < items.size(); i++) {
            inv.setPageContent(i, items.get(i));
        }
        ItemStack[] pageTools = getPageTools(inv).clone();
        pageTools[4] = getSelectNaviItem(name, inv, box);
        inv.setPageTools(pageTools);
        ItemStack[] playercontents = new ItemStack[36];
        for (int i = 0; i < p.getInventory().getStorageContents().length; i++) {
            playercontents[i] = p.getInventory().getStorageContents()[i]; // was using clone
            p.getInventory().setItem(i, null);
        }
        inv.setObj(new Quadruple(playercontents, name, cp, box.getDrops()));
        int rs = 0;
        for (ItemStack item : playercontents) {
            if (item != null) rs++;
        }
        for (int i = 0; i < rs; i++) {
            p.getInventory().setItem(35 - i, getBanInteractItem());
        }
        inv.update();
        p.openInventory(inv.getInventory());
    }

    public static ItemStack getBanInteractItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(lang.get("box_ban_item"));
        item.setItemMeta(im);
        return NBT.setStringTag(item, "ban", "true");
    }

    public static ItemStack getSelectedItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(lang.get("box_selected_item"));
        item.setItemMeta(im);
        return NBT.setStringTag(item, "ban", "true");
    }

    public static ItemStack getSelectNaviItem(String name, DInventory inv, GiftBox box) {
        ItemStack item = NBT.setStringTag(new ItemStack(Material.LIME_STAINED_GLASS_PANE), "dpcb_select", name);
        ItemMeta im = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        im.setDisplayName(lang.get("box_select_name"));
        lore.add(lang.get("current_page") + (inv.getCurrentPage() + 1) + "/" + (inv.getPages() + 1));
        lore.add(lang.get("box_select_lore") + "0 / " + box.getDrops());
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }

    public static Player getPlayer(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return offlinePlayer.getPlayer();
        } else {
            return null;
        }
    }

    public static void giveGiftBox(CommandSender sender, String name, Player receiver, boolean silence) {
        if (receiver == null) {
            sender.sendMessage(prefix + lang.get("player_wrong"));
            return;
        }
        if (!isBoxExist(name)) {
            sender.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        ItemStack item = box.getCouponItem();
        if (!InventoryUtils.hasEnoughSpace(receiver.getInventory().getStorageContents(), item)) {
            sender.sendMessage(prefix + lang.get("player_has_no_space"));
            return;
        }
        receiver.getInventory().addItem(item);
        if (silence) return;
        sender.sendMessage(prefix + name + lang.get("box_give_coupon"));
        receiver.sendMessage(prefix + name + lang.get("box_receive_coupon"));
    }

    public static void getBoxList(Player p) {
        DInventory inv = new DInventory(lang.get("box_list_title"), 54, true, plugin);
        inv.setChannel(2);
        int maxPage = (int) Math.ceil((plugin.boxes.size() / 45.0));
        int count = plugin.boxes.size();
        List<GiftBox> boxes = new ArrayList<>(plugin.boxes.values());
        inv.setPages(maxPage);
        for (int page = 0; page < maxPage; page++) {
            ItemStack[] contents = new ItemStack[45];
            for (int i = 0; i < 45 && i < count; i++) {
                GiftBox box = boxes.get(i + page * 45);
                ItemStack item = box.getCouponItem().clone();
                ItemMeta im = item.getItemMeta();
                List<String> lore = im.getLore() == null ? new ArrayList<>() : im.getLore();
                lore.add(lang.get("box_list_name") + box.getName());
                im.setLore(lore);
                item.setItemMeta(im);
                contents[i] = item;
            }
            inv.setPageContent(page, contents);
            count -= 45;
        }
        inv.setPageTools(getPageTools(inv));
        updateCurrentPage(inv);
        p.openInventory(inv.getInventory());
    }

    public static void saveBoxItems(Player p, String name, DInventory inv) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        box.setItems(inv);
        plugin.boxes.put(name, box);
        saveBox(box);
        p.sendMessage(prefix + name + lang.get("box_item_save"));
    }

    public static void updateCurrentPage(DInventory inv) {
        ItemStack[] tools = inv.getPageTools();
        ItemStack cpage = tools[4];
        ItemMeta im = cpage.getItemMeta();
        im.setDisplayName(lang.get("current_page") + (inv.getCurrentPage() + 1) + "/" + (inv.getPages() + 1));
        cpage.setItemMeta(im);
        inv.setPageTools(tools);
        inv.update();
    }

    public static void updateCurrentPage2(DInventory inv, String name, int drop) {
        ItemStack[] tools = inv.getPageTools();
        GiftBox box = getBox(name);
        ItemStack cpage = tools[4];
        ItemMeta im = cpage.getItemMeta();
        List<String> lore = im.getLore();
        lore.set(0, lang.get("current_page") + (inv.getCurrentPage() + 1) + "/" + (inv.getPages() + 1));
        lore.set(1, lang.get("box_select_lore") + (box.getDrops() - drop) + " / " + box.getDrops());
        im.setLore(lore);
        cpage.setItemMeta(im);
        inv.setPageTools(tools);
    }

    public static void deleteGiftBox(Player p, String name) {
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        plugin.boxes.remove(name);
        new File(plugin.getDataFolder() + "/data/" + name + ".yml").delete();
        p.sendMessage(prefix + name + lang.get("box_delete"));
    }

    public static void setGiftBoxDrop(Player p, String name, String var) {
        int drop;
        try {
            drop = Integer.parseInt(var);
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + lang.get("box_wrong_drop"));
            return;
        }
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }

        GiftBox box = getBox(name);
        Map<Integer, ItemStack[]> items = box.clone().getItems();
        int size = 0;
        if (items != null | !items.isEmpty()) {
            size = items.get(box.getMaxPage()).length;
        }
        int maxSize = box.getMaxPage() * 45 + size;
        if (drop <= 0 || drop > maxSize) {
            p.sendMessage(prefix + lang.get("box_big_or_small_drop"));
            return;
        }
        box.setDrops(drop);
        plugin.boxes.put(name, box);
        saveBox(box);
        p.sendMessage(prefix + name + lang.get("box_drop_set"));

    }

    public static void setGiftBoxPage(Player p, String name, String var) {
        int page;
        try {
            page = Integer.parseInt(var);
            if (page < 0){
                p.sendMessage(prefix + lang.get("box_wrong_page"));
                return;
            }
        } catch (NumberFormatException e) {
            p.sendMessage(prefix + lang.get("box_wrong_page"));
            return;
        }
        if (!isBoxExist(name)) {
            p.sendMessage(prefix + lang.get("box_not_exists"));
            return;
        }
        GiftBox box = getBox(name);
        Map<Integer, ItemStack[]> items = box.getItems();
        if (page < box.getMaxPage()) {
            for (int i = box.getMaxPage(); i > page; i--) {
                items.remove(i);
            }
        }
        box.setItems(items);
        box.setMaxPage(page);
        plugin.boxes.put(name, box);
        saveBox(box);
        p.sendMessage(prefix + name + lang.get("box_page_set"));

    }

}
