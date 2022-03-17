package com.mycompany.app;

import org.apache.commons.io.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

// inspired from 
// https://www.techiedelight.com/download-file-from-url-java/

public class App 
{
    public static void downloadFile(URL url, String fileName) throws IOException {
        FileUtils.copyURLToFile(url, new File(fileName));
    }
 
    public static void main(String[] args) throws Exception {
        downloadFile(new URL("https://www.nsf.gov/awardsearch/download?DownloadFileName=1968&All=true"), "1968.zip");
    }
}

