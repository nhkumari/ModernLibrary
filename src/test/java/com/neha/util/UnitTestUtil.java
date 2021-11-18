package com.neha.util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


public class UnitTestUtil {
    private static String url = "/v1/modernlibrary/";

    public MvcResult getOperation(final MockMvc mockMvc, final String endPoint) throws Exception {
        return mockMvc.perform(get(url + endPoint))
                .andExpect(status().isOk())
                .andReturn();
    }
}
