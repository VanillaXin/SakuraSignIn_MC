package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.rewards.RewardParser;

public class EffectRewardParser implements RewardParser<EffectInstance> {

    @Override
    public EffectInstance deserialize(JSONObject json) {
        String effectId = json.getString("effect");
        int duration = json.getIntValue("duration", 0);
        int amplifier = json.getIntValue("amplifier", 0);

        Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectId));
        if (effect == null) {
            throw new JSONException("Unknown potion effect ID: " + effectId);
        }
        return new EffectInstance(effect, duration, amplifier);
    }

    @Override
    public JSONObject serialize(EffectInstance reward) {
        JSONObject json = new JSONObject();
        json.put("effect", reward.getEffect().getRegistryName().toString());
        json.put("duration", reward.getDuration());
        json.put("amplifier", reward.getAmplifier());
        return json;
    }
}
