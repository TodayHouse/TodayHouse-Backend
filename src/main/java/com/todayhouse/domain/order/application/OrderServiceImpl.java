package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.order.dto.request.DeliverySaveRequest;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;
import com.todayhouse.domain.order.exception.OrderNotFoundException;
import com.todayhouse.domain.product.dao.ChildOptionRepository;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.dao.SelectionOptionRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.product.exception.ChildOptionNotFoundException;
import com.todayhouse.domain.product.exception.ParentOptionNotFoundException;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.product.exception.SelectionOptionNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.InvalidRequestException;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ChildOptionRepository childOptionRepository;
    private final ParentOptionRepository parentOptionRepository;
    private final SelectionOptionRepository selectionOptionRepository;

    @Override
    public Order saveOrder(OrderSaveRequest orderRequest, DeliverySaveRequest deliveryRequest) {
        User user = getValidUser();
        Product product = productRepository.findById(orderRequest.getProductId())
                .orElseThrow(ProductNotFoundException::new);
        ParentOption parentOption = parentOptionRepository.findById(orderRequest.getParentOptionId())
                .orElseThrow(ParentOptionNotFoundException::new);
        ChildOption childOption = orderRequest.getChildOptionId() == null ? null : childOptionRepository.findById(orderRequest.getChildOptionId())
                .orElseThrow(ChildOptionNotFoundException::new);
        SelectionOption selectionOption = orderRequest.getSelectionOptionId() == null ? null : selectionOptionRepository.findById(orderRequest.getSelectionOptionId())
                .orElseThrow(SelectionOptionNotFoundException::new);


        checkValidRequest(product, parentOption, childOption, selectionOption);
        calcStock(parentOption, childOption, selectionOption,
                -orderRequest.getProductQuantity(), -orderRequest.getSelectionQuantity());

        Order order = orderRequest.toEntity(user, product, parentOption, childOption, selectionOption);
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserName(String userName) {
        User user = userRepository.findByNickname(userName).orElseThrow(UserNotFoundException::new);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public void cancelOrder(Long orderId) {
        getValidOrder(orderId);
        Order order = orderRepository.findByIdWithOptions(orderId);
        calcStock(order.getParentOption(), order.getChildOption(), order.getSelectionOption(),
                order.getProductQuantity(), order.getSelectionQuantity());
        order.updateStatus(Status.CANCELED);
    }

    @Override
    public void completeOrder(Long orderId) {
        Order order = getValidOrder(orderId);
        order.updateStatus(Status.COMPLETED);
    }

    // 요청 유저와 상품을 주문한 유저가 같은지 확인
    private Order getValidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        User user = getValidUser();
        if (order.getUser().getId() != user.getId())
            throw new InvalidRequestException();
        return order;
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private void calcStock(ParentOption parent, ChildOption child, SelectionOption selection,
                           int productQuantity, int selectionQuantity) {
        if (child == null)
            parent.addStock(productQuantity);
        else
            child.addStock(productQuantity);

        if (selection == null)
            selection.addStock(selectionQuantity);
    }

    private void checkValidRequest(Product product, ParentOption parent,
                                   ChildOption child, SelectionOption selection) {
        if (parent.getProduct() != product ||
                (child != null && child.getParent() != parent) ||
                (selection != null && selection.getProduct() != product))
            throw new InvalidRequestException();
    }
}
