package xin.vanilla.mc.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xin.vanilla.mc.SakuraSignIn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class TextureUtils {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation loadCustomTexture(String textureName) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        ResourceLocation customTextureLocation = new ResourceLocation(SakuraSignIn.MODID, textureName);
        if (!TextureUtils.isTextureAvailable(customTextureLocation)) {
            // 指定外部路径的纹理文件
            File textureFile = new File(Minecraft.getInstance().gameDirectory, textureName);
            // 检查文件是否存在
            if (!textureFile.exists()) {
                LOGGER.warn("Texture file not found: {}", textureFile.getAbsolutePath());
                return null;
            }

            try (InputStream inputStream = Files.newInputStream(textureFile.toPath())) {
                // 直接从InputStream创建NativeImage
                net.minecraft.client.renderer.texture.NativeImage nativeImage = net.minecraft.client.renderer.texture.NativeImage.read(inputStream);
                // 创建DynamicTexture并注册到TextureManager
                DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                textureManager.register(customTextureLocation, dynamicTexture);
                return customTextureLocation;
            } catch (IOException e) {
                LOGGER.warn("Failed to load texture: {}", textureFile.getAbsolutePath());
                LOGGER.error(e);
                return new ResourceLocation(SakuraSignIn.MODID, "textures/gui/sign_in_arrow_tap.png");
            }
        }
        return customTextureLocation;
    }

    public static boolean isTextureAvailable(ResourceLocation resourceLocation) {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        try {
            textureManager.bind(resourceLocation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
