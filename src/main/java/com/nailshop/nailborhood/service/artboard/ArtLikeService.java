package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtLike;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.artboard.ArtLikeResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtLikeRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
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
    public CommonResponseDto<Object> likeArt(MemberDetails memberDetails, Long artRefId) {

        // 멤버 확인
        Member member = memberRepository.findByMemberIdAndIsDeleted(memberDetails.getMember().getMemberId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // ArtRef 정보 get
        ArtRef artRef = artRefRepository.findById(artRefId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // artLike 존재 여부 확인
        Optional<ArtLike> artLike = artLikeRepository.findByMemberMemberIdAndArtRefArtRefId(member.getMemberId(), artRefId);

        if (artLike.isEmpty()){
            // 첫 좋아요
            ArtLike newArtLike = ArtLike.builder()
                    .status(true)
                    .artRef(artRef)
                    .member(member)
                    .build();

            artLikeRepository.save(newArtLike);
            artRefRepository.increaseLikeCount(artRefId);

            ArtLikeResponseDto artLikeResponseDto = ArtLikeResponseDto.builder()
                    .status(true)
                    .build();

            return commonService.successResponse(SuccessCode.ART_LIKE_SUCCESS.getDescription(), HttpStatus.OK, artLikeResponseDto);

        } else {

            ArtLike existingArtLike = artLike.get();

            if (existingArtLike.getStatus()){
                // 좋아요 true -> false
                artLikeRepository.toggleStatus(existingArtLike.getArtLikeId(), false);
                artRefRepository.decreaseLikeCount(artRefId);

                ArtLikeResponseDto artLikeResponseDto = ArtLikeResponseDto.builder()
                        .status(false)
                        .build();

                return commonService.successResponse(SuccessCode.ART_DISLIKE_SUCCESS.getDescription(), HttpStatus.OK, artLikeResponseDto);
            } else {
                // 좋아요 false -> true
                artLikeRepository.toggleStatus(existingArtLike.getArtLikeId(), true);
                artRefRepository.increaseLikeCount(artRefId);

                ArtLikeResponseDto artLikeResponseDto = ArtLikeResponseDto.builder()
                        .status(true)
                        .build();

                return commonService.successResponse(SuccessCode.ART_LIKE_SUCCESS.getDescription(), HttpStatus.OK, artLikeResponseDto);
            }
        }
    }
}
