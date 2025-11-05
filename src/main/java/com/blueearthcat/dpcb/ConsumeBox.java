package com.blueearthcat.dpcb;

import com.blueearthcat.dpcb.box.GiftBox;
import com.blueearthcat.dpcb.commands.DPCBCommand;
import com.blueearthcat.dpcb.events.DPCBEvent;
import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;

@DPPCoreVersion(since = "5.3.0")
public class ConsumeBox extends DPlugin {
    public static ConsumeBox plugin;
    public static DataContainer<String, GiftBox> boxes;

    public static ConsumeBox getInstance() {
        return plugin;
    }

    public ConsumeBox() {
        super(true);
        plugin = this;
        init();
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(plugin, 25979);
        boxes = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "data"), GiftBox.class);
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(new DPCBEvent(), plugin);
        getCommand("dpcb").setExecutor(new DPCBCommand().getExecuter());
    }

    @Override
    public void onDisable() {
        saveAllData();
    }
}
