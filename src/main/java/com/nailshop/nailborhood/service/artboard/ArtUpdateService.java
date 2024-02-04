package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.dto.artboard.ArtUpdateDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtUpdateService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final ArtImgRepository artImgRepository;
    private final ArtRefRepository artRefRepository;

    @Transactional
    public CommonResponseDto<Object> updateArt(List<MultipartFile> multipartFileList, ArtUpdateDto artUpdateDto, Long artRefId) {

        // artRef 정보 get
        ArtRef artRef = artRefRepository.findById(artRefId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // 변경사항 체크 후 수정
        String name = artUpdateDto.getName().equals(artRef.getName()) ? artRef.getName() : artUpdateDto.getName();
        String content = artUpdateDto.getContent().equals(artRef.getContent()) ? artRef.getContent() : artUpdateDto.getContent();

        artRef.updateArtRef(name, content);

        // 기존 이미지 삭제 후 새 이미지 저장
        updateArtRefImg(multipartFileList, artRef);

        return commonService.successResponse(SuccessCode.ART_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    private void updateArtRefImg(List<MultipartFile> multipartFileList, ArtRef artRef) {

        Long artRefId = artRef.getArtRefId();

        if (multipartFileList != null) {
            // url 값 삭제
            List<ArtImg> artImgList = artImgRepository.findByArtRefId(artRefId);
            for (ArtImg artImg : artImgList) {
                String artImgUrl = artImg.getImgPath();
                // s3 이미지 삭제
                s3UploadService.deleteShopImg(artImgUrl);
            }

            // DB url 삭제
            artImgRepository.deleteByArtRefId(artRefId);
        }

        // s3에 이미지 업로드
        List<String> artImgUrlList = s3UploadService.artImgUpload(multipartFileList);

        // 이미지 번호 1번 부터 시작
        int imgNum = 1;

        for(String imgPath : artImgUrlList){
            ArtImg artImg = ArtImg.builder()
                    .imgPath(imgPath)
                    .imgNum(imgNum)
                    .isDeleted(false)
                    .artRef(artRef)
                    .build();

            artImgRepository.save(artImg);
            imgNum++;
        }
    }
}
