package com.infoworks.lab.util.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.util.services.iResourceService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;

public class ResourceService implements iResourceService {

    private static Logger LOG = Logger.getLogger(ResourceService.class.getSimpleName());

    public InputStream createStream(File file) {
        ClassLoader loader = getClass().getClassLoader();
        InputStream in = loader.getResourceAsStream(file.getPath());
        return in;
    }

    public String readAsString(String filename) {
        InputStream is = createStream(new File(filename));
        return readAsString(is);
    }

    public String readAsString(InputStream in) {
        StringBuffer buffer = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line);
            }
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        return buffer.toString();
    }

    public byte[] readAsBytes(InputStream ios) {
        if (ios == null) return new byte[0];
        try {
            byte[] bites = new byte[ios.available()];
            ios.read(bites);
            return bites;
        } catch (IOException e) {
        }
        return new byte[0];
    }

    public List<Map<String, Object>> readAsJsonObject(String json) {
        if (Message.isValidJson(json)){
            if (json.trim().startsWith("{")){
                try {
                    ObjectMapper objectMapper = Message.getJsonSerializer();
                    Map res = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                    return Arrays.asList(res);
                } catch (IOException e) {}
            }else{
                try {
                    ObjectMapper objectMapper = Message.getJsonSerializer();
                    List res = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
                    return res;
                } catch (IOException e) {}
            }
        }
        return new ArrayList<>();
    }

    public <T> T readAsJsonObject(String json, TypeReference<T> typeReference) {
        if (Message.isValidJson(json)){
            try {
                ObjectMapper objectMapper = Message.getJsonSerializer();
                T res = objectMapper.readValue(json, typeReference);
                return res;
            } catch (IOException e) {}
        }
        return null;
    }

    @Override
    public <T> T readAsJsonObject(String json, Class<T> classReference) {
        if (Message.isValidJson(json)){
            try {
                ObjectMapper objectMapper = Message.getJsonSerializer();
                T res = objectMapper.readValue(json, classReference);
                return res;
            } catch (IOException e) {}
        }
        return null;
    }

    public byte[] readImageAsBytes(BufferedImage img, Format format) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(img, format.value(), bos);
        byte[] bytes = bos.toByteArray();
        bos.close();
        return bytes;
    }

    public String readImageAsBase64(BufferedImage img, Format format) throws IOException {
        byte[] bytes = readImageAsBytes(img, format);
        String encoded = new String(Base64.getEncoder().encode(bytes), "UTF-8");
        return encoded;
    }

    public BufferedImage readImageFromBase64(String content) throws IOException {
        byte[] imgBites = Base64.getDecoder().decode(content);
        ByteArrayInputStream bios = new ByteArrayInputStream(imgBites);
        BufferedImage img = readAsImage(bios, BufferedImage.TYPE_INT_RGB);
        bios.close();
        return img;
    }

    /**
     * @param ios
     * @param imgType e.g. BufferedImage.TYPE_INT_RGB (<-by default when set to -1)
     *                , BufferedImage.TYPE_INT_ARGB
     *                , BufferedImage.TYPE_INT_ARGB_PRE
     *                etc ...
     * @return
     * @throws IOException
     */
    public BufferedImage readAsImage(InputStream ios, int imgType) throws IOException {
        Image img = ImageIO.read(ios);
        if (img instanceof BufferedImage)
            return (BufferedImage) img;
        else{
            int imageType = (imgType < 0) ? BufferedImage.TYPE_INT_RGB : imgType;
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            BufferedImage imgBuffered = createCopyFrom(img, width, height, imageType);
            return imgBuffered;
        }
    }

    public BufferedImage createCopyFrom(Image originalImage, int scaledWidth, int scaledHeight, int imageType) {
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

}
