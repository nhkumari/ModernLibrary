package com.neha;

import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neha.model.Book;
import com.neha.model.User;
import com.neha.service.ModernLibraryService;
import com.neha.util.UnitTestUtil;


@RunWith(SpringRunner.class)
@WebMvcTest
public class ModernLibraryControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean ModernLibraryService libraryService;
    UnitTestUtil util;
    private static String url = "/v1/modernlibrary";
    @Before
    public void setUp() {
        util = new UnitTestUtil();
    }

    @Test
    public void getBooks_Should_Return_Books() throws Exception {

        given(libraryService.findAvailableBooks()).willReturn(new ArrayList<Book>() {{
            add(new Book(1l, "A", null));
            add(new Book(2l, "B", null));
            add(new Book(3l, "C", null));
        }});
        MvcResult result = util.getOperation(mockMvc, "books", status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        List<Book> actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                });
        Assertions.assertThat(actual.get(1).getTitle()).isEqualTo("B");
    }

    @Test
    public void getAllBooks_Should_Return_Empty() throws Exception {

        given(libraryService.findAvailableBooks()).willReturn(new ArrayList<Book>());
        MvcResult result = util.getOperation(mockMvc, "books", status().isOk());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("Empty Library");
    }

    @Test
    public void testBorrowBooksInvalidUser() throws Exception {
        given(libraryService.findUser(111)).willReturn(Optional.empty());
        MvcResult result = util.getOperation(mockMvc, "borrow/111/1", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("invalid data");
    }

    @Test
    public void testBorrowBooksInvalidBookId() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(222)).willReturn(Optional.empty());
        MvcResult result = util.getOperation(mockMvc, "borrow/1/222", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("invalid data");
    }

    @Test
    public void testBorrowBooks_Should_Return_Limit_Exceeded() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(2)).willReturn(Optional.of(new Book(2, "India Today", null)));
        List<Book> bookList = new ArrayList<Book>() {{
            add(new Book(1, "The Great Wall", new User()));
            add(new Book(3, "History Of India", new User()));
        }};
        given(libraryService.findBooksByUserId(1)).willReturn(bookList);
        MvcResult result = util.getOperation(mockMvc, "borrow/1/2", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("borrow limit exceeded");
    }

    @Test
    public void testBorrowBooks_Should_Return_Not_Available() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(2)).willReturn(Optional.of(new Book(2, "India Today", new User(2, "abc", "def",
                null))));
        List<Book> bookList = new ArrayList<Book>() {{
            add(new Book(1, "The Great Wall", new User()));
        }};
        given(libraryService.findBooksByUserId(1)).willReturn(bookList);
        MvcResult result = util.getOperation(mockMvc, "borrow/1/2", status().isBadRequest());
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("book is not available");
    }
    @Test
    public void testBorrowBooks() throws Exception {
        given(libraryService.findUser(1)).willReturn(Optional.of(new User(1, "foo", "boo", null)));
        given(libraryService.getBook(2)).willReturn(Optional.of(new Book(2, "India Today",null)));
        List<Book> bookList = new ArrayList<Book>() {{
            add(new Book(1, "The Great Wall", null));
        }};
        given(libraryService.findBooksByUserId(1)).willReturn(bookList);
        MvcResult result = util.getOperation(mockMvc, "borrow/1/2", status().isOk());
        ObjectMapper mapper = new ObjectMapper();
        User actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<User>() {
                });
        Assertions.assertThat(actual.getBookList().size()).isEqualTo(2);
    }
}
