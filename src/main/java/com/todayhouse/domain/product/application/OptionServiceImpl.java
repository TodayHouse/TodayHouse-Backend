package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.dao.ChildOptionRepository;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.dao.SelectionOptionRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.product.dto.request.*;
import com.todayhouse.domain.product.exception.ChildOptionNotFoundException;
import com.todayhouse.domain.product.exception.ParentOptionNotFoundException;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.product.exception.SelectionOptionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final ProductRepository productRepository;
    private final ParentOptionRepository parentOptionRepository;
    private final ChildOptionRepository childOptionRepository;
    private final SelectionOptionRepository selectionOptionRepository;

    @Override
    public ParentOption saveParentOption(ParentOptionSaveRequest parentOptionSaveRequest) {
        Product product = productRepository.findById(parentOptionSaveRequest.getProductId()).orElseThrow(ProductNotFoundException::new);
        ParentOption parentOption = parentOptionSaveRequest.toEntity(product);
        return parentOptionRepository.save(parentOption);
    }

    @Override
    public ChildOption saveChildOption(ChildOptionSaveRequest childOptionSaveRequest) {
        ParentOption parentOption = parentOptionRepository.findById(childOptionSaveRequest.getParentOptionId()).orElseThrow(ParentOptionNotFoundException::new);
        ChildOption childOption = childOptionSaveRequest.toEntity(parentOption);
        return childOptionRepository.save(childOption);
    }

    @Override
    public SelectionOption saveSelectionOption(SelectionOptionSaveRequest selectionOptionSaveRequest) {
        Product product = productRepository.findById(selectionOptionSaveRequest.getProductId()).orElseThrow(ProductNotFoundException::new);
        SelectionOption selectionOption = selectionOptionSaveRequest.toEntity(product);
        return selectionOptionRepository.save(selectionOption);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ParentOption> findParentOptionsByProductId(Long productId) {
        return parentOptionRepository.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ChildOption> findChildOptionsByParentId(Long parentId) {
        return childOptionRepository.findByParentOptionId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SelectionOption> findSelectionOptionsByProductId(Long productId) {
        return selectionOptionRepository.findByProductId(productId);
    }

    @Override
    public ParentOption updateParentOption(ParentOptionUpdateRequest parentOptionUpdateRequest) {
        ParentOption parentOption = parentOptionRepository.findById(parentOptionUpdateRequest.getId()).orElseThrow(ParentOptionNotFoundException::new);
        parentOption.update(parentOptionUpdateRequest);
        return parentOption;
    }

    @Override
    public ChildOption updateChildOption(ChildOptionUpdateRequest childOptionUpdateRequest) {
        ChildOption childOption = childOptionRepository.findById(childOptionUpdateRequest.getId()).orElseThrow(ChildOptionNotFoundException::new);
        childOption.update(childOptionUpdateRequest);
        return childOption;
    }

    @Override
    public SelectionOption updateSelectionOption(SelectionOptionUpdateRequest selectionOptionUpdateRequest) {
        SelectionOption selectionOption = selectionOptionRepository.findById(selectionOptionUpdateRequest.getId()).orElseThrow(SelectionOptionNotFoundException::new);
        selectionOption.update(selectionOptionUpdateRequest);
        return selectionOption;
    }

    @Override
    public void deleteParentOptionById(Long id) {
        parentOptionRepository.deleteById(id);
    }

    @Override
    public void deleteChildOptionById(Long id) {
        childOptionRepository.deleteById(id);
    }

    @Override
    public void deleteSelectionOptionById(Long id) {
        selectionOptionRepository.deleteById(id);
    }
}
