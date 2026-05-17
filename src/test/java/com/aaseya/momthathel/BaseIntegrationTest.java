package com.aaseya.momthathel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(BaseIntegrationTest.TestBeans.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected TestDataHelper testData;

    protected MockMvc mockMvc;

    /**
     * Locally constructed ObjectMapper — avoids dependency on Spring Boot's
     * Jackson auto-config bean wiring, which moved to a separate module in 4.0.
     */
    protected final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testData.clearAll();
    }

    @TestConfiguration
    public static class TestBeans {
        @Bean
        public TestDataHelper testDataHelper() {
            return new TestDataHelper();
        }
    }
}
