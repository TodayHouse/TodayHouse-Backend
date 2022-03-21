package com.todayhouse.domain.category.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    private int depth;

    @Builder
    public Category(String name, Category parent) {
        this.name = name;
        setParent(parent);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void setParent(Category parent) {
        if (this.parent != null) return;

        this.parent = parent;
        if (parent == null)
            this.depth = 0;
        else {
            this.depth = parent.getDepth() + 1;
            parent.getChildren().add(this);
        }
    }
}
