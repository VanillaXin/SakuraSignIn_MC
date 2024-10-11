package xin.vanilla.mc.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec.BooleanValue SHOW_FPS;
    public static final ForgeConfigSpec.DoubleValue FOV;

    static {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        // 定义客户端配置项
        CLIENT_BUILDER.comment("Client Settings").push("client");

        SHOW_FPS = CLIENT_BUILDER
                .comment("Show FPS on screen")
                .define("showFps", true);

        FOV = CLIENT_BUILDER
                .comment("Field of view (FOV)")
                .defineInRange("fov", 90.0, 30.0, 120.0);

        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
