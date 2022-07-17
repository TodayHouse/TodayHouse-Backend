package com.todayhouse.domain.inquiry.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.inquiry.dao.AnswerRepository;
import com.todayhouse.domain.inquiry.dao.InquiryRepository;
import com.todayhouse.domain.inquiry.domain.Answer;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.AnswerSaveRequest;
import com.todayhouse.domain.inquiry.dto.request.InquirySaveRequest;
import com.todayhouse.domain.inquiry.dto.response.InquiryResponse;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InquiryControllerTest extends IntegrationBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AnswerRepository answerRepository;

    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    ObjectMapper objectMapper;

    User user1, user2, user3, seller1;
    Product product1;

    @BeforeEach
    void setUp() {
        Seller seller = sellerRepository.save(Seller.builder().email("seller").build());
        seller1 = userRepository.save(User.builder().email("seller").seller(seller).build());
        product1 = productRepository.save(Product.builder().seller(seller).title("product").build());

        user1 = userRepository.save(User.builder().nickname("test1").email("test1@test").build());
        user2 = userRepository.save(User.builder().nickname("test2").email("test2@test").build());
        user3 = userRepository.save(User.builder().nickname("test3").email("test3@test").build());
    }

    @Test
    @DisplayName("문의 저장")
    void saveInquiry() throws Exception {
        String jwt = jwtTokenProvider.createToken("test1@test", List.of(Role.USER));
        String url = "http://localhost:8080/inquires";
        String content = "문제가 있습니다.";
        InquirySaveRequest inquiryRequest = InquirySaveRequest.builder()
                .category("배송").content(content).productId(product1.getId()).isSecret(false)
                .build();

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(inquiryRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Inquiry inquiry = inquiryRepository.findById(objectMapper.convertValue(response.getResult(), Long.class)).orElse(null);
        assertThat(inquiry.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("문의와 답변 조회")
    void findInquires() throws Exception {
        Answer answer1 = Answer.builder().content("content1").build();
        Answer answer2 = Answer.builder().content("content2").build();
        Answer answer3 = Answer.builder().content("content3").build();
        Inquiry inquiry1 = Inquiry.builder().product(product1).isSecret(true).user(user1).content("content1").answer(answer1).build();
        Inquiry inquiry2 = Inquiry.builder().product(product1).isSecret(true).user(user2).content("content2").answer(answer2).build();
        Inquiry inquiry3 = Inquiry.builder().product(product1).isSecret(false).user(user3).content("content3").answer(answer3).build();
        inquiryRepository.save(inquiry1);
        inquiryRepository.save(inquiry2);
        inquiryRepository.save(inquiry3);

        List<String> names = List.of("tes**", "tes**", "test1");
        List<String> contents = List.of("content3", "비밀글입니다.", "content1");
        String jwt = jwtTokenProvider.createToken("test1@test", List.of(Role.USER));
        String url = "http://localhost:8080/inquires?size=3&page=0&sort=id,DESC";

        MvcResult mvcResult = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        PageDto<InquiryResponse> pageDto = objectMapper.convertValue(response.getResult(), new TypeReference<>() {
        });
        List<InquiryResponse> inquiries = pageDto.getContent();
        assertThat(pageDto.getTotalPages()).isEqualTo(1);
        assertThat(pageDto.getTotalElements()).isEqualTo(3);
        for (int i = 0; i < inquiries.size(); i++) {
            assertThat(inquiries.get(i).getContent()).isEqualTo(contents.get(i));
            assertThat(inquiries.get(i).getUserName()).isEqualTo(names.get(i));
            assertThat(inquiries.get(i).getAnswer()).isEqualTo(contents.get(i));
        }
    }

    @Test
    @DisplayName("로그인 없이 문의와 답변 조회")
    void findInquiresWithoutLogin() throws Exception {
        Answer answer2 = Answer.builder().content("content2").build();
        Answer answer3 = Answer.builder().content("content3").build();
        Inquiry inquiry1 = Inquiry.builder().product(product1).isSecret(true).user(user1).content("content1").build();
        Inquiry inquiry2 = Inquiry.builder().product(product1).isSecret(true).user(user2).content("content2").answer(answer2).build();
        Inquiry inquiry3 = Inquiry.builder().product(product1).isSecret(false).user(user3).content("content3").answer(answer3).build();
        inquiryRepository.save(inquiry1);
        inquiryRepository.save(inquiry2);
        inquiryRepository.save(inquiry3);

        List<String> names = List.of("tes**", "tes**", "tes**");
        List<String> contents = List.of("content3", "비밀글입니다.", "비밀글입니다.");
        List<Optional<String>> answers = List.of(Optional.of("content3"), Optional.of("비밀글입니다."), Optional.ofNullable(null));
        String url = "http://localhost:8080/inquires?size=3&page=0&sort=id,DESC";

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        PageDto<InquiryResponse> pageDto = objectMapper.convertValue(response.getResult(), new TypeReference<>() {
        });
        List<InquiryResponse> inquiries = pageDto.getContent();
        assertThat(pageDto.getTotalPages()).isEqualTo(1);
        assertThat(pageDto.getTotalElements()).isEqualTo(3);
        for (int i = 0; i < inquiries.size(); i++) {
            assertThat(inquiries.get(i).getContent()).isEqualTo(contents.get(i));
            assertThat(inquiries.get(i).getUserName()).isEqualTo(names.get(i));
            assertThat(inquiries.get(i).getAnswer()).isEqualTo(answers.get(i).orElse(null));
        }
    }

    @Test
    @DisplayName("문의 삭제 시 답변도 같이 삭제")
    void deleteInquiry() throws Exception {
        Answer answer = Answer.builder().build();
        Answer saveAnswer = answerRepository.save(answer);
        Inquiry inquiry = Inquiry.builder().product(product1).isSecret(true).user(user1).content("content1").answer(saveAnswer).build();
        Inquiry saveInquiry = inquiryRepository.save(inquiry);

        String jwt = jwtTokenProvider.createToken("test1@test", List.of(Role.USER));
        String url = "http://localhost:8080/inquires/" + saveInquiry.getId();

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        Inquiry findInquiry = inquiryRepository.findById(saveInquiry.getId()).orElse(null);
        Answer findAnswer = answerRepository.findById(saveAnswer.getId()).orElse(null);
        assertThat(findInquiry).isNull();
        assertThat(findAnswer).isNull();
    }

    @Test
    @DisplayName("답변 저장")
    void saveAnswer() throws Exception {
        String url = "http://localhost:8080/inquires/answer";
        String jwt = jwtTokenProvider.createToken("seller", List.of(Role.USER));
        Inquiry inquiry = Inquiry.builder().product(product1).isSecret(true).user(user1).content("content1").build();
        Inquiry saveInquiry = inquiryRepository.save(inquiry);
        AnswerSaveRequest answer = AnswerSaveRequest.builder()
                .inquiryId(saveInquiry.getId()).productId(product1.getId()).content("answer")
                .build();

        MvcResult mvcResult = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Long answerId = objectMapper.convertValue(response.getResult(), Long.class);
        Answer save = answerRepository.findById(answerId).orElse(null);
        assertThat(save.getContent()).isEqualTo(answer.getContent());
        assertThat(save.getName()).isEqualTo(seller1.getNickname());
    }

    @Test
    @DisplayName("답변 삭제")
    void deleteAnswer() throws Exception {
        String jwt = jwtTokenProvider.createToken("seller", List.of(Role.USER));
        Answer answer = Answer.builder().name(seller1.getNickname()).build();
        Answer saveAnswer = answerRepository.save(answer);
        Inquiry inquiry = Inquiry.builder().product(product1).isSecret(true).user(user1).content("content1").answer(saveAnswer).build();
        Inquiry saveInquiry = inquiryRepository.save(inquiry);
        String url = "http://localhost:8080/inquires/answer/" + saveAnswer.getId() + "?productId=" + product1.getId();

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print());

        Answer save = answerRepository.findById(saveAnswer.getId()).orElse(null);
        assertThat(save).isNull();
    }
}