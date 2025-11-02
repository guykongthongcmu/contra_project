package se233.contra_project.ui;

import java.awt.*;
import java.io.File;
import java.io.InputStream;

public class FontManager {
    private static Font customFont;
    private static javafx.scene.text.Font customFxFont;
    private static String fxFontFamily;

    // โหลดฟอนต์แบบมีขนาด
    public static Font getFont(float size) {
        ensureFontLoaded(); // ✅ ตรวจให้แน่ใจว่าโหลดแล้ว
        return customFont.deriveFont(size);
    }

    // โหลดฟอนต์ครั้งแรกถ้ายังไม่ได้โหลด
    private static void ensureFontLoaded() {
        if (customFont != null) return;

        try {
            File fontFile = new File("C:\\Users\\CAMT-STD\\Desktop\\se233\\contra_project\\src\\main\\resources\\se233\\contra_project\\font\\Contra.ttf");

            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);

        } catch (Exception e) {
            customFont = new Font("Arial", Font.BOLD, 24);
        }
    }

    // สำหรับเรียกใช้โดยไม่ต้องระบุขนาด (เช่นใน paint)
    public static Font getContraFont(float size) {
        return getFont(size);
    }

    public static javafx.scene.text.Font getFxFont(double size) {
        ensureFxFontLoaded();
        if (fxFontFamily != null) {
            return javafx.scene.text.Font.font(fxFontFamily, size);
        }
        return customFxFont != null
                ? javafx.scene.text.Font.font(customFxFont.getFamily(), size)
                : javafx.scene.text.Font.font("System", size);
    }

    private static void ensureFxFontLoaded() {
        if (fxFontFamily != null) {
            return;
        }

        try (InputStream stream = FontManager.class.getResourceAsStream("/se233/contra_project/font/Contra.ttf")) {
            if (stream != null) {
                customFxFont = javafx.scene.text.Font.loadFont(stream, 24);
                if (customFxFont != null) {
                    fxFontFamily = customFxFont.getFamily();
                    return;
                }
            }
        } catch (Exception ignored) {
            // Fallback handled below
        }

        customFxFont = javafx.scene.text.Font.font("Arial", 24);
        fxFontFamily = customFxFont.getFamily();
    }
}
