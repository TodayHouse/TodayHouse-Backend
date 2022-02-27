package com.todayhouse.domain.category.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategorySaveResponse;
import com.todayhouse.domain.category.dto.response.CategoryUpdateResponse;
import com.todayhouse.global.common.BaseResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)//@BeforeAll 사용
class CategoryControllerTest extends IntegrationBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        categoryRepository.deleteAll();
        Category p1 = Category.builder().name("p1").build();
        Category c1 = Category.builder().name("c1").parent(p1).build();
        Category c2 = Category.builder().name("c2").parent(p1).build();
        Category cc1 = Category.builder().name("cc1").parent(c1).build();

        categoryRepository.save(p1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("새 카테고리 저장")
    void saveCategory() throws Exception {
        String url = "http://localhost:8080/categories";
        CategorySaveRequest request = CategorySaveRequest.builder().parentName("c2").name("cc2").build();
        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse base = getResponseFromMvcResult(mvcResult);
        CategorySaveResponse saveResponse = objectMapper.convertValue(base.getResult(), CategorySaveResponse.class);
        assertThat(saveResponse.getName()).isEqualTo("cc2");
        assertThat(saveResponse.getParentName()).isEqualTo("c2");
    }

    @Test
    @DisplayName("모든 카테고리 찾기")
    void findAll() throws Exception {
        String url = "http://localhost:8080/categories";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("특정 하위 카테고리 찾기")
    void findAllSub() throws Exception {
        String url = "http://localhost:8080/categories/c1";
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 수정")
    void updateCategoryName() throws Exception {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder().name("c2").changeName("newc2").build();
        String url = "http://localhost:8080/categories";
        MvcResult mvcResult = mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        CategoryUpdateResponse categoryUpdateResponse = objectMapper.convertValue(baseResponse.getResult(), CategoryUpdateResponse.class);
        assertThat(categoryUpdateResponse.getName()).isEqualTo("newc2");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 삭제")
    void deleteCategory() throws Exception {
        String url = "http://localhost:8080/categories/p1";
        mockMvc.perform(delete(url))
                .andExpect(status().isOk());

        int size = categoryRepository.findAll().size();
        assertThat(size).isEqualTo(0);
    }
}