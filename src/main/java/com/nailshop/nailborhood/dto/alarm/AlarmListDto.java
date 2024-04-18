package com.nailshop.nailborhood.dto.alarm;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class AlarmListDto {
    List<AlarmResponseDto> alarmResponseDtoList;
    private PaginationDto paginationDto;
}
