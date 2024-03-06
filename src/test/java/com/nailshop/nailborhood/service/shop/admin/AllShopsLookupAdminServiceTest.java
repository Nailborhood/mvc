package com.nailshop.nailborhood.service.shop.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsListResponseDto;
import com.nailshop.nailborhood.dto.shop.response.admin.AllShopsLookupResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AllShopsLookupAdminServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllShops() throws Exception {
        // 정렬 기준을 reviewCnt로 설정
        String sort = "DESC";
        String criteria = "rateAvg";
        // 첫 페이지의 크기를 10으로 설정
        int size = 10;
        int page = 1;

        // 요청을 수행

        MvcResult mvcResult = mockMvc.perform(get("/nailshop/admin/shopList")
                                             .param("page", String.valueOf(page))
                                             .param("size", String.valueOf(size))
                                             .param("sort", sort)
                                             .param("orderby", criteria))
                                     .andExpect(status().isOk())
                                     .andReturn();

        // 응답 본문을 가져옴
        String response = mvcResult.getResponse().getContentAsString();

        // 응답 본문을 객체로 변환

        ResultDto<AllShopsListResponseDto> resultDto = objectMapper.readValue(response, new TypeReference<ResultDto<AllShopsListResponseDto>>() {});

        // 매장 목록을 가져옴
        AllShopsListResponseDto allShopsListResponseDto = resultDto.getData();

        // 매장 목록이 비어있지 않은지 확인
        assertFalse(allShopsListResponseDto.getAllShopsLookupResponseDtoList().isEmpty());

        // 매장 목록이 reviewCnt 기준으로 내림차순 정렬되어 있는지 확인
        int previousReviewCnt = Integer.MAX_VALUE;
        for (AllShopsLookupResponseDto shop : allShopsListResponseDto.getAllShopsLookupResponseDtoList()) {
            assertTrue(previousReviewCnt >= shop.getReviewCnt());
            previousReviewCnt = shop.getReviewCnt();
        }
    }
}
