package com.infoworks.lab.services.excel;

import com.infoworks.lab.definition.ReadingService;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelReadingService implements ReadingService {

    private static Logger LOG = Logger.getLogger(ExcelReadingService.class.getSimpleName());

    public void readAsync(InputStream inputStream
            , Integer bufferSize
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(pageSize)
                .bufferSize(bufferSize)
                .open(inputStream);
        configureWorkbook(workbook);
        readBuffered(workbook, sheetAt, beginIndex, endIndex, pageSize, consumer);
        workbook.close();
    }

    public void readAsync(File file
            , Integer bufferSize
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(pageSize)
                .bufferSize(bufferSize)
                .open(file);
        configureWorkbook(workbook);
        readBuffered(workbook, sheetAt, beginIndex, endIndex, pageSize, consumer);
        workbook.close();
    }

    /**
     *
     * @param workbook
     * @param sheetAt
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex the ending index, exclusive.
     * @param pageSize
     * @param consumer
     * @throws IOException
     */
    private void readBuffered(Workbook workbook
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Sheet sheet = workbook.getSheetAt(sheetAt);
        int maxCount = sheet.getLastRowNum() + 1;
        pageSize = (pageSize > maxCount) ? maxCount : pageSize;
        if (endIndex <= 0 || endIndex == Integer.MAX_VALUE) endIndex = maxCount;
        //
        int idx = -1;
        Map<Integer, List<String>> data = new HashMap<>();
        for (Row row : sheet){
            if (++idx < beginIndex) {continue;}
            if (idx >= endIndex) {break;}
            //
            data.put(idx, new ArrayList<>());
            for (Cell cell : row){
                addInto(data, idx, cell);
            }
            if (consumer != null && data.size() == pageSize ){
                Map xData = new HashMap(data);
                data.clear();
                consumer.accept(xData);
            }
        }
        //left-over
        if (consumer != null && data.size() > 0 ){
            Map xData = new HashMap(data);
            data.clear();
            consumer.accept(xData);
        }
    }

    public void read(InputStream inputStream
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = WorkbookFactory.create(inputStream);
        configureWorkbook(workbook);
        readAsync(workbook, sheetAt, startAt, pageSize, consumer);
        workbook.close();
    }

    public void read(File file
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Workbook workbook = WorkbookFactory.create(file);
        configureWorkbook(workbook);
        readAsync(workbook, sheetAt, startAt, pageSize, consumer);
        workbook.close();
    }

    private void readAsync(Workbook workbook
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException {
        //
        Sheet sheet = workbook.getSheetAt(sheetAt);
        int maxCount = sheet.getLastRowNum() + 1;
        int loopCount = (pageSize == maxCount) ? 1 : (maxCount / pageSize) + 1;
        pageSize = (pageSize > maxCount) ? maxCount : pageSize;
        int index = 0;
        int start = (startAt < 0 || startAt >= maxCount) ? 0 : startAt;
        while (index < loopCount){
            int end = start + pageSize;
            if (end >= maxCount) end = maxCount;
            Map res = parseContent(workbook, sheetAt, start, end);
            if (consumer != null && res.size() > 0){
                consumer.accept(res);
            }
            //
            start += pageSize;
            index++;
        }
    }

    public Map<Integer, List<String>> read(InputStream inputStream, Integer sheetAt, Integer start, Integer end) throws IOException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        configureWorkbook(workbook);
        Map res = parseContent(workbook, sheetAt, start, end);
        workbook.close();
        return res;
    }

    public Map<Integer, List<String>> readXls(InputStream inputStream, Integer sheetAt, Integer start, Integer end) throws IOException {
        Workbook workbook = new HSSFWorkbook(inputStream);
        configureWorkbook(workbook);
        Map res = parseContent(workbook, sheetAt, start, end);
        workbook.close();
        return res;
    }

    public Map<Integer, List<String>> read(File file, Integer sheetAt, Integer start, Integer end) throws IOException {
        Workbook workbook = WorkbookFactory.create(file);
        configureWorkbook(workbook);
        Map res = parseContent(workbook, sheetAt, start, end);
        workbook.close();
        return res;
    }

    private void configureWorkbook(Workbook workbook) {
        if (workbook != null){
            try {
                //Add All kind of setting for workbook:
                workbook.setMissingCellPolicy(Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            }catch (UnsupportedOperationException e){
                LOG.log(Level.WARNING, e.getMessage());
            }catch (Exception e){
                LOG.log(Level.WARNING, e.getMessage());
            }
        }
    }

    private Map<Integer, List<String>> parseContent(Workbook workbook, Integer sheetAt, Integer start, Integer end) throws IOException {
        //DoTheMath:
        Sheet sheet = workbook.getSheetAt(sheetAt);
        Map<Integer, List<String>> data = new HashMap<>();
        //
        if (end <= 0 || end == Integer.MAX_VALUE){
            end = sheet.getLastRowNum() + 1;
        }
        int idx = (start < 0) ? 0 : start;
        while (idx < end) {
            data.put(idx, new ArrayList<>());
            for (Cell cell : sheet.getRow(idx)) {
                addInto(data, idx, cell);
            }
            idx++;
        }
        return data;
    }

    private void addInto(Map<Integer, List<String>> data, int idx, Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                data.get(idx).add(cell.getRichStringCellValue().getString());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    data.get(idx).add(cell.getDateCellValue() + "");
                } else {
                    data.get(idx).add(NumberToTextConverter.toText(cell.getNumericCellValue()));
                }
                break;
            case BOOLEAN:
                data.get(idx).add(cell.getBooleanCellValue() + "");
                break;
            case FORMULA:
                data.get(idx).add(cell.getStringCellValue() + "");
                break;
            default:
                data.get(idx).add(" ");
        }
    }

}
