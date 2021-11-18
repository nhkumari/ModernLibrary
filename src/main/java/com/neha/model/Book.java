package com.neha.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name="TBL_MODERN_BOOKS")
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title")
    private String title;
    @Column(name = "copies")
    private int copies;

    public Book(final long id, final String title, final int copies) {
        this.id = id;
        this.title = title;
        this.copies = copies;
    }
}
