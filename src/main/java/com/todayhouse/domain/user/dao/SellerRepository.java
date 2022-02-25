package com.todayhouse.domain.user.dao;

import com.todayhouse.domain.user.domain.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
