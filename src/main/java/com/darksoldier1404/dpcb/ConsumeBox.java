package com.darksoldier1404.dpcb;

import com.darksoldier1404.dpcb.obj.Box;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dpcb.commands.DPCBCommand;
import com.darksoldier1404.dpcb.events.DPCBEvent;
import com.darksoldier1404.dppc.utils.Tuple;

import java.util.HashMap;
import java.util.UUID;

public class ConsumeBox extends DPlugin {
    public static ConsumeBox plugin;
    public static DataContainer<String, Box> data;
    public static HashMap<UUID, Integer> currentSelectedCount = new HashMap<>();
    public HashMap<UUID, Tuple<DInventory, DInventory.PageItemSet>> currentWeightSet = new HashMap<>();

    public ConsumeBox() {
        super(false);
        plugin = this;
        init();
        data = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "data"), Box.class);
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(plugin, 25979);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DPCBEvent(), plugin);
        DPCBCommand.init();
    }

    @Override
    public void onDisable() {
        saveAllData();
    }
}
