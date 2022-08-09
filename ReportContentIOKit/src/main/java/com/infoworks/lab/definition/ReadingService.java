package com.infoworks.lab.definition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface ReadingService {

    void readAsync(InputStream inputStream
            , Integer bufferSize
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException;

    void readAsync(File file
            , Integer bufferSize
            , Integer sheetAt
            , Integer beginIndex
            , Integer endIndex
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException;

    void read(InputStream inputStream
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException;

    void read(File file
            , Integer sheetAt
            , Integer startAt
            , Integer pageSize
            , Consumer<Map<Integer, List<String>>> consumer) throws IOException;

    Map<Integer, List<String>> read(InputStream inputStream, Integer sheetAt, Integer start, Integer end) throws IOException;

    Map<Integer, List<String>> read(File file, Integer sheetAt, Integer start, Integer end) throws IOException;
}
