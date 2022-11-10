package com.infoworks.lab.services.csv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.util.services.iResourceService;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class CsvWritingServiceTest {

    @Test
    public void csvWriter() throws Exception {
        //Reading From Resources:
        iResourceService manager = iResourceService.create();
        File imfFile = new File("sample-data-rows.json");
        InputStream ios = manager.createStream(imfFile);
        String json = manager.readAsString(ios);
        ios.close();
        Map<Integer, List<String>> data = manager.readAsJsonObject(json, new TypeReference<Map<Integer, List<String>>>() {});
        //
        //CSV Writer:
        CsvWritingService writingService = new CsvWritingService();
        ContentWriter writer = writingService.createWriter("target/rider-report-1.csv", true);
        //writer.write(data, true);
        writer.write(data);
        writer.close();
        Assert.assertTrue(true);
    }

}