package com.infoworks.lab.services.csv;

import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.definition.WritingService;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvWritingService implements WritingService {

    private static Logger LOG = Logger.getLogger(CsvWritingService.class.getSimpleName());

    @Override
    public void write(OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception {
        AsyncWriter writer = new AsyncWriter(outputStream);
        writer.write(sheetName, data, false);
        writer.close();
    }

    public ContentWriter createWriter(String outFileName, boolean replace) {
        try {
            if(outFileName == null || outFileName.isEmpty()) return null;
            if (replace) removeIfExist(outFileName);
            return new AsyncWriter(outFileName);
        } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
        return null;
    }

    private boolean removeIfExist(String outFileName) {
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

        protected OutputStreamWriter writer;

        public AsyncWriter(OutputStream outputStream) throws IOException {
            this.writer = new OutputStreamWriter(outputStream);
        }

        public AsyncWriter(String fileNameToWrite) throws IOException {
            this.writer = new FileWriter(fileNameToWrite, true);
        }

        @Override
        public void close() throws Exception {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
            writer = null;
        }

        @Override
        public void write(String sheetName, Map<Integer, List<String>> rows, boolean skipZeroIndex) {
            if (rows.isEmpty()) return;
            if (skipZeroIndex) {
                Optional<Integer> maxIndex = rows.keySet().stream().min(Integer::compareTo);
                rows.remove(maxIndex.get());
            }
            rows.forEach((index, row) -> {
                try {
                    this.writer.write(String.join(",", row));
                    this.writer.write("\n");
                } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
            });
            try {
                writer.flush();
            } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage());}
        }
    }

}
