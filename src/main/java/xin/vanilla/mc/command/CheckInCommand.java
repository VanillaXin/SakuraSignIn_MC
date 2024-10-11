package xin.vanilla.mc.command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class CheckInCommand {

    /**
     * 注册命令到命令调度器
     * 此方法用于注册一个特定的命令"checkin"到命令调度器当玩家执行这个命令时，
     * 系统会尝试给玩家的一个空物品栏槽位中添加一个苹果如果玩家的背包已满，则会发送失败的消息
     *
     * @param dispatcher 命令调度器，用于管理服务器中的所有命令
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        // 注册"checkin"命令，当执行该命令时，会调用后面的lambda表达式
        dispatcher.register(Commands.literal("checkin")
                .executes(context -> {
                    // 获取执行命令的玩家实体
                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    // 尝试给玩家添加一个苹果到背包，如果成功则返回"签到成功"，否则返回"签到失败, 背包已满"
                    String result = player.addItem(Items.APPLE.getDefaultInstance()) ? "签到成功" : "签到失败, 背包已满";
                    // 给玩家发送签到结果消息
                    player.sendMessage(new StringTextComponent(result), player.getUUID());
                    // 命令执行成功，返回1
                    return 1;
                })
                // 子命令 /checkin give <item>
                .then(Commands.literal("give")
                        .then(Commands.argument("item", StringArgumentType.word())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                    String itemName = StringArgumentType.getString(context, "item");
                                    Item item = getItemFromString(itemName);
                                    if (item != null) {
                                        ItemStack stack = new ItemStack(item);
                                        player.addItem(stack);
                                        player.sendMessage(new StringTextComponent("你获得了 " + itemName), player.getUUID());
                                    } else {
                                        player.sendMessage(new StringTextComponent("物品 " + itemName + " 不存在"), player.getUUID());
                                    }
                                    return 1;
                                })
                        )
                )
                // 子命令 /checkin sethome <x> <y> <z>
                .then(Commands.literal("sethome")
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                                                    int x = IntegerArgumentType.getInteger(context, "x");
                                                    int y = IntegerArgumentType.getInteger(context, "y");
                                                    int z = IntegerArgumentType.getInteger(context, "z");
                                                    // 将玩家的家设置到指定坐标
                                                    BlockPos homePos = new BlockPos(x, y, z);
                                                    player.setRespawnPosition(World.OVERWORLD, homePos, 0, true, false);
                                                    player.sendMessage(new StringTextComponent("你的家已设置在 " + x + ", " + y + ", " + z), player.getUUID());
                                                    return 1;
                                                })
                                        )))
                )
        );
    }

    private static Item getItemFromString(String itemName) {
        switch (itemName.toLowerCase()) {
            case "apple":
                return Items.APPLE;
            case "diamond":
                return Items.DIAMOND;
            default:
                return null; // 未找到物品时返回null
        }
    }
}
