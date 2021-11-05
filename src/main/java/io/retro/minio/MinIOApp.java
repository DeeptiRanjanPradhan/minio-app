package io.retro.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.err;
import static java.lang.System.out;

public class MinIOApp {
    public static void main(String[] args) {
        try {
            uploadFile(new File(args[0]), args[1], args[2]);
        } catch (Exception ex) {
            err.println("Awe snap! Something is up Chuck! " + ex);
        }
    }

    public static void uploadFile(final File sourceFile, final String bucketName, final String objectName)
        throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        try {
            MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("corbs", "howdy123")
                .build();
            boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());
            if(!found) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                out.println("Bucket " + bucketName + " already exists");
            }

            ObjectWriteResponse response = minioClient.uploadObject(
                UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .filename(sourceFile.getAbsolutePath())
                    .build());

            out.printf("Successfully uploaded file %s, to bucket %s, as object %s%n",
                sourceFile.getAbsolutePath(), bucketName, objectName);

        } catch(MinioException ex) {
            err.println("Caught MinIO exception in uploadFile:" + ex);
            err.println("HTTP Trace: " + ex.httpTrace());
        }
    }
}
