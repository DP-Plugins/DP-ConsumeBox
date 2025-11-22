package com.darksoldier1404.dpcb.obj;

import com.darksoldier1404.dpcb.enums.BoxType;
import com.darksoldier1404.dpcb.enums.RandomType;
import com.darksoldier1404.dpcb.functions.DPCBFunction;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DataCargo;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.darksoldier1404.dpcb.ConsumeBox.plugin;

public class Box implements DataCargo {
    private String name;
    private BoxType type = BoxType.RANDOM;
    private RandomType randomType = RandomType.SIMPLE;
    private DInventory inventory;
    private @Nullable ItemStack couponItem;
    private int rewardAmount = 0;
    private final List<BoxWeight> weightList = new ArrayList<>();

    public Box() {
    }

    public Box(String name, BoxType type, RandomType randomType, DInventory inventory, @Nullable ItemStack couponItem, int rewardAmount) {
        this.name = name;
        this.type = type;
        this.randomType = randomType;
        this.inventory = inventory;
        this.couponItem = couponItem;
        this.rewardAmount = rewardAmount;
        inventory.setObj(this);
    }

    public String getName() {
        return name;
    }

    public BoxType getType() {
        return type;
    }

    public void setType(BoxType type) {
        this.type = type;
    }

    public RandomType getRandomType() {
        return randomType;
    }

    public void setRandomType(RandomType randomType) {
        this.randomType = randomType;
    }

    public DInventory getInventory() {
        return inventory;
    }

    public void setInventory(DInventory inventory) {
        this.inventory = inventory;
    }

    @Nullable
    public ItemStack getCouponItem() {
        return couponItem;
    }

    public void setCouponItem(@Nullable ItemStack couponItem) {
        this.couponItem = couponItem;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public List<BoxWeight> getWeightList() {
        return weightList;
    }

    public int findWeight(int page, int slot) {
        for (BoxWeight boxWeight : weightList) {
            if (boxWeight.getPage() == page && boxWeight.getSlot() == slot) {
                return boxWeight.getWeight();
            }
        }
        return 0;
    }

    public BoxWeight findBoxWeight(int page, int slot) {
        for (BoxWeight boxWeight : weightList) {
            if (boxWeight.getPage() == page && boxWeight.getSlot() == slot) {
                return boxWeight;
            }
        }
        return new BoxWeight(page, slot, 0);
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("name", this.name);
        data.set("type", this.type.toString());
        data.set("randomType", this.randomType.toString());
        data.set("couponItem", this.couponItem);
        data.set("rewardAmount", this.rewardAmount);
        for (BoxWeight boxWeight : weightList) {
            data = boxWeight.serialize(data);
        }
        data = inventory.serialize(data);
        return data;
    }

    @Override
    public Box deserialize(YamlConfiguration data) {
        this.name = data.getString("name");
        this.type = BoxType.valueOf(data.getString("type"));
        this.randomType = RandomType.valueOf(data.getString("randomType"));
        this.couponItem = data.getItemStack("couponItem");
        this.rewardAmount = data.getInt("rewardAmount", 0);
        if (data.contains("BoxWeight")) {
            data.getConfigurationSection("BoxWeight").getKeys(false).forEach(pageKey -> {
                int page = Integer.parseInt(pageKey);
                data.getConfigurationSection("BoxWeight." + pageKey).getKeys(false).forEach(slotKey -> {
                    int slot = Integer.parseInt(slotKey);
                    int weight = data.getInt("BoxWeight." + pageKey + "." + slotKey + ".weight", 0);
                    BoxWeight boxWeight = new BoxWeight(page, slot, weight);
                    weightList.add(boxWeight);
                });
            });
        }
        this.inventory = new DInventory(name, 54, true, plugin).deserialize(data);
        return this;
    }

    public void openInventory(Player p) {
        inventory.setObj(this);
        inventory.setCurrentPage(0);
        inventory.update();
        inventory.openInventory(p);
    }

    public void openSelectInventory(Player p) {
        inventory.setObj(this);
        inventory.setCurrentPage(0);
        inventory.update();
        inventory.setChannel(3);
        inventory.clone().openInventory(p);
    }

    public void openWeightEditInventory(Player p, int page) {
        inventory.setObj(this);
        inventory.update();
        inventory.setChannel(2);
        DInventory inv = inventory.clone();
        inv.setCurrentPage(page);
        inv.applyAllItemChanges(pis -> {
            ItemStack item = pis.getItem();
            pis.setItem(NBT.setStringTag(item, "dpcb_weight", "true"));
            return pis;
        });
        DPCBFunction.updateChanceLore(inv);
        inv.openInventory(p);
    }
}
