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
    public static ArrayList<String> filterOutFiles(List<String> paths) {
        ArrayList<String> result = new ArrayList<>();
        if (paths == null) {
            return result;
        }
        paths = new ArrayList<>(new HashSet<>(paths)); // get only unique elements

        for (String path : paths) {
            if (isFilePathInvalid(path)) {
//                Log.e("Backtrace.io", String.format("Path for file '%s' is incorrect.", path));
                continue;
            }

            result.add(path);
        }
        return result;
    }

    /**
     * Get file extension
     *
     * @param file file whose extension will be returned
     * @return file extension
     */
    public static String getFileExtension(File file) {

        String name = file.getName().substring(Math.max(file.getName().lastIndexOf('/'),
                file.getName().lastIndexOf('\\')) < 0 ? 0 : Math.max(file.getName().lastIndexOf('/'),
                file.getName().lastIndexOf('\\')));
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1); // doesn't return "." with extension
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
    public static boolean isFileExists(String absoluteFilePath) {
        return new File(absoluteFilePath).exists();
    }


    public static String readFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            StringBuilder sb = new StringBuilder();

            while (scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }

            scanner.close();

            return sb.toString();
        } catch (Exception e) {
//            Log.e("Backtrace.IO", e.getMessage());
            return null;
        }
    }
}
