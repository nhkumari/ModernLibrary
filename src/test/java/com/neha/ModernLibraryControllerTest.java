package com.neha;

import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.List;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neha.model.Book;
import com.neha.repository.BookRepository;
import com.neha.service.ModernLibraryService;
import com.neha.util.UnitTestUtil;


@RunWith(SpringRunner.class)
@WebMvcTest
public class ModernLibraryControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean BookRepository bookRepository;

    @MockBean ModernLibraryService libraryService;
    UnitTestUtil util;

    @Before
    public void setUp() {
        util = new UnitTestUtil();
    }

    @Test
    public void getBooks_Should_Return_Books() throws Exception {

        given(libraryService.getALlBooks()).willReturn(new ArrayList<Book>() {{
            add(new Book(1l, "A", 2));
            add(new Book(2l, "B", 1));
            add(new Book(3l, "C", 6));
        }});
        MvcResult result = util.getOperation(mockMvc, "books");
        ObjectMapper mapper = new ObjectMapper();
        List<Book> actual = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Book>>() {
                });
        Assertions.assertThat(actual.get(1).getTitle()).isEqualTo("B");
    }

    @Test
    public void getAllBooks_Should_Return_Empty() throws Exception {

        given(libraryService.getALlBooks()).willReturn(new ArrayList<Book>());
        MvcResult result = util.getOperation(mockMvc, "books");
        Assertions.assertThat(result.getResponse().getContentAsString()).isEqualTo("Empty Library");
    }
}
