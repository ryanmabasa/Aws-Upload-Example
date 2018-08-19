package com.example.demo.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.example.demo.config.AppConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadService{

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);


    @Autowired
    private AppConfiguration appConfiguration;

    // Aws Credentials
    private static BasicAWSCredentials awsCreds = null;
    private static ClientConfiguration config = null;
	private static AmazonS3 s3Client = null;


    public void connectAWS(){

        logger.info("Initialize Aws Credentials...");

        awsCreds = new BasicAWSCredentials(
            appConfiguration.getAccess_key_id(), 
            appConfiguration.getSecret_access_key()
        );

        config = new ClientConfiguration();

        if(appConfiguration.getProxy().getEnabled()){
            config.setProxyHost(appConfiguration.getProxy().getUrl());
            config.setProxyPort(appConfiguration.getProxy().getPort());
        }

        logger.info("Building S3 Client...");

        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withClientConfiguration(config)
                .withRegion(appConfiguration.getClient_region())
                .build();

        logger.info("Done...");
    }

    public Boolean uploadFile(File file){

        Boolean flag = false;

        long contentLength = file.length();
        long partSize = 5 * 1024 * 1024; // Set part size to 5 MB. 

        try {
            connectAWS();
                       
            // Create a list of ETag objects. You retrieve ETags for each object part uploaded,
            // then, after each individual part has been uploaded, pass the list of ETags to 
            // the request to complete the upload.
            List<PartETag> partETags = new ArrayList<PartETag>();

            // Initiate the multipart upload.
            logger.info("Initiating Multipart Upload..." + file.getName());
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(appConfiguration.getBucket_name(), file.getName());
            InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

            // Upload the file parts.
            long filePosition = 0;
            for (int i = 1; filePosition < contentLength; i++) {
                // Because the last part could be less than 5 MB, adjust the part size as needed.
                partSize = Math.min(partSize, (contentLength - filePosition));

                // Create the request to upload a part.
                logger.info("Creating a request to upload...");
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(appConfiguration.getBucket_name())
                        .withKey(file.getName())
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(file)
                        .withPartSize(partSize);

                // Upload the part and add the response's ETag to our list.
                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
                partETags.add(uploadResult.getPartETag());

                filePosition += partSize;
            }

            // Complete the multipart upload.
            logger.info("Uploading a File..." + file.getName());
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(appConfiguration.getBucket_name(), file.getName(),
                    initResponse.getUploadId(), partETags);
            s3Client.completeMultipartUpload(compRequest);
            flag = true;
            logger.info("File Uploaded...");
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        return flag;

    }
    
}