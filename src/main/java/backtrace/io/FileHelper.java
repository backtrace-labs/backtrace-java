package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * Helper class for access to files
 */
public class FileHelper {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);
    /***
     * Get file name with extension from file path
     * @param absolutePath absolute path to file
     * @return file name with extension
     */
    static String getFileNameFromPath(String absolutePath) {
        return absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
    }

    /***
     * Remove from path list invalid paths like empty or incorrect paths or not existing files
     * @param paths list of paths to files
     * @return filtered list of file paths
     */
    static ArrayList<String> filterOutFiles(List<String> paths) {
        ArrayList<String> result = new ArrayList<>();
        if (paths == null) {
            return result;
        }
        paths = new ArrayList<>(new HashSet<>(paths)); // get only unique elements

        for (String path : paths) {
            if (isFilePathInvalid(path)) {
                LOGGER.error(String.format("Path for file '%s' is incorrect.", path));
                continue;
            }

            result.add(path);
        }
        return result;
    }

    /***
     * Check does file path is invalid, null, empty or file not exists
     * @param filePath path to the file to be checked
     * @return true if path is invalid
     */
    private static boolean isFilePathInvalid(String filePath) {
        return filePath == null || filePath.isEmpty() || !isFileExists(filePath);
    }

    /***
     * Check does file exist
     * @param absoluteFilePath absolute path to the file to be checked
     * @return true if file exists
     */
    private static boolean isFileExists(String absoluteFilePath) {
        return new File(absoluteFilePath).exists();
    }

}
