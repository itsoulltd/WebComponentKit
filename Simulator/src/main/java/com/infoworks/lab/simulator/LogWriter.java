package com.infoworks.lab.simulator;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class LogWriter {

    protected Logger LOG;

    public LogWriter(Class type) {
        this(type, type.getSimpleName() + ".log");
    }

    public LogWriter(Class type, String fileName) {
        LOG = Logger.getLogger(type.getName());
        this.fileName = fileName;
        this.output = new File(fileName);
    }

    protected String fileName;
    protected File output;
    protected ExecutorService service = Executors.newSingleThreadExecutor();

    public void createIfNotExist(boolean deleteOld){
        if (output == null) return;
        if (deleteOld && output.exists()){
            output.delete();
        }
        try {
            if (output.exists() == false) {
                if (!output.getParentFile().exists())
                    output.getParentFile().mkdirs();
                output.createNewFile();
            }
        } catch (IOException e) {
            output = null;
        }
    }

    public void write(String message){
        LOG.info(message);
        if (output == null || output.exists() == false) return;
        service.submit(() -> {
            try (FileWriter writer = new FileWriter(output, true);
                 BufferedWriter bfwriter = new BufferedWriter(writer);
                 PrintWriter printer = new PrintWriter(bfwriter)) {
                printer.println(message);
                LOG.info("Successfully Copied JSON Object to File...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
