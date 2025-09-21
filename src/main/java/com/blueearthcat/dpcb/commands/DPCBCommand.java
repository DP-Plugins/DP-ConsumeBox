package com.blueearthcat.dpcb.commands;

import com.blueearthcat.dpcb.functions.DPCBFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.blueearthcat.dpcb.ConsumeBox.*;
public class DPCBCommand {
    private final CommandBuilder builder;
    public DPCBCommand() {
        builder = new CommandBuilder(plugin);

        builder.addSubCommand("create", "dpcb.create", plugin.getLang().get("help_create"), true, (p, args) -> {
            if (args.length == 3) DPCBFunction.createBox((Player) p, args[1], args[2]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_create"));
            return true;
        });
        builder.addSubCommand("item", "dpcb.item", plugin.getLang().get("help_item"), true, (p, args) -> {
            if (args.length == 2) DPCBFunction.setGiftBoxItem((Player) p, args[1]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_item"));
            return true;
        });
        builder.addSubCommand("coupon", "dpcb.coupon", plugin.getLang().get("help_coupon"), true, (p, args) -> {
            if (args.length == 2) DPCBFunction.setCouponItem((Player) p, args[1]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_coupon"));
            return true;
        });
        builder.addSubCommand("type", "dpcb.type", plugin.getLang().get("help_type"), true, (p, args) -> {
            if (args.length == 3) DPCBFunction.setGiftBoxType((Player) p, args[1], args[2]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_type"));
            return true;
        });
        builder.addSubCommand("give", "dpcb.give", plugin.getLang().get("help_give"), false, (p, args) -> {
            if (args.length == 2) {
                if (!(p instanceof Player)){
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().get("player_only"));
                    return true;
                }
                DPCBFunction.giveGiftBox(p, args[1], (Player) p, false);
            }
            else if (args.length == 3) DPCBFunction.giveGiftBox(p, args[1],DPCBFunction.getPlayer(args[2]), false);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_give"));
            return true;
        });
        builder.addSubCommand("list", "dpcb.list", plugin.getLang().get("help_list"), true, (p, args) -> {
            if (args.length == 1) DPCBFunction.getBoxList((Player)p);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_list"));
            return true;
        });
        builder.addSubCommand("delete", "dpcb.delete", plugin.getLang().get("help_delete"), true, (p, args) -> {
            if (args.length == 2) DPCBFunction.deleteGiftBox((Player)p, args[1]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_delete"));
            return true;
        });
        builder.addSubCommand("drop", "dpcb.drop", plugin.getLang().get("help_drop"), true, (p, args) -> {
            if (args.length == 3) DPCBFunction.setGiftBoxDrop((Player)p, args[1], args[2]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_drop"));
            return true;
        });
        builder.addSubCommand("page", "dpcb.page", plugin.getLang().get("help_page"), true, (p, args) -> {
            if (args.length == 3) DPCBFunction.setGiftBoxPage((Player)p, args[1], args[2]);
            else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("help_page"));
            return true;
        });
        List<String> commands = Arrays.asList("create", "item", "coupon", "type", "give", "delete", "drop", "page");
        for (String c: commands) {
            builder.addTabCompletion(c, args -> {
                if (args.length == 2) return new ArrayList<>(plugin.boxes.keySet());
                else if (args.length == 3)
                  if (c.equalsIgnoreCase("create") | c.equalsIgnoreCase("type"))
                    return Arrays.asList("select", "random", "gift");
                return null;
            });
        }


    }

    public CommandExecutor getExecuter() {
        return builder;
    }
}
