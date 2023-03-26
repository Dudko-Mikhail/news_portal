package by.dudko.newsportal.integration.controller;

import by.dudko.newsportal.integration.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class UserRestControllerIntegrationTest {
    private final MockMvc mockMvc;
}
