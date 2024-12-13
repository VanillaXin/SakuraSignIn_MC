package xin.vanilla.mc.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;

public class ClientProxy {
    public static void handleSynPlayerData(PlayerDataSyncPacket packet) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            PlayerSignInDataCapability.setData(player, packet.getData());
            SakuraSignIn.setEnabled(true);
        }
    }

    public static void handleAdvancement(AdvancementPacket packet) {
        SakuraSignIn.setAdvancementData(packet.getAdvancements());
    }
}
