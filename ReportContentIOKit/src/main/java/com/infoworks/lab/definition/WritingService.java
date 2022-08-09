package com.infoworks.lab.definition;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface WritingService {
    void write(OutputStream outputStream, String sheetName, Map<Integer, List<String>> data) throws Exception;
}
