package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtImg;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtImgRepository;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtDeleteService {

    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final ArtImgRepository artImgRepository;
    private final ArtRefRepository artRefRepository;
    private final ArtLikeRepository artLikeRepository;
    private final CategoryArtRepository categoryArtRepository;
    private final MemberRepository memberRepository;



    @Transactional
    public CommonResponseDto<Object> deleteArt(Long memberId, Long artRefId) {

        // 멤버 확인
        memberRepository.findByMemberIdAndIsDeleted(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // ArtRef 정보 get
        ArtRef artRef = artRefRepository.findById(artRefId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // 이미지 삭제(isDeleted -> true)
        List<ArtImg> artImgList = artRef.getArtImgList();

        for (ArtImg artImg : artImgList){
            s3UploadService.deleteArtImg(artImg.getImgPath());
            artImgRepository.deleteByArtImgId(artImg.getArtImgId(), true);
        }

        // 좋아요 수 0 및 ArtLike 삭제
        artRefRepository.makeLikeCountZero(artRefId);
        artLikeRepository.deleteByArtRefId(artRefId);

        // CategoryArt 삭제
        categoryArtRepository.deleteByArtRefArtRefId(artRefId);

        // ArtRef 삭제(isDeleted -> true)
        artRefRepository.deleteByArtRefId(artRefId, true);

        return commonService.successResponse(SuccessCode.ART_DELETE_SUCCESS.getDescription(), HttpStatus.OK, null);
    }
}
