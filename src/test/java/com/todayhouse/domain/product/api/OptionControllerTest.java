package com.todayhouse.domain.product.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.product.dao.ChildOptionRepository;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ParentOptionSaveRequest;
import com.todayhouse.domain.product.dto.request.ParentOptionUpdateRequest;
import com.todayhouse.domain.product.dto.response.ParentOptionResponse;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.global.common.BaseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OptionControllerTest extends IntegrationBase {

    @Autowired
    OptionController optionController;

    @Autowired
    ParentOptionRepository parentOptionRepository;

    @Autowired
    ChildOptionRepository childOptionRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @PersistenceContext
    EntityManager em;

    Product product;
    ParentOption parentOption;
    ChildOption childOption;

    @BeforeEach
    void setUp() {
        Seller seller = Seller.builder().brand("brand").build();
        em.persist(seller);

        product = Product.builder().seller(seller).option1("p1").option2("c1").selectionOption("s1").build();
        parentOption = ParentOption.builder().product(product).content("ppp").build();
        childOption = ChildOption.builder().parent(parentOption).content("ccc").price(10000).stock(1).build();

        em.persist(product);

        em.flush();
        em.clear();
    }

    @Test
    @WithMockUser(roles = "USER")
    void ParentOption_저장() throws Exception {
        String url = "http://localhost:8080/options/parent";
        ParentOptionSaveRequest request = ParentOptionSaveRequest.builder().content("p1").price(1000).stock(10).productId(product.getId()).build();

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        ParentOptionResponse parentOptionResponse = objectMapper.convertValue(response.getResult(), ParentOptionResponse.class);
        assertThat(parentOptionResponse.getContent()).isEqualTo("p1");
    }

    @Test
    void ParentOption_찾기() throws Exception {
        String url = "http://localhost:8080/options/parents?productId=" + product.getId();

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Set<ParentOptionResponse> set = objectMapper.readValue(objectMapper.writeValueAsString(response.getResult()), new TypeReference<>() {
        });
        assertThat(set.size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    void ParentOption_수정() throws Exception {
        String url = "http://localhost:8080/options/parent";
        ParentOptionUpdateRequest request = ParentOptionUpdateRequest.builder()
                .id(parentOption.getId()).content("new").price(0).stock(0).build();

        MvcResult mvcResult = mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        ParentOptionResponse options = objectMapper.convertValue(response.getResult(), ParentOptionResponse.class);
        assertThat(options.getContent()).isEqualTo("new");
        assertThat(options.getPrice()).isEqualTo(0);
        assertThat(options.getStock()).isEqualTo(0);
    }

    @Test
    @WithMockUser(roles = "USER")
    void ParentOption_삭제() throws Exception {
        String url = "http://localhost:8080/options/parents/" + parentOption.getId();

        mockMvc.perform(delete(url))
                .andExpect(status().isOk())
                .andDo(print());

        ParentOption parentOption = parentOptionRepository.findById(this.parentOption.getId()).orElse(null);
        assertThat(parentOption).isNull();
    }
}
