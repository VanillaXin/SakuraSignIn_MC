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
            Date signInTime;
            ESignInType signInType;
            try {
                int year = RelativeDateArgument.getInteger(context, "year");
                int month = RelativeDateArgument.getInteger(context, "month");
                int day = RelativeDateArgument.getInteger(context, "day");
                signInTime = DateUtils.getDate(year, month, day);
                signInType = ESignInType.RE_SIGN_IN;
            } catch (IllegalArgumentException ignored) {
                signInTime = DateUtils.getServerDate();
                signInType = ESignInType.SIGN_IN;
            }
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
            RewardManager.signIn(player, new SignInPacket(signInTime, signInData.isAutoRewarded(), signInType));
            return 1;
        };
        Command<CommandSource> rewardCommand = context -> {
            Date signInTime;
            try {
                int year = RelativeDateArgument.getInteger(context, "year");
                int month = RelativeDateArgument.getInteger(context, "month");
                int day = RelativeDateArgument.getInteger(context, "day");
                signInTime = DateUtils.getDate(year, month, day);
            } catch (IllegalArgumentException ignored) {
                signInTime = DateUtils.getServerDate();
            }
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            RewardManager.signIn(player, new SignInPacket(signInTime, true, ESignInType.REWARD));
            return 1;
        };
        Command<CommandSource> signAndRewardCommand = context -> {
            Date signInTime;
            ESignInType signInType;
            try {
                int year = RelativeDateArgument.getInteger(context, "year");
                int month = RelativeDateArgument.getInteger(context, "month");
                int day = RelativeDateArgument.getInteger(context, "day");
                signInTime = DateUtils.getDate(year, month, day);
                signInType = ESignInType.RE_SIGN_IN;
            } catch (IllegalArgumentException ignored) {
                signInTime = DateUtils.getServerDate();
                signInType = ESignInType.SIGN_IN;
            }
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            RewardManager.signIn(player, new SignInPacket(signInTime, true, signInType));
            return 1;
        };
        Command<CommandSource> helpCommand = context -> {
            // TODO 发送帮助信息
            int page = 1;
            try {
                page = IntegerArgumentType.getInteger(context, "page");
            } catch (IllegalArgumentException ignored) {
            }
            ServerPlayerEntity player = context.getSource().getPlayerOrException();
            player.sendMessage(new StringTextComponent("help" + page), player.getUUID());
            // 命令执行成功，返回1
            return 1;
        };

        // 签到 /sign
        dispatcher.register(Commands.literal("sign").executes(signInCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("year", RelativeDateArgument.year(1, 9999))
                        .then(Commands.argument("month", RelativeDateArgument.month(1, 12))
                                .then(Commands.argument("day", RelativeDateArgument.date(1, 31))
                                        .executes(signInCommand)
                                )
                        )
                )
        );

        // 领取奖励 /reward
        dispatcher.register(Commands.literal("reward").executes(rewardCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("year", RelativeDateArgument.year(1, 9999))
                        .then(Commands.argument("month", RelativeDateArgument.month(1, 12))
                                .then(Commands.argument("day", RelativeDateArgument.date(1, 31))
                                        .executes(rewardCommand)
                                )
                        )
                )
        );

        // 签到并领取奖励 /signex
        dispatcher.register(Commands.literal("signex").executes(signAndRewardCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("year", RelativeDateArgument.year(-9, 9999))
                        .then(Commands.argument("month", RelativeDateArgument.month(-12, 12))
                                .then(Commands.argument("day", RelativeDateArgument.date(-31, 31))
                                        .executes(signAndRewardCommand)
                                )
                        )
                )
        );

        // 注册有前缀的指令
        dispatcher.register(Commands.literal("va")
                        .executes(helpCommand)
                        .then(Commands.literal("help")
                                .executes(helpCommand)
                                .then(Commands.argument("page", IntegerArgumentType.integer(1, 4))
                                        .executes(helpCommand)
                                )
                        )
                        // 签到 /va sign
                        .then(Commands.literal("sign").executes(signInCommand)
                                // 补签 /va sign <year> <month> <day>
                                .then(Commands.argument("year", RelativeDateArgument.year(-9, 9999))
                                        .then(Commands.argument("month", RelativeDateArgument.month(-12, 12))
                                                .then(Commands.argument("day", RelativeDateArgument.date(-31, 31))
                                                        .executes(signInCommand)
                                                )
                                        )
                                )
                        )
                        // 奖励 /va reward
                        .then(Commands.literal("reward").executes(rewardCommand)
                                // 补签 /va sign <year> <month> <day>
                                .then(Commands.argument("year", RelativeDateArgument.year(-9, 9999))
                                        .then(Commands.argument("month", RelativeDateArgument.month(-12, 12))
                                                .then(Commands.argument("day", RelativeDateArgument.date(-31, 31))
                                                        .executes(rewardCommand)
                                                )
                                        )
                                )
                        )
                        // 签到并领取奖励 /signex
                        .then(Commands.literal("signex").executes(signAndRewardCommand)
                                // 补签 /va signex <year> <month> <day>
                                .then(Commands.argument("year", RelativeDateArgument.year(-9, 9999))
                                        .then(Commands.argument("month", RelativeDateArgument.month(-12, 12))
                                                .then(Commands.argument("day", RelativeDateArgument.date(-31, 31))
                                                        .executes(signAndRewardCommand)
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
                                        .then(Commands.argument("year", RelativeDateArgument.year(-9, 9999))
                                                .then(Commands.argument("month", RelativeDateArgument.month(-12, 12))
                                                        .then(Commands.argument("day", RelativeDateArgument.date(-31, 31))
                                                                .then(Commands.argument("hour", RelativeDateArgument.hour(-23, 23))
                                                                        .then(Commands.argument("minute", RelativeDateArgument.minute(-59, 59))
                                                                                .then(Commands.argument("second", RelativeDateArgument.second(-59, 59))
                                                                                        .executes(context -> {
                                                                                            int year = RelativeDateArgument.getInteger(context, "year");
                                                                                            int month = RelativeDateArgument.getInteger(context, "month");
                                                                                            int day = RelativeDateArgument.getInteger(context, "day");
                                                                                            int hour = RelativeDateArgument.getInteger(context, "hour");
                                                                                            int minute = RelativeDateArgument.getInteger(context, "minute");
                                                                                            int second = RelativeDateArgument.getInteger(context, "second");
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
                                                    player.sendMessage(new StringTextComponent(String.format("已增加%d张补签卡", num)), player.getUUID());
                                                    PlayerSignInDataCapability.syncPlayerData(player);
                                                    return 1;
                                                })
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> {
                                                            int num = IntegerArgumentType.getInteger(context, "num");
                                                            ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                            signInData.setSignInCard(signInData.getSignInCard() + num);
                                                            player.sendMessage(new StringTextComponent(String.format("已增加%d张补签卡", num)), player.getUUID());
                                                            PlayerSignInDataCapability.syncPlayerData(player);
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
                                                    player.sendMessage(new StringTextComponent(String.format("补签卡被设置为了%d张", num)), player.getUUID());
                                                    PlayerSignInDataCapability.syncPlayerData(player);
                                                    return 1;
                                                })
                                                .then(Commands.argument("player", EntityArgument.player())
                                                        .executes(context -> {
                                                            int num = IntegerArgumentType.getInteger(context, "num");
                                                            ServerPlayerEntity player = EntityArgument.getPlayer(context, "player");
                                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                            signInData.setSignInCard(num);
                                                            player.sendMessage(new StringTextComponent(String.format("补签卡被设置为了%d张", num)), player.getUUID());
                                                            PlayerSignInDataCapability.syncPlayerData(player);
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
