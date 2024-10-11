package xin.vanilla.mc.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec SERVER_CONFIG;
    public static final ForgeConfigSpec.IntValue MAX_PLAYERS;
    public static final ForgeConfigSpec.BooleanValue ALLOW_FLYING;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        // 定义服务器配置项
        SERVER_BUILDER.comment("Server Settings").push("server");

        MAX_PLAYERS = SERVER_BUILDER
                .comment("Max number of players allowed on the server")
                .defineInRange("maxPlayers", 20, 1, 100);

        ALLOW_FLYING = SERVER_BUILDER
                .comment("Allow players to fly?")
                .define("allowFlying", false);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
