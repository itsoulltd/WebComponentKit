package com.infoworks.lab.services.excel;

import com.infoworks.lab.util.services.iResourceService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExcelReadingServiceTest {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void readFromResourceV2() {
        iResourceService manager = iResourceService.create();
        InputStream ios = createFileInputStreamV2(manager, "/Download/file_example_XLSX_50.xlsx");
        //Assert.assertTrue(ios != null);
        String message = (ios == null) ? "File Not Found!" : "File Exist!";
        System.out.println(message);
    }

    private InputStream createFileInputStreamV2(iResourceService manager, String fileName) {
        File imfFile = new File(fileName);
        InputStream ios = manager.createStream(imfFile);
        return ios;
    }

    @Test
    public void readFromResourceV1() throws FileNotFoundException {
        InputStream ios = createFileInputStream("/Download/file_example_XLSX_50.xlsx");
        //Assert.assertTrue(ios != null);
        String message = (ios == null) ? "File Not Found!" : "File Exist!";
        System.out.println(message);
    }

    private InputStream createFileInputStream(String fileName) throws FileNotFoundException {
        Path path = Paths.get("src","test","resources", fileName);
        File imfFile = new File(path.toFile().getAbsolutePath());
        InputStream ios = new FileInputStream(imfFile);
        return ios;
    }

    @Test
    public void readFileXlsx() throws IOException {
        //Read src/test/resources/Download/file_example_XLSX_50.xls
        InputStream ios = createFileInputStream("/Download/file_example_XLSX_50.xlsx");
        Assert.assertTrue(ios != null);
        //
        ExcelReadingService excelReadService = new ExcelReadingService();
        Map<Integer, List<String>> rows = excelReadService.read(ios, 0, 0, Integer.MAX_VALUE);
        rows.forEach((index, row) -> {
            System.out.println("Index: " + index);
            System.out.println("Row: " + String.join("; ", row));
        });
        //
    }

    @Test
    public void readFileXlsxV2() throws IOException {
        //Read src/test/resources/Download/file_example_XLSX_50.xls
        InputStream ios = createFileInputStream("/Download/file_example_XLSX_50.xlsx");
        Assert.assertTrue(ios != null);
        //
        ExcelReadingService excelReadService = new ExcelReadingService();
        Map<Integer, List<String>> rows = excelReadService.read(ios, 0, 0, 15);
        rows.forEach((index, row) -> {
            System.out.println("Index: " + index);
            System.out.println("Row: " + String.join("; ", row));
        });
        //
    }

    @Test(expected = NullPointerException.class)
    public void readFileXlsxFailed() throws IOException {
        //Read src/test/resources/Download/file_example_XLSX_50.xls
        InputStream ios = createFileInputStream("/Download/file_example_XLSX_50.xlsx");
        Assert.assertTrue(ios != null);
        //Exceeding Row-Length > (50 + 1)
        ExcelReadingService excelReadService = new ExcelReadingService();
        Map<Integer, List<String>> rows = excelReadService.read(ios, 0, 0, 52);
        rows.forEach((index, row) -> {
            System.out.println("Index: " + index);
            System.out.println("Row: " + String.join("; ", row));
        });
        //
    }

    @Test
    public void readFileXls() throws IOException {
        //Read src/test/resources/Download/file_example_XLS_10.xls
        InputStream ios = createFileInputStream("/Download/file_example_XLS_10.xls");
        Assert.assertTrue(ios != null);
        //
        ExcelReadingService excelReadService = new ExcelReadingService();
        Map<Integer, List<String>> rows = excelReadService.read(ios, 0, 0, Integer.MAX_VALUE);
        rows.forEach((index, row) -> {
            System.out.println("Index: " + index);
            System.out.println("Row: " + String.join("; ", row));
        });
        //
    }

    @Test(expected = NullPointerException.class)
    public void readFileXlsFailed() throws IOException {
        //Read src/test/resources/Download/file_example_XLS_10.xls
        InputStream ios = createFileInputStream("/Download/file_example_XLS_10.xls");
        Assert.assertTrue(ios != null);
        //Exceeding Row-Length > (50 + 1) //FIXME: file has empty rows
        ExcelReadingService excelReadService = new ExcelReadingService();
        Map<Integer, List<String>> rows = excelReadService.read(ios, 0, 0, 55);
        rows.forEach((index, row) -> {
            System.out.println("Index: " + index);
            System.out.println("Row: " + String.join("; ", row));
        });
        //
    }
}