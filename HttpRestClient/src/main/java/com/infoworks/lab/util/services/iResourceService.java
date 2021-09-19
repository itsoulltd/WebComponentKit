package com.infoworks.lab.util.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.util.impl.ResourceService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface iResourceService {
    static iResourceService create(){
        return new ResourceService();
    }
    InputStream createStream(File file);
    String readAsString(String filename);
    String readAsString(InputStream ios);
    byte[] readAsBytes(InputStream ios);
    List<Map<String, Object>> readAsJsonObject(String json);
    <T> T readAsJsonObject(String json, TypeReference<T> typeReference);
    byte[] readImageAsBytes(BufferedImage img, iResourceService.Format format) throws IOException;
    String readImageAsBase64(BufferedImage img, iResourceService.Format format) throws IOException;
    BufferedImage readImageFromBase64(String content) throws IOException;
    BufferedImage readAsImage(InputStream ios, int imgType) throws IOException;
    BufferedImage createCopyFrom(Image originalImage, int scaledWidth, int scaledHeight, int imageType);

    enum Format{
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
}
