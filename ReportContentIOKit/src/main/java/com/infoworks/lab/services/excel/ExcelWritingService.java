package com.infoworks.lab.services.excel;

import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.definition.WritingService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelWritingService implements WritingService {

    private static Logger LOG = Logger.getLogger(ExcelWritingService.class.getSimpleName());

    public void write(boolean xssf, OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {
        AsyncWriter writer = new AsyncWriter(xssf, outputStream);
        writer.write(sheetName, data, false);
        writer.close();
    }

    public void write(OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {
        AsyncWriter writer = new AsyncStreamWriter(100, outputStream);
        writer.write(sheetName, data, false);
        writer.close();
    }

    public ContentWriter createWriter(boolean xssf, String outFileName, boolean replace) {
        try {
            if(outFileName == null || outFileName.isEmpty()) return null;
            if (replace) removeIfExist(outFileName);
            return new AsyncWriter(xssf, outFileName);
        } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
        return null;
    }

    public ContentWriter createAsyncWriter(int rowSize, String outFileName, boolean replace) {
        try {
            if(outFileName == null || outFileName.isEmpty()) return null;
            if (replace) removeIfExist(outFileName);
            return new AsyncStreamWriter(rowSize, outFileName);
        } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
        return null;
    }

    private boolean removeIfExist(String outFileName){
        try {
            File outFile = new File(outFileName);
            if (outFile.exists() && outFile.isFile()){
                return outFile.delete();
            }
        } catch (Exception e) {LOG.log(Level.WARNING, e.getMessage());}
        return false;
    }

    //AsyncWriter Start:
    public static class AsyncWriter implements ContentWriter<List<String>> {

        protected Workbook workbook;
        protected OutputStream outfile;

        public AsyncWriter() {}

        public AsyncWriter(boolean xssf, OutputStream outputStream) throws IOException {
            this.workbook = WorkbookFactory.create(xssf);
            this.outfile = outputStream;
        }

        public AsyncWriter(boolean xssf, String fileNameToWrite) throws IOException {
            this(xssf, new FileOutputStream(fileNameToWrite, true));
        }

        @Override
        public void close() throws Exception {
            if (workbook != null) {
                workbook.write(outfile);
                if (outfile != null) {
                    outfile.close();
                    outfile = null;
                }
                if (workbook instanceof SXSSFWorkbook){
                    ((SXSSFWorkbook) workbook).dispose();
                }
                workbook.close();
                workbook = null;
            }
        }

        public void write(String sheetName, Map<Integer, List<String>> data, boolean skipZeroIndex) {
            //DoTheMath:
            Sheet sheet = workbook.getSheet(sheetName);
            if(sheet == null) sheet = workbook.createSheet(sheetName);
            int rowIndex = 0;
            for (Map.Entry<Integer, List<String>> entry : data.entrySet()){
                Row row = sheet.createRow((skipZeroIndex) ? entry.getKey() : rowIndex);
                int cellIndex = 0;
                for (String cellVal : entry.getValue()) {
                    Cell cell = row.createCell(cellIndex);
                    cell.setCellValue(cellVal);
                    if(sheet instanceof XSSFSheet)
                        sheet.autoSizeColumn(cellIndex);
                    cellIndex++;
                }
                rowIndex++;
            }
        }

    }
    //AsyncWriter Done:

    public static class AsyncStreamWriter extends AsyncWriter{

        public AsyncStreamWriter(int rowSize, OutputStream outputStream){
            if (rowSize <= 0) rowSize = 100;
            this.workbook = new SXSSFWorkbook(rowSize);
            this.outfile = outputStream;
        }

        public AsyncStreamWriter(int rowSize, String fileNameToWrite) throws IOException {
            this(rowSize, new FileOutputStream(fileNameToWrite, true));
        }

    }

}
