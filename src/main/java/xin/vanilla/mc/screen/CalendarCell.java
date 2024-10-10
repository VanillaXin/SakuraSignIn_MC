package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.enums.ESignInStatus;
import xin.vanilla.mc.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarCell {
    private static final ResourceLocation CHECKED_IN_TEXTURE = new ResourceLocation(SakuraSignIn.MODID, "textures/gui/singed_in.png");
    // 物品图标的大小
    private final int itemIconSize = 16;
    public float x, y, width, height, scale;
    public ItemStack itemStack;
    public int month, day;
    /**
     * @see xin.vanilla.mc.enums.ESignInStatus
     */
    public int status;

    public CalendarCell(float x, float y, float width, float height, float scale, ItemStack itemStack, int month, int day, int status) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemStack = itemStack;
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
    public void renderCustomItem(MatrixStack matrixStack, ItemRenderer itemRenderer, ItemStack itemStack, int itemX, int itemY, float scale, int zLevel) {
        // TODO 缩放图标以适应格子大小
        float blitOffset = itemRenderer.blitOffset;
        itemRenderer.blitOffset = zLevel;
        itemRenderer.renderGuiItem(itemStack, itemX, itemY);
        itemRenderer.blitOffset = blitOffset;
    }

    // 渲染格子
    public void render(MatrixStack matrixStack, FontRenderer font, ItemRenderer itemRenderer, int mouseX, int mouseY) {
        boolean isHovered = isMouseOver(mouseX, mouseY);
        // 绘制格子背景
        if (isHovered) {
            AbstractGui.fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + height), 0xFFAAAAAA);
        }

        if (status == ESignInStatus.REWARDED.getCode()) {
            // 绘制已领取图标
            Minecraft.getInstance().getTextureManager().bind(CHECKED_IN_TEXTURE);
            AbstractGui.blit(matrixStack, (int) x, (int) y, 0, 0, (int) width, (int) height, (int) width, (int) height);
        } else {
            // 绘制物品图标
            renderCustomItem(matrixStack, itemRenderer, itemStack, (int) (x + (width - 16) / 2), (int) (y + (height - 16) / 2), width / itemIconSize, 0);
        }

        // 绘制日期
        String dayStr = String.valueOf(day);
        float dayWidth = font.width(dayStr);
        int color;
        switch (ESignInStatus.fromCode(status)) {
            case NO_ACTION:
                if (month == DateUtils.getMonthOfDate(new Date())) {
                    // 白色
                    color = 0xFFFFFFFF;
                } else {
                    // 灰色
                    color = 0xFFAAAAAA;
                }
                break;
            case CAN_REPAIR:
                // 红色
                color = 0xFF0000FF;
                break;
            case NOT_SIGNED_IN:
                // 粉红色
                color = 0xFFFF00FF;
                break;
            case SIGNED_IN:
                // 绿色
                color = 0xFF00FF00;
                break;
            case REWARDED:
                // 灰绿色
                color = 0xFF00AAAA;
                break;
            default:
                color = 0xFF000000;
        }
        font.draw(matrixStack, dayStr, x + (width - dayWidth) / 2, y + height + 2, color);

        // 绘制弹出层
        if (isHovered) {
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

        // 弹出层物品列表
        List<ItemStack> itemsToShow = new ArrayList<ItemStack>() {{
            add(new ItemStack(Items.IRON_SWORD, 30));
            add(new ItemStack(Items.APPLE, 10));
            add(new ItemStack(Items.APPLE, 20));
            add(new ItemStack(Items.IRON_SWORD, 10));
        }};

        // 弹出层文字
        StringTextComponent title = new StringTextComponent(month + "月" + day + "日");
        int fontWidth = fontRenderer.width(title);

        // 物品图标之间的间距
        int padding = 5;
        // 弹出层物品图标集合总宽度
        int iconsWidth = itemsToShow.size() * (itemIconSize + padding) - padding;
        // 弹出层宽度
        int tooltipWidth = Math.max(fontWidth, iconsWidth) + padding * 2;
        int tooltipHeight = itemIconSize + padding * 4 + fontRenderer.lineHeight;

        // 在鼠标位置左上角绘制弹出层背景
        fill(matrixStack, mouseX - tooltipWidth, mouseY - tooltipHeight, mouseX, mouseY, 0xCC000000);

        for (int i = 0; i < itemsToShow.size(); i++) {
            ItemStack stack = itemsToShow.get(i);
            // 物品图标在弹出层中的 x 位置
            int itemX = mouseX + padding + i * (itemIconSize + padding) - tooltipWidth;
            // 物品图标在弹出层中的 y 位置
            int itemY = mouseY + padding - tooltipHeight;

            // 渲染物品图标
            renderCustomItem(matrixStack, itemRenderer, stack, itemX, itemY, width / itemIconSize, 200);
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
