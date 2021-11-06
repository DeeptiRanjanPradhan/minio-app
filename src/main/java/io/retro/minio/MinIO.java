package io.retro.minio;

import io.minio.MinioClient;

public final class MinIO {
    private static final MinIO INSTANCE = new MinIO();
    private final MinioClient client;
    private MinIO() {
        client = MinioClient.builder()
                    .endpoint(System.getenv("MINIO_ENDPOINT"))
                    .credentials(System.getenv("MINIO_ACCESS_KEY"),
                        System.getenv("MINIO_SECRET_KEY"))
                    .build();
    }

    public static MinIO instance() {
        return INSTANCE;
    }

    public MinioClient client() {
        return this.client;
    }

}
