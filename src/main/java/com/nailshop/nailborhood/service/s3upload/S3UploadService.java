package com.nailshop.nailborhood.service.s3upload;


import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.type.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Component
@Service
public class S3UploadService {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final String SHOP_PATH = "shop";
    private final String CERTIFICATE_PATE = "certificate";
    private final String ARTREF_PATH = "artRef";
    private final String REVIEW_PATH = "review";
    private final String PROFILE_PATH = "profile";



    // 매장 이미지 업로드
    public List<String> shopImgUpload(List<MultipartFile> multipartFileList) {
        return uploadImg(multipartFileList ,SHOP_PATH);
    }

    // 사업자 증명 이미지 업로드
    public List<String> certificateImgUpload(List<MultipartFile> multipartFileList) {
        return uploadImg(multipartFileList ,CERTIFICATE_PATE);
    }

    // 아트판 이미지 업로드
    public List<String> artImgUpload(List<MultipartFile> multipartFileList) {
        return uploadImg(multipartFileList,ARTREF_PATH);
    }

    // 리뷰 이미지 업로드
    public List<String> reviewImgUpload(List<MultipartFile> multipartFileList) {
        return uploadImg(multipartFileList,REVIEW_PATH);
    }

    // 프로필 이미지 업로드
    public String profileImgUpload(MultipartFile multipartFile) {
        return uploadProfile(multipartFile ,PROFILE_PATH);
    }


    // 여러 이미지 업로드 ( 매장,리뷰,아트판 사진 )
    public List<String> uploadImg(List<MultipartFile> multipartFiles, String folder) {
        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            String fileName = createFileName(file.getOriginalFilename());

            try {
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                    .bucket(bucket)
                                                                    .key(folder + "/" + fileName)
                                                                    .acl("public-read")
                                                                    .contentType(file.getContentType())
                                                                    .build();

                PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                if (putObjectResponse.sdkHttpResponse().isSuccessful()) {
                    imgUrlList.add(s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(folder + "/" + fileName)).toString());
                } else {
                    throw new BadRequestException(ErrorCode.IMAGE_UPLOAD_FAIL);
                }
            } catch (IOException e) {
                throw new BadRequestException(ErrorCode.IMAGE_UPLOAD_FAIL);
            }
        }
        return imgUrlList;
    }

    // 단일 이미지 업로드 ( 프로필 이미지 )
    public String uploadProfile(MultipartFile multipartFile , String folder) {
        String fileName = createFileName(multipartFile.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(bucket)
                                                                .key(folder + "/" + fileName)
                                                                .acl("public-read")
                                                                .contentType(multipartFile.getContentType())
                                                                .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            if (putObjectResponse.sdkHttpResponse().isSuccessful()) {
                return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(folder + "/" + fileName)).toString();
            } else {
                throw new BadRequestException(ErrorCode.IMAGE_UPLOAD_FAIL);
            }
        } catch (IOException e) {
            throw new BadRequestException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }


    // 이미지 삭제 (매장)
    public void deleteShopImg(String imgPath) {
        deleteImage(imgPath, SHOP_PATH);
    }

    // 이미지 삭제 (사업자 증명)
    public void deleteCertificateImg(String imgPath) {
        deleteImage(imgPath, CERTIFICATE_PATE);
    }

    // 이미지 삭제 (아트)
    public void deleteArtImg(String imgPath) {
        deleteImage(imgPath, ARTREF_PATH);
    }

    // 이미지 삭제 (리뷰)
    public void deleteReviewImg(String imgPath) {
        deleteImage(imgPath, REVIEW_PATH);
    }

    // 이미지 삭제 (프로필)
    public void deleteProfileImg(String imgPath) {
        deleteImage(imgPath, PROFILE_PATH);
    }

    // 이미지 삭제
    public void deleteImage(String imgPath, String folder) {
        int lastIndex = imgPath.lastIndexOf("/") + 1;
        String substringImgPath = imgPath.substring(lastIndex);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                                                                     .bucket(bucket)
                                                                     .key(folder + "/" + substringImgPath)
                                                                     .build();

        s3Client.deleteObject(deleteObjectRequest);
    }



    // 이미지 파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 확인
    private String getFileExtension(String fileName) {

        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));

        if (!fileValidate.contains(idxFileName)) {
            throw new NotFoundException(ErrorCode.FILE_EXTENSION_NOT_FOUND);
        }

        return idxFileName;
    }
}


