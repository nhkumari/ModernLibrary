package com.neha.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UnitTestUtil {
    private static String url = "/v1/modernlibrary/";

    public MvcResult getOperation(final MockMvc mockMvc, final String endPoint, final ResultMatcher status)
            throws Exception {
        return mockMvc.perform(get(url + endPoint))
                .andExpect(status)
                .andReturn();
    }
    public MvcResult postOperation(final MockMvc mockMvc, final String endPoint, final ResultMatcher status,
            final List<String> content)
            throws Exception {
        ObjectMapper mapper =new ObjectMapper();
        return mockMvc.perform(post(url + endPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(content))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status)
                .andReturn();
    }
}
