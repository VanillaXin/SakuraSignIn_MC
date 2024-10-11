package xin.vanilla.mc.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class PlayerSignInDataCapability {
    // 定义 Capability 实例
    @CapabilityInject(IPlayerSignInData.class)
    public static Capability<IPlayerSignInData> PLAYER_DATA = null;

    // 注册方法，用于在模组初始化期间注册 Capability
    public static void register() {
        // 注册 Capability 时，绑定接口、存储以及默认实现类
        CapabilityManager.INSTANCE.register(IPlayerSignInData.class, new PlayerSignInDataStorage(), PlayerSignInData::new);
    }

    // 获取玩家签到数据的方法
    // 通过此方法可以从玩家实体中获取签到数据，如果玩家实体尚未拥有该 Capability，则会抛出异常
    // 参数: player - 玩家实体
    // 返回值: 玩家的签到数据
    // 异常: 如果玩家实体没有注册的 Capability，则抛出 IllegalArgumentException
    public static IPlayerSignInData getData(PlayerEntity player) {
        return player.getCapability(PLAYER_DATA).orElseThrow(() -> new IllegalArgumentException("Player data capability is missing"));
    }
}
