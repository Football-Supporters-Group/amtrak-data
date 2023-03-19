package com.wolginm.amtrak.data.exception;

import java.nio.file.FileSystemException;

/**
 * An exception that indicates the supplied file is not a file.
 */
public class NotFileException extends FileSystemException {
    public NotFileException(String file) {
        super(file);
    }
}
