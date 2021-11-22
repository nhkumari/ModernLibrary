package com.neha.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * This will return all unassigned books, a book which is already assigned to a user
     * will not be shown in the library
     * user-story-1 <View All Books>
     */
    @RequestMapping(method = RequestMethod.GET, value = "/books")
    public ResponseEntity getAllBooks() {
        ResponseEntity rs = null;
        List<Book> books = service.findAvailableBooks();
        if (books.isEmpty()) {
            return new ResponseEntity<>("Empty Library", HttpStatus.OK);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * This api is for borrowing a book by passing user-id
     * user-story-2 <Borrow a Book>
     **/
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

    /**
     * This api is for borrowing a book by passing isbn number of the book
     * user-story-3 <Borrow a Copy>
     **/
    @RequestMapping(method = RequestMethod.GET, value = "/borrow_copy/{user_id}/{isbn}")
    public ResponseEntity<Object> borrowACopy(@PathVariable("user_id") long userId,
            @PathVariable("isbn") String isbn) {
        ResponseEntity<Object> rs = null;

        Optional<User> user = service.findUser(userId);
        List<Book> copies = service.findALlAvailableCopies(isbn);
        if (user.isPresent() && !copies.isEmpty()) {
            List<Book> bookList = service.findAssignedCopies(userId, isbn);
            if (!bookList.isEmpty()) {
                return new ResponseEntity<>("user already has the book", HttpStatus.FORBIDDEN);
            }
            rs = assignBook(copies.get(0), user.get());

            return rs;

        }
        return new ResponseEntity<>("invalid data/book is not available", HttpStatus.BAD_REQUEST);
    }

    /**
     * This api is for returning one/multiple borrowed books by passing isbn list
     * user-story-4 <Return Book>
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/return_books/{user_id}")
    public ResponseEntity<Object> returnBooks(@PathVariable("user_id") long userId,
            @RequestBody List<String> isbnList) {
        Optional<User> user = service.findUser(userId);
        if (user.isPresent()) {
            List<Book> bookList = new ArrayList<>();
            for (String isbn : isbnList) {
                List<Book> isbnBookList = service.findAssignedCopies(user.get().getId(), isbn);
                if (isbnBookList.isEmpty()) {
                    return new ResponseEntity<>("couldn't perform operation, book with isbn: " + isbn + ", is not "
                            + "assigned to requested user",
                            HttpStatus.BAD_REQUEST);
                }
                bookList.addAll(isbnBookList);
            }
            return updateBorrowList(user.get(), bookList);
        }

        return new ResponseEntity<>("requested user doesn't exists", HttpStatus.BAD_REQUEST);
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

    private ResponseEntity<Object> updateBorrowList(User user, List<Book> booksToReturn) {

        List<Book> bookList = service.findBooksByUserId(user.getId());
        bookList.removeAll(booksToReturn);
        user.setBookList(bookList);
        service.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
