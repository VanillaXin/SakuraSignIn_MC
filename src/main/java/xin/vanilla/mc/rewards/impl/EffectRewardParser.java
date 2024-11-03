package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.rewards.RewardParser;

public class EffectRewardParser implements RewardParser<EffectInstance> {

    @Override
    public EffectInstance deserialize(JsonObject json) {
        String effectId = json.get("effect").getAsString();
        int duration = json.get("duration").getAsInt();
        int amplifier = json.get("amplifier").getAsInt();

        Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectId));
        if (effect == null) {
            throw new JsonParseException("Unknown potion effect ID: " + effectId);
        }
        return new EffectInstance(effect, duration, amplifier);
    }

    @Override
    public JsonObject serialize(EffectInstance reward) {
        JsonObject json = new JsonObject();
        json.addProperty("effect", reward.getEffect().getRegistryName().toString());
        json.addProperty("duration", reward.getDuration());
        json.addProperty("amplifier", reward.getAmplifier());
        return json;
    }
}
