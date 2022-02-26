package com.todayhouse.domain.image.dao;

import com.todayhouse.domain.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
