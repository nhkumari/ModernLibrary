package com.neha.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.neha.model.Book;
import com.neha.model.User;
import com.neha.service.ModernLibraryService;


@RestController
@RequestMapping(value = "/v1/modernlibrary")
public class ModernLibraryController {
    @Autowired
    private ModernLibraryService service;

    @RequestMapping(method = RequestMethod.GET, value = "/books")
    public ResponseEntity getAllBooks() {
        ResponseEntity rs = null;
        List<Book> books = service.findAvailableBooks();
        if (books.isEmpty()) {
            return new ResponseEntity<>("Empty Library", HttpStatus.OK);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/borrow/{user_id}/{book_id}")
    public ResponseEntity<Object> borrowBooks(@PathVariable("user_id") long userId,
            @PathVariable("book_id") long bookId) {
        ResponseEntity<Object> re = null;

        Optional<User> user = service.findUser(userId);
        Optional<Book> book = service.getBook(bookId);
        if (user.isPresent() && book.isPresent()) {
            if (Objects.nonNull(book.get().getUser())) {
                return new ResponseEntity<>("book is not available", HttpStatus.BAD_REQUEST);
            }
            re = assignBook(book.get(), user.get());
            return re;

        }
        return new ResponseEntity<>("invalid data", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> assignBook(Book book, User user) {
        List<Book> bookList = service.findBooksByUserId(user.getId());
        if (bookList.isEmpty()) {
            bookList = new ArrayList<>();
        }
        if (bookList.size() >= 2) {
            return new ResponseEntity<>("borrow limit exceeded", HttpStatus.BAD_REQUEST);
        }
        bookList.add(book);
        user.setBookList(bookList);
        service.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
