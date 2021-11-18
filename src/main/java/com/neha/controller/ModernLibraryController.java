package com.neha.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.neha.model.Book;
import com.neha.service.ModernLibraryService;


@RestController
@RequestMapping(value="/v1/modernlibrary")
public class ModernLibraryController {
    @Autowired
    private ModernLibraryService service;

    @RequestMapping(method = RequestMethod.GET, value = "/books")
    public ResponseEntity getAllBooks() {
        ResponseEntity rs = null;
        List<Book> books = service.getALlBooks();
        if (books.isEmpty()) {
            return new ResponseEntity<>("Empty Library", HttpStatus.OK);
        }
        return new ResponseEntity<>(service.getALlBooks(), HttpStatus.OK);
    }

}
