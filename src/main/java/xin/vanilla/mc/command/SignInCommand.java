package xin.vanilla.mc.command;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
import xin.vanilla.mc.config.KeyValue;
import xin.vanilla.mc.config.ServerConfig;
import xin.vanilla.mc.enums.ESignInType;
import xin.vanilla.mc.enums.ETimeCoolingMethod;
import xin.vanilla.mc.network.SignInPacket;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.util.DateUtils;
import xin.vanilla.mc.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static xin.vanilla.mc.util.I18nUtils.getI18nKey;

public class SignInCommand {

    public static int HELP_INFO_NUM_PER_PAGE = 5;

    public static final List<KeyValue<String, String>> HELP_MESSAGE = new ArrayList<>() {{
        add(new KeyValue<>("/va help[ <page>]", "va_help"));                                                 // 获取帮助信息
        add(new KeyValue<>("/sign[ <year> <month> <day>]", "sign"));                                         // 签到简洁版本
        add(new KeyValue<>("/reward[ <year> <month> <day>]", "reward"));                                     // 领取今天的奖励简洁版本
        add(new KeyValue<>("/signex[ <year> <month> <day>]", "signex"));                                     // 签到并领取奖励简洁版本
        add(new KeyValue<>("/va sign <year> <month> <day>", "va_sign"));                                     // 签到/补签指定日期
        add(new KeyValue<>("/va reward[ <year> <month> <day>]", "va_reward"));                               // 领取指定日期奖励
        add(new KeyValue<>("/va signex[ <year> <month> <day>]", "va_signex"));                               // 签到/补签并领取指定日期奖励
        add(new KeyValue<>("/va card give <num>[ <player>]", "va_card_give"));                               // 给予玩家补签卡
        add(new KeyValue<>("/va card set <num>[ <player>]", "va_card_set"));                                 // 设置玩家补签卡
        add(new KeyValue<>("/va card get <player>", "va_card_get"));                                         // 获取玩家补签卡
        add(new KeyValue<>("/va config get", "va_config_get"));                                              // 获取服务器配置项信息
        add(new KeyValue<>("/va config set date <year> <month> <day> <hour> <minute> <second>", "va_config_set_date"));    // 设置服务器时间
    }};

    /*
        1：绕过服务器原版的出生点保护系统，可以破坏出生点地形。
        2：使用原版单机一切作弊指令（除了/publish，因为其只能在单机使用，/debug也不能使用）。
        3：可以使用大多数多人游戏指令，例如/op，/ban（/debug属于3级OP使用的指令）。
        4：使用所有命令，可以使用/stop关闭服务器。
    */

    /**
     * 注册命令到命令调度器
     *
     * @param dispatcher 命令调度器，用于管理服务器中的所有命令
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // 提供日期建议的 SuggestionProvider
        SuggestionProvider<CommandSourceStack> dateSuggestions = (context, builder) -> {
            LocalDateTime localDateTime = DateUtils.getLocalDateTime(DateUtils.getServerDate());
            builder.suggest(localDateTime.getYear() + " " + localDateTime.getMonthValue() + " " + localDateTime.getDayOfMonth());
            builder.suggest("~ ~ ~");
            builder.suggest("~ ~ ~-1");
            return builder.buildFuture();
        };
        SuggestionProvider<CommandSourceStack> datetimeSuggestions = (context, builder) -> {
            LocalDateTime localDateTime = DateUtils.getLocalDateTime(DateUtils.getServerDate());
            builder.suggest(localDateTime.getYear() + " " + localDateTime.getMonthValue() + " " + localDateTime.getDayOfMonth()
                    + " " + localDateTime.getHour() + " " + localDateTime.getMinute() + " " + localDateTime.getSecond());
            builder.suggest("~ ~ ~ ~ ~ ~");
            builder.suggest("~ ~ ~ ~ ~ ~-1");
            return builder.buildFuture();
        };

        Command<CommandSourceStack> signInCommand = context -> {
            Date signInTime;
            ESignInType signInType;
            try {
                long date = getRelativeLong(context, "date");
                signInTime = DateUtils.getDate(date);
                signInType = ESignInType.RE_SIGN_IN;
            } catch (IllegalArgumentException ignored) {
                signInTime = DateUtils.getServerDate();
                signInType = ESignInType.SIGN_IN;
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
            RewardManager.signIn(player, new SignInPacket(signInTime, signInData.isAutoRewarded(), signInType));
            return 1;
        };
        Command<CommandSourceStack> rewardCommand = context -> {
            Date rewardTime;
            try {
                long date = getRelativeLong(context, "date");
                rewardTime = DateUtils.getDate(date);
            } catch (IllegalArgumentException ignored) {
                rewardTime = DateUtils.getServerDate();
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            RewardManager.signIn(player, new SignInPacket(rewardTime, true, ESignInType.REWARD));
            return 1;
        };
        Command<CommandSourceStack> signAndRewardCommand = context -> {
            Date signInTime;
            ESignInType signInType;
            try {
                long date = getRelativeLong(context, "date");
                signInTime = DateUtils.getDate(date);
                signInType = ESignInType.RE_SIGN_IN;
            } catch (IllegalArgumentException ignored) {
                signInTime = DateUtils.getServerDate();
                signInType = ESignInType.SIGN_IN;
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            RewardManager.signIn(player, new SignInPacket(signInTime, true, signInType));
            return 1;
        };
        Command<CommandSourceStack> helpCommand = context -> {
            int page = 1;
            try {
                page = IntegerArgumentType.getInteger(context, "page");
            } catch (IllegalArgumentException ignored) {
            }
            int pages = (int) Math.ceil((double) HELP_MESSAGE.size() / HELP_INFO_NUM_PER_PAGE);
            if (page < 1 || page > pages) {
                throw new IllegalArgumentException("page must be between 1 and " + (HELP_MESSAGE.size() / HELP_INFO_NUM_PER_PAGE));
            }
            MutableComponent helpInfo = Component.literal("-----==== Sakura Sign In Help (" + page + "/" + pages + ") ====-----\n");
            for (int i = 0; (page - 1) * HELP_INFO_NUM_PER_PAGE + i < HELP_MESSAGE.size() && i < HELP_INFO_NUM_PER_PAGE; i++) {
                KeyValue<String, String> keyValue = HELP_MESSAGE.get((page - 1) * HELP_INFO_NUM_PER_PAGE + i);
                MutableComponent commandTips = Component.translatable("command." + SakuraSignIn.MODID + "." + keyValue.getValue());
                commandTips.withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
                helpInfo.append(keyValue.getKey())
                        .append(Component.literal(" -> ").withStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)))
                        .append(commandTips);
                if (i != HELP_MESSAGE.size() - 1) {
                    helpInfo.append("\n");
                }
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.sendSystemMessage(helpInfo);
            return 1;
        };

        // 签到 /sign
        dispatcher.register(Commands.literal("sign").executes(signInCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("date", StringArgumentType.greedyString())
                        .suggests(dateSuggestions)
                        .executes(signInCommand)
                )
        );

        // 领取奖励 /reward
        dispatcher.register(Commands.literal("reward").executes(rewardCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("date", StringArgumentType.greedyString())
                        .suggests(dateSuggestions)
                        .executes(rewardCommand)
                )
        );

        // 签到并领取奖励 /signex
        dispatcher.register(Commands.literal("signex").executes(signAndRewardCommand)
                // 带有日期参数 -> 补签
                .then(Commands.argument("date", StringArgumentType.greedyString())
                        .suggests(dateSuggestions)
                        .executes(signAndRewardCommand)
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
                        .then(Commands.argument("date", StringArgumentType.greedyString())
                                .suggests(dateSuggestions)
                                .executes(signInCommand)
                        )
                )
                // 奖励 /va reward
                .then(Commands.literal("reward").executes(rewardCommand)
                        // 补签 /va sign <year> <month> <day>
                        .then(Commands.argument("date", StringArgumentType.greedyString())
                                .suggests(dateSuggestions)
                                .executes(rewardCommand)
                        )
                )
                // 签到并领取奖励 /va signex
                .then(Commands.literal("signex").executes(signAndRewardCommand)
                        // 补签 /va signex <year> <month> <day>
                        .then(Commands.argument("date", StringArgumentType.greedyString())
                                .suggests(dateSuggestions)
                                .executes(signAndRewardCommand)
                        )
                )
                // 获取补签卡数量 /va card
                .then(Commands.literal("card")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            if (!ServerConfig.SIGN_IN_CARD.get()) {
                                player.sendSystemMessage(Component.translatable(getI18nKey("服务器未开启补签功能哦。")));
                            } else {
                                player.sendSystemMessage(Component.translatable(getI18nKey("当前拥有%d张补签卡"), PlayerSignInDataCapability.getData(player).getSignInCard()));
                            }
                            return 1;
                        })
                        // 增加/减少补签卡 /va card give <num> [<player>]
                        .then(Commands.literal("give")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("num", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int num = IntegerArgumentType.getInteger(context, "num");
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                            signInData.setSignInCard(signInData.getSignInCard() + num);
                                            player.sendSystemMessage(Component.translatable(getI18nKey("给予%d张补签卡"), num));
                                            PlayerSignInDataCapability.syncPlayerData(player);
                                            return 1;
                                        })
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int num = IntegerArgumentType.getInteger(context, "num");
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                    signInData.setSignInCard(signInData.getSignInCard() + num);
                                                    player.sendSystemMessage(Component.translatable(getI18nKey("获得%d张补签卡"), num));
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
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                            signInData.setSignInCard(num);
                                            player.sendSystemMessage(Component.translatable(getI18nKey("补签卡被设置为了%d张"), num));
                                            PlayerSignInDataCapability.syncPlayerData(player);
                                            return 1;
                                        })
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> {
                                                    int num = IntegerArgumentType.getInteger(context, "num");
                                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                                    IPlayerSignInData signInData = PlayerSignInDataCapability.getData(player);
                                                    signInData.setSignInCard(num);
                                                    player.sendSystemMessage(Component.translatable(getI18nKey("补签卡被设置为了%d张"), num));
                                                    PlayerSignInDataCapability.syncPlayerData(player);
                                                    return 1;
                                                })
                                        )
                                )

                        )
                        // 获取补签卡数量 /va card get [<player>]
                        .then(Commands.literal("get")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> {
                                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                                            IPlayerSignInData signInData = PlayerSignInDataCapability.getData(target);
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            player.sendSystemMessage(Component.translatable(getI18nKey("玩家[%s]拥有%d张补签卡"), target.getDisplayName().getString(), signInData.getSignInCard()));
                                            PlayerSignInDataCapability.syncPlayerData(target);
                                            return 1;
                                        })
                                )

                        )
                )
                // 获取服务器配置 /va config get
                .then(Commands.literal("config")
                        .then(Commands.literal("get")
                                .then(Commands.literal("autoSignIn")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            player.sendSystemMessage(Component.translatable(getI18nKey(String.format("服务器%s自动签到", ServerConfig.AUTO_SIGN_IN.get() ? "已启用" : "未启用"))));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("timeCoolingMethod")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            ETimeCoolingMethod coolingMethod = ServerConfig.TIME_COOLING_METHOD.get();
                                            player.sendSystemMessage(Component.translatable(getI18nKey("服务器签到时间冷却方式为: %s"), coolingMethod.getName()));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("timeCoolingTime")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            Double time = ServerConfig.TIME_COOLING_TIME.get();
                                            player.sendSystemMessage(Component.translatable(getI18nKey("服务器签到冷却刷新时间为: %05.2f"), time));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("timeCoolingInterval")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            Double time = ServerConfig.TIME_COOLING_INTERVAL.get();
                                            player.sendSystemMessage(Component.translatable(getI18nKey("服务器签到冷却刷新间隔为: %05.2f"), time));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("signInCard")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            player.sendSystemMessage(Component.translatable(getI18nKey(String.format("服务器%s补签卡", ServerConfig.SIGN_IN_CARD.get() ? "已启用" : "未启用"))));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("reSignInDays")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            int time = ServerConfig.RE_SIGN_IN_DAYS.get();
                                            player.sendSystemMessage(Component.translatable(getI18nKey("服务器最大补签天数为: %d"), time));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("signInCardOnlyBaseReward")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            player.sendSystemMessage(Component.translatable(getI18nKey(String.format("服务器%s补签仅获得基础奖励", ServerConfig.SIGN_IN_CARD_ONLY_BASE_REWARD.get() ? "已启用" : "未启用"))));
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("date")
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            player.sendSystemMessage(Component.translatable(getI18nKey("服务器当前时间: %s"), DateUtils.toDateTimeString(DateUtils.getServerDate())));
                                            return 1;
                                        })
                                )
                        )
                        // 设置服务器时间 /va config set date <year> <month> <day> <hour> <minute> <second>
                        .then(Commands.literal("set")
                                        .requires(source -> source.hasPermission(3))
                                        .then(Commands.literal("date")
                                                .then(Commands.argument("datetime", StringArgumentType.greedyString())
                                                        .suggests(datetimeSuggestions)
                                                        .executes(context -> {
                                                            long datetime = getRelativeLong(context, "datetime");
                                                            Date date = DateUtils.getDate(datetime);
                                                            ServerConfig.SERVER_TIME.set(DateUtils.toDateTimeString(new Date()));
                                                            ServerConfig.ACTUAL_TIME.set(DateUtils.toDateTimeString(date));
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            player.sendSystemMessage(Component.translatable(getI18nKey("服务器时间已设置为: %s"), DateUtils.toDateTimeString(date)));
                                                            return 1;
                                                        })
                                                )
                                        )
                                // TODO 继续注册管理员指令实现修改所有服务器配置
                        )
                )
        );
    }

    private static long getRelativeLong(CommandContext<CommandSourceStack> context, @NonNull String name) throws CommandSyntaxException {
        String string = StringArgumentType.getString(context, name);
        if (StringUtils.isNullOrEmptyEx(string)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(string);
        }
        String[] split = string.split(" ");
        String[] units;
        if ((name.equalsIgnoreCase("date") && split.length == 3)) {
            units = new String[]{"year", "month", "day"};
        } else if ((name.equalsIgnoreCase("time") && split.length == 3)) {
            units = new String[]{"hour", "minute", "second"};
        } else if (name.equalsIgnoreCase("datetime") && split.length == 6) {
            units = new String[]{"year", "month", "day", "hour", "minute", "second"};
        } else {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(string);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            int input;
            int offset;
            String inputString = split[i];
            if (inputString.startsWith("_") || inputString.startsWith("~")) {
                offset = switch (units[i]) {
                    case "year" -> DateUtils.getLocalDateTime(DateUtils.getServerDate()).getYear();
                    case "month" -> DateUtils.getLocalDateTime(DateUtils.getServerDate()).getMonthValue();
                    case "day" -> DateUtils.getLocalDateTime(DateUtils.getServerDate()).getDayOfMonth();
                    case "hour" -> DateUtils.getLocalDateTime(DateUtils.getServerDate()).getHour();
                    case "minute" -> DateUtils.getLocalDateTime(DateUtils.getServerDate()).getMinute();
                    case "second" -> DateUtils.getLocalDateTime(DateUtils.getServerDate()).getSecond();
                    default -> 0;
                };
                if (inputString.equalsIgnoreCase("_") || inputString.equalsIgnoreCase("~")) {
                    inputString = "0";
                } else {
                    inputString = inputString.substring(1);
                }
            } else {
                offset = 0;
            }
            try {
                input = Integer.parseInt(inputString);
            } catch (NumberFormatException e) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(inputString);
            }
            if (units[i].equalsIgnoreCase("year")) {
                result.append(String.format("%04d", offset + input));
            } else {
                result.append(String.format("%02d", offset + input));
            }
        }
        return Long.parseLong(result.toString());
    }
}
