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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category addCategory(CategorySaveRequest request) {
        if (categoryRepository.existsByName(request.getName()))
            throw new CategoryExistException();

        Category par = request.getParentId() == null ?
                null : categoryRepository.findById(request.getParentId()).orElseThrow(CategoryNotFoundException::new);
        Category child = Category.builder().name(request.getName()).parent(par).build();

        return categoryRepository.save(child);
    }

    @Override
    public Category updateCategory(CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(request.getId()).orElseThrow(CategoryNotFoundException::new);
        category.updateName(request.getChangeName());
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllWithChildrenAll() {
        return createCategoryResponsesAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findOneWithChildrenAllById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        return createCategoryResponse(category);
    }

    private CategoryResponse createCategoryResponse(Category category) {
        List<Category> categories = categoryRepository.findOneWithAllChildrenById(category.getId());
        Map<Long, CategoryResponse> map = new HashMap<>();
        categories.stream().forEach(c -> {
            CategoryResponse response = new CategoryResponse(c);
            map.put(c.getId(), response);
            if (c.getId() != category.getId())
                map.get(c.getParent().getId()).getSubCategories().add(response);
        });
        return map.get(category.getId());
    }

    private List<CategoryResponse> createCategoryResponsesAll(){
        List<Category> categories = categoryRepository.findAllByOrderByDepthAscParentAscIdAsc();
        Map<Long, CategoryResponse> map = new HashMap<>();
        List<CategoryResponse> responses = new ArrayList<>();
        categories.stream().forEach(c -> {
            CategoryResponse response = new CategoryResponse(c);
            map.put(c.getId(), response);
            if (c.getDepth() != 0)
                map.get(c.getParent().getId()).getSubCategories().add(response);
            else
                responses.add(response);
        });
        return responses;
    }

    @PostConstruct
    public void perCategory() {
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
