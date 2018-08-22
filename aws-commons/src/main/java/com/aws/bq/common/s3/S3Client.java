package com.aws.bq.common.s3;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.s3.impl.S3BuilderImpl;
import com.aws.bq.common.s3.impl.TransferMgrBuilderImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/22/2018
 */
@Slf4j
public class S3Client {

    private S3Client(){}

    /**
     * 初始化
     * @return
     */
    public static S3Client build() {
        return new S3Client();
    }

    /**
     * 通过默认S3Client上传
     * @param amazonS3
     * @param bucketName
     * @param key
     * @param file_path
     * @return
     */
    public PutObjectResult upload(AmazonS3 amazonS3, String bucketName, String key, String file_path) {
        IS3Builder builder = new S3BuilderImpl();
        return builder.putObject(amazonS3, bucketName, key, file_path);
    }

    /**
     * 通过默认S3Client下载
     * @param amazonS3
     * @param contracts
     * @return
     */
    public List<File> download(AmazonS3 amazonS3, List<Contract> contracts) {
        List<File> files = new ArrayList<>();
        IS3Builder builder = new S3BuilderImpl();

        for (Contract contract : contracts) {
            File file = builder.getObject(amazonS3, contract.getS3Bucket(), contract.getS3Key());
            files.add(file);
        }
        return files;
    }

    /**
     * 通过TransferManager上传
     * @param manager
     * @param bucketName
     * @param key
     * @return
     */
    public UploadResult uploadByTransferMgr(TransferManager manager, String bucketName, String key, File file) {
        ITransferMgrBuilder builder = new TransferMgrBuilderImpl();
        return builder.putObject(manager, bucketName, key, file);
    }

    /**
     * 通过TransferManager上传
     * @param manager
     * @param bucketName
     * @param key
     * @param progressListener
     * @return
     */
    public UploadResult uploadByTransferMgr(TransferManager manager, String bucketName,
                                            String key, File file, ProgressListener progressListener) {
        ITransferMgrBuilder builder = new TransferMgrBuilderImpl();
        return builder.putObject(manager, bucketName, key, file, progressListener);
    }

    /**
     * 通过TransferManager下载
     * @param manager
     * @param contracts
     * @return
     */
    public List<File> downloadByTransferMgr(TransferManager manager, List<Contract> contracts) {
        List<File> files = new ArrayList<>();
        ITransferMgrBuilder builder = new TransferMgrBuilderImpl();

        for (Contract contract : contracts) {
            String fileName = getFileName(contract.getS3Key());
            File file = builder.getObject(manager, contract.getS3Bucket(), contract.getS3Key(), fileName);
            files.add(file);
        }
        return files;
    }

    /**
     * 通过TransferManager下载
     * @param manager
     * @param contracts
     * @param timeout
     * @return
     */
    public List<File> downloadByTransferMgr(TransferManager manager, List<Contract> contracts, long timeout) {
        List<File> files = new ArrayList<>();
        ITransferMgrBuilder builder = new TransferMgrBuilderImpl();

        for (Contract contract : contracts) {
            String fileName = getFileName(contract.getS3Key());
            File file = builder.getObject(manager, contract.getS3Bucket(), contract.getS3Key(), fileName, timeout);
            files.add(file);
        }
        return files;
    }

    /**
     * 通过默认S3Client获取URL
     * @param amazonS3
     * @param bucket
     * @param key
     * @return
     */
    public URL getURL(AmazonS3 amazonS3, String bucket, String key) {
        IS3Builder builder = new S3BuilderImpl();
        return builder.getUrl(amazonS3, bucket, key);
    }

    /**
     * 通过TransferManager获取URL
     * @param manager
     * @param bucket
     * @param key
     * @return
     */
    public URL getURL(TransferManager manager, String bucket, String key) {
        ITransferMgrBuilder builder = new TransferMgrBuilderImpl();
        return builder.getUrl(manager, bucket, key);
    }

    /**
     * 获取文件名
     * @param s3Key
     * @return
     */
    private String getFileName(String s3Key) {
        return s3Key.contains("/") ? s3Key.substring(s3Key.lastIndexOf("/") + 1) : s3Key;
    }
}
