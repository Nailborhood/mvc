package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtDeleteService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final ArtImgRepository artImgRepository;
    private final ArtRefRepository artRefRepository;

    public CommonResponseDto<Object> deleteArt(List<MultipartFile> multipartFileList, Long artRefId) {

        // ArtRef 정보 get
        ArtRef artRef = artRefRepository.findById(artRefId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // ArtRef 삭제(isDeleted -> true)
        artRefRepository.deleteByArtRefId(artRefId, true);

        // 이미지 삭제(isDeleted -> true)
        deleteArtImg(multipartFileList, artRef);

        // TODO: 좋아요 삭제(isDeleted -> true)

        return commonService.successResponse(SuccessCode.ART_DELETE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    private void deleteArtImg(List<MultipartFile> multipartFileList, ArtRef artRef){

        Long artRefId = artRef.getArtRefId();

        if (multipartFileList != null) {
            // url 값 삭제
            List<ArtImg> artImgList = artImgRepository.findByArtRefId(artRefId);
            for (ArtImg artImg : artImgList) {

                // s3 이미지 삭제
                String artImgUrl = artImg.getImgPath();
                s3UploadService.deleteShopImg(artImgUrl);

                // DB isDeleted true 변경
                artImgRepository.deleteByArtImgId(artImg.getArtImgId(), true);
            }
        }
    }
}
