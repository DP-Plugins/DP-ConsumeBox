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

        builder.beginSubCommand("create", plugin.getLang().get("command_create_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING)
                .withArgument(ARG_1, ArgumentType.STRING, Arrays.stream(BoxType.values()).map(Enum::name).collect(Collectors.toList()))
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    BoxType type = BoxType.valueOf(args.getString(ARG_1).toUpperCase());
                    DPCBFunction.createBox(p, name, type);
                    return true;
                });
        builder.beginSubCommand("items", plugin.getLang().get("command_items_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.editItems(p, name);
                    return true;
                });

        builder.beginSubCommand("maxpage", plugin.getLang().get("command_max_page_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.INTEGER)
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    int page = args.getInteger(ARG_1);
                    DPCBFunction.setMaxPage(p, name, page);
                    return true;
                });

        builder.beginSubCommand("setSelectedItem", plugin.getLang().get("command_set_selected_item_description"))
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    DPCBFunction.openSelectedItemEditor(p);
                    return true;
                });

        builder.beginSubCommand("setDefaultCoupon", plugin.getLang().get("command_set_default_coupon_description"))
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    DPCBFunction.openDefaultCouponEditor(p);
                    return true;
                });

        builder.beginSubCommand("setCoupon", plugin.getLang().get("command_set_coupon_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.openCouponEditor(p, name);
                    return true;
                });

        builder.beginSubCommand("remove", plugin.getLang().get("command_remove_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.removeBox(p, name);
                    return true;
                });

        builder.beginSubCommand("type", plugin.getLang().get("command_type_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.STRING, Arrays.stream(BoxType.values()).map(Enum::name).collect(Collectors.toList()))
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    BoxType type = BoxType.valueOf(args.getString(ARG_1).toUpperCase());
                    DPCBFunction.changeBoxType(p, name, type);
                    return true;
                });
        builder.beginSubCommand("randomType", plugin.getLang().get("command_random_type_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.STRING, Arrays.stream(RandomType.values()).map(Enum::name).collect(Collectors.toList()))
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    RandomType randomType = RandomType.valueOf(args.getString(ARG_1).toUpperCase());
                    DPCBFunction.setRandomType(p, name, randomType);
                    return true;
                });

        builder.beginSubCommand("weight", plugin.getLang().get("command_weight_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    DPCBFunction.openWeightEditor(p, name);
                    return true;
                });

        builder.beginSubCommand("rewardAmount", plugin.getLang().get("command_reward_amount_description"))
                .withPermission("dpcb.admin")
                .withArgument(ARG_0, ArgumentType.STRING, ConsumeBox.data.keySet())
                .withArgument(ARG_1, ArgumentType.INTEGER)
                .executesPlayer((p, args) -> {
                    String name = args.getString(ARG_0);
                    int amount = args.getInteger(ARG_1);
                    DPCBFunction.setRewardAmount(p, name, amount);
                    return true;
                });

        builder.beginSubCommand("giveCoupon", plugin.getLang().get("command_give_coupon_description"))
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

        builder.beginSubCommand("list", plugin.getLang().get("command_list_description"))
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    DPCBFunction.listBoxes(p);
                    return true;
                });

        builder.beginSubCommand("reload", plugin.getLang().get("command_reload_description"))
                .withPermission("dpcb.admin")
                .executesPlayer((p, args) -> {
                    plugin.reload();
                    return true;
                });

        builder.build("dpcb");
    }
}
