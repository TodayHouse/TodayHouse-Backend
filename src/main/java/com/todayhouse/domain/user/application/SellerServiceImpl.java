package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.SellerRequest;
import com.todayhouse.domain.user.exception.SellerExistException;
import com.todayhouse.domain.user.exception.SellerNotFoundException;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final UserRepository userRepository;

    @Override
    public Seller saveSellerRequest(SellerRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(UserNotFoundException::new);
        // 중복 판매자 등록 처리
        if (user.getSeller() != null)
            throw new SellerExistException();
        user.createSeller(request);
        User save = userRepository.save(user);
        return save.getSeller();
    }

    @Override
    @Transactional(readOnly = true)
    public Seller findSeller(Long sellerId) {
        User user = userRepository.findBySellerIdWithSeller(sellerId).orElseThrow(UserNotFoundException::new);
        Seller seller = user.getSeller();
        if (seller == null)
            throw new SellerNotFoundException();
        return seller;
    }
}
