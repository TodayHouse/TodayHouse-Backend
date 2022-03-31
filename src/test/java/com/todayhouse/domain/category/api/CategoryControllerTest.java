package com.todayhouse.domain.category.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategoryPathResponse;
import com.todayhouse.domain.category.dto.response.CategoryResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    Category p1, c1, c2, cc1, p2;

    @BeforeAll
    void setUp() {
        categoryRepository.deleteAll();
        p1 = Category.builder().name("p1").build();
        c1 = Category.builder().name("c1").parent(p1).build();
        c2 = Category.builder().name("c2").parent(p1).build();
        cc1 = Category.builder().name("cc1").parent(c1).build();
        p2 = Category.builder().name("p2").build();

        categoryRepository.save(p1);
        categoryRepository.save(p2);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("새 카테고리 저장")
    void saveCategory() throws Exception {
        String url = "http://localhost:8080/categories";
        CategorySaveRequest request = CategorySaveRequest.builder().parentId(c2.getId()).name("cc2").build();
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
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        List<CategoryResponse> categories= objectMapper.readValue(objectMapper.writeValueAsString(response.getResult()), new TypeReference<>() {
        });
        assertThat(categories.size()).isEqualTo(2);
        categories.stream().forEach(c->{
            if(c.getSubCategories().size()==0)
                assertThat(c.getName()).isEqualTo("p2");
            else{
                assertThat(c.getName()).isEqualTo("p1");
                assertThat(c.getSubCategories().size()).isEqualTo(2);
                c.getSubCategories().stream().forEach(cc->{
                    if(cc.getSubCategories().size()==0)
                        assertThat(cc.getName()).isEqualTo("c2");
                    else{
                        assertThat(cc.getName()).isEqualTo("c1");
                        assertThat(cc.getSubCategories().size()).isEqualTo(1);
                        assertThat(cc.getSubCategories().get(0).getName()).isEqualTo("cc1");
                    }
                });
            }
        });
    }

    @Test
    @DisplayName("특정 하위 카테고리 찾기")
    void findAllSub() throws Exception {
        String url = "http://localhost:8080/categories/" + p1.getId();
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponse response = getResponseFromMvcResult(mvcResult);
        CategoryResponse parent = objectMapper.convertValue(response.getResult(), CategoryResponse.class);
        List<CategoryResponse> children = parent.getSubCategories();
        assertThat(parent.getName()).isEqualTo(p1.getName());
        assertThat(children.size()).isEqualTo(2);
        assertTrue(children.stream().anyMatch(c -> c.getSubCategories().size() == 1 &&
                c.getSubCategories().get(0).getName().equals("cc1")));
    }

    @Test
    @DisplayName("category_id부터 root까지의 경로")
    void findRootPath() throws Exception {
        String url = "http://localhost:8080/categories/path/" + cc1.getId();
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        CategoryPathResponse pathResponse = objectMapper.readValue(objectMapper.writeValueAsString(response.getResult()), new TypeReference<>() {
        });
        List<CategoryResponse> categoryPath = pathResponse.getCategoryPath();
        assertThat(categoryPath.size()).isEqualTo(3);
        assertThat(categoryPath.get(0).getName()).isEqualTo(p1.getName());
        assertThat(categoryPath.get(1).getName()).isEqualTo(c1.getName());
        assertThat(categoryPath.get(2).getName()).isEqualTo(cc1.getName());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("카테고리 수정")
    void updateCategoryName() throws Exception {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder().id(c2.getId()).changeName("newc2").build();
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
        String url = "http://localhost:8080/categories/" + p1.getId();
        mockMvc.perform(delete(url))
                .andExpect(status().isOk());

        int size = categoryRepository.findAll().size();
        assertThat(size).isEqualTo(1);
    }
}