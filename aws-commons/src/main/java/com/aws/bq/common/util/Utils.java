package com.aws.bq.common.util;


import com.aws.bq.common.model.ZipFileResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/5/2018
 */
public class Utils {
    /**
     * 将从S3下载的文件进行压缩打包
     * @param files
     * @param zipFileName
     * @return
     */
    public static ZipFileResult zipFiles(List<File> files, String zipFileName) {
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        ZipFileResult result = new ZipFileResult(false);

        try {
            fos = new FileOutputStream(zipFileName);
            zipOut = new ZipOutputStream(fos);
            for (File fileToZip : files) {
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        } finally {
            try {
                if (null != zipOut) {
                    zipOut.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 随机生成UUID
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
