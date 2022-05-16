package com.wolginm.amtrak.data.util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {
    

    public Path resolvePath(final String path) {
        return FileSystems.getDefault()
            .getPath(path)
            .normalize()
            .toAbsolutePath();
    }

    /**
     * Assumes the path is starting as a file
     * @param path
     * @return
     */
    public int prepFoldersForFile(final Path path) {
        return this.prepFoldersForFile(path, 0);
    }

    private int prepFoldersForFile(final Path path, int depth) {
        File current = path.getParent().toFile();
        if (current.exists()) {
            
        } else {
            log.info("At directory: {}", path.toString());
            this.prepFoldersForFile(path.getParent(), depth ++);
            current.mkdir();
            log.info("Directory created: {}", path.toString());
        }
        return depth;
    }
}
