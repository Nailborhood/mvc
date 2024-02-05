package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtLike;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.artboard.ArtLikeDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArtLikeService {

    private final CommonService commonService;
    private final ArtRefRepository artRefRepository;
    private final ArtLikeRepository artLikeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommonResponseDto<Object> likeArt(Long artRefId) {

        // TODO: 토큰에서 멤버 정보 get
        Long memberId = 1L;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // ArtRef 정보 get
        ArtRef artRef = artRefRepository.findById(artRefId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // artLike 존재 여부 확인
        Optional<ArtLike> artLike = artLikeRepository.findByMemberMemberIdAndArtRefArtRefId(memberId, artRefId);

        if (artLike.isEmpty()){
            // 첫 좋아요
            ArtLike newArtLike = ArtLike.builder()
                    .status(true)
                    .artRef(artRef)
                    .member(member)
                    .build();

            artLikeRepository.save(newArtLike);
            artRefRepository.increaseLikeCount(artRefId);

            ArtLikeDto artLikeDto = ArtLikeDto.builder()
                    .status(true)
                    .build();

            return commonService.successResponse(SuccessCode.ART_LIKE_SUCCESS.getDescription(), HttpStatus.OK, artLikeDto);

        } else {

            ArtLike existingArtLike = artLike.get();

            if (existingArtLike.getStatus()){
                // 좋아요 true -> false
                artLikeRepository.toggleStatus(existingArtLike.getArtLikeId(), false);
                artRefRepository.decreaseLikeCount(artRefId);

                ArtLikeDto artLikeDto = ArtLikeDto.builder()
                        .status(false)
                        .build();

                return commonService.successResponse(SuccessCode.ART_DISLIKE_SUCCESS.getDescription(), HttpStatus.OK, artLikeDto);
            } else {
                // 좋아요 false -> true
                artLikeRepository.toggleStatus(existingArtLike.getArtLikeId(), true);
                artRefRepository.increaseLikeCount(artRefId);

                ArtLikeDto artLikeDto = ArtLikeDto.builder()
                        .status(true)
                        .build();

                return commonService.successResponse(SuccessCode.ART_LIKE_SUCCESS.getDescription(), HttpStatus.OK, artLikeDto);
            }
        }
    }
}
