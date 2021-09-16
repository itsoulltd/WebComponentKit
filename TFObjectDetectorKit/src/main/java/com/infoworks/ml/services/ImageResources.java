package com.infoworks.ml.services;

import com.infoworks.ml.domain.detectors.Recognition;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageResources {

    public enum Format{
        JPEG("jpeg"),
        PNG("PNG");

        private String value;

        Format(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "format{" +
                    "value='" + value + '\'' +
                    '}';
        }

        public String value(){
            return value;
        }
    }

    public BufferedImage readImage(InputStream ios) throws IOException {
        Image img = ImageIO.read(ios);
        if (img instanceof BufferedImage)
            return (BufferedImage) img;
        else{
            int imageType = BufferedImage.TYPE_INT_RGB; //BufferedImage.TYPE_INT_ARGB;
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            BufferedImage imgBuffered = createResizedCopy(img, width, height, imageType);
            return imgBuffered;
        }
    }

    public BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, int imageType) {
        //
        if (scaledWidth == originalImage.getWidth(null)
                && scaledHeight == originalImage.getHeight(null))
            return new BufferedImage(scaledWidth, scaledHeight, imageType);
        //
        if (scaledWidth <= 0)
            scaledWidth = originalImage.getWidth(null);
        if (scaledHeight <= 0)
            scaledHeight = originalImage.getHeight(null);
        //
        BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = bufferedImage.createGraphics();
        if (imageType == BufferedImage.TYPE_INT_RGB) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return bufferedImage;
    }

    public BufferedImage createAnnotatedImage(BufferedImage image, Recognition recognition) {
        return createAnnotatedImage(image, recognition, true);
    }

    public BufferedImage createAnnotatedImage(BufferedImage image, Recognition recognition, boolean skipName) {
        //
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = img.createGraphics();
        g2D.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        //Draw rectangle & text.
        if (recognition != null && recognition.getLocation() != null){
            Rectangle rectangle = new Rectangle(
                    (int) recognition.getLocation().getMinX(),
                    (int) recognition.getLocation().getMinY(),
                    (int) (recognition.getLocation().getWidth() + 0.5),
                    (int) (recognition.getLocation().getHeight() + 0.5));
            //Draw Rect:
            g2D.setColor (Color.GREEN);
            g2D.setStroke(new BasicStroke((image.getWidth() * 5) / 1000));
            g2D.draw(rectangle);
            //Draw Text
            if (!skipName){
                Point center = new Point(img.getWidth() / 2, img.getHeight() / 2);
                printText(center
                        , rectangle
                        , g2D
                        , recognition.getTitle()
                        , Color.BLACK
                        , createTextFont(Font.MONOSPACED, Font.PLAIN, 9));
            }
        }
        //
        g2D.dispose();
        return img;
    }

    private Font createTextFont(String name, int style, int size){
        Font font = new Font(name, style, size);
        Map<TextAttribute, Object> attributes = new HashMap();
        attributes.put(TextAttribute.TRACKING, 0.13);
        return font.deriveFont(attributes);
    }

    private void printText(Point center, Rectangle rectangle, Graphics2D g2D, String title, Color color, Font font) {
        if (g2D == null) return;
        g2D.setFont(font);
        FontMetrics fm = g2D.getFontMetrics();
        //Calculate the center of Image then decide the beginning
        // of text should start from x of rect OR (x - (width-of-rect - with-of-fm.stringWidth(s))) of rect.
        // Draw Text Background:
        //if the rect is on the left side of the center:
        int x = (center.x >= rectangle.x)
                ? rectangle.x
                : (rectangle.x - (fm.stringWidth(title) - rectangle.width));
        //
        int y = (center.y <= rectangle.y)
                ? rectangle.y - fm.getHeight()
                : rectangle.y + rectangle.height + fm.getHeight() ;
        //
        g2D.setPaint(Color.ORANGE);
        Rectangle rectangle1 = new Rectangle(x, y - 5, fm.stringWidth(title), fm.getHeight());
        g2D.fill(rectangle1);
        // Draw Text:
        g2D.setPaint(color);
        g2D.drawString(title, x, y + 4);
    }

    public void saveAnnotatedImage(BufferedImage image, Recognition recognition, Format format, String outputImageFilePath) throws IOException {
        BufferedImage img = createAnnotatedImage(image, recognition);
        ImageIO.write(img, format.value(), new File(outputImageFilePath));
    }

    public byte[] getBytes(BufferedImage img, Format format) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, format.value(), bos);
        return bos.toByteArray();
    }
}
