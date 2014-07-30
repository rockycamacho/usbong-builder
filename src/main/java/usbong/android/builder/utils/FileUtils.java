package usbong.android.builder.utils;

import android.util.Log;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by Rocky Camacho on 7/7/2014.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final int BUFFER_SIZE = 8192;
    public static final String UTREE_FILE_EXTENSION = "\\.utree";

    private FileUtils() {
    }

    public static void zip(String zipFilePath, String contentLocation) {
        File contentDir = new File(contentLocation);
        if (!contentDir.exists()) {
            throw new IllegalArgumentException(contentLocation + " does not exist");
        }
        File zipFile = new File(zipFilePath);
        mkdir(zipFilePath.substring(0, zipFilePath.lastIndexOf("/")));
        ZipOutputStream out = null;
        Queue<File> files = new LinkedList<File>();
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
            byte data[] = new byte[BUFFER_SIZE];
            files.addAll(Arrays.asList(contentDir.listFiles()));
            while (!files.isEmpty()) {
                File file = files.remove();
                Log.d(TAG, "file.getPath(): " + file.getPath() + " file.isDirectory(): " + file.isDirectory());
                if (file.isDirectory()) {
                    ZipEntry entry = new ZipEntry(file.getAbsolutePath().substring(contentDir.getAbsolutePath().length() + 1) + "/");
                    try {
                        out.putNextEntry(entry);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                    files.addAll(Arrays.asList(file.listFiles()));
                    continue;
                }
                FileInputStream fi = null;
                BufferedInputStream bis = null;
                try {
                    fi = new FileInputStream(file);
                    bis = new BufferedInputStream(fi, BUFFER_SIZE);
                    Log.d(TAG, "zip file path: " + file.getAbsolutePath().substring(contentDir.getAbsolutePath().length() + 1));
                    ZipEntry entry = new ZipEntry(file.getAbsolutePath().substring(contentDir.getAbsolutePath().length() + 1));
                    if (!entry.isDirectory()) {
                        try {
                            out.putNextEntry(entry);
                            int count;
                            while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                                out.write(data, 0, count);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                } finally {
                    ResourceUtils.close(bis);
                    ResourceUtils.close(fi);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.close(out);
        }

    }

    public static void unzip(String zipLocation, String outputFolderLocation) {
        try {
            File file = new File(zipLocation);
            if (!file.exists()) {
                throw new IllegalArgumentException("invalid file location");
            }
            Log.d(TAG, "file.getAbsolutePath(): " + file.getAbsolutePath());
            ZipFile zipFile = new ZipFile(file.getAbsolutePath());
            ZipEntry zipEntry = null;
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                zipEntry = entries.nextElement();
                File newFile = new File(outputFolderLocation + File.separator + zipEntry.getName().replaceAll(UTREE_FILE_EXTENSION, ""));

                Log.d(TAG, "newFile: " + newFile.getAbsolutePath());

                mkdir(newFile.getParent() + File.separator);
                if (!zipEntry.isDirectory()) {
                    copyZipFile(zipFile, zipEntry, newFile);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static void copyZipFile(ZipFile zipFile, ZipEntry zipEntry, File newFile) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = zipFile.getInputStream(zipEntry);
            output = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            ResourceUtils.close(input);
            ResourceUtils.close(output);
        }
    }

    public static void copy(File sourceFile, File destinationFile) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(sourceFile);
            output = new FileOutputStream(destinationFile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            ResourceUtils.close(input);
            ResourceUtils.close(output);
        }
    }

    public static void mkdir(String location) {
        mkdir(new File(location));
    }

    public static void mkdir(File file) {
        Log.d(TAG, "mkdir: " + file.getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void copyAll(String sourceLocation, String destinationLocation) {
        mkdir(destinationLocation);
        Stack<File> files = new Stack<File>();
        File sourceFolder = new File(sourceLocation);
        files.addAll(Arrays.asList(sourceFolder.listFiles()));
        while(!files.isEmpty()) {
            File file = files.pop();
            File newFile = new File(destinationLocation + file.getAbsolutePath().substring(sourceFolder.getAbsolutePath().length() + 1));
            if(file.isDirectory()) {
                mkdir(newFile);
                files.addAll(Arrays.asList(file.listFiles()));
            }
            else {
                FileUtils.copy(file, newFile);
            }
        }
    }

    public static void delete(String tempFolderLocation) {
        Queue<File> files = new LinkedList<File>();
        File file = new File(tempFolderLocation);
        files.add(file);
        while(!files.isEmpty()) {
            File fileToBeDeleted = files.remove();
            if(fileToBeDeleted.isDirectory()) {
                if(fileToBeDeleted.listFiles().length > 0) {
                    files.addAll(Arrays.asList(fileToBeDeleted.listFiles()));
                    files.add(fileToBeDeleted);
                }
                else {
                    fileToBeDeleted.delete();
                }
            }
            else {
                fileToBeDeleted.delete();
            }
        }
    }
}
