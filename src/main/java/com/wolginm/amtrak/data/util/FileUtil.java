package com.wolginm.amtrak.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.wolginm.amtrak.data.exception.NotFileException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtil {

    private final int BUFFER_SIZE = 4096;

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

    /**
     * Prepares a folder to have files if the folder does not exist.
     * @param path
     * @param depth
     * @return
     */
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

    /**
     * {@code 0} if this {@code FileTime} is equal to {@code other}, a
     *          value less than 0 if this {@code FileTime} represents a time
     *          that is before {@code other}, and a value greater than 0 if this
     *          {@code FileTime} represents a time that is after {@code other}
     * @param alpha
     * @param beta
     * @return
     * @throws IOException
     */
    public int compareFileTimes(File alpha, File beta) throws IOException {
        BasicFileAttributes alphaAttributes = Files.readAttributes(alpha.toPath(), BasicFileAttributes.class);
        BasicFileAttributes betaAttributes = Files.readAttributes(beta.toPath(), BasicFileAttributes.class);

        return alphaAttributes
                .creationTime()
                .compareTo(betaAttributes
                        .creationTime());
    }

    /**
     * Will copy and then delete, all the file in a directory to a new directory.
     * @param sourceDirectory       - Source directory
     * @param destinationDirectory  - Destination directory
     * @param recursive             - To be a recursive copy.
     */
    public void moveAllFiles(File sourceDirectory, File destinationDirectory, boolean recursive) {

    }

    /**
     * This method will be used to delete a file.
     * @param file - File to be deleted.
     * @return True if the files is successfully deleted, False otherwise.
     */
    public boolean deleteFile(File file) throws NotFileException {
        if (!file.isFile()) {
            log.error("Supplied file is, well, not a file, file: {}", file);
            throw new NotFileException(file.getPath());
        }

        boolean deletionStatus;
        try {
            deletionStatus = file.delete();
            log.debug("Deleted file: {}", file);
        } catch (RuntimeException e) {
            log.error("Failed to delete file: {}", file);
            throw e;
        }

        return deletionStatus;
    }

    /**
     * This method will be used to delete an empty directory.
     * @param directory - Directory to be deleted.  Directory must be empty.
     * @return True if the directory is successfully deleted, False otherwise.
     */
    public boolean deleteDirectory(File directory) throws NotDirectoryException {
        if (!directory.isDirectory()) {
            log.error("Supplied directory is not a directory, directory: {}", directory);
            throw new NotDirectoryException(directory.getPath());
        } else if (directory.list().length != 0) {
            Arrays.stream(directory.listFiles()).forEach((file) -> log.debug("File exists: {}", file.getPath()));
            log.error("Supplied directory cannot be deleted, due to files existing in the directory.");
        }

        boolean deletionStatus;
        try {
            deletionStatus = directory.delete();
            log.debug("Deleted directory: {}", directory);
        } catch (RuntimeException e) {
            log.error("Failed to delete directory: {}", directory);
            throw e;
        }

        return deletionStatus;
    }

    /**
     * This method will be used to delete all files in a specified folder.
     * @param directory - The directory to delete.
     * @param recursive - Should the delete action be applied recursively.
     *                  If so, it will also delete the recursive folders.
     * @return True if the files is successfully deleted, False otherwise.
     */
    public boolean deleteAllFilesInFolder(File directory, boolean recursive) {
        boolean deleteAllFiles = true;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory.toPath())) {
            File file;
            for (Path entry: stream) {
                file = entry.toFile();
                log.debug("Attempting to delete directory: {}", file.getPath());
                if (file.isFile()) {
                    deleteAllFiles &= this.deleteFile(file);
                } else if (recursive && file.isDirectory()) {
                    deleteAllFiles &= this.deleteAllFilesInFolder(file, true);
                    deleteAllFiles &= this.deleteDirectory(file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deleteAllFiles;
    }

    public long calcualteChecksum(File checkedFile) {
        long checksum = -1;
        try (CheckedInputStream checkedInputStream
                     = new CheckedInputStream(new FileInputStream(checkedFile), new CRC32())){

            byte[] buffer = new byte[this.BUFFER_SIZE];
            while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {
            }
            checksum = checkedInputStream.getChecksum().getValue();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return checksum;
    }
}
