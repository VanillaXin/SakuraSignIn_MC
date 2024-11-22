package xin.vanilla.mc.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import xin.vanilla.mc.enums.ERewardType;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.screen.CalendarTextureCoordinate;
import xin.vanilla.mc.screen.TextureCoordinate;

import java.util.Random;

/**
 * AbstractGui工具类
 */
public class AbstractGuiUtils {

    public final static int ITEM_ICON_SIZE = 16;

    public static void blit(MatrixStack matrixStack, int x0, int y0, int z, int destWidth, int destHeight, TextureAtlasSprite sprite) {
        AbstractGui.blit(matrixStack, x0, y0, z, destWidth, destHeight, sprite);
    }

    public static void blit(MatrixStack matrixStack, int x0, int y0, int z, float u0, float v0, int width, int height, int textureHeight, int textureWidth) {
        AbstractGui.blit(matrixStack, x0, y0, z, u0, v0, width, height, textureHeight, textureWidth);
    }

    /**
     * 使用指定的纹理坐标和尺寸信息绘制一个矩形区域。
     *
     * @param matrixStack   用于变换绘制坐标系的矩阵堆栈。
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
    public static void blit(MatrixStack matrixStack, int x0, int y0, int destWidth, int destHeight, float u0, float v0, int srcWidth, int srcHeight, int textureWidth, int textureHeight) {
        AbstractGui.blit(matrixStack, x0, y0, destWidth, destHeight, u0, v0, srcWidth, srcHeight, textureWidth, textureHeight);
    }

    public static void blit(MatrixStack matrixStack, int x0, int y0, float u0, float v0, int destWidth, int destHeight, int textureWidth, int textureHeight) {
        AbstractGui.blit(matrixStack, x0, y0, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
    }

    public enum EllipsisPosition {
        START,    // 省略号在文本开头
        MIDDLE,   // 省略号在文本中间
        END       // 省略号在文本结尾
    }

    /**
     * 获取多行文本的高度，以\n为换行符
     *
     * @param fontRenderer  字体渲染器
     * @param textComponent 要绘制的文本
     */
    public static int multilineTextHeight(FontRenderer fontRenderer, TextComponent textComponent) {
        return textComponent.getString().replaceAll("\r\n", "\n").split("\n").length * fontRenderer.lineHeight;
    }

    /**
     * 获取多行文本的宽度，以\n为换行符
     *
     * @param fontRenderer  字体渲染器
     * @param textComponent 要绘制的文本
     */
    public static int multilineTextWidth(FontRenderer fontRenderer, TextComponent textComponent) {
        int width = 0;
        if (!StringUtils.isNotNullOrEmpty(textComponent.getString())) {
            for (String s : textComponent.getString().replaceAll("\r\n", "\n").split("\n")) {
                width = Math.max(width, fontRenderer.width(s));
            }
        }
        return width;
    }

    /**
     * 绘制多行文本，以\n为换行符
     *
     * @param matrixStack   渲染矩阵
     * @param fontRenderer  字体渲染器
     * @param textComponent 要绘制的文本
     * @param x             绘制的X坐标
     * @param y             绘制的Y坐标
     * @param colors        文本颜色
     */
    public static void drawMultilineText(MatrixStack matrixStack, FontRenderer fontRenderer, TextComponent textComponent, int x, int y, int... colors) {
        if (StringUtils.isNotNullOrEmpty(textComponent.getString())) {
            String[] lines = textComponent.getString().replaceAll("\r\n", "\n").split("\n");
            for (int i = 0; i < lines.length; i++) {
                int color;
                if (colors.length == lines.length) {
                    color = colors[i];
                } else if (colors.length > 0) {
                    color = colors[i % colors.length];
                } else {
                    color = 0xFFFFFF;
                }
                fontRenderer.draw(matrixStack, lines[i], x, y + i * fontRenderer.lineHeight, color);
            }
        }
    }

    /**
     * 绘制限制长度的文本，超出部分末尾以省略号表示
     *
     * @param matrixStack  渲染矩阵
     * @param fontRenderer 字体渲染器
     * @param text         要绘制的文本
     * @param x            绘制的X坐标
     * @param y            绘制的Y坐标
     * @param maxWidth     文本显示的最大宽度
     * @param color        文本颜色
     */
    public static void drawLimitedText(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color, int maxWidth) {
        drawLimitedText(matrixStack, fontRenderer, text, x, y, color, maxWidth, EllipsisPosition.END);
    }

    /**
     * 绘制限制长度的文本，超出部分以省略号表示，可选择省略号的位置
     *
     * @param matrixStack  渲染矩阵
     * @param fontRenderer 字体渲染器
     * @param text         要绘制的文本
     * @param x            绘制的X坐标
     * @param y            绘制的Y坐标
     * @param maxWidth     文本显示的最大宽度
     * @param color        文本颜色
     * @param position     省略号位置（开头、中间、结尾）
     */
    public static void drawLimitedText(MatrixStack matrixStack, FontRenderer fontRenderer, String text, int x, int y, int color, int maxWidth, EllipsisPosition position) {
        String ellipsis = "...";
        int ellipsisWidth = fontRenderer.width(ellipsis);

        if (fontRenderer.width(text) > maxWidth) {
            switch (position) {
                case START:
                    while (fontRenderer.width(ellipsis + text) > maxWidth && text.length() > 1) {
                        // 从头部截取字符
                        text = text.substring(1);
                    }
                    text = ellipsis + text;
                    break;
                case MIDDLE:
                    int halfWidth = (maxWidth - ellipsisWidth) / 2;
                    String start = text;
                    String end = text;

                    while (fontRenderer.width(start) > halfWidth && start.length() > 1) {
                        // 截取前半部分
                        start = start.substring(0, start.length() - 1);
                    }
                    while (fontRenderer.width(end) > halfWidth && end.length() > 1) {
                        // 截取后半部分
                        end = end.substring(1);
                    }
                    text = start + ellipsis + end;
                    break;
                case END:
                default:
                    while (fontRenderer.width(text + ellipsis) > maxWidth && text.length() > 1) {
                        // 从尾部截取字符
                        text = text.substring(0, text.length() - 1);
                    }
                    text = text + ellipsis;
                    break;
            }
        }

        fontRenderer.draw(matrixStack, text, x, y, color);
    }

    /**
     * 绘制效果图标
     *
     * @param matrixStack    用于变换绘制坐标系的矩阵堆栈
     * @param fontRenderer   字体渲染器
     * @param effectInstance 待绘制的效果实例
     * @param x              矩形的左上角x坐标
     * @param y              矩形的左上角y坐标
     * @param width          目标矩形的宽度，决定了图像在屏幕上的宽度
     * @param height         目标矩形的高度，决定了图像在屏幕上的高度
     * @param showText       是否显示效果等级和持续时间
     */
    public static void drawEffectIcon(MatrixStack matrixStack, FontRenderer fontRenderer, EffectInstance effectInstance, ResourceLocation textureLocation, CalendarTextureCoordinate textureCoordinate, int x, int y, int width, int height, boolean showText) {
        ResourceLocation effectIcon = TextureUtils.getEffectTexture(effectInstance);
        if (effectIcon == null) {
            Minecraft.getInstance().getTextureManager().bind(textureLocation);
            TextureCoordinate buffUV = textureCoordinate.getBuffUV();
            AbstractGuiUtils.blit(matrixStack, x, y, width, height, (float) buffUV.getU0(), (float) buffUV.getV0(), (int) buffUV.getUWidth(), (int) buffUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        } else {
            Minecraft.getInstance().getTextureManager().bind(effectIcon);
            AbstractGuiUtils.blit(matrixStack, x, y, 0, 0, width, height, width, height);
        }
        if (showText) {
            // 效果等级
            if (effectInstance.getAmplifier() >= 0) {
                StringTextComponent amplifierString = new StringTextComponent(StringUtils.intToRoman(effectInstance.getAmplifier() + 1));
                int amplifierWidth = fontRenderer.width(amplifierString);
                float fontX = x + width - (float) amplifierWidth / 2;
                float fontY = y - 1;
                fontRenderer.drawShadow(matrixStack, amplifierString, fontX, fontY, 0xFFFFFF);
            }
            // 效果持续时间
            if (effectInstance.getDuration() > 0) {
                StringTextComponent durationString = new StringTextComponent(DateUtils.toMaxUnitString(effectInstance.getDuration(), DateUtils.DateUnit.SECOND, 0, 1));
                int durationWidth = fontRenderer.width(durationString);
                float fontX = x + width - (float) durationWidth / 2 - 2;
                float fontY = y + (float) height / 2 + 1;
                fontRenderer.drawShadow(matrixStack, durationString, fontX, fontY, 0xFFFFFF);
            }
        }
    }

    /**
     * 绘制自定义图标
     *
     * @param matrixStack     用于变换绘制坐标系的矩阵堆栈
     * @param fontRenderer    字体渲染器
     * @param reward          待绘制的奖励
     * @param textureLocation 纹理位置
     * @param textureUV       纹理坐标
     * @param x               矩形的左上角x坐标
     * @param y               矩形的左上角y坐标
     * @param totalWidth      纹理总宽度
     * @param totalHeight     纹理总高度
     * @param showText        是否显示物品数量等信息
     */
    public static void drawCustomIcon(MatrixStack matrixStack, FontRenderer fontRenderer, Reward reward, ResourceLocation textureLocation, TextureCoordinate textureUV, int x, int y, int totalWidth, int totalHeight, boolean showText) {
        Minecraft.getInstance().getTextureManager().bind(textureLocation);
        AbstractGuiUtils.blit(matrixStack, x, y, ITEM_ICON_SIZE, ITEM_ICON_SIZE, (float) textureUV.getU0(), (float) textureUV.getV0(), (int) textureUV.getUWidth(), (int) textureUV.getVHeight(), totalWidth, totalHeight);
        if (showText) {
            StringTextComponent num = new StringTextComponent(String.valueOf((Integer) RewardManager.deserializeReward(reward)));
            int numWidth = fontRenderer.width(num);
            float fontX = x + ITEM_ICON_SIZE - (float) numWidth / 2 - 2;
            float fontY = y + (float) ITEM_ICON_SIZE - fontRenderer.lineHeight + 2;
            fontRenderer.drawShadow(matrixStack, num, fontX, fontY, 0xFFFFFF);
        }
    }

    /**
     * 渲染奖励图标
     *
     * @param matrixStack  用于变换绘制坐标系的矩阵堆栈
     * @param itemRenderer 物品渲染器
     * @param fontRenderer 字体渲染器
     * @param reward       待绘制的奖励
     * @param x            图标的x坐标
     * @param y            图标的y坐标
     * @param showText     是否显示物品数量等信息
     */
    public static void renderCustomReward(MatrixStack matrixStack, ItemRenderer itemRenderer, FontRenderer fontRenderer, ResourceLocation textureLocation, CalendarTextureCoordinate textureUV, Reward reward, int x, int y, boolean showText) {
        if (reward.getType().equals(ERewardType.ITEM)) {
            ItemStack itemStack = RewardManager.deserializeReward(reward);
            itemRenderer.renderGuiItem(itemStack, x, y);
            if (showText) {
                itemRenderer.renderGuiItemDecorations(fontRenderer, itemStack, x, y, String.valueOf(itemStack.getCount()));
            }
        } else if (reward.getType().equals(ERewardType.EFFECT)) {
            EffectInstance effectInstance = RewardManager.deserializeReward(reward);
            AbstractGuiUtils.drawEffectIcon(matrixStack, fontRenderer, effectInstance, textureLocation, textureUV, x, y, ITEM_ICON_SIZE, ITEM_ICON_SIZE, showText);
        } else if (reward.getType().equals(ERewardType.EXP_POINT)) {
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getPointUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.EXP_LEVEL)) {
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getLevelUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.SIGN_IN_CARD)) {
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getCardUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), showText);
        } else if (reward.getType().equals(ERewardType.MESSAGE)) {
            // 这玩意不是Integer类型也没有数量, 不能showText
            AbstractGuiUtils.drawCustomIcon(matrixStack, fontRenderer, reward, textureLocation, textureUV.getMessageUV(), x, y, textureUV.getTotalWidth(), textureUV.getTotalHeight(), false);
        }
    }

    /**
     * 绘制一个“像素”矩形
     *
     * @param matrixStack 用于变换绘制坐标系的矩阵堆栈
     * @param x           像素的 X 坐标
     * @param y           像素的 Y 坐标
     * @param color       像素的颜色
     */
    public static void drawPixel(MatrixStack matrixStack, int x, int y, int color) {
        AbstractGui.fill(matrixStack, x, y, x + 1, y + 1, color);
    }

    /**
     * 绘制旋转的纹理
     *
     * @param matrixStack       矩阵栈
     * @param texture           纹理
     * @param textureCoordinate 纹理坐标
     * @param coordinate        绘制相对坐标
     * @param baseX             绘制的基础坐标X
     * @param baseY             绘制的基础坐标Y
     * @param scale             Screen纹理缩放比例
     * @param angle             旋转角度
     * @param flipHorizontal    水平翻转
     * @param flipVertical      垂直翻转
     */
    public static void renderRotatedTexture(MatrixStack matrixStack, ResourceLocation texture, CalendarTextureCoordinate textureCoordinate, TextureCoordinate coordinate, double baseX, double baseY, double scale, double angle, boolean flipHorizontal, boolean flipVertical) {
        double x = baseX + coordinate.getX() * scale;
        double y = baseY + coordinate.getY() * scale;
        int width = (int) (coordinate.getWidth() * scale);
        int height = (int) (coordinate.getHeight() * scale);
        float u0 = (float) coordinate.getU0();
        float v0 = (float) coordinate.getV0();
        int uWidth = (int) coordinate.getUWidth();
        int vHeight = (int) coordinate.getVHeight();
        // 绑定纹理
        Minecraft.getInstance().getTextureManager().bind(texture);
        // 保存当前矩阵状态
        matrixStack.pushPose();
        // 平移到旋转中心 (x + width / 2, y + height / 2)
        matrixStack.translate(x + width / 2.0, y + height / 2.0, 0);
        // 进行旋转，angle 是旋转角度，单位是度数，绕 Z 轴旋转
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float) angle));
        // 左右翻转
        if (flipHorizontal) {
            u0 += uWidth;
            uWidth = -uWidth;
        }
        // 上下翻转
        if (flipVertical) {
            v0 += vHeight;
            vHeight = -vHeight;
        }
        // 平移回原点
        matrixStack.translate(-width / 2.0, -height / 2.0, 0);
        // 绘制纹理
        AbstractGuiUtils.blit(matrixStack, 0, 0, width, height, u0, v0, uWidth, vHeight, textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        // 恢复矩阵状态
        matrixStack.popPose();
    }

    /**
     * 绘制 颤抖的 纹理
     *
     * @param matrixStack        矩阵栈
     * @param texture            纹理
     * @param textureCoordinate  纹理坐标
     * @param coordinate         绘制相对坐标
     * @param baseX              绘制的基础坐标X
     * @param baseY              绘制的基础坐标Y
     * @param scale              Screen纹理缩放比例
     * @param affectLight        是否受光照影响
     * @param tremblingAmplitude 颤抖幅度
     */
    public static void renderTremblingTexture(MatrixStack matrixStack, ResourceLocation texture, CalendarTextureCoordinate textureCoordinate, TextureCoordinate coordinate, double baseX, double baseY, double scale, boolean affectLight, double tremblingAmplitude) {
        double x = baseX + coordinate.getX() * scale;
        double y = baseY + coordinate.getY() * scale;
        int width = (int) (coordinate.getWidth() * scale);
        int height = (int) (coordinate.getHeight() * scale);
        float u0 = (float) coordinate.getU0();
        float v0 = (float) coordinate.getV0();
        int uWidth = (int) coordinate.getUWidth();
        int vHeight = (int) coordinate.getVHeight();
        Random random = new Random();
        // 绑定纹理
        Minecraft.getInstance().getTextureManager().bind(texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrixStack.pushPose();
        // 添加偏移
        if (tremblingAmplitude > 0) {
            if (!affectLight || WorldUtils.getEnvironmentBrightness(Minecraft.getInstance().player) > 4) {
                x += (random.nextFloat() - 0.5) * tremblingAmplitude;
                y += (random.nextFloat() - 0.5) * tremblingAmplitude;
            }
        }
        matrixStack.translate(x, y, 0);
        // 绘制纹理
        AbstractGuiUtils.blit(matrixStack, 0, 0, width, height, u0, v0, uWidth, vHeight, textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        matrixStack.popPose();
        RenderSystem.disableBlend();
    }

    /**
     * 绘制一个矩形边框
     *
     * @param thickness 边框厚度
     * @param color     边框颜色
     */
    public static void fillOutLine(MatrixStack matrixStack, int x, int y, int width, int height, int thickness, int color) {
        // 上边
        AbstractGui.fill(matrixStack, x, y, x + width, y + thickness, color);
        // 下边
        AbstractGui.fill(matrixStack, x, y + height - thickness, x + width, y + height, color);
        // 左边
        AbstractGui.fill(matrixStack, x, y, x + thickness, y + height, color);
        // 右边
        AbstractGui.fill(matrixStack, x + width - thickness, y, x + width, y + height, color);
    }
}
