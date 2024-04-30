package com.nailshop.nailborhood.service.artboard;

import com.nailshop.nailborhood.domain.artboard.ArtBookMark;
import com.nailshop.nailborhood.domain.artboard.ArtLike;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.artboard.ArtBookMarkResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtLikeResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtBookMarkRepository;
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
    private final ArtBookMarkRepository artBookMarkRepository;


    @Transactional
    public CommonResponseDto<Object> likeArt(Long memberId, Long artRefId) {

        // 멤버 확인
        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
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

    // 아트 북마크
    @Transactional
    public CommonResponseDto<Object> bookMarkArt(Long memberId, Long artRefId) {
        // 멤버 확인
        Member member = memberRepository.findByMemberIdAndIsDeleted(memberId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // ArtRef 정보 get
        ArtRef artRef = artRefRepository.findById(artRefId)
                                        .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));

        // artLike 존재 여부 확인
        Optional<ArtBookMark> artBookMark = artBookMarkRepository.findByMemberMemberIdAndArtRefArtRefId(member.getMemberId(), artRefId);

        if (artBookMark.isEmpty()){
            // 첫 좋아요
            ArtBookMark bookMark = ArtBookMark.builder()
                                        .status(true)
                                        .artRef(artRef)
                                        .member(member)
                                        .build();

            artBookMarkRepository.save(bookMark);
            artRefRepository.increaseBookMarkCount(artRefId);

            ArtBookMarkResponseDto artBookMarkResponseDto = ArtBookMarkResponseDto.builder()
                                                                          .status(true)
                                                                          .build();

            return commonService.successResponse(SuccessCode.ART_LIKE_SUCCESS.getDescription(), HttpStatus.OK, artBookMarkResponseDto);

        } else {

            ArtBookMark existingArtBookMark = artBookMark.get();

            if (existingArtBookMark.getStatus()){
                // 좋아요 true -> false
                artBookMarkRepository.toggleStatus(existingArtBookMark.getArtBookMarkId(), false);
                artRefRepository.decreaseBookMarkCount(artRefId);

                ArtBookMarkResponseDto artBookMarkResponseDto = ArtBookMarkResponseDto.builder()
                                                                                      .status(true)
                                                                                      .build();

                return commonService.successResponse(SuccessCode.ART_DISLIKE_SUCCESS.getDescription(), HttpStatus.OK, artBookMarkResponseDto);
            } else {
                // 좋아요 false -> true
                artBookMarkRepository.toggleStatus(existingArtBookMark.getArtBookMarkId(), true);
                artRefRepository.increaseBookMarkCount(artRefId);

                ArtBookMarkResponseDto artBookMarkResponseDto = ArtBookMarkResponseDto.builder()
                                                                                      .status(true)
                                                                                      .build();

                return commonService.successResponse(SuccessCode.ART_LIKE_SUCCESS.getDescription(), HttpStatus.OK, artBookMarkResponseDto);
            }
        }
    }
}
