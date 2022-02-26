package com.todayhouse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.global.common.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public abstract class IntegrationBase {
    @Autowired
    ObjectMapper objectMapper;

    public BaseResponse getResponseFromMvcResult(MvcResult mvcResult) throws Exception {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, BaseResponse.class);
    }
}
