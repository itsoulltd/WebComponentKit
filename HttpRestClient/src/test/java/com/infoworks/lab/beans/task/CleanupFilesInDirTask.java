package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CleanupFilesInDirTask extends ExecutableTask<Message, Response> {

    private static Logger LOG = Logger.getLogger(CleanupFilesInDirTask.class.getSimpleName());

    public CleanupFilesInDirTask(SearchQuery query) {
        getMessage().setEvent(query);
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        if (message == null) message = getMessage();
        SearchQuery query = (SearchQuery) message.getEvent(SearchQuery.class);
        if (query != null) {
            query.getProperties()
                    .stream()
                    .filter(qp -> qp.getValue() != null && !qp.getValue().isEmpty())
                    .forEach(qp -> {
                        String dirPath = qp.getValue();
                        File dirFile = Paths.get(dirPath).toFile();
                        File[] results = dirFile.listFiles();
                        if (results == null) return; //Means Continue:
                        List<File> allFiles = new ArrayList<>();
                        allFiles.addAll(Arrays.stream(results)
                                        .filter(File::isFile)
                                        .collect(Collectors.toList()));
                        allFiles.addAll(Arrays.stream(results)
                                .filter(File::isDirectory)
                                .flatMap(inDir -> {
                                    File[] files = inDir.listFiles();
                                    return (files != null) ? Arrays.stream(files) : null;
                                })
                                .collect(Collectors.toList()));
                        //Start CleanUp:
                        allFiles.forEach(file -> {
                            String fileName = file.getName();
                            LOG.info(String.format("FileName: %s IS DELETED: %s"
                                    , fileName
                                    , (file.delete() ? "Yes" : "No")));
                        });
                    });
        }
        return new Response().setStatus(200).setMessage("Cleanup Successful.");
    }

}
