package xin.vanilla.mc.rewards.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.NonNull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.rewards.RewardParser;

public class EffectRewardParser implements RewardParser<MobEffectInstance> {

    @Override
    public @NonNull MobEffectInstance deserialize(JsonObject json) {
        MobEffectInstance mobEffectInstance;
        try {
            String effectId = json.get("effect").getAsString();
            int duration = json.get("duration").getAsInt();
            int amplifier = json.get("amplifier").getAsInt();

            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectId));
            if (effect == null) {
                throw new JsonParseException("Unknown potion effect ID: " + effectId);
            }
            mobEffectInstance = new MobEffectInstance(effect, duration, amplifier);
        } catch (Exception e) {
            LOGGER.error("Failed to parse effect reward", e);
            mobEffectInstance = new MobEffectInstance(MobEffects.LUCK, 0, 0);
        }
        return mobEffectInstance;
    }

    @Override
    public JsonObject serialize(MobEffectInstance reward) {
        JsonObject json = new JsonObject();
        json.addProperty("effect", reward.getEffect().getRegistryName().toString());
        json.addProperty("duration", reward.getDuration());
        json.addProperty("amplifier", reward.getAmplifier());
        return json;
    }
}
