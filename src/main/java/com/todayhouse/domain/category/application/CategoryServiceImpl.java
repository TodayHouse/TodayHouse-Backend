package com.todayhouse.domain.category.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategoryResponse;
import com.todayhouse.domain.category.exception.CategoryExistException;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(CategorySaveRequest request) {
        if (categoryRepository.existsByName(request.getName()))
            throw new CategoryExistException();

        Category par = request.getParentName() == null ?
                null : categoryRepository.findByName(request.getParentName()).orElseThrow(CategoryNotFoundException::new);
        Category child = Category.builder().name(request.getName()).parent(par).build();

        return categoryRepository.save(child);
    }

    @Override
    public Category updateCategory(CategoryUpdateRequest request) {
        Category category = categoryRepository.findByName(request.getName()).orElseThrow(CategoryNotFoundException::new);
        category.updateName(request.getChangeName());
        return category;
    }

    @Override
    public void deleteCategory(String name) {
        categoryRepository.deleteByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        List<Category> categories = categoryRepository.findByDepth(0);
        return categories.stream().map(c -> new CategoryResponse(c)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findAllByName(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(CategoryNotFoundException::new);
        return new CategoryResponse(category);
    }

    @PostConstruct
    public void perCategory(){
        Category 가구 = Category.builder().name("가구").build();
        Category 가전 = Category.builder().name("가전").build();
        Category 생필품 = Category.builder().name("생필품").build();
        Category 패브릭 = Category.builder().name("패브릭").build();


        Category 침대 = Category.builder().name("침대").parent(가구).build();
        Category 수납장 = Category.builder().name("수납장").parent(가구).build();
        Category 의자 = Category.builder().name("의자").parent(가구).build();
        Category 주방가전 = Category.builder().name("주방가전").parent(가전).build();
        Category.builder().name("가스레인지").parent(주방가전).build();
        Category.builder().name("전기레인지").parent(주방가전).build();
        Category.builder().name("에어프라이기").parent(주방가전).build();
        Category.builder().name("밥솥").parent(주방가전).build();
        Category.builder().name("기타").parent(주방가전).build();

        Category 에어컨 = Category.builder().name("에어컨").parent(가전).build();
        Category 컴노 = Category.builder().name("컴퓨터/노트북").parent(가전).build();
        Category.builder().name("컴퓨터").parent(컴노).build();
        Category.builder().name("노트북").parent(컴노).build();

        Category 생활잡화 = Category.builder().name("생활잡화").parent(생필품).build();
        Category.builder().name("여행용품").parent(생활잡화).build();
        Category.builder().name("기타생활잡화").parent(생활잡화).build();
        Category 세탁용품 = Category.builder().name("세탁용품").parent(생활잡화).build();
        Category.builder().name("빨래건조대").parent(세탁용품).build();
        Category.builder().name("세탁잡화").parent(세탁용품).build();

        Category 침구류 = Category.builder().name("침구류").parent(패브릭).build();
        Category 커튼 = Category.builder().name("커튼").parent(패브릭).build();
        Category.builder().name("일반커튼").parent(커튼).build();
        Category.builder().name("암막커튼").parent(커튼).build();
        Category.builder().name("기타커튼").parent(커튼).build();
        Category 쿠션 = Category.builder().name("쿠션").parent(패브릭).build();

        categoryRepository.save(가구);
        categoryRepository.save(가전);
        categoryRepository.save(생필품);
        categoryRepository.save(패브릭);
    }
}
