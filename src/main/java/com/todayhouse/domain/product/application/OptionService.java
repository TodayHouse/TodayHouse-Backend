package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.product.dto.request.*;

import java.util.Set;

public interface OptionService {
    ParentOption saveParentOption(ParentOptionSaveRequest parentOptionSaveRequest);

    ChildOption saveChildOption(ChildOptionSaveRequest childOptionSaveRequest);

    SelectionOption saveSelectionOption(SelectionOptionSaveRequest selectionOptionSaveRequest);

    Set<ParentOption> findParentOptionsByProductId(Long productId);

    Set<ChildOption> findChildOptionsByParentId(Long parentId);

    Set<SelectionOption> findSelectionOptionsByProductId(Long productId);

    ParentOption updateParentOption(ParentOptionUpdateRequest parentOptionUpdateRequest);

    ChildOption updateChildOption(ChildOptionUpdateRequest childOptionUpdateRequest);

    SelectionOption updateSelectionOption(SelectionOptionUpdateRequest selectionOptionUpdateRequest);


    void deleteParentOptionById(Long id);

    void deleteChildOptionById(Long id);

    void deleteSelectionOptionById(Long id);
}
