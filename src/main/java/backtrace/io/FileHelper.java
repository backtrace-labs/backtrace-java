package backtrace.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Helper class for access to files
 */
public class FileHelper {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);

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
     * Check does file path is invalid, null, empty or file not exists or is directory
     * @param filePath path to the file to be checked
     * @return true if path is invalid
     */
    private static boolean isFilePathInvalid(String filePath) {
        return filePath == null || filePath.isEmpty() || !isFileExists(filePath) || new File(filePath).isDirectory();
    }

    /***
     * Check does file exist
     * @param absoluteFilePath absolute path to the file to be checked
     * @return true if file exists
     */
    private static boolean isFileExists(String absoluteFilePath) {
        return new File(absoluteFilePath).exists();
    }

    /**
     * Delete recursive all files
     * @param f File
     * @throws Exception if failed deleting file
     */
    static void deleteRecursive(File f) throws Exception {
        try {
            if (!f.exists()){
                return;
            }
            if (f.isDirectory()) {
                for (File c : f.listFiles()) {
                    deleteRecursive(c);
                }
            }
            if (!f.delete()) {
                throw new Exception("Delete command returned false for file: " + f);
            }
        }
        catch (Exception e) {
            LOGGER.error("Failed to delete the folder: " + f, e);
        }
    }

    /**
     * Custom comparator which compare two file names
     * @return Custom comparator which compare two file names
     */
    static Comparator<File> getFileNameComparator() {
        return new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getAbsoluteFile().compareTo(o2.getAbsoluteFile());
            }
        };
    }

}
