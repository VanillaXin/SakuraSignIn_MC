package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import xin.vanilla.mc.config.ClientConfig;
import xin.vanilla.mc.enums.ESignInStatus;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardList;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.DateUtils;
import xin.vanilla.mc.util.PNGUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static xin.vanilla.mc.SakuraSignIn.PNG_CHUNK_NAME;

@Data
@Accessors(chain = true)
public class CalendarCell {
    private final ResourceLocation BACKGROUND_TEXTURE;
    private final CalendarTextureCoordinate textureCoordinate;

    /**
     * 当前滚动偏移量
     */
    private int tooltipScrollOffset = 0;
    /**
     * 显示的最大项目数
     */
    public static final int TOOLTIP_MAX_VISIBLE_ITEMS = 5;

    // 物品图标的大小
    private final int itemIconSize = 16;
    public double x, y, width, height, scale;
    public RewardList rewardList;
    public int year, month, day;
    /**
     * @see xin.vanilla.mc.enums.ESignInStatus
     */
    public int status;
    private boolean showIcon;
    private boolean showText;
    private boolean showHover;

    public CalendarCell(ResourceLocation resourceLocation, double x, double y, double width, double height, double scale, @NonNull RewardList rewardList, int year, int month, int day, int status) {
        BACKGROUND_TEXTURE = resourceLocation;
        CalendarTextureCoordinate textureCoordinate1;
        try {
            InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(BACKGROUND_TEXTURE).getInputStream();
            textureCoordinate1 = PNGUtils.readLastPrivateChunk(inputStream, PNG_CHUNK_NAME);
        } catch (IOException | ClassNotFoundException ignored) {
            textureCoordinate1 = CalendarTextureCoordinate.getDefault();
        }
        textureCoordinate = textureCoordinate1;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rewardList = rewardList;
        this.year = year;
        this.month = month;
        this.day = day;
        this.scale = scale;
        this.status = status;
    }

    // 判断鼠标是否在当前格子内
    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // 渲染格子
    public void render(MatrixStack matrixStack, FontRenderer font, ItemRenderer itemRenderer, int mouseX, int mouseY) {
        boolean isHovered = isMouseOver(mouseX, mouseY);
        if (showIcon) {
            Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
            if (status == ESignInStatus.REWARDED.getCode()) {
                // 绘制已领取图标
                TextureCoordinate signedInUV = textureCoordinate.getRewardedUV();
                AbstractGuiUtils.blit(matrixStack, (int) x, (int) y, (int) width, (int) height, (float) signedInUV.getU0(), (float) signedInUV.getV0(), (int) signedInUV.getUWidth(), (int) signedInUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
            } else {
                TextureCoordinate rewardUV;
                // 绘制奖励图标
                if (status == ESignInStatus.SIGNED_IN.getCode() || ClientConfig.AUTO_REWARDED.get()) {
                    rewardUV = textureCoordinate.getSignedInUV();
                } else {
                    rewardUV = textureCoordinate.getNotSignedInUV();
                }
                // 绘制格子背景
                if (isHovered) {
                    AbstractGuiUtils.blit(matrixStack, (int) x - 2, (int) y - 2, (int) width + 4, (int) height + 4, (float) rewardUV.getU0(), (float) rewardUV.getV0(), (int) rewardUV.getUWidth(), (int) rewardUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
                } else {
                    AbstractGuiUtils.blit(matrixStack, (int) x, (int) y, (int) width, (int) height, (float) rewardUV.getU0(), (float) rewardUV.getV0(), (int) rewardUV.getUWidth(), (int) rewardUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
                }
            }
        }

        if (showText) {
            // 绘制日期
            String dayStr = String.valueOf(day);
            float dayWidth = font.width(dayStr);
            int color;
            switch (ESignInStatus.valueOf(status)) {
                case NO_ACTION:
                    Date date = new Date();
                    if (year == DateUtils.getYearPart(date) && month == DateUtils.getMonthOfDate(date)) {
                        color = textureCoordinate.getTextColorNoActionCur();
                    } else {
                        color = textureCoordinate.getTextColorNoAction();
                    }
                    break;
                case CAN_REPAIR:
                    color = textureCoordinate.getTextColorCanRepair();
                    break;
                case NOT_SIGNED_IN:
                    color = textureCoordinate.getTextColorNotSignedIn();
                    break;
                case SIGNED_IN:
                    color = textureCoordinate.getTextColorSignedIn();
                    break;
                case REWARDED:
                    color = textureCoordinate.getTextColorRewarded();
                    break;
                default:
                    color = textureCoordinate.getTextColorDefault();
            }
            font.draw(matrixStack, dayStr, (float) (x + (width - dayWidth) / 2), (float) (y + height + 0.1f), color);
        }

        // 绘制弹出层
        if (showHover && isHovered) {
            renderTooltip(matrixStack, itemRenderer, font, mouseX, mouseY);
        }
    }

    // 绘制奖励详情弹出层
    private void renderTooltip(MatrixStack matrixStack, ItemRenderer itemRenderer, FontRenderer fontRenderer, int mouseX, int mouseY) {
        // 禁用深度测试
        RenderSystem.disableDepthTest();
        matrixStack.pushPose();
        // 提升Z坐标以确保弹出层在最上层
        matrixStack.translate(0, 0, 200.0F);

        // FIXME 弹出层深度问题
        TextureCoordinate tooltipUV = textureCoordinate.getTooltipUV();
        TextureCoordinate cellCoordinate = textureCoordinate.getTooltipCellCoordinate();
        // 物品图标之间的间距
        double margin = textureCoordinate.getTooltipCellHMargin();
        // 弹出层宽高
        double tooltipWidth = margin + (itemIconSize + margin) * TOOLTIP_MAX_VISIBLE_ITEMS;
        double tooltipHeight = (tooltipWidth * tooltipUV.getVHeight() / tooltipUV.getUWidth());
        // 弹出层缩放比例
        double tooltipScale = tooltipWidth / tooltipUV.getUWidth();

        // 开启 OpenGL 的混合模式，使得纹理的透明区域渲染生效
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 在鼠标位置左上角绘制弹出层背景
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
        double tooltipX0 = mouseX - tooltipWidth / 2;
        double tooltipY0 = mouseY - tooltipHeight - 1;
        AbstractGuiUtils.blit(matrixStack, (int) tooltipX0, (int) tooltipY0, (int) tooltipWidth, (int) tooltipHeight, (float) tooltipUV.getU0(), (float) tooltipUV.getV0(), (int) tooltipUV.getUWidth(), (int) tooltipUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
        // 关闭 OpenGL 的混合模式
        RenderSystem.disableBlend();

        // 绘制滚动条
        TextureCoordinate scrollCoordinate = textureCoordinate.getTooltipScrollCoordinate();
        double outScrollX0 = tooltipX0 + scrollCoordinate.getX() * tooltipScale;
        double outScrollX1 = outScrollX0 + scrollCoordinate.getWidth() * tooltipScale;
        double outScrollY0 = tooltipY0 + scrollCoordinate.getY() * tooltipScale;
        double outScrollY1 = outScrollY0 + scrollCoordinate.getHeight() * tooltipScale;
        AbstractGui.fill(matrixStack, (int) outScrollX0, (int) outScrollY0, (int) outScrollX1, (int) outScrollY1, 0xCC232323);
        // 滚动条百分比
        double inScrollWidthScale = rewardList.size() > TOOLTIP_MAX_VISIBLE_ITEMS ? (double) TOOLTIP_MAX_VISIBLE_ITEMS / rewardList.size() : 1;
        // 多出来的格子数量
        int outCell = Math.max(rewardList.size() - TOOLTIP_MAX_VISIBLE_ITEMS, 0);
        // 多出来的每个格子所占的空余条长度
        double outCellWidth = outCell == 0 ? 0 : (1 - inScrollWidthScale) * scrollCoordinate.getWidth() * tooltipScale / outCell;
        // 滚动条左边距长度
        double inScrollLeftWidth = tooltipScrollOffset * outCellWidth;
        // 滚动条长度
        double inScrollWidth = scrollCoordinate.getWidth() * tooltipScale * inScrollWidthScale;

        double inScrollX0 = outScrollX0 + inScrollLeftWidth;
        double inScrollX1 = inScrollX0 + inScrollWidth;
        double inScrollY0 = outScrollY0;
        double inScrollY1 = outScrollY1;
        AbstractGui.fill(matrixStack, (int) inScrollX0 + 1, (int) inScrollY0, (int) inScrollX1 - 1, (int) inScrollY1, 0xCCCCCCCC);

        for (int i = 0; i < TOOLTIP_MAX_VISIBLE_ITEMS; i++) {
            int index = i + (rewardList.size() > TOOLTIP_MAX_VISIBLE_ITEMS ? tooltipScrollOffset : 0);
            if (index >= 0 && index < rewardList.size()) {
                Reward reward = rewardList.get(index);
                // 物品图标在弹出层中的 x 位置
                double itemX = (tooltipX0 + cellCoordinate.getX() * tooltipScale) + i * (itemIconSize + margin);
                // 物品图标在弹出层中的 y 位置
                double itemY = tooltipY0 + cellCoordinate.getY() * tooltipScale;
                // 渲染物品图标
                AbstractGuiUtils.renderCustomReward(matrixStack, itemRenderer, fontRenderer, reward, (int) itemX, (int) itemY, true);
            }
        }
        // 绘制文字
        StringTextComponent title = new StringTextComponent(month + "月" + day + "日");
        double fontWidth = fontRenderer.width(title);
        TextureCoordinate dateCoordinate = textureCoordinate.getTooltipDateCoordinate();
        double tooltipDateX = tooltipX0 + (tooltipWidth - fontWidth) / 2;
        double tooltipDateY = tooltipY0 + (dateCoordinate.getY() * tooltipScale);
        fontRenderer.draw(matrixStack, title, (int) tooltipDateX, (int) tooltipDateY, 0xFFFFFF);

        // 恢复原来的矩阵状态
        matrixStack.popPose();
        // 恢复深度测试
        RenderSystem.enableDepthTest();
    }
}
