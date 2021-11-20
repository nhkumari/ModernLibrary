package com.neha.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;


public class UnitTestUtil {
    private static String url = "/v1/modernlibrary/";

    public MvcResult getOperation(final MockMvc mockMvc, final String endPoint, final ResultMatcher status)
            throws Exception {
        return mockMvc.perform(get(url + endPoint))
                .andExpect(status)
                .andReturn();
    }
}
