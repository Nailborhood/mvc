package com.nailshop.nailborhood.controller.alarm;

import com.nailshop.nailborhood.domain.alarm.Alarm;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.alarm.AlarmListDto;
import com.nailshop.nailborhood.dto.alarm.AlarmRequestDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.alarm.AlarmService;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    private final MemberService memberService;


    // 알람 전체 조회
    @GetMapping("/alarm/list")
    public String getShopReviewList(Model model,
                                    @AuthenticationPrincipal MemberDetails memberDetails, Authentication authentication,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria
    ) {


        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        Member member = memberService.getMemberInfo(sessionDto.getId());

        if (member.getRole()
                  .equals(Role.ROLE_ADMIN)) {
            CommonResponseDto<Object> alarmList = alarmService.getAllListByAdmin(member, page, size, criteria);
            ResultDto<AlarmListDto> resultDto = ResultDto.in(alarmList.getStatus(), alarmList.getMessage());
            resultDto.setData((AlarmListDto) alarmList.getData());
            model.addAttribute("resultDto", resultDto);
            model.addAttribute("orderby", criteria);
            model.addAttribute("size", size);

            return "alarm/admin_alarm_list";
        } else if (member.getRole()
                         .equals(Role.ROLE_OWNER)) {
            CommonResponseDto<Object> alarmList = alarmService.getAllListByOwner(member, page, size, criteria);
            ResultDto<AlarmListDto> resultDto = ResultDto.in(alarmList.getStatus(), alarmList.getMessage());
            resultDto.setData((AlarmListDto) alarmList.getData());
            model.addAttribute("resultDto", resultDto);
            model.addAttribute("orderby", criteria);
            model.addAttribute("size", size);

            return "alarm/owner_alarm_list";
        } else {
            CommonResponseDto<Object> alarmList = alarmService.getAllListByUser(member, page, size, criteria);
            ResultDto<AlarmListDto> resultDto = ResultDto.in(alarmList.getStatus(), alarmList.getMessage());
            resultDto.setData((AlarmListDto) alarmList.getData());
            model.addAttribute("resultDto", resultDto);
            model.addAttribute("orderby", criteria);
            model.addAttribute("size", size);

            return "alarm/my_alarm_list";
        }

    }

    // 알람 등록
    @PostMapping("/alarm/save")
    public ResponseEntity<?> saveAlarm(@AuthenticationPrincipal MemberDetails memberDetails,
                                       @RequestBody AlarmRequestDto alarmRequestDto,
                                       Authentication authentication) {

        SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);

        Member member = memberService.getMemberInfo(sessionDto.getId());
        try {
            Alarm alarm = alarmService.saveAlarm(member, alarmRequestDto);
            return ResponseEntity.ok(Collections.singletonMap("alarmId", alarm.getAlarmId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error saving alarm");
        }
    }

    // 알람 카운트 조회
//    @GetMapping("/alarm/count")
//    public int getAlarmCount(@AuthenticationPrincipal MemberDetails memberDetails) {
//
//       if(memberDetails !=null) {
//           Member member =  memberDetails.getMember();
//           return alarmService.getAlarmCount(member);
//       }
//
//        return 0;
//    }

    // 알람 카운트 조회
    @GetMapping("/alarm/count")
    public ResponseEntity<?> getAlarmCount(@AuthenticationPrincipal MemberDetails memberDetails, Authentication authentication) {
        int count = 0;

        if (authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
            Member member = memberService.getMemberInfo(sessionDto.getId());
            count = alarmService.getAlarmCount(member);
        }

        return ResponseEntity.ok()
                             .body(Collections.singletonMap("count", count));
    }

    // 알람 상태 변경
    @PostMapping("/alarm/isChecked")
    public ResponseEntity<?> updateIsChecked(@RequestBody Map<String, Long> payload) {
        Long alarmId = payload.get("alarmId");
        try {
            // 알람 상태 true 로 변경( 읽음 )
            alarmService.updateIsChecked(alarmId);
            return ResponseEntity.ok()
                                 .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error updating alarm");
        }
    }

}
