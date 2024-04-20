package com.nailshop.nailborhood.service.alarm;

import com.nailshop.nailborhood.domain.alarm.Alarm;
import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.chat.ChattingRoom;
import com.nailshop.nailborhood.domain.member.Admin;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.member.Owner;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.dto.alarm.AlarmResponseDto;
import com.nailshop.nailborhood.dto.alarm.AlarmListDto;
import com.nailshop.nailborhood.dto.alarm.AlarmRequestDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.alarm.AlarmRepository;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.chat.ChattingRoomRepository;
import com.nailshop.nailborhood.repository.member.AdminRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.repository.member.OwnerRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.common.TimeFormatter;
import com.nailshop.nailborhood.type.AlarmType;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    private final CommonService commonService;
    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;
    private final ArtRefRepository artRefRepository;
    private final AdminRepository adminRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ReviewRepository reviewRepository;

    // owner 알람 조회
    public CommonResponseDto<Object> getAllListByOwner(Member member, int page, int size, String criteria) {

        // 정렬기준 설정
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(criteria)
                                                                  .descending());


//        List<Alarm> alarmList = alarmRepository.findAllByMemberId(member.getMemberId());
        Page<Alarm> alarmPage = alarmRepository.findAllByMemberId(member.getMemberId(), pageable);

        List<Alarm> alarmList = alarmPage.getContent();
        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmList) {
            // 공통
            String formattedTime = TimeFormatter.formatRelative(alarm.getCreatedAt());
            AlarmResponseDto.AlarmResponseDtoBuilder builder = AlarmResponseDto.builder()
                                                                               .alarmType(alarm.getAlarmType())
                                                                               .alarmId(alarm.getAlarmId())
                                                                               .url(alarm.getUrl())
                                                                               .receiverId(alarm.getReceiver()
                                                                                                .getMemberId())
                                                                               .senderId(alarm.getSender()
                                                                                              .getMemberId())
                                                                               .senderName(alarm.getSender()
                                                                                                .getName())
                                                                               .createdAt(formattedTime)
                                                                               .shopName(alarm.getReceiver()
                                                                                              .getOwner()
                                                                                              .getShop()
                                                                                              .getName())
                                                                               .isChecked(alarm.isChecked());


            // 아트판 정보
            if (alarm.getAlarmType() == AlarmType.LIKE_ART) {
                String[] parts = alarm.getUrl()
                                      .split("/");
                Long artRefId = parts.length > 3 ? Long.valueOf(parts[parts.length - 1]) : null;
                if (artRefId != null) {
                    ArtRef artRef = artRefRepository.findById(artRefId)
                                                    .orElseThrow(() -> new NotFoundException(ErrorCode.ART_NOT_FOUND));
                    builder.artRefName(artRef.getName());
                }

            }
            alarmResponseDtoList.add(builder.build());

        }

        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(alarmPage.getTotalPages())
                                                   .totalElements(alarmPage.getTotalElements())
                                                   .pageNo(alarmPage.getNumber())
                                                   .isLastPage(alarmPage.isLast())
                                                   .build();

        AlarmListDto alarmListDto = AlarmListDto.builder()
                                                .alarmResponseDtoList(alarmResponseDtoList)
                                                .paginationDto(paginationDto)
                                                .build();
        return commonService.successResponse(SuccessCode.ALARM_FOUND_SUCCESS.getDescription(), HttpStatus.OK, alarmListDto);
    }


    // 관리자 알람 조회
    public CommonResponseDto<Object> getAllListByAdmin(Member member, int page, int size, String criteria) {
        //  정렬기준 설정
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(criteria)
                                                                  .descending());


        //  List<Alarm> alarmList = alarmRepository.findAllByMemberId(member.getMemberId());
        Page<Alarm> alarmPage = alarmRepository.findAllByMemberId(member.getMemberId(), pageable);

        List<Alarm> alarmList = alarmPage.getContent();
        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmList) {
            // 공통
            String formattedTime = TimeFormatter.formatRelative(alarm.getCreatedAt());
            AlarmResponseDto.AlarmResponseDtoBuilder builder = AlarmResponseDto.builder()
                                                                               .alarmType(alarm.getAlarmType())
                                                                               .alarmId(alarm.getAlarmId())
                                                                               .url(alarm.getUrl())
                                                                               .receiverId(alarm.getReceiver()
                                                                                                .getMemberId())
                                                                               .senderId(alarm.getSender()
                                                                                              .getMemberId())
                                                                               .senderName(alarm.getSender()
                                                                                                .getName())
                                                                               .createdAt(formattedTime)
                                                                               .isChecked(alarm.isChecked())
                                                                               .shopName(alarm.getSender()
                                                                                              .getOwner()
                                                                                              .getShop()
                                                                                              .getName());


            alarmResponseDtoList.add(builder.build());
        }

        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(alarmPage.getTotalPages())
                                                   .totalElements(alarmPage.getTotalElements())
                                                   .pageNo(alarmPage.getNumber())
                                                   .isLastPage(alarmPage.isLast())
                                                   .build();

        AlarmListDto alarmListDto = AlarmListDto.builder()
                                                .alarmResponseDtoList(alarmResponseDtoList)
                                                .paginationDto(paginationDto)
                                                .build();
        return commonService.successResponse(SuccessCode.ALARM_FOUND_SUCCESS.getDescription(), HttpStatus.OK, alarmListDto);
    }

    // 일반 유저 알람 조회
    public CommonResponseDto<Object> getAllListByUser(Member member, int page, int size, String criteria) {

        // 정렬기준 설정
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(criteria)
                                                                  .descending());


//        List<Alarm> alarmList = alarmRepository.findAllByMemberId(member.getMemberId());
        Page<Alarm> alarmPage = alarmRepository.findAllByMemberId(member.getMemberId(), pageable);

        List<Alarm> alarmList = alarmPage.getContent();
        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();

        for (Alarm alarm : alarmList) {
            // 공통
            String formattedTime = TimeFormatter.formatRelative(alarm.getCreatedAt());
            AlarmResponseDto.AlarmResponseDtoBuilder builder = AlarmResponseDto.builder()
                                                                               .alarmType(alarm.getAlarmType())
                                                                               .alarmId(alarm.getAlarmId())
                                                                               .url(alarm.getUrl())
                                                                               .receiverId(alarm.getReceiver()
                                                                                                .getMemberId())
                                                                               .senderId(alarm.getSender()
                                                                                              .getMemberId())
                                                                               .senderName(alarm.getSender()
                                                                                                .getName())
                                                                               .createdAt(formattedTime)
                                                                               .isChecked(alarm.isChecked());


            // 리뷰 정보
/*            if (alarm.getAlarmType() == AlarmType.LIKE_REVIEW) {
                String[] parts = alarm.getUrl()
                                      .split("/");
                // /review/inquiry/${reviewId}?shopId=${shopId}

                Long reviewId = parts.length > 3 ? Long.valueOf(parts[parts.length - 1]) : null;
                if (reviewId != null) {
                    Review review = reviewRepository.findById(reviewId)
                                             .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

                    builder.shopName(review.getShop().getName());
                }

            }*/

            if (alarm.getAlarmType() == AlarmType.LIKE_REVIEW) {
                String url = alarm.getUrl();
                String[] parts = url.split("/");

                // 마지막 경로 세그먼트 추출
                String lastSegment = parts[parts.length - 1];
                // 쿼리 스트링 제거 (경로 세그먼트에서 첫 번째 '?' 전까지 추출)
                String reviewIdString = lastSegment.contains("?") ? lastSegment.substring(0, lastSegment.indexOf('?')) : lastSegment;

                Long reviewId = null;

                reviewId = Long.parseLong(reviewIdString);


                if (reviewId != null) {
                    Review review = reviewRepository.findById(reviewId)
                                                    .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

                    builder.shopName(review.getShop()
                                           .getName());
                }
            }
            alarmResponseDtoList.add(builder.build());

        }

        PaginationDto paginationDto = PaginationDto.builder()
                                                   .totalPages(alarmPage.getTotalPages())
                                                   .totalElements(alarmPage.getTotalElements())
                                                   .pageNo(alarmPage.getNumber())
                                                   .isLastPage(alarmPage.isLast())
                                                   .build();

        AlarmListDto alarmListDto = AlarmListDto.builder()
                                                .alarmResponseDtoList(alarmResponseDtoList)
                                                .paginationDto(paginationDto)
                                                .build();
        return commonService.successResponse(SuccessCode.ALARM_FOUND_SUCCESS.getDescription(), HttpStatus.OK, alarmListDto);
    }


    // 알람 저장

    public Alarm saveAlarm(Member member, AlarmRequestDto alarmRequestDto) {
        Member receiver = memberRepository.findByMemberNameAndIsDeleted(alarmRequestDto.getReceiver())
                                          .orElseThrow(() -> new NotFoundException(ErrorCode.OWNER_NOT_FOUND));

        // alarmType 으로 변경
//            AlarmType alarmType = AlarmType.fromString(alarmRequestDto.getAlarmType());

        Alarm alarm = Alarm.builder()
                           .alarmType(alarmRequestDto.getAlarmType())
                           .isChecked(false)
                           .sender(member)
                           .receiver(receiver)
                           .url(alarmRequestDto.getUrl())
                           .build();

        alarmRepository.save(alarm);
        return alarm;
    }

    public int getAlarmCount(Member member) {
        Long receiverId = member.getMemberId();
        int alarmCount = alarmRepository.countByReceiverId(receiverId);
        return alarmCount;
    }

    // 알람 체크 완료 상태로 변경
    @Transactional
    public void updateIsChecked(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.ALARM_NOT_FOUND));

        alarm.updateIsChecked(true);
    }


    // 알람에서 필요한 매장 owner 정보
    public Member getOwnerInfo(Long shopId) {
        Owner owner = ownerRepository.findByShopId(shopId);
        return memberRepository.findByOwnerId(owner.getOwnerId());
    }

    // 알람에서 필요한 관리자 정보 (채팅)
    public Member getAdminInfo(Long roomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId)
                                                          .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));


        Admin admin = adminRepository.findById(chattingRoom.getAdmin()
                                                           .getAdminId())
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return memberRepository.findByAdminId(admin.getAdminId());
    }

    // 알람에서 필요한 owner 정보 (채팅)
    public Member getOwnerInfoByChat(Long roomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId)
                                                          .orElseThrow(() -> new NotFoundException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        Owner owner = ownerRepository.findById(chattingRoom.getOwner()
                                                           .getOwnerId())
                                     .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        return memberRepository.findByOwnerId(owner.getOwnerId());
    }

    // 알람에서 필요한 user 정보 ( 리뷰 좋아요 )
/*    public Member getUserInfo(Long reviewId) {
        Review review = reviewRepository.findByReviewId(reviewId);

       Member user =  memberRepository.findByMemberIdAndIsDeleted(review.getCustomer().getMember().getMemberId()).orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));

       return user;
    }*/
}

