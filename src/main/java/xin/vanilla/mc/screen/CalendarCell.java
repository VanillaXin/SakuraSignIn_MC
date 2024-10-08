package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import xin.vanilla.mc.enums.ESignInStatus;
import xin.vanilla.mc.util.DateUtils;

import java.util.Date;

public class CalendarCell {
    public final float x, y, width, height, scale;
    public final ItemStack itemStack;
    public final int month, day;
    /**
     * @see xin.vanilla.mc.enums.ESignInStatus
     */
    public final int status;

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

    // 渲染格子
    public void render(MatrixStack matrixStack, FontRenderer font, ItemRenderer itemRenderer) {
        // 绘制格子背景
        // fill(matrixStack, x, y, x + width, y + height, 0xFFAAAAAA);

        // 绘制物品图标
        itemRenderer.renderGuiItem(itemStack, (int) x, (int) y);

        // 绘制日期
        String dayStr = String.valueOf(day);
        float dayWidth = font.width(dayStr) * scale;
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
    }
}
