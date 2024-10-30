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
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import xin.vanilla.mc.enums.ERewardType;
import xin.vanilla.mc.enums.ESignInStatus;
import xin.vanilla.mc.rewards.Reward;
import xin.vanilla.mc.rewards.RewardList;
import xin.vanilla.mc.rewards.RewardManager;
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

    // 渲染物品图标
    public void renderCustomReward(MatrixStack matrixStack, ItemRenderer itemRenderer, FontRenderer fontRenderer, Reward reward, int itemX, int itemY, double scale, int zLevel, boolean showNum) {
        // TODO 缩放图标以适应格子大小
        float blitOffset = itemRenderer.blitOffset;
        itemRenderer.blitOffset = zLevel;
        // TODO 根据奖励类型渲染
        if (reward.getType().equals(ERewardType.ITEM)) {
            ItemStack itemStack = RewardManager.deserializeReward(reward);
            itemRenderer.renderGuiItem(itemStack, itemX, itemY);
            if (showNum) {
                itemRenderer.renderGuiItemDecorations(fontRenderer, itemStack, itemX, itemY, String.valueOf(itemStack.getCount()));
            }
        } else if (reward.getType().equals(ERewardType.EFFECT)) {
            EffectInstance effectInstance = RewardManager.deserializeReward(reward);
            AbstractGuiUtils.drawEffectIcon(matrixStack, fontRenderer, effectInstance, itemX, itemY, itemIconSize, itemIconSize, true);
        } else if (reward.getType().equals(ERewardType.EXP_POINT)) {

        } else if (reward.getType().equals(ERewardType.EXP_LEVEL)) {

        } else if (reward.getType().equals(ERewardType.SIGN_IN_CARD)) {

        }
        itemRenderer.blitOffset = blitOffset;
    }

    // 渲染格子
    public void render(MatrixStack matrixStack, FontRenderer font, ItemRenderer itemRenderer, int mouseX, int mouseY) {
        boolean isHovered = isMouseOver(mouseX, mouseY);
        if (showIcon) {
            Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
            // TODO 单独绘制已签到未领取奖励图标
            if (status == ESignInStatus.REWARDED.getCode() || status == ESignInStatus.SIGNED_IN.getCode()) {
                // 绘制已领取图标
                TextureCoordinate signedInUV = textureCoordinate.getSignedInUV();
                AbstractGuiUtils.blit(matrixStack, (int) x, (int) y, (int) width, (int) height, (float) signedInUV.getU0(), (float) signedInUV.getV0(), (int) signedInUV.getUWidth(), (int) signedInUV.getVHeight(), textureCoordinate.getTotalWidth(), textureCoordinate.getTotalHeight());
            } else {
                // 绘制奖励图标
                TextureCoordinate rewardUV = textureCoordinate.getRewardUV();
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

        // 弹出层文字
        StringTextComponent title = new StringTextComponent(month + "月" + day + "日");
        int fontWidth = fontRenderer.width(title);

        // 物品图标之间的间距
        int padding = 5;
        // 弹出层物品图标集合总宽度
        int iconsWidth = rewardList.size() * (itemIconSize + padding) - padding;
        // 弹出层宽度
        int tooltipWidth = Math.max(fontWidth, iconsWidth) + padding * 2;
        int tooltipHeight = itemIconSize + padding * 4 + fontRenderer.lineHeight;

        // 在鼠标位置左上角绘制弹出层背景
        fill(matrixStack, mouseX - tooltipWidth, mouseY - tooltipHeight, mouseX, mouseY, 0xCC000000);

        for (int i = 0; i < rewardList.size(); i++) {
            Reward reward = rewardList.get(i);
            // 物品图标在弹出层中的 x 位置
            int itemX = mouseX + padding + i * (itemIconSize + padding) - tooltipWidth;
            // 物品图标在弹出层中的 y 位置
            int itemY = mouseY + padding - tooltipHeight;

            // 渲染物品图标
            renderCustomReward(matrixStack, itemRenderer, fontRenderer, reward, itemX, itemY, width / itemIconSize, 200, true);
        }
        // 绘制文字
        fontRenderer.draw(matrixStack, title, mouseX + padding - tooltipWidth, mouseY + padding + itemIconSize + padding * 2 - tooltipHeight, 0xFFFFFF);

        // 恢复原来的矩阵状态
        matrixStack.popPose();
        // 恢复深度测试
        RenderSystem.enableDepthTest();
    }

    // 绘制背景
    public static void fill(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int color) {
        // TODO 样式优化
        AbstractGui.fill(matrixStack, x1, y1, x2, y2, color);
    }
}
