package com.tenerity.nordic.client;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Service
public class AmazonClient {
    private AmazonS3 s3Client;

    @Value("${amazonProperties.awsRegion}")
    private String awsRegion;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @PostConstruct
    private void initializeAmazon() {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder
                .EndpointConfiguration("https://s3.eu-west-1.amazonaws.com", awsRegion);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withPathStyleAccessEnabled(true)
                .build();
    }

    public String uploadObject(String filename, File data) {
        s3Client.putObject(new PutObjectRequest(bucketName, filename, data));
        return s3Client.getUrl(bucketName, filename).toString();
    }

    public String removeObject(String filename) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, filename));
        return s3Client.getUrl(bucketName, filename).toString();
    }

    public byte[] readObject(String filename) {
        byte[] content = null;
        var s3ObjectInputStream = s3Client.getObject(new GetObjectRequest(bucketName, filename)).getObjectContent();
        try {
            content = s3ObjectInputStream.readAllBytes();
        } catch (IOException e) {
            s3ObjectInputStream.abort();
        }
        return content;
    }

    public String getUrl(String filename) {
        return s3Client.getUrl(bucketName, filename).toString();
    }
}
