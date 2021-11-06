package io.retro.minio;

import io.minio.*;
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
            if(args.length == 3) {
                uploadFile(new File(args[0]), args[1], args[2]);
            } else if(args.length == 2) {
                uploadFile(new File(args[0]), args[1]);
            } else {
                out.println("Usage:");
                out.println("  1) uploadFile [sourceFile] [bucketName]");
                out.println("     *Note* Name of sourceFile is used for objectName.");
                out.println("  2) uploadFile [sourceFile] [bucketName] [objectName]");
            }
        } catch (Exception ex) {
            err.println("Awe snap! Something is up Chuck! " + ex);
        }
    }

    public static void uploadFile(final File sourceFile, final String bucketName)
        throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        uploadFile(sourceFile, bucketName, sourceFile.getName());
    }

    public static void uploadFile(final File sourceFile, final String bucketName, final String objectName)
        throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        try {
            MinioClient minioClient = MinIO.instance().client();
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

            out.printf("Successfully uploaded file %s, to bucket %s, as object %s, version %s%n",
                sourceFile.getAbsolutePath(), response.bucket(), response.object(), response.versionId());

        } catch(MinioException ex) {
            err.println("Caught MinIO exception in uploadFile:" + ex);
            err.println("HTTP Trace: " + ex.httpTrace());
        }
    }
}
