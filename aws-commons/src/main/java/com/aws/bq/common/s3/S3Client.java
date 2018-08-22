package com.aws.bq.common.s3;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.aws.bq.common.model.Contract;
import com.aws.bq.common.s3.impl.S3BuilderImpl;
import com.aws.bq.common.s3.impl.TransferMgrBuilderImpl;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/22/2018
 */
@Slf4j
public class S3Client {

    public static final int TASK_SIZE = 10;
    private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(TASK_SIZE));

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
        return downloadByTransferMgr(manager, contracts, 0);
    }

    private List<List<Contract>> splitTasks(List<Contract> contracts, int total) {
        List<List<Contract>> list = new ArrayList<>();

        int len = contracts.size() < total ? contracts.size() : total;
        int size = contracts.size() < total ? 1 : contracts.size() / total;

        int index = 0;
        for (int i = 0; i < len; i++) {
            if (i < len - 1) {
                list.add(contracts.subList(index, index + size));
                index = index + size;
            } else {
                list.add(contracts.subList(index, contracts.size()));
            }
        }

        return list;
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
        List<Future<List<File>>> futures = new ArrayList<>();
        ITransferMgrBuilder builder = new TransferMgrBuilderImpl();

        List<List<Contract>> taskList = splitTasks(contracts, TASK_SIZE);

        for (List<Contract> task : taskList) {
            ListenableFuture<List<File>> future = service.submit(new Callable<List<File>>() {
                @Override
                public List<File> call() {
                    List<File> files = new ArrayList<>();
                    for (Contract contract : task) {
                        String fileName = getFileName(contract.getS3Key());
                        File file = builder.getObject(manager, contract.getS3Bucket(), contract.getS3Key(), fileName, timeout);
                        files.add(file);
                    }
                    return files;
                }
            });
            Futures.addCallback(future, new FutureCallback<List<File>>() {
                @Override
                public void onSuccess(@Nullable List<File> files) {
                    log.info("[TransferMgrBuilderImpl] ============> Download (" + files.size() + ") items");
                }

                @Override
                public void onFailure(Throwable e) {
                    log.error("[S3Client] ========> Exception:", e);
                }
            });
            futures.add(future);
        }

        try {
            for (Future<List<File>> future : futures) {
                List<File> subFiles = future.get();
                files.addAll(subFiles);
            }
        } catch (Exception e) {
            log.error("[S3Client] ========> Exception:", e);
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
