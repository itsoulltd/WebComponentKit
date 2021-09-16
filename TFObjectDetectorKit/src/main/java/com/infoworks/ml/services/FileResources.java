package com.infoworks.ml.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResources {

    private static Logger LOG = LoggerFactory.getLogger("FileResources");

    public File getFile(String fileName) throws URISyntaxException {
        /*String workingDir = System.getProperty("user.dir");
        LOG.info("==>" + workingDir + "/" + fileName);
        Path path = Paths.get(workingDir + "/" + fileName);*/
        String filepath = fileName;
        if (!fileName.trim().startsWith("/")){
            String workingDir = System.getProperty("user.dir");
            filepath = workingDir + "/" + fileName;
        }
        LOG.info("==>" + filepath);
        Path path = Paths.get(filepath);
        if (Files.exists(path) && !Files.isDirectory(path)){
            return path.toFile();
        }else{
            //File file = ResourceUtils.getFile("classpath:" + fileName);
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(fileName);
            if (resource == null) {
                throw new IllegalArgumentException("file not found! " + fileName);
            } else {
                return new File(resource.toURI());
            }
        }
    }

    public InputStream getFileFromResourceAsStream(String fileName) throws IOException {
        try {
            File file = getFile(fileName);
            InputStream inputStream = new FileInputStream(file);
            // the stream holding the file content
            if (inputStream == null) {
                throw new IllegalArgumentException("file not found! " + fileName);
            } else {
                return inputStream;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] readAllBytesOrExit(String filename) {
        try {
            InputStream ios = getFileFromResourceAsStream(filename);
            byte[] bites = new byte[ios.available()];
            ios.read(bites);
            return bites;
        } catch (IOException e) {
            System.err.println("Failed to read [" + filename + "]: " + e.getMessage());
            System.exit(1);
        }
        return new byte[0];
    }

    public byte[] readAllBytes(InputStream ios) {
        if (ios == null) return new byte[0];
        try {
            byte[] bites = new byte[ios.available()];
            ios.read(bites);
            return bites;
        } catch (IOException e) {
        }
        return new byte[0];
    }

}
