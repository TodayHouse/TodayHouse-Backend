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
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionServiceImplTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    ChildOptionRepository childOptionRepository;

    @Mock
    ParentOptionRepository parentOptionRepository;

    @Mock
    SelectionOptionRepository selectionOptionRepository;

    @InjectMocks
    OptionServiceImpl optionService;

    @Test
    @DisplayName("parentOption 저장")
    void saveParentOption() {
        ParentOptionSaveRequest request = ParentOptionSaveRequest.builder().productId(1L).build();
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        ParentOption mock = Mockito.mock(ParentOption.class);
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        when(parentOptionRepository.save(any(ParentOption.class))).thenReturn(mock);

        assertThat(optionService.saveParentOption(request)).isEqualTo(mock);
    }

    @Test
    @DisplayName("parentOption 저장 예외처리")
    void saveParentOptionException() {
        ParentOptionSaveRequest request = ParentOptionSaveRequest.builder().productId(1L).build();
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> optionService.saveParentOption(request));
    }

    @Test
    @DisplayName("childOption 저장")
    void saveChildOption() {
        ChildOptionSaveRequest request = ChildOptionSaveRequest.builder().parentOptionId(1L).build();
        ParentOption parent = ParentOption.builder().product(Mockito.mock(Product.class)).build();
        ChildOption mock = Mockito.mock(ChildOption.class);
        when(parentOptionRepository.findById(1L)).thenReturn(Optional.ofNullable(parent));
        when(childOptionRepository.save(any(ChildOption.class))).thenReturn(mock);

        assertThat(optionService.saveChildOption(request)).isEqualTo(mock);
    }

    @Test
    @DisplayName("childOption 저장 예외처리")
    void saveChildOptionException() {
        ChildOptionSaveRequest request = ChildOptionSaveRequest.builder().parentOptionId(1L).build();
        when(parentOptionRepository.findById(1L)).thenThrow(ParentOptionNotFoundException.class);

        assertThrows(ParentOptionNotFoundException.class, () -> optionService.saveChildOption(request));
    }

    @Test
    @DisplayName("selectionOptions 저장")
    void saveSelectionOption() {
        SelectionOptionSaveRequest request = SelectionOptionSaveRequest.builder().productId(1L).build();
        Product product = Product.builder().seller(Mockito.mock(Seller.class)).build();
        SelectionOption mock = Mockito.mock(SelectionOption.class);
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        when(selectionOptionRepository.save(any(SelectionOption.class))).thenReturn(mock);

        assertThat(optionService.saveSelectionOption(request)).isEqualTo(mock);
    }

    @Test
    @DisplayName("selectionOptions 저장 예외처리")
    void saveSelectionOptionException() {
        SelectionOptionSaveRequest request = SelectionOptionSaveRequest.builder().productId(1L).build();
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> optionService.saveSelectionOption(request));
    }

    @Test
    @DisplayName("parentOption 찾기")
    void findParentOptionsByProductId() {
        Set<ParentOption> set = Set.of(ParentOption.builder().price(1).build());
        when(parentOptionRepository.findByProductId(1L)).thenReturn(set);

        assertThat(optionService.findParentOptionsByProductId(1L)).isEqualTo(set);
    }

    @Test
    @DisplayName("childOption 찾기")
    void findChildOptionsByParentId() {
        Set<ChildOption> set = Set.of(ChildOption.builder().price(1).build());
        when(childOptionRepository.findByParentOptionId(1L)).thenReturn(set);

        assertThat(optionService.findChildOptionsByParentId(1L)).isEqualTo(set);
    }

    @Test
    @DisplayName("selectionOption 찾기")
    void findSelectionOptionsByProductId() {
        Set<SelectionOption> set = Set.of(SelectionOption.builder().price(1).build());
        when(selectionOptionRepository.findByProductId(1L)).thenReturn(set);

        assertThat(optionService.findSelectionOptionsByProductId(1L)).isEqualTo(set);
    }

    @Test
    @DisplayName("parentOption 업데이트")
    void updateParentOption() {
        ParentOptionUpdateRequest request = ParentOptionUpdateRequest.builder().id(1L).stock(1).price(1000).content("test").build();
        ParentOption parentOption = ParentOption.builder().build();
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(parentOption));

        ParentOption result = optionService.updateParentOption(request);
        assertThat(result.getContent()).isEqualTo("test");
        assertThat(result.getPrice()).isEqualTo(1000);
        assertThat(result.getStock()).isEqualTo(1);
    }

    @Test
    @DisplayName("childOption 업데이트")
    void updateChildOption() {
        ChildOptionUpdateRequest request = ChildOptionUpdateRequest.builder().id(1L).stock(1).price(1000).content("test").build();
        ChildOption selectionOption = ChildOption.builder().build();
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(selectionOption));

        ChildOption result = optionService.updateChildOption(request);
        assertThat(result.getContent()).isEqualTo("test");
        assertThat(result.getPrice()).isEqualTo(1000);
        assertThat(result.getStock()).isEqualTo(1);
    }

    @Test
    @DisplayName("selectionOption 업데이트")
    void updateSelectionOption() {
        SelectionOptionUpdateRequest request = SelectionOptionUpdateRequest.builder().id(1L).stock(1).price(1000).content("test").build();
        SelectionOption selectionOption = SelectionOption.builder().build();
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(selectionOption));

        SelectionOption result = optionService.updateSelectionOption(request);
        assertThat(result.getContent()).isEqualTo("test");
        assertThat(result.getPrice()).isEqualTo(1000);
        assertThat(result.getStock()).isEqualTo(1);
    }

    @Test
    @DisplayName("parentOption 업데이트 예외처리")
    void updateParentOptionException() {
        ParentOptionUpdateRequest request = ParentOptionUpdateRequest.builder().id(1L).stock(1).price(1000).content("test").build();
        when(parentOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ParentOptionNotFoundException.class, () -> optionService.updateParentOption(request));
    }

    @Test
    @DisplayName("childOption 업데이트 예외처리")
    void updateChildOptionException() {
        ChildOptionUpdateRequest request = ChildOptionUpdateRequest.builder().id(1L).stock(1).price(1000).content("test").build();
        when(childOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ChildOptionNotFoundException.class, () -> optionService.updateChildOption(request));
    }

    @Test
    @DisplayName("selectionOption 업데이트 예외처리")
    void updateSelectionOptionException() {
        SelectionOptionUpdateRequest request = SelectionOptionUpdateRequest.builder().id(1L).stock(1).price(1000).content("test").build();
        when(selectionOptionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(SelectionOptionNotFoundException.class, () -> optionService.updateSelectionOption(request));
    }

    @Test
    @DisplayName("parentOption Id로 삭제")
    void deleteParentOptionById() {
        doNothing().when(parentOptionRepository).deleteById(anyLong());

        optionService.deleteParentOptionById(1L);

        verify(parentOptionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("childOption Id로 삭제")
    void deleteChildOptionById() {
        doNothing().when(childOptionRepository).deleteById(anyLong());

        optionService.deleteChildOptionById(1L);

        verify(childOptionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("selectionOption Id로 삭제")
    void deleteSelectionOptionById() {
        doNothing().when(selectionOptionRepository).deleteById(anyLong());

        optionService.deleteSelectionOptionById(1L);

        verify(selectionOptionRepository).deleteById(1L);
    }
}