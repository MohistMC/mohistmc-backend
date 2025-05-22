package com.mohistmc.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {
    public static void unzipFirstEntry(InputStream fileInputStream, Path destDir) throws IOException {
        Files.createDirectories(destDir.getParent());

        try (ZipInputStream zis = new ZipInputStream(fileInputStream)) {
            ZipEntry entry = zis.getNextEntry();

            if (entry != null && !entry.isDirectory()) {
                try (FileOutputStream fos = new FileOutputStream(destDir.toFile())) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            } else {
                throw new IOException("The zip entry is null or is a directory");
            }

            zis.closeEntry();
        }
    }

    public static void unzipFirstEntry(Path zipFilePath, Path destDir) throws IOException {
        unzipFirstEntry(new FileInputStream(zipFilePath.toFile()), destDir);
    }
}
