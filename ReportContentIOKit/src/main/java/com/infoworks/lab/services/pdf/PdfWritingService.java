package com.infoworks.lab.services.pdf;


import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.definition.WritingService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfWritingService implements WritingService {

    private static Logger LOG = Logger.getLogger(PdfWritingService.class.getSimpleName());

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

        protected OutputStream outfile;

        public AsyncWriter(OutputStream outputStream) throws IOException {
            this.outfile = outputStream;
        }

        public AsyncWriter(String fileNameToWrite) throws IOException {
            this(new FileOutputStream(fileNameToWrite, true));
        }

        @Override
        public void close() throws Exception {
            if (outfile != null) {
                outfile.close();
                outfile = null;
            }
        }

        @Override
        public void write(String sheetName, Map<Integer, List<String>> data, boolean skipZeroIndex) {
            //TODO: Use iText Pdf writing api to implement pdf content writing:
        }

    }
}
