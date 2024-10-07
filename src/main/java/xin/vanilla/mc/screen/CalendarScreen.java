package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import xin.vanilla.mc.SakuraSignIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class CalendarScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SakuraSignIn.MODID, "textures/gui/checkin_background.png");
    private final List<CalendarCell> calendarCells = new ArrayList<>();
    private int startX = 50, startY = 50;
    int totalWidth = 0, totalHeight = 0;
    private int slotSize = 40; // 格子大小
    private final int columns = 7;  // 列数
    private final int rows = 6;     // 行数
    private final int topMargin = 20;  // 顶部预留距离
    private final int bottomMargin = 22 + 39;  // 底部预留距离
    private final int horizontalPadding = 10;  // 左右间距
    private final int verticalPadding = 15;    // 上下间距

    public CalendarScreen() {
        super(new TranslationTextComponent("calendar.title"));
    }

    @Override
    protected void init() {
        super.init();
        updateLayout(); // 初始化布局
    }

    public static ItemStack getRandomItem(int amount) {
        List<Item> items = new ArrayList<>(ForgeRegistries.ITEMS.getValues());

        Random random = new Random();
        Item randomItem = items.get(random.nextInt(items.size()));
        return new ItemStack(randomItem, amount);
    }

    // 创建日历格子
    private void createCalendarCells() {
        calendarCells.clear();  // 清除原有格子，避免重复添加

        int itemIndex = 0; // 用于模拟日期

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = startX + col * (slotSize + horizontalPadding);
                int y = startY + row * (slotSize + verticalPadding);

                // 创建物品格子
                ItemStack itemStack = getRandomItem(1); // 模拟显示随机物品
                CalendarCell cell = new CalendarCell(x, y, slotSize, slotSize, itemStack, itemIndex + 1);

                // 添加到列表
                calendarCells.add(cell);

                itemIndex++;
            }
        }
    }

    // 计算并更新布局
    private void updateLayout() {
        // 获取屏幕宽度和高度
        int screenWidth = this.width;
        int screenHeight = this.height;

        // 计算宽高比满足7:6的情况
        int availableHeight = screenHeight - topMargin - bottomMargin - (rows - 1) * verticalPadding;
        int availableWidth = screenWidth - (columns - 1) * horizontalPadding;

        // 根据可用空间计算格子的大小
        slotSize = Math.min(availableWidth / columns, availableHeight / rows);

        // 计算整个日历的总宽度和总高度
        totalWidth = columns * slotSize + (columns - 1) * horizontalPadding;
        totalHeight = rows * slotSize + (rows - 1) * verticalPadding;

        // 居中对齐
        this.startX = (screenWidth - totalWidth) / 2;
        this.startY = topMargin;

        // 创建或更新格子位置
        createCalendarCells();
    }

    // 绘制背景纹理
    private void renderBackgroundTexture(MatrixStack matrixStack, int x, int y, int width, int height) {
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
        blit(matrixStack, x, y, 0, 0, width, height);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackgroundTexture(matrixStack, startX, startY, width, height);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // 渲染标题，居中显示
        String title = new TranslationTextComponent("calendar.title").getString();
        int titleWidth = this.font.width(title);
        this.font.draw(matrixStack, title, (this.width - titleWidth) / 2.0f, startY - slotSize * 1.5f, 0xFFFFFF00);  // 标题居中显示在日历上方

        // 渲染所有格子
        for (CalendarCell cell : calendarCells) {
            cell.render(matrixStack, this.font, this.itemRenderer);
        }
    }

    // 检测鼠标点击事件
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        for (CalendarCell cell : calendarCells) {
            if (cell.isMouseOver((int) mouseX, (int) mouseY)) {
                // 点击了该格子，执行对应操作
                if (player != null) {
                    player.sendMessage(new StringTextComponent("Clicked on day " + cell.day), player.getUUID());
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 窗口缩放时重新计算布局
    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        this.width = width;
        this.height = height;
        updateLayout(); // 在窗口大小变化时更新布局
    }
}
