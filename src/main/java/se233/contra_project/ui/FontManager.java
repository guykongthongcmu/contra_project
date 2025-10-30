package se233.contra_project.ui;

import java.awt.*;
import java.io.File;

public class FontManager {
    private static Font customFont;

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
}
