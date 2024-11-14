package xin.vanilla.mc.util;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import xin.vanilla.mc.enums.ERewardType;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.screen.CalendarTextureCoordinate;
import xin.vanilla.mc.screen.TextureCoordinate;

import javax.annotation.Nullable;

/**
 * AbstractGui工具类
 */
public class AbstractGuiUtils {

    public final static int ITEM_ICON_SIZE = 16;

    public static void blit(GuiGraphics graphics, int x0, int y0, int z, int destWidth, int destHeight, TextureAtlasSprite sprite) {
        graphics.blit(x0, y0, z, destWidth, destHeight, sprite);
    }

    public static void blit(GuiGraphics graphics, ResourceLocation resourceLocation, int x0, int y0, int z, float u0, float v0, int width, int height, int textureHeight, int textureWidth) {
        graphics.blit(resourceLocation, x0, y0, z, u0, v0, width, height, textureHeight, textureWidth);
    }

    /**
     * 使用指定的纹理坐标和尺寸信息绘制一个矩形区域。
     *
     * @param graphics      用于变换绘制坐标系的矩阵堆栈。
     * @param x0            矩形的左上角x坐标。
     * @param y0            矩形的左上角y坐标。
     * @param destWidth     目标矩形的宽度，决定了图像在屏幕上的宽度。
     * @param destHeight    目标矩形的高度，决定了图像在屏幕上的高度。
     * @param u0            源图像上矩形左上角的u轴坐标。
     * @param v0            源图像上矩形左上角的v轴坐标。
     * @param srcWidth      源图像上矩形的宽度，用于确定从源图像上裁剪的部分。
     * @param srcHeight     源图像上矩形的高度，用于确定从源图像上裁剪的部分。
     * @param textureWidth  整个纹理的宽度，用于计算纹理坐标。
     * @param textureHeight 整个纹理的高度，用于计算纹理坐标。
     */
    public static void blit(GuiGraphics graphics, ResourceLocation resourceLocation, int x0, int y0, int destWidth, int destHeight, float u0, float v0, int srcWidth, int srcHeight, int textureWidth, int textureHeight) {
        // 设置纹理的颜色(RGBA)为白色，表示没有颜色变化，纹理将按原样显示。
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(resourceLocation, x0, y0, destWidth, destHeight, u0, v0, srcWidth, srcHeight, textureWidth, textureHeight);
    }

    public static void blit(GuiGraphics graphics, ResourceLocation resourceLocation, int x0, int y0, float u0, float v0, int destWidth, int destHeight, int textureWidth, int textureHeight) {
        graphics.blit(resourceLocation, x0, y0, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
    }

    public static void drawString(GuiGraphics graphics, Font font, @Nullable String text, float x, float y, int color) {
        AbstractGuiUtils.drawString(graphics, font, text, x, y, color, true);
    }

    public static void drawString(GuiGraphics graphics, Font font, @Nullable String text, float x, float y, int color, EDepth depth) {
        AbstractGuiUtils.drawString(graphics, font, text, x, y, color, true, depth);
    }

    public static void drawString(GuiGraphics graphics, Font font, @Nullable String text, float x, float y, int color, boolean shadow) {
        AbstractGuiUtils.drawString(graphics, font, text, x, y, color, shadow, EDepth.FOREGROUND);
    }

    public static void drawString(GuiGraphics graphics, Font font, @Nullable String text, float x, float y, int color, boolean shadow, EDepth depth) {
        AbstractGuiUtils.setDepth(graphics, depth);
        graphics.drawString(font, text, x, y, color, shadow);
        AbstractGuiUtils.resetDepth(graphics);
    }

    public static void drawString(GuiGraphics graphics, Font font, Component text, float x, float y, int color) {
        AbstractGuiUtils.drawString(graphics, font, text, x, y, color, true);
    }

    public static void drawString(GuiGraphics graphics, Font font, Component text, float x, float y, int color, EDepth depth) {
        AbstractGuiUtils.drawString(graphics, font, text, x, y, color, true, depth);
    }

    public static void drawString(GuiGraphics graphics, Font font, Component text, float x, float y, int color, boolean shadow) {
        AbstractGuiUtils.drawString(graphics, font, text, x, y, color, shadow, EDepth.FOREGROUND);
    }

    public static void drawString(GuiGraphics graphics, Font font, Component text, float x, float y, int color, boolean shadow, EDepth depth) {
        AbstractGuiUtils.setDepth(graphics, depth);
        graphics.drawString(font, text, (int) x, (int) y, color, shadow);
        AbstractGuiUtils.resetDepth(graphics);
    }

    public enum EllipsisPosition {
        START,    // 省略号在文本开头
        MIDDLE,   // 省略号在文本中间
        END       // 省略号在文本结尾
    }

    /**
     * 获取多行文本的高度，以\n为换行符
     *
     * @param font             字体渲染器
     * @param MutableComponent 要绘制的文本
     */
    public static int multilineTextHeight(Font font, MutableComponent MutableComponent) {
        return MutableComponent.getString().replaceAll("\r\n", "\n").split("\n").length * font.lineHeight;
    }

    /**
     * 获取多行文本的宽度，以\n为换行符
     *
     * @param font             字体渲染器
     * @param MutableComponent 要绘制的文本
     */
    public static int multilineTextWidth(Font font, MutableComponent MutableComponent) {
        int width = 0;
        if (!StringUtils.isNotNullOrEmpty(MutableComponent.getString())) {
            for (String s : MutableComponent.getString().replaceAll("\r\n", "\n").split("\n")) {
                width = Math.max(width, font.width(s));
            }
        }
        return width;
    }

    /**
     * 绘制多行文本，以\n为换行符
     *
     * @param graphics         渲染矩阵
     * @param font             字体渲染器
     * @param MutableComponent 要绘制的文本
     * @param x                绘制的X坐标
     * @param y                绘制的Y坐标
     * @param colors           文本颜色
     */
    public static void drawMultilineText(GuiGraphics graphics, Font font, MutableComponent MutableComponent, int x, int y, int... colors) {
        if (StringUtils.isNotNullOrEmpty(MutableComponent.getString())) {
            String[] lines = MutableComponent.getString().replaceAll("\r\n", "\n").split("\n");
            for (int i = 0; i < lines.length; i++) {
                int color;
                if (colors.length == lines.length) {
                    color = colors[i];
                } else if (colors.length > 0) {
                    color = colors[i % colors.length];
                } else {
                    color = 0xFFFFFF;
                }
                AbstractGuiUtils.drawString(graphics, font, lines[i], x, y + i * font.lineHeight, color);
            }
        }
    }

    /**
     * 绘制限制长度的文本，超出部分末尾以省略号表示
     *
     * @param graphics 渲染矩阵
     * @param font     字体渲染器
     * @param text     要绘制的文本
     * @param x        绘制的X坐标
     * @param y        绘制的Y坐标
     * @param maxWidth 文本显示的最大宽度
     * @param color    文本颜色
     */
    public static void drawLimitedText(GuiGraphics graphics, Font font, String text, int x, int y, int color, int maxWidth) {
        drawLimitedText(graphics, font, text, x, y, color, maxWidth, EllipsisPosition.END);
    }

    /**
     * 绘制限制长度的文本，超出部分以省略号表示，可选择省略号的位置
     *
     * @param graphics 渲染矩阵
     * @param font     字体渲染器
     * @param text     要绘制的文本
     * @param x        绘制的X坐标
     * @param y        绘制的Y坐标
     * @param maxWidth 文本显示的最大宽度
     * @param color    文本颜色
     * @param position 省略号位置（开头、中间、结尾）
     */
    public static void drawLimitedText(GuiGraphics graphics, Font font, String text, int x, int y, int color, int maxWidth, EllipsisPosition position) {
        String ellipsis = "...";
        int ellipsisWidth = font.width(ellipsis);

        if (font.width(text) > maxWidth) {
            switch (position) {
                case START:
                    while (font.width(ellipsis + text) > maxWidth && text.length() > 1) {
                        // 从头部截取字符
                        text = text.substring(1);
                    }
                    text = ellipsis + text;
                    break;
                case MIDDLE:
                    int halfWidth = (maxWidth - ellipsisWidth) / 2;
                    String start = text;
                    String end = text;

                    while (font.width(start) > halfWidth && start.length() > 1) {
                        // 截取前半部分
                        start = start.substring(0, start.length() - 1);
                    }
                    while (font.width(end) > halfWidth && end.length() > 1) {
                        // 截取后半部分
                        end = end.substring(1);
                    }
                    text = start + ellipsis + end;
                    break;
                case END:
                default:
                    while (font.width(text + ellipsis) > maxWidth && text.length() > 1) {
                        // 从尾部截取字符
                        text = text.substring(0, text.length() - 1);
                    }
                    text = text + ellipsis;
                    break;
            }
        }

        AbstractGuiUtils.drawString(graphics, font, text, x, y, color);
    }

    /**
     * 绘制效果图标
     *
     * @param graphics          用于变换绘制坐标系的矩阵堆栈
     * @param font              字体渲染器
     * @param mobEffectInstance 待绘制的效果实例
     * @param x                 矩形的左上角x坐标
     * @param y                 矩形的左上角y坐标
     * @param width             目标矩形的宽度，决定了图像在屏幕上的宽度
     * @param height            目标矩形的高度，决定了图像在屏幕上的高度
     * @param showText          是否显示效果等级和持续时间
     */
    public static void drawEffectIcon(GuiGraphics graphics, Font font, MobEffectInstance mobEffectInstance, ResourceLocation textureLocation, CalendarTextureCoordinate textureCoordinate, int x, int y, int width, int height, boolean showText) {
        ResourceLocation effectIcon = TextureUtils.getEffectTexture(mobEffectInstance);
        if (effectIcon == null) {
            // AbstractGuiUtils.bindTexture(textureLocation);
            TextureCoordinate buffUV = textureCoordinate.getBuffUV();
            AbstractGuiUtils.blit(graphics, textureLocation, x, y, width, height, (float) buffUV.getU0(), (float) buffUV.getV0(), (int) buffUV.getUWidth(), (int) buffUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        } else {
            // AbstractGuiUtils.bindTexture(effectIcon);
            AbstractGuiUtils.blit(graphics, effectIcon, x, y, 0, 0, width, height, width, height);
        }
        if (showText) {
            // 效果等级
            if (mobEffectInstance.getAmplifier() >= 0) {
                String amplifierString = StringUtils.intToRoman(mobEffectInstance.getAmplifier() + 1);
                int amplifierWidth = font.width(amplifierString);
                float fontX = x + width - (float) amplifierWidth / 2;
                float fontY = y - 1;
                AbstractGuiUtils.drawString(graphics, font, amplifierString, fontX, fontY, 0xFFFFFF, true);
            }
            // 效果持续时间
            if (mobEffectInstance.getDuration() > 0) {
                String durationString = DateUtils.toMaxUnitString(mobEffectInstance.getDuration(), DateUtils.DateUnit.SECOND, 0, 1);
                int durationWidth = font.width(durationString);
                float fontX = x + width - (float) durationWidth / 2 - 2;
                float fontY = y + (float) height / 2 + 1;
                AbstractGuiUtils.drawString(graphics, font, durationString, fontX, fontY, 0xFFFFFF, true);
            }
        }
    }

    /**
     * 绘制自定义图标
     *
     * @param graphics        用于变换绘制坐标系的矩阵堆栈
     * @param font            字体渲染器
     * @param reward          待绘制的奖励
     * @param textureLocation 纹理位置
     * @param textureUV       纹理坐标
     * @param x               矩形的左上角x坐标
     * @param y               矩形的左上角y坐标
     * @param totalWidth      纹理总宽度
     * @param totalHeight     纹理总高度
     * @param showText        是否显示物品数量等信息
     */
    public static void drawCustomIcon(GuiGraphics graphics, Font font, Reward reward, ResourceLocation textureLocation, TextureCoordinate textureUV, int x, int y, int totalWidth, int totalHeight, boolean showText) {
        // AbstractGuiUtils.bindTexture(textureLocation);
        AbstractGuiUtils.blit(graphics, textureLocation, x, y, ITEM_ICON_SIZE, ITEM_ICON_SIZE, (float) textureUV.getU0(), (float) textureUV.getV0(), (int) textureUV.getUWidth(), (int) textureUV.getVHeight(), totalWidth, totalHeight);
        if (showText) {
            String num = String.valueOf((Integer) RewardManager.deserializeReward(reward));
            int numWidth = font.width(num);
            float fontX = x + ITEM_ICON_SIZE - (float) numWidth / 2 - 2;
            float fontY = y + (float) ITEM_ICON_SIZE - font.lineHeight + 2;
            AbstractGuiUtils.drawString(graphics, font, num, fontX, fontY, 0xFFFFFF, true);
        }
    }

    /**
     * 渲染奖励图标
     *
     * @param graphics 用于变换绘制坐标系的矩阵堆栈
     * @param font     字体渲染器
     * @param reward   待绘制的奖励
     * @param x        图标的x坐标
     * @param y        图标的y坐标
     * @param showText 是否显示物品数量等信息
     */
    public static void renderCustomReward(GuiGraphics graphics, Font font, ResourceLocation textureLocation, CalendarTextureCoordinate textureUV, Reward reward, int x, int y, boolean showText) {
        if (reward.getType().equals(ERewardType.ITEM)) {
            ItemStack itemStack = RewardManager.deserializeReward(reward);
            graphics.renderItem(itemStack, x, y);
            if (showText) {
                graphics.renderItemDecorations(font, itemStack, x, y, String.valueOf(itemStack.getCount()));
            }
        } else if (reward.getType().equals(ERewardType.EFFECT)) {
            MobEffectInstance mobEffectInstance = RewardManager.deserializeReward(reward);
            AbstractGuiUtils.drawEffectIcon(graphics, font, mobEffectInstance, textureLocation, textureUV, x, y, ITEM_ICON_SIZE, ITEM_ICON_SIZE, showText);
        } else if (reward.getType().equals(ERewardType.EXP_POINT)) {
            AbstractGuiUtils.drawCustomIcon(graphics, font, reward, textureLocation, textureUV.getPointUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.EXP_LEVEL)) {
            AbstractGuiUtils.drawCustomIcon(graphics, font, reward, textureLocation, textureUV.getLevelUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.SIGN_IN_CARD)) {
            AbstractGuiUtils.drawCustomIcon(graphics, font, reward, textureLocation, textureUV.getCardUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.MESSAGE)) {
            // 这玩意不是Integer类型也没有数量, 不能showText
            AbstractGuiUtils.drawCustomIcon(graphics, font, reward, textureLocation, textureUV.getMessageUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), false);
        }
    }

    public static void bindTexture(ResourceLocation resourceLocation) {
        // 设置纹理的颜色(RGBA)为白色，表示没有颜色变化，纹理将按原样显示。
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, resourceLocation);
    }

    @Getter
    public enum EDepth {
        BACKGROUND(1),
        FOREGROUND(10),
        OVERLAY(100),
        TOOLTIP(200);

        private final int depth;

        EDepth(int depth) {
            this.depth = depth;
        }
    }

    /**
     * 设置深度
     */
    public static void setDepth(GuiGraphics graphics) {
        AbstractGuiUtils.setDepth(graphics, EDepth.FOREGROUND);
    }

    /**
     * 设置深度
     *
     * @param graphics 用于变换绘制坐标系的矩阵堆栈
     * @param depth    深度
     */
    public static void setDepth(GuiGraphics graphics, EDepth depth) {
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, depth.getDepth());
    }

    /**
     * 重置深度
     */
    public static void resetDepth(GuiGraphics graphics) {
        graphics.pose().popPose();
    }
}
