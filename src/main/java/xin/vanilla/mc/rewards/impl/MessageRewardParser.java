package xin.vanilla.mc.rewards.impl;

import com.alibaba.fastjson2.JSONObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import xin.vanilla.mc.rewards.RewardParser;

public class MessageRewardParser implements RewardParser<StringTextComponent> {

    @Override
    public StringTextComponent deserialize(JSONObject json) {
        StringTextComponent message = new StringTextComponent(json.getString("contents"));
        JSONObject styleJson = json.getJSONObject("style");
        Style style = Style.EMPTY;
        if (styleJson.containsKey("color"))
            style.withColor(Color.parseColor(styleJson.getString("color")));
        style.withBold(styleJson.getBoolean("bold"));
        style.withItalic(styleJson.getBoolean("italic"));
        style.withUnderlined(styleJson.getBoolean("underlined"));
        style.setStrikethrough(styleJson.getBoolean("strikethrough"));
        style.setObfuscated(styleJson.getBoolean("obfuscated"));
        style.withFont(new ResourceLocation(styleJson.getString("font")));
        message.setStyle(style);
        return message;
    }

    @Override
    public JSONObject serialize(StringTextComponent reward) {
        JSONObject result = new JSONObject();
        JSONObject styleJson = new JSONObject();
        Style style = reward.getStyle();
        if (style.getColor() != null) {
            styleJson.put("color", style.getColor().serialize());
        }
        styleJson.put("bold", style.isBold());
        styleJson.put("italic", style.isItalic());
        styleJson.put("underlined", style.isUnderlined());
        styleJson.put("strikethrough", style.isStrikethrough());
        styleJson.put("obfuscated", style.isObfuscated());
        styleJson.put("font", style.getFont().toString());
        result.put("contents", reward.getContents());
        result.put("style", styleJson);
        return result;
    }
}
