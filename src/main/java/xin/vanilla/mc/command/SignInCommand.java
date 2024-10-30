package xin.vanilla.mc.command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.enums.ESignInType;
import xin.vanilla.mc.network.SignInPacket;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.util.DateUtils;

import java.util.Date;

public class SignInCommand {

    /**
     * 注册命令到命令调度器
     * 此方法用于注册一个特定的命令"checkin"到命令调度器当玩家执行这个命令时，
     * 系统会尝试给玩家的一个空物品栏槽位中添加一个苹果如果玩家的背包已满，则会发送失败的消息
     *
     * @param dispatcher 命令调度器，用于管理服务器中的所有命令
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        /*
            0 - 所有玩家。
            1 - 拥有操作员权限的玩家（/op 命令）。
            2 - 能够使用 /give 和 /clear 等管理指令的操作员。
            3 - 能够使用 /ban 和 /kick 等服务器管理指令的操作员。
            4 - 服务器所有者或控制台。
         */
        Command<CommandSource> signInCommand = context -> {
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
            RewardManager.signIn(player, new SignInPacket(DateUtils.getServerDate(), signInData.isAutoRewarded(), ESignInType.SIGN_IN));
            return 1;
        };
        Command<CommandSource> reSignInCommand = context -> {
            int year = IntegerArgumentType.getInteger(context, "year");
            int month = IntegerArgumentType.getInteger(context, "month");
            int day = IntegerArgumentType.getInteger(context, "day");
            Date signInTime = DateUtils.getDate(year, month, day);
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            if (!ServerConfig.SIGN_IN_CARD.get()) {
                player.sendMessage(new StringTextComponent("服务器未开启补签功能，补签失败"), player.getUUID());
            } else {
                IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                RewardManager.signIn(player, new SignInPacket(signInTime, signInData.isAutoRewarded(), ESignInType.RE_SIGN_IN));
            }
            return 1;
        };
        Command<CommandSource> helpCommand = context -> {
            // TODO 发送帮助信息
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            player.sendMessage(new StringTextComponent("help"), player.getUUID());
            // 命令执行成功，返回1
            return 1;
        };

        // 注册无前缀的快捷指令
        dispatcher.register(Commands.literal("sign").executes(signInCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("year", IntegerArgumentType.integer(1, 9999))
                        .then(Commands.argument("month", IntegerArgumentType.integer(1, 12))
                                .then(Commands.argument("day", IntegerArgumentType.integer(1, 31))
                                        .executes(reSignInCommand)
                                )
                        )
                )
        );
        // 注册有前缀的指令
        dispatcher.register(Commands.literal("va")
                        .executes(helpCommand)
                        // 签到 /va sign
                        .then(Commands.literal("sign").executes(signInCommand)
                                // 补签 /va sign <year> <month> <day>
                                .then(Commands.argument("year", IntegerArgumentType.integer(1, 9999))
                                        .then(Commands.argument("month", IntegerArgumentType.integer(1, 12))
                                                .then(Commands.argument("day", IntegerArgumentType.integer(1, 31))
                                                        .executes(reSignInCommand)
                                                )
                                        )
                                )
                        )
                        // 获取服务器时间 /va date get
                        .then(Commands.literal("date")
                                .then(Commands.literal("get")
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                            player.sendMessage(new StringTextComponent(String.format("服务器当前时间: %s", DateUtils.toDateTimeString(DateUtils.getServerDate()))), player.getUUID());
                                            return 1;
                                        })
                                )
                                // ↓ 不好评价的代码
                                // 设置服务器时间 /va date set <year> <month> <day> <hour> <minute> <second>
                                .then(Commands.literal("set")
                                        .requires(source -> source.hasPermission(3))
                                        .then(Commands.argument("year", IntegerArgumentType.integer(1, 9999))
                                                .then(Commands.argument("month", IntegerArgumentType.integer(1, 12))
                                                        .then(Commands.argument("day", IntegerArgumentType.integer(1, 31))
                                                                .then(Commands.argument("hour", IntegerArgumentType.integer(0, 23))
                                                                        .then(Commands.argument("minute", IntegerArgumentType.integer(0, 59))
                                                                                .then(Commands.argument("second", IntegerArgumentType.integer(0, 59))
                                                                                        .executes(context -> {
                                                                                            int year = IntegerArgumentType.getInteger(context, "year");
                                                                                            int month = IntegerArgumentType.getInteger(context, "month");
                                                                                            int day = IntegerArgumentType.getInteger(context, "day");
                                                                                            int hour = IntegerArgumentType.getInteger(context, "hour");
                                                                                            int minute = IntegerArgumentType.getInteger(context, "minute");
                                                                                            int second = IntegerArgumentType.getInteger(context, "second");
                                                                                            Date date = DateUtils.getDate(year, month, day, hour, minute, second);
                                                                                            ServerConfig.SERVER_TIME.set(DateUtils.toDateTimeString(new Date()));
                                                                                            ServerConfig.ACTUAL_TIME.set(DateUtils.toDateTimeString(date));
                                                                                            ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                                                                            player.sendMessage(new StringTextComponent(String.format("服务器时间已设置为: %s", DateUtils.toDateTimeString(date))), player.getUUID());
                                                                                            return 1;
                                                                                        })
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        // 获取补签卡数量 /va card
                        .then(Commands.literal("card")
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                    if (!ServerConfig.SIGN_IN_CARD.get()) {
                                        player.sendMessage(new StringTextComponent("服务器未开启补签功能"), player.getUUID());
                                    } else {
                                        player.sendMessage(new StringTextComponent(String.format("当前拥有%d张补签卡", PlayerSignInDataCapability.getData(player).getSignInCard())), player.getUUID());
                                    }
                                    return 1;
                                })
                                // 增加/减少补签卡 /va card add <num> [<player>]
                                .then(Commands.literal("add")
                                        .requires(source -> source.hasPermission(2))
                                        .then(Commands.argument("num", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    int num = IntegerArgumentType.getInteger(context, "num");
                                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                                    IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                    signInData.setSignInCard(signInData.getSignInCard() + num);
                                                    return 1;
                                                })
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> {
                                                            int num = IntegerArgumentType.getInteger(context, "num");
                                                            ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                            signInData.setSignInCard(signInData.getSignInCard() + num);
                                                            return 1;
                                                        })
                                                )

                                        )
                                )
                                // 设置补签卡数量 /va card set <num> [<player>]
                                .then(Commands.literal("set")
                                        .requires(source -> source.hasPermission(2))
                                        .then(Commands.argument("num", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    int num = IntegerArgumentType.getInteger(context, "num");
                                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                                    IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                    signInData.setSignInCard(num);
                                                    return 1;
                                                })
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> {
                                                            int num = IntegerArgumentType.getInteger(context, "num");
                                                            ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                            signInData.setSignInCard(num);
                                                            return 1;
                                                        })
                                                )
                                        )

                                )
                        )
                // TODO @macro 继续注册管理员指令实现能查询与修改所有服务器配置
        );
    }
}
