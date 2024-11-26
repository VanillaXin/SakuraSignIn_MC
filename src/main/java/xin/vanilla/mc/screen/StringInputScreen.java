package xin.vanilla.mc.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import xin.vanilla.mc.screen.component.Text;
import xin.vanilla.mc.util.AbstractGuiUtils;
import xin.vanilla.mc.util.StringUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

public class StringInputScreen extends Screen {

    /**
     * 父级 Screen
     */
    private final Screen previousScreen;
    /**
     * 标题
     */
    private final Text titleText;
    /**
     * 提示
     */
    private final Text messageText;
    /**
     * 输入数据校验
     */
    private final String validator;
    private final Consumer<String> onDataReceived;  // 回调函数
    private TextFieldWidget inputField;  // 输入框


    public StringInputScreen(Screen callbackScreen, Text titleText, Text messageText, String validator, Consumer<String> onDataReceived) {
        super(new StringTextComponent("StringInputScreen"));
        this.previousScreen = callbackScreen;
        this.onDataReceived = onDataReceived;
        this.titleText = titleText;
        this.messageText = messageText;
        this.validator = validator;
    }

    @Override
    protected void init() {
        // 创建文本输入框
        inputField = new TextFieldWidget(this.font, this.width / 2 - 100, this.height / 2 - 20, 200, 20
                , AbstractGuiUtils.textToComponent(this.messageText));
        if (StringUtils.isNotNullOrEmpty(validator)) {
            inputField.setFilter(s -> s.matches(validator));
        }
        this.addButton(inputField);
        // 创建提交按钮
        this.addButton(new Button(this.width / 2 - 100, this.height / 2 + 20, 200, 20, new StringTextComponent("Submit"), button -> {
            // 获取输入的数据，并执行回调
            onDataReceived.accept(inputField.getValue());
            // 关闭当前屏幕并返回到调用者的 Screen
            Minecraft.getInstance().setScreen(previousScreen);
        }));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        // 绘制背景
        super.render(matrixStack, mouseX, mouseY, delta);
        // 绘制标题
        AbstractGuiUtils.drawString(titleText, this.width / 2.0f - 100, this.height / 2.0f - 30);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
