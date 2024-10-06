package xin.vanilla.mc.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import xin.vanilla.mc.SakuraSignIn;
import xin.vanilla.mc.network.ItemStackPacket;
import xin.vanilla.mc.network.ModNetworkHandler;
import xin.vanilla.mc.util.StringUtils;

@OnlyIn(Dist.CLIENT)
public class CheckInScreen extends Screen {

    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(SakuraSignIn.MODID, "textures/gui/checkin_background.png");
    private TextFieldWidget inputField;
    private Button submitButton;

    public CheckInScreen() {
        super(new StringTextComponent("check in"));
    }

    /**
     * 初始化方法
     * 该方法主要用于设置输入字段和提交按钮的初始属性和位置
     */
    @Override
    protected void init() {
        // 计算屏幕中心点的坐标，用于后续布局输入框和按钮
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 创建输入字段实例，并设置其初始属性：字体、位置、大小以及提示文本
        this.inputField = new TextFieldWidget(this.font, centerX - 100, centerY - 50, 200, 20, new StringTextComponent("输入你的名字"));
        // 设置输入字段的最大长度为50个字符
        this.inputField.setMaxLength(50);
        // 设置输入字段为焦点状态，以便用户可以直接输入
        this.inputField.setFocus(true);
        // 将输入字段添加为当前类的子组件
        this.children.add(this.inputField);

        // 创建提交按钮实例，并设置其初始属性：位置、大小、文本以及点击事件处理函数
        this.submitButton = new Button(centerX - 50, centerY + 20, 100, 20, new StringTextComponent("提交"), button -> {
            onSubmit();
        });
        // 将提交按钮添加为当前类的按钮
        this.addButton(this.submitButton);
    }

    /**
     * 提交表单的方法
     * 此方法用于处理用户在游戏中的提交操作，它尝试将用户输入的文本转换为物品堆叠并将其添加到玩家的库存中
     * 如果库存已满，则将物品作为掉落物实体添加到世界上
     */
    private void onSubmit() {
        // 获取用户输入的文本
        String inputText = this.inputField.getValue();

        // 确保Minecraft实例不为空
        if (this.minecraft != null) {
            // 获取当前玩家实体
            ClientPlayerEntity player = this.minecraft.player;

            // 确保玩家实体不为空
            if (player != null) {
                // 根据用户输入的文本创建一个物品堆叠，假设输入的是数字，表示物品的数量
                ItemStack itemStack = new ItemStack(Items.APPLE, StringUtils.toInt(inputText));

                ModNetworkHandler.INSTANCE.sendToServer(new ItemStackPacket(itemStack));
            }
        }

        // 调用关闭方法，通常用于关闭当前界面
        this.onClose();
    }

    /**
     * 重写render方法，用于渲染屏幕上的图像
     *
     * @param matrixStack  模型视图矩阵堆栈，用于渲染的坐标变换
     * @param mouseX       鼠标的X坐标，用于响应鼠标事件
     * @param mouseY       鼠标的Y坐标，用于响应鼠标事件
     * @param partialTicks 帧插值因子，用于平滑动画效果
     */
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // 渲染背景
        this.renderBackground(matrixStack);

        // 绑定背景纹理，以便在屏幕上显示
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND_TEXTURE);
        // 从纹理中复制图像到屏幕上，位置和大小根据屏幕的宽高进行调整
        blit(matrixStack, this.width / 2 - 128, this.height / 2 - 128, 0, 0, 256, 256, 256, 256);

        // 调用父类的render方法，进行其他渲染操作
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // 渲染输入框，处理鼠标交互
        this.inputField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    /**
     * 重写isPauseScreen方法
     * 该方法用于指示当前屏幕是否应该暂停游戏
     *
     * @return boolean 表示是否应该暂停游戏的布尔值
     * 返回false表示不希望暂停游戏
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
