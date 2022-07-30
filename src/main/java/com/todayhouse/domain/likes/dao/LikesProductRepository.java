package com.todayhouse.domain.likes.dao;

import com.todayhouse.domain.likes.domain.LikesProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesProductRepository extends JpaRepository<LikesProduct, Long> {


    @Query("select lp from LikesProduct lp " +
            "join fetch lp.product p " +
            "join fetch lp.user u " +
            "where u.id = :userId and p.id = :productId")
    Optional<LikesProduct> findByUser_IdAndProduct_Id(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("select lp from LikesProduct lp " +
            "join fetch lp.product p " +
            "join fetch lp.user u " +
            "where u.email = :email and p.id = :productId")
    Optional<LikesProduct> findByUser_EmailAndProduct_Id(@Param("email") String email, @Param("productId") Long productId);

    boolean existsByProduct_IdAndUser_Email(Long id, String email);

    long countByProduct_Id(Long id);
}
