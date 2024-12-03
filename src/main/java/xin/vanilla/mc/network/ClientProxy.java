package xin.vanilla.mc.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.IPlayerSignInData;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;

public class ClientProxy {
    public static void handleSynPlayerData(PlayerDataSyncPacket packet) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            IPlayerSignInData clientData = PlayerSignInDataCapability.getData(player);
            PlayerSignInDataCapability.PLAYER_DATA.readNBT(clientData, null, packet.getData().serializeNBT());
            SakuraSignIn.setEnabled(true);
        }
    }

    public static void handleAdvancement(AdvancementPacket packet) {
        SakuraSignIn.setAdvancementData(packet.getAdvancements());
    }
}
