package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.dao.DeliveryRepository;
import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.order.dto.request.DeliverySaveRequest;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;
import com.todayhouse.domain.product.dao.ChildOptionRepository;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.dao.SelectionOptionRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.product.exception.*;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.InvalidRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    DeliveryRepository deliveryRepository;

    @Mock
    ChildOptionRepository childOptionRepository;

    @Mock
    ParentOptionRepository parentOptionRepository;

    @Mock
    SelectionOptionRepository selectionOptionRepository;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("주문 저장, 제품 수량 감소")
    void saveOrder() {
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption parentOption = ParentOption.builder().product(product).build();
        ChildOption childOption = ChildOption.builder().stock(1).parent(parentOption).build();
        SelectionOption selectionOption = SelectionOption.builder().stock(1).product(product).build();
        Delivery delivery = Delivery.builder().build();

        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L)
                .selectionOptionId(1L)
                .productQuantity(1)
                .selectionQuantity(1).build();

        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(product));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(parentOption));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(childOption));
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(selectionOption));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);

        Orders save = orderService.saveOrder(orderRequest, deliveryRequest);
        assertThat(delivery.getOrder()).isEqualTo(save);
        assertThat(childOption.getStock()).isEqualTo(0);
        assertThat(selectionOption.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("product를 찾을 수 없음")
    void saveOrderProductException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("parentOption을 찾을 수 없음")
    void saveOrderParentOptionException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(Product.class)));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ParentOptionNotFoundException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("childOption을 찾을 수 없음")
    void saveOrderChildOptionException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(Product.class)));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(ParentOption.class)));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ChildOptionNotFoundException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("selctionOption을 찾을 수 없음")
    void saveOrderSelectionOptionException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L)
                .selectionOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(Product.class)));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(ParentOption.class)));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(ChildOption.class)));
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(SelectionOptionNotFoundException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("parentOption의 제품과 product가 다름")
    void saveOrderProductAndOptionException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L)
                .selectionOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        Product product1 = Product.builder().seller(Mockito.mock(Seller.class)).build();
        Product product2 = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption.builder().product(product1).build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(product2));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(ParentOption.class)));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(ChildOption.class)));
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(SelectionOption.class)));

        assertThrows(InvalidRequestException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("parentOption과 childOption의 parent가 다름")
    void saveOrderParentChildOptionException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L)
                .selectionOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        Product product = Product.builder().seller(mock(Seller.class)).build();
        ParentOption parent1 = ParentOption.builder().product(product).build();
        ParentOption parent2 = ParentOption.builder().product(product).build();
        ChildOption child = ChildOption.builder().parent(parent1).build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(product));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(parent2));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(child));
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Mockito.mock(SelectionOption.class)));

        assertThrows(InvalidRequestException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("selectionOption 제품과 product가 다름")
    void saveOrderProductAndSelectionOptionException() {
        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .selectionOptionId(1L).build();
        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        Product product1 = Product.builder().seller(mock(Seller.class)).build();
        Product product2 = Product.builder().seller(mock(Seller.class)).build();
        ParentOption parent = ParentOption.builder().product(product1).build();
        SelectionOption selection = SelectionOption.builder().product(product2).build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(product1));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(parent));
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(selection));

        assertThrows(InvalidRequestException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("product 수량 부족")
    void productQuantityException() {
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption parentOption = ParentOption.builder().product(product).build();
        ChildOption childOption = ChildOption.builder().parent(parentOption).build();

        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L)
                .productQuantity(2).build();

        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(product));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(parentOption));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(childOption));

        assertThrows(StockNotEnoughException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("selection 수량 부족")
    void selectionQuantityException() {
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption parentOption = ParentOption.builder().product(product).build();
        ChildOption childOption = ChildOption.builder().stock(1).parent(parentOption).build();
        SelectionOption selectionOption = SelectionOption.builder().stock(1).product(product).build();

        OrderSaveRequest orderRequest = OrderSaveRequest.builder().productId(1L)
                .parentOptionId(1L)
                .childOptionId(1L)
                .selectionOptionId(1L)
                .productQuantity(1)
                .selectionQuantity(2).build();

        DeliverySaveRequest deliveryRequest = DeliverySaveRequest.builder().build();

        getValidUser();
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(product));
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(parentOption));
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(childOption));
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(selectionOption));

        assertThrows(StockNotEnoughException.class, () -> orderService.saveOrder(orderRequest, deliveryRequest));
    }

    @Test
    @DisplayName("해당 user의 모든 주문 조회")
    void findOrders() {
        Orders o1 = Mockito.mock(Orders.class);
        Orders o2 = Mockito.mock(Orders.class);
        List<Orders> orders = List.of(o1, o2);
        PageRequest request = PageRequest.of(0, 10);
        User user = getValidUser();
        when(orderRepository.findByUserIdWithProduct(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(orders));

        Page<Orders> page = orderService.findOrders(request);
        assertThat(page.getContent()).isEqualTo(orders);
    }

    @Test
    @DisplayName("주문 취소")
    void cancelOrder() {
        User user = getValidUser();
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption parentOption = ParentOption.builder().stock(1).product(product).build();
        SelectionOption selectionOption = SelectionOption.builder().stock(1).product(product).build();
        Orders orders = Orders.builder().user(user).product(product)
                .productQuantity(1).selectionQuantity(1)
                .parentOption(parentOption)
                .selectionOption(selectionOption).build();

        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(orders));
        when(orderRepository.findByIdWithOptions(anyLong())).thenReturn(Optional.ofNullable(orders));

        orderService.cancelOrder(1L);

        assertThat(parentOption.getStock()).isEqualTo(2);
        assertThat(selectionOption.getStock()).isEqualTo(2);
        assertThat(orders.getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    @DisplayName("주문 완료")
    void completeOrder() {
        User user = getValidUser();
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption parentOption = ParentOption.builder().product(product).build();
        Orders orders = Orders.builder().user(user).product(product)
                .productQuantity(1).parentOption(parentOption).build();

        when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(orders));

        orderService.completeOrder(1L);

        assertThat(orders.getStatus()).isEqualTo(Status.COMPLETED);
    }


    private User getValidUser() {
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn("test");
        SecurityContextHolder.setContext(securityContext);
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));
        return user;
    }
}