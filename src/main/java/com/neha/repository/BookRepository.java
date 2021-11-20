package com.neha.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.neha.model.Book;


@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findAll();
    List<Book> findByUser_Id(long user_id);
    List<Book> findByUser_IdIsNull();
}
