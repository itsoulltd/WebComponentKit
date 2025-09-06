package com.infoworks.lab.util.services;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class iResourceServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void readJson(){
        iResourceService manager = iResourceService.create();
        String json = manager.readAsString("data/rider-mock-data.json");
        System.out.println(json);

        List<Map<String, Object>> jObj = manager.readAsJsonObject(json);
        System.out.println(jObj.toString());
    }

    @Test
    public void imageAsString() throws IOException {

        iResourceService manager = iResourceService.create();
        InputStream ios = createFileInputStreamV2(manager, "data/final-architecture.png");
        //
        BufferedImage bufferedImage = manager.readAsImage(ios, BufferedImage.TYPE_INT_RGB);
        ios.close();
        String base64Image = manager.readImageAsBase64(bufferedImage, iResourceService.Format.PNG);
        System.out.println("Message: " + base64Image);
        //
        BufferedImage decryptedImg = manager.readImageFromBase64(base64Image);
        Assert.assertNotNull(decryptedImg);
    }

    private InputStream createFileInputStreamV2(iResourceService manager, String fileName) {
        File imfFile = new File(fileName);
        InputStream ios = manager.createStream(imfFile);
        return ios;
    }

    @Test
    public void imageAsStringWithAlternativeFileReading() throws IOException {

        InputStream ios = createFileInputStream("/data/final-architecture.png");
        Assert.assertTrue(ios.available() > 0);
        //
        iResourceService manager = iResourceService.create();
        BufferedImage bufferedImage = manager.readAsImage(ios, BufferedImage.TYPE_INT_RGB);
        ios.close();
        String base64Image = manager.readImageAsBase64(bufferedImage, iResourceService.Format.PNG);
        System.out.println("Message: " + base64Image);
        //
        BufferedImage decryptedImg = manager.readImageFromBase64(base64Image);
        Assert.assertNotNull(decryptedImg);
    }

    private InputStream createFileInputStream(String fileName) throws FileNotFoundException {
        Path path = Paths.get("src","test","resources", fileName);
        File imfFile = new File(path.toFile().getAbsolutePath());
        InputStream ios = new FileInputStream(imfFile);
        return ios;
    }
}