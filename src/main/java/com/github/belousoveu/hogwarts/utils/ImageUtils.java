package com.github.belousoveu.hogwarts.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static byte[] getPreviewImage(MultipartFile file, int width, int height) throws IOException {

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int x = (originalWidth - width) / 2;
        int y = (originalHeight - height) / 2;

        BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

        BufferedImage previewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = previewImage.createGraphics();
        graphics2D.drawImage(croppedImage, 0, 0, width, height, null);
        graphics2D.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(previewImage, "jpg", outputStream);

        return outputStream.toByteArray();
    }
}