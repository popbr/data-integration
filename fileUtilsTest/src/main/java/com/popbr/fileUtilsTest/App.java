package com.popbr.fileUtilsTest;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.*;

public class App {
    public static void main(String[] args) {
        
        String url = "https://www.nsf.gov/awardsearch/download?DownloadFileName="; // don't just assign the string to the url: create a URL object with it. https://www.nsf.gov/awardsearch/download.jsp
        String Fname = "target" + File.separator +"webpages" + File.separator; // Give some extension to your file (here, I added ".html").
        String Fdestination = "target" + File.separator +"downloads" + File.separator; 
        Fdestination = "C:" + File.separator + "Users" + File.separator + "sleep" + File.separator + "Desktop" + File.separator + "fileUtilsTest" + File.separator + "target" + File.separator + "downloads";

        listFiles(Fdestination);

        //scrapeWebsite(url, Fname);
        /*
        for(int i = 2000; i < 2003; i++) {
            getFile(url + i + "&All=true", Fname + i + "DB.zip");
            unzipFile( Fname + i + "DB.zip", Fdestination + i + "DB");
        } 
        */

    }

    public static void scrapeWebsite(URL pageURL, String destination) {
        try {
            File fileDestination = new File(destination); // You could avoid creating a string, but ok.
            FileUtils.copyURLToFile(pageURL, fileDestination);
        } catch (IOException e) { // You were not catching exceptions, which is weird to me.
            e.printStackTrace();
        }
    }

    public static void getFile(String fileURL, String destination) {
        System.out.println("Retrieving file...");
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileURL).openStream());          
            FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzipFile(String fileURL, String destination) {
        System.out.println("Unzipping file...");

        try {
            String fileZip = fileURL;
            File destDir = new File(destination);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    
                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
    
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
    
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
    
        return destFile;
    }

    public static void listFiles(String destinationDir) {
        
        File f = new File(destinationDir);
		File[] fileN = f.listFiles(); // target\downloads\2000DB

        for (int i = 0; i < fileN.length; i++) {
            f = fileN[i];
            File[] fileInFileN = f.listFiles();
            //System.out.println(fileN[i]);
            for(int j = 0; j < fileInFileN.length; j++) {
                //System.out.println(fileInFileN[j]);
                moveFile(fileInFileN[j], destinationDir);
            }
        }
    }
    
    public static void moveFile(File sourceFile, String targetPath) {

        File fileToMove = sourceFile;
        fileToMove.renameTo(new File(targetPath));
    }

} //https://www.nsf.gov/awardsearch/download?DownloadFileName=2023&All=true