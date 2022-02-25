package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.dto.request.SellerRequest;

import java.util.Optional;

public interface SellerService {
    Seller saveSellerRequest(SellerRequest request);

    Optional<Seller> findSeller(Long userId);
}
