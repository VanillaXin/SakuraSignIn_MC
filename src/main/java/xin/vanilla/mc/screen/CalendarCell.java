package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

import static net.minecraft.client.gui.AbstractGui.fill;

public class CalendarCell {
    public final int x, y, width, height;
    public final ItemStack itemStack;
    public final int day;

    public CalendarCell(int x, int y, int width, int height, ItemStack itemStack, int day) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemStack = itemStack;
        this.day = day;
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
        itemRenderer.renderGuiItem(itemStack, x + (width - 16) / 2, y + (height - 16) / 2);

        // 绘制日期
        String dayStr = String.valueOf(day);
        int dayWidth = font.width(dayStr);
        font.draw(matrixStack, dayStr, x + (float) (width - dayWidth) / 2, y + height + 2, 0xFFFFFFFF);
    }
}
