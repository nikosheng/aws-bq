package com.aws.bq.common.s3;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import java.io.File;
import java.net.URL;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/22/2018
 */
public interface ITransferMgrBuilder {
    /**
     * 上传对象
     * @param manager
     * @param bucketName
     * @param key
     * @param file
     * @return
     */
    UploadResult putObject(TransferManager manager, String bucketName, String key, File file);

    /**
     * 上传对象
     * @param manager
     * @param bucketName
     * @param key
     * @param file
     * @param progressListener
     * @return
     */
    UploadResult putObject(TransferManager manager, String bucketName, String key, File file, ProgressListener progressListener);

    /**
     * 获取对象
     * @param manager
     * @param bucketName
     * @param key
     * @return
     */
    File getObject(TransferManager manager, String bucketName, String key);

    /**
     * 获取对象
     * @param manager
     * @param bucketName
     * @param key
     * @param timeout
     * @return
     */
    File getObject(TransferManager manager, String bucketName, String key, long timeout);

    /**
     * 获取S3对象URL
     * @param manager
     * @param bucket
     * @param key
     * @return
     */
    URL getUrl(TransferManager manager, String bucket, String key);
}
