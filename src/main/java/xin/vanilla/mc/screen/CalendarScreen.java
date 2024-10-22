package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.capability.PlayerSignInDataCapability;
import xin.vanilla.mc.enums.ESignInStatus;
import xin.vanilla.mc.event.ClientEventHandler;
import xin.vanilla.mc.rewards.RewardList;
import xin.vanilla.mc.rewards.RewardManager;
import xin.vanilla.mc.util.CollectionUtils;
import xin.vanilla.mc.util.DateUtils;
import xin.vanilla.mc.util.PNGUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CalendarScreen extends Screen {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final String BACKGROUND_PNG_CHUNK_NAME = "vacb";
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SakuraSignIn.MODID, "textures/gui/sign_in_calendar_bg.png");

    private final List<CalendarCell> calendarCells = new ArrayList<>();
    private CalendarBackgroundConf calendarBackgroundConf;

    private final int columns = 7;  // 列数
    private final int rows = 6;     // 行数

    private int bgH = Math.max(this.height - 20, 120);
    private int bgW = Math.max(bgH * 5 / 6, 100);
    private int bgX = (this.width - bgW) / 2;
    private int bgY = 0;

    private Date currentDate;

    private float scale = 1.0F;

    public CalendarScreen() {
        super(new TranslationTextComponent("calendar.title"));
    }

    @Override
    protected void init() {
        super.init();
        currentDate = new Date();
        try {
            InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(BACKGROUND_TEXTURE).getInputStream();
            calendarBackgroundConf = PNGUtils.readLastPrivateChunk(inputStream, BACKGROUND_PNG_CHUNK_NAME);
        } catch (IOException | ClassNotFoundException ignored) {
        }
        if (calendarBackgroundConf == null) {
            // 使用默认配置
            calendarBackgroundConf = CalendarBackgroundConf.getDefault();
        }
        updateLayout(); // 初始化布局
    }

    // 创建日历格子

    /**
     * 创建日历格子
     * 此方法用于生成日历控件中的每日格子，包括当前月和上月的末尾几天
     * 它根据当前日期计算出上月和本月的天数以及每周的起始天数，并据此创建相应数量的格子
     */
    private void createCalendarCells(Date current) {
        // 清除原有格子，避免重复添加
        calendarCells.clear();

        int itemIndex = 0;
        float startX = bgX + calendarBackgroundConf.getCellStartX() * this.scale;
        float startY = bgY + calendarBackgroundConf.getCellStartY() * this.scale;
        Date lastMonth = DateUtils.addMonth(current, -1);
        int daysOfLastMonth = DateUtils.getDaysOfMonth(lastMonth);
        int dayOfWeekOfMonthStart = DateUtils.getDayOfWeekOfMonthStart(current);
        int daysOfCurrentMonth = DateUtils.getDaysOfMonth(current);

        // 获取奖励列表
        if (Minecraft.getInstance().player != null) {
            Map<Integer, RewardList> monthRewardList = RewardManager.getMonthRewardList(current, PlayerSignInDataCapability.getData(Minecraft.getInstance().player));

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    // 检查是否已超过设置显示上限
                    if (itemIndex >= 40) break;
                    float x = startX + col * (calendarBackgroundConf.getCellWidth() + calendarBackgroundConf.getCellHMargin()) * this.scale;
                    float y = startY + row * (calendarBackgroundConf.getCellHeight() + calendarBackgroundConf.getCellVMargin()) * this.scale;
                    int year, month, day, status;
                    // 根据itemIndex确定日期和状态
                    if (itemIndex >= dayOfWeekOfMonthStart + daysOfCurrentMonth) {
                        // 属于下月的日期
                        year = DateUtils.getYearPart(DateUtils.addMonth(current, 1));
                        month = DateUtils.getMonthOfDate(DateUtils.addMonth(current, 1));
                        day = itemIndex - dayOfWeekOfMonthStart - daysOfCurrentMonth + 1;
                        status = ESignInStatus.NO_ACTION.getCode();
                    } else if (itemIndex >= dayOfWeekOfMonthStart) {
                        // 属于当前月的日期
                        year = DateUtils.getYearPart(current);
                        month = DateUtils.getMonthOfDate(current);
                        day = itemIndex - dayOfWeekOfMonthStart + 1;
                        status = ESignInStatus.NO_ACTION.getCode();
                        // 如果是今天，则设置为未签到状态
                        if (year == DateUtils.getYearPart(new Date()) && day == DateUtils.getDayOfMonth(new Date()) && month == DateUtils.getMonthOfDate(new Date())) {
                            status = ESignInStatus.NOT_SIGNED_IN.getCode();
                        }
                    } else {
                        // 属于上月的日期
                        year = DateUtils.getYearPart(lastMonth);
                        month = DateUtils.getMonthOfDate(lastMonth);
                        day = daysOfLastMonth - (dayOfWeekOfMonthStart - itemIndex) + 1;
                        status = ESignInStatus.NO_ACTION.getCode();
                    }
                    int key = year * 10000 + month * 100 + day;
                    RewardList rewards = monthRewardList.get(key);
                    if (CollectionUtils.isNullOrEmpty(rewards)) break;
                    // 创建物品格子
                    CalendarCell cell = new CalendarCell(x, y, calendarBackgroundConf.getCellWidth() * this.scale, calendarBackgroundConf.getCellHeight() * this.scale, this.scale, rewards, year, month, day, status);
                    // 添加到列表
                    calendarCells.add(cell);
                    itemIndex++;
                }
                // 检查是否已超过设置显示上限
                if (itemIndex >= 40) break;
            }
        }
    }

    // 计算并更新布局
    private void updateLayout() {
        bgH = Math.max(this.height - 20, 120);
        bgW = Math.max(bgH * 5 / 6, 100);
        bgX = (this.width - bgW) / 2;
        this.scale = bgH * 1.0f / calendarBackgroundConf.getTotalHeight();
        // 创建或更新格子位置
        createCalendarCells(currentDate);
    }

    // 绘制背景纹理
    private void renderBackgroundTexture(MatrixStack matrixStack) {
        // 绘制背景纹理，使用缩放后的宽度和高度
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
        // 绘制的位置坐标x
        // 绘制的位置坐标y
        // 绘制的纹理中的u坐标
        // 绘制的纹理中的v坐标
        // 绘制的纹理的宽度
        // 绘制的纹理的高度
        // 绘制的width宽度
        // 绘制的height高度
        // 以上注释仅供参考, 搞不懂一点
        blit(matrixStack, bgX, bgY, 0, 0, bgW, bgH, bgW, bgH);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // 绘制背景
        renderBackground(matrixStack);

        // 绘制缩放背景纹理
        renderBackgroundTexture(matrixStack);

        // 渲染年份
        String yearTitle = DateUtils.getYearPart(currentDate) + "年";
        this.font.draw(matrixStack, yearTitle, bgX + calendarBackgroundConf.getTitleStartX() * this.scale, bgY + calendarBackgroundConf.getTitleStartY() * this.scale, 0xFFFFFF00);
        // 渲染月份
        String monthTitle = DateUtils.getMonthOfDate(currentDate) + "月";
        this.font.draw(matrixStack, monthTitle, bgX + calendarBackgroundConf.getSubTitleStartX() * this.scale, bgY + calendarBackgroundConf.getSubTitleStartY() * this.scale, 0xFFFFFF00);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        // 渲染所有格子
        for (CalendarCell cell : calendarCells) {
            cell.render(matrixStack, this.font, this.itemRenderer, mouseX, mouseY);
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
                    if (cell.status == ESignInStatus.NOT_SIGNED_IN.getCode()) {
                        player.sendMessage(new StringTextComponent("Successful sign-in : " + cell.day), player.getUUID());
                        // TODO 领取奖励
                        // ModNetworkHandler.INSTANCE.sendToServer(new ItemStackPacket(cell.itemStack));
                        // cell.itemStack.setCount(0);
                        cell.status = ESignInStatus.REWARDED.getCode();
                    } else if (cell.status == ESignInStatus.SIGNED_IN.getCode()) {
                        player.sendMessage(new StringTextComponent("Signed in: " + cell.day), player.getUUID());
                    } else if (cell.status == ESignInStatus.NO_ACTION.getCode()) {
                        player.sendMessage(new StringTextComponent("Cannot sign-in: " + cell.day), player.getUUID());
                    } else {
                        player.sendMessage(new StringTextComponent(ESignInStatus.fromCode(cell.status).getDescription() + ": " + cell.day), player.getUUID());
                    }
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
        // 在窗口大小变化时更新布局
        updateLayout();
        LOGGER.debug("{},{}", this.width, this.height);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * 重写keyPressed方法，处理键盘按键事件
     *
     * @param keyCode   按键的键码
     * @param scanCode  按键的扫描码
     * @param modifiers 按键时按下的修饰键（如Shift、Ctrl等）
     * @return boolean 表示是否消耗了该按键事件
     * <p>
     * 此方法主要监听特定的按键事件，当按下CALENDAR_KEY或E键时，触发onClose方法，执行一些关闭操作
     * 对于其他按键，则交由父类处理
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 当按键等于CALENDAR_KEY键的值或E键时，调用onClose方法，并返回true，表示该按键事件已被消耗
        if (keyCode == ClientEventHandler.CALENDAR_KEY.getKey().getValue() || keyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        } else {
            // 对于其他按键，交由父类处理，并返回父类的处理结果
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            currentDate = DateUtils.addMonth(currentDate, -1);
            updateLayout();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            currentDate = DateUtils.addMonth(currentDate, 1);
            updateLayout();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            currentDate = DateUtils.addYear(currentDate, -1);
            updateLayout();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            currentDate = DateUtils.addYear(currentDate, 1);
            updateLayout();
            return true;
        } else {
            // 对于其他按键，交由父类处理，并返回父类的处理结果
            return super.keyReleased(keyCode, scanCode, modifiers);
        }
    }
}
