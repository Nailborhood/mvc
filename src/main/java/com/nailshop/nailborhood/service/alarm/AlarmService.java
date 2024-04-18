package com.nailshop.nailborhood.service.alarm;

import com.nailshop.nailborhood.domain.alarm.Alarm;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.alarm.AlarmResponseDto;
import com.nailshop.nailborhood.dto.alarm.AlarmListDto;
import com.nailshop.nailborhood.dto.alarm.AlarmRequestDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.alarm.AlarmRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
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


    public CommonResponseDto<Object> getAllList(Member member, int page, int size, String criteria) {

        // 정렬기준 설정
        PageRequest pageable =  PageRequest.of(page - 1, size, Sort.by(criteria).descending());


//        List<Alarm> alarmList = alarmRepository.findAllByMemberId(member.getMemberId());
        Page<Alarm> alarmPage = alarmRepository.findAllByMemberId(member.getMemberId(), pageable);

        List<Alarm> alarmList = alarmPage.getContent();
        List<AlarmResponseDto> alarmResponseDtoList = new ArrayList<>();
        for (Alarm alarm : alarmList) {
            String formattedTime = TimeFormatter.formatRelative(alarm.getCreatedAt());
            AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                                                                .alarmType(alarm.getAlarmType().getDescription())
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
                                                                .build();
            alarmResponseDtoList.add(alarmResponseDto);
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

    public Alarm saveAlarm(Member member, AlarmRequestDto alarmRequestDto) {
        Member receiver = memberRepository.findByMemberNameAndIsDeleted(alarmRequestDto.getReceiver())
                                          .orElseThrow(() -> new NotFoundException(ErrorCode.OWNER_NOT_FOUND));

        // alarmType 으로 변경
        AlarmType alarmType = AlarmType.fromString(alarmRequestDto.getAlarmType());

        Alarm alarm = Alarm.builder()
                           .alarmType(alarmType)
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
}
