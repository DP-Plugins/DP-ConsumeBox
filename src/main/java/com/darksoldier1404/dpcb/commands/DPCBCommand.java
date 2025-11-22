package com.darksoldier1404.dpcb.commands;

import com.darksoldier1404.dpcb.ConsumeBox;
import com.darksoldier1404.dpcb.enums.BoxType;
import com.darksoldier1404.dpcb.enums.RandomType;
import com.darksoldier1404.dpcb.functions.DPCBFunction;
import com.darksoldier1404.dppc.builder.command.ArgumentType;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.darksoldier1404.dpcb.ConsumeBox.plugin;
import static com.darksoldier1404.dppc.builder.command.ArgumentIndex.*;


public class DPCBCommand {

    public static void init() {
        final CommandBuilder builder = new CommandBuilder(plugin);

        builder.beginSubCommand("create", "/dpcb create <name> <type> - Create a new Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING)
                .withArgument(ARG_1, ArgumentType.STRING, Arrays.stream(BoxType.values()).map(Enum::name).collect(Collectors.toList()))
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    BoxType type = BoxType.valueOf(args.getString(ARG_1).toUpperCase());
                    DPCBFunction.createBox(p, name, type);
                    return true;
                });
        builder.beginSubCommand("items", "/dpcb items <name> - Edit items an existing Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.editItems(p, name);
                    return true;
                });

        builder.beginSubCommand("maxpage", "/dpcb maxpage <name> <page> - Set maximum page for a specific Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.INTEGER)
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    int page = args.getInteger(ARG_1);
                    DPCBFunction.setMaxPage(p, name, page);
                    return true;
                });

        builder.beginSubCommand("setSelectedItem", "/dpcb setSelectedItem - Set selected item for SELECT type Consume Box")
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    DPCBFunction.openSelectedItemEditor(p);
                    return true;
                });

        builder.beginSubCommand("setDefaultCoupon", "/dpcb setDefaultCoupon - Set default coupon item for all Consume Box")
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    DPCBFunction.openDefaultCouponEditor(p);
                    return true;
                });

        builder.beginSubCommand("setCoupon", "/dpcb setCoupon <name> - Set coupon item for a specific Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.openCouponEditor(p, name);
                    return true;
                });

        builder.beginSubCommand("remove", "/dpcb remove <name> - Remove an existing Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.removeBox(p, name);
                    return true;
                });

        builder.beginSubCommand("type", "/dpcb type <name> <type> - Change the type of an existing Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.STRING, Arrays.stream(BoxType.values()).map(Enum::name).collect(Collectors.toList()))
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    BoxType type = BoxType.valueOf(args.getString(ARG_1).toUpperCase());
                    DPCBFunction.changeBoxType(p, name, type);
                    return true;
                });
        builder.beginSubCommand("randomType", "/dpcb randomType <name> <randomType> - Set random type for a specific Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.STRING, Arrays.stream(RandomType.values()).map(Enum::name).collect(Collectors.toList()))
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    RandomType randomType = RandomType.valueOf(args.getString(ARG_1).toUpperCase());
                    DPCBFunction.setRandomType(p, name, randomType);
                    return true;
                });

        builder.beginSubCommand("weight", "/dpcb weight <name> - Open weight editor for a specific Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.openWeightEditor(p, name);
                    return true;
                });

        builder.beginSubCommand("rewardAmount", "/dpcb rewardAmount <name> <amount> - Set reward amount for a specific Consume Box")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.INTEGER)
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    int amount = args.getInteger(ARG_1);
                    DPCBFunction.setRewardAmount(p, name, amount);
                    return true;
                });

        builder.beginSubCommand("giveCoupon", "/dpcb giveCoupon <name> <player> <amount> - Give coupon item of a specific Consume Box to a player")
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.OFFLINE_PLAYER)
                .withArgument(ARG_2, ArgumentType.INTEGER)
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    OfflinePlayer target = args.getOfflinePlayer(ARG_1);
                    int amount = args.getInteger(ARG_2);
                    DPCBFunction.giveCoupon(p, name, target, amount);
                    return true;
                });

        builder.beginSubCommand("list", "/dpcb list - List all Consume Boxes")
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    DPCBFunction.listBoxes(p);
                    return true;
                });

        builder.beginSubCommand("reload", "/dpcb reload - Reload ConsumeBox plugin")
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    plugin.reload();
                    return true;
                });

        builder.build("dpcb");
    }
}
