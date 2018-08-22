package com.aws.bq.common.s3.impl;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.aws.bq.common.s3.ITransferMgrBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/22/2018
 */
@Slf4j
public class TransferMgrBuilderImpl implements ITransferMgrBuilder {

    @Override
    public UploadResult putObject(TransferManager manager, String bucketName, String key, File file) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            Upload upload = manager.upload(request);
            return upload.waitForUploadResult();
        } catch (Exception e) {
            log.error("[TransferMgrBuilderImpl] ========> Exception:", e);
        }
        return null;
    }

    @Override
    public UploadResult putObject(TransferManager manager, String bucketName, String key, File file, ProgressListener progressListener) {
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead)
                    .withGeneralProgressListener(progressListener);
            Upload upload = manager.upload(request);
            return upload.waitForUploadResult();
        } catch (Exception e) {
            log.error("[TransferMgrBuilderImpl] ========> Exception:", e);
        }
        return null;
    }

    @Override
    public File getObject(TransferManager manager, String bucketName, String key, String fileName, long timeout) {
        try {
            File file = new File(fileName);
            Download download = manager.download(new GetObjectRequest(bucketName, key), file, timeout);
            download.waitForCompletion();
            if (download.getState() == Transfer.TransferState.Completed) {
                return file;
            }
        } catch (Exception e) {
            log.error("[TransferMgrBuilderImpl] ========> Exception:", e);
        }
        return null;
    }

    @Override
    public File getObject(TransferManager manager, String bucketName, String key, String fileName) {
        try {
            File file = new File(fileName);
            Download download = manager.download(new GetObjectRequest(bucketName, key), file);
            download.waitForCompletion();
            if (download.getState() == Transfer.TransferState.Completed) {
                return file;
            }
        } catch (Exception e) {
            log.error("[TransferMgrBuilderImpl] ========> Exception:", e);
        }
        return null;
    }

    @Override
    public URL getUrl(TransferManager manager, String bucket, String key) {
        return manager.getAmazonS3Client().getUrl(bucket, key);
    }
}
