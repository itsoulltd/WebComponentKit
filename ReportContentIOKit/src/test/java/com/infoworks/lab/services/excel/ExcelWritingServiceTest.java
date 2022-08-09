package com.infoworks.lab.services.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.util.services.iResourceService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ExcelWritingServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void readJsonAndWriteToExcel() throws Exception {
        //Reading From Resources:
        iResourceService manager = iResourceService.create();
        File imfFile = new File("rider-mock-data.json");
        InputStream ios = manager.createStream(imfFile);
        String json = manager.readAsString(ios);
        ios.close();
        Map<String, Object> data = manager.readAsJsonObject(json, new TypeReference<Map<String, Object>>() {});

        //Prepare Report-Data:
        List<Map<String, Object>> riders = (List<Map<String, Object>>) data.get("findRiders");
        Map<Integer, List<String>> inferable = new HashMap();
        AtomicInteger count = new AtomicInteger(0);
        //Adding Header
        inferable.put(count.getAndIncrement(), Arrays.asList("Name", "Geo-Hash", "Email", "Age", "Gender"));
        riders.forEach(entry -> {
            List<String> loj = entry
                    .values().stream()
                    .filter(val -> val != null)
                    .map(val -> val.toString())
                    .collect(Collectors.toList());
            inferable.put(count.getAndIncrement(), loj);
        });

        //Excel Writer:
        ExcelWritingService writingService = new ExcelWritingService();
        //ContentWriter writer = writingService.createWriter(true, "target/rider-report-1.xlsx", true);
        ContentWriter writer = writingService.createAsyncWriter(100, "target/rider-report-2.xlsx", true);
        writer.write("output", inferable, true);
        writer.close();
        Assert.assertTrue(true);
    }
}