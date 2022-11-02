package com.infoworks.lab.beans.task;

import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DirectoryCleanupTask extends ExecutableTask<Message, Response> {

    private static Logger LOG = Logger.getLogger(DirectoryCleanupTask.class.getSimpleName());

    public DirectoryCleanupTask(String dirname) {
        SearchQuery query = Pagination.createQuery(SearchQuery.class, 10, SortOrder.ASC);
        query.add("dirname").isEqualTo(dirname);
        getMessage().setEvent(query);
    }

    public DirectoryCleanupTask(SearchQuery query) {
        getMessage().setEvent(query);
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        if (message == null) message = getMessage();
        SearchQuery query = (SearchQuery) message.getEvent(SearchQuery.class);
        if (query != null) {
            try {
                String dirname = query.get("dirname", String.class);
                List<Path> res = findChildren(Paths.get(dirname), new ArrayList<>(), 3);
                deleteFiles(res, new Date());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return new Response().setStatus(200).setMessage("Cleanup Successful.");
    }

    /**
     * Tail-recursion
     * @param root
     * @param paths
     * @param traverse
     * @return
     * @throws IOException
     */
    private List<Path> findChildren(Path root, List<Path> paths, int traverse) throws IOException {
        if (root == null || traverse == 0) return paths; //recursion terminator
        List<Path> items = Files.list(root).collect(Collectors.toList());
        for (Path path : items) {
            if (Files.isDirectory(path)){
                findChildren(path, paths, traverse - 1);
            }else {
                paths.add(path);
            }
        }
        return findChildren(null, paths, traverse - 1);
    }

    private void deleteFiles(List<Path> dirs, Date givenDate) {
        LOG.info("Item founds: " + dirs.size());
        LOG.info("Given Date: " + new SimpleDateFormat("yyyy-MM-dd").format(givenDate));
        dirs.stream()
                .filter(path -> Files.exists(path))
                .filter(path -> path.toFile().lastModified() <= givenDate.getTime())
                .forEach(path -> {
                    try {
                        //If It Is Dir & Is Empty
                        if (Files.isDirectory(path)){
                            //If Dir is empty: only then try to delete.
                            if (Files.list(path).count() == 0) {
                                deleteFileAtPath(path);
                            }
                        }else {
                            deleteFileAtPath(path);
                        }
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, e.getMessage(), e);
                    }
                });
    }

    private void deleteFileAtPath(Path path) throws IOException {
        try {
            boolean deleted = Files.deleteIfExists(path);
            LOG.info(String.format("FileName: %s IS DELETED: %s"
                    , path.getFileName()
                    , (deleted ? "Yes" : "No")));
        } catch (IOException e) {
            throw new IOException(String.format("FileName: %s IS DELETED: %s"
                    , path.getFileName()
                    , "No"));
        }
    }
}
