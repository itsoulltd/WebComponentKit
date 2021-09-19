package com.infoworks.lab.util.services;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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
    public void imageAsString() throws URISyntaxException, IOException {

        iResourceService manager = iResourceService.create();
        File imfFile = new File("data/final-architecture.png");
        InputStream ios = manager.createStream(imfFile);
        //
        BufferedImage bufferedImage = manager.readAsImage(ios, BufferedImage.TYPE_INT_RGB);
        String base64Image = manager.readImageAsBase64(bufferedImage, iResourceService.Format.PNG);
        System.out.println("Message: " + base64Image);
        //
        BufferedImage decryptedImg = manager.readImageFromBase64(base64Image);
        System.out.println("");
    }
}