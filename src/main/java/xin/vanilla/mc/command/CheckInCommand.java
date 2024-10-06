package xin.vanilla.mc.command;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;

public class CheckInCommand {

    /**
     * 注册命令到命令调度器
     * 此方法用于注册一个特定的命令"va checkin"到命令调度器当玩家执行这个命令时，
     * 系统会尝试给玩家的一个空物品栏槽位中添加一个苹果如果玩家的背包已满，则会发送失败的消息
     *
     * @param dispatcher 命令调度器，用于管理服务器中的所有命令
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        // 注册"va checkin"命令，当执行该命令时，会调用后面的lambda表达式
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
        );
    }
}
