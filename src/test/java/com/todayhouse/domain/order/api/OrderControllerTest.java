package com.todayhouse.domain.order.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.order.dao.DeliveryRepository;
import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.order.dto.request.DeliverySaveRequest;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;
import com.todayhouse.domain.order.dto.response.DeliveryResponse;
import com.todayhouse.domain.order.dto.response.OrderResponse;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends IntegrationBase {

    @PersistenceContext
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Seller seller;
    Product p;
    ParentOption op;

    @BeforeEach
    void setUp() {
        seller = Seller.builder().brand("test").build();
        p = Product.builder().seller(seller).build();
        op = ParentOption.builder().product(p).stock(1).price(1000).build();

        em.persist(seller);
        em.persist(p);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("주문 저장")
    void saveOrder() throws Exception {
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder()
                .sender("from").receiver("to")
                .address1("a1").address2("a2")
                .receiverPhoneNumber("r")
                .senderPhoneNumber("s")
                .zipCode("12345").build();
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(p.getId())
                .parentOptionId(op.getId()).productQuantity(1)
                .memo("testmemo")
                .deliverySaveRequest(deliveryRequest).build();
        String jwt = tokenProvider.createToken("a@a.com", List.of(Role.USER));
        String url = "http://localhost:8080/orders";

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Long id = objectMapper.convertValue(response.getResult(), Long.class);
        Delivery delivery = deliveryRepository.findByOrderIdWithOrder(id).orElse(null);
        assertThat(delivery.getReceiver().equals(deliveryRequest.getReceiver()) &&
                delivery.getSender().equals(deliveryRequest.getSender())).isTrue();
        assertThat(delivery.getOrder().getProduct().getId()).isEqualTo(p.getId());
    }

    @Test
    @DisplayName("Order를 주문일 날짜 내림차순으로 페이징하여 조회")
    void findUserOrdersPaging() throws Exception {
        User user = userRepository.findByEmail("a@a.com").orElse(null);
        Orders o1 = Orders.builder()
                .parentOption(op).product(p).user(user).productQuantity(1).build();
        Orders o2 = Orders.builder()
                .parentOption(op).product(p).user(user).productQuantity(2).build();
        Orders o3 = Orders.builder()
                .parentOption(op).product(p).user(user).productQuantity(3).build();
        Orders o4 = Orders.builder()
                .parentOption(op).product(p).user(user).productQuantity(4).build();
        em.persist(o1);
        em.persist(o2);
        em.persist(o3);
        em.persist(o4);
        String jwt = tokenProvider.createToken("a@a.com", List.of(Role.USER));
        String url = "http://localhost:8080/orders?page=0&size=3&sort=createdAt,DESC";
        MvcResult mvcResult = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponse response = getResponseFromMvcResult(mvcResult);
        PageDto<OrderResponse> page = objectMapper.readValue(objectMapper.writeValueAsString(response.getResult()), new TypeReference<>() {
        });
        List<OrderResponse> list = objectMapper.readValue(objectMapper.writeValueAsString(page.getContent()), new TypeReference<>() {
        });
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0).getProductQuantity()).isEqualTo(o4.getProductQuantity());
        assertThat(list.get(1).getProductQuantity()).isEqualTo(o3.getProductQuantity());
        assertThat(list.get(2).getProductQuantity()).isEqualTo(o2.getProductQuantity());
    }

    @Test
    @DisplayName("주문 상세 조회")
    void findOrderDetail() throws Exception {
        ChildOption chop = ChildOption.builder().parent(op).content("chop").build();
        SelectionOption selop = SelectionOption.builder().product(p).content("selop").build();
        em.persist(chop);
        em.persist(selop);

        User user = userRepository.findByEmail("a@a.com").orElse(null);
        Orders order = Orders.builder()
                .product(p).parentOption(op).childOption(chop).selectionOption(selop)
                .user(user).productQuantity(1).build();
        Delivery delivery = Delivery.builder().sender("send").receiver("receive")
                .order(order).build();

        Delivery save = deliveryRepository.save(delivery);
        String url = "http://localhost:8080/orders/" + save.getOrder().getId();

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        OrderResponse orderResponse = objectMapper.convertValue(response.getResult(), OrderResponse.class);
        DeliveryResponse deliveryResponse = objectMapper.convertValue(orderResponse.getDeliveryResponse(), DeliveryResponse.class);
        assertThat(orderResponse.getId()).isEqualTo(save.getOrder().getId());
        assertThat(orderResponse.getBrand()).isEqualTo(p.getBrand());
        assertThat(orderResponse.getChildOption()).isEqualTo(chop.getContent());
        assertThat(orderResponse.getSelectionOptions()).isEqualTo(selop.getContent());
        assertTrue(deliveryResponse.getReceiver().equals(delivery.getReceiver()) &&
                deliveryResponse.getSender().equals(delivery.getSender()));
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder() throws Exception {
        User user = userRepository.findByEmail("a@a.com").orElse(null);
        Orders order = Orders.builder()
                .parentOption(op).product(p).user(user).productQuantity(1).build();
        Orders save = orderRepository.save(order);
        String jwt = tokenProvider.createToken("a@a.com", List.of(Role.USER));
        String url = "http://localhost:8080/orders/cancel/" + save.getId();

        mockMvc.perform(put(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        Orders find = orderRepository.findById(save.getId()).orElse(null);
        assertThat(find.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    @DisplayName("구매 완료")
    void completeOrder() throws Exception {
        User user = userRepository.findByEmail("a@a.com").orElse(null);
        Orders order = Orders.builder()
                .parentOption(op).product(p).user(user).productQuantity(1).build();
        Orders save = orderRepository.save(order);
        String jwt = tokenProvider.createToken("a@a.com", List.of(Role.USER));
        String url = "http://localhost:8080/orders/complete/" + save.getId();

        mockMvc.perform(put(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        Orders find = orderRepository.findById(save.getId()).orElse(null);
        assertThat(find.getStatus()).isEqualTo(Status.COMPLETED);
    }
}