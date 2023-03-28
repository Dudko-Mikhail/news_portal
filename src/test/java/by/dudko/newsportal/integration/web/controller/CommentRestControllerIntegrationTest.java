package by.dudko.newsportal.integration.web.controller;

import by.dudko.newsportal.integration.IntegrationTest;
import by.dudko.newsportal.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static by.dudko.newsportal.integration.web.controller.UserDetailsProvider.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class CommentRestControllerIntegrationTest {
    private static final long COMMENT_ID = 1L;
    private static final long NON_EXISTENT_COMMENT_ID = -1L;
    private static final long NEWS_ID = 2L;
    private static final long NON_EXISTENT_NEWS_ID = -1L;
    private final MockMvc mockMvc;
    private final CommentRepository commentRepository;

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/api/comments/{id}", COMMENT_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "text": "comment text1"
                        }
                        """));
    }

    @Test
    void findByIdWithNonExistentCommentId() throws Exception {
        mockMvc.perform(get("/api/comments/{id}", NON_EXISTENT_COMMENT_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByUserId() throws Exception {
        long userId = 1L;
        mockMvc.perform(get("/api/users/{id}/comments", userId)
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(20)))
                .andExpectAll(
                        jsonPath("metadata.page").value(0),
                        jsonPath("metadata.size").value(20),
                        jsonPath("metadata.numberOfElements").value(20),
                        jsonPath("metadata.totalElements").value(82),
                        jsonPath("metadata.totalPages").value(5)
                );
    }

    @Test
    void findAllByUserIdWithNonExistentUserId() throws Exception {
        long nonExistentUserId = -1L;
        mockMvc.perform(get("/api/users/{id}/comments", nonExistentUserId)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByNewsId() throws Exception {
        mockMvc.perform(get("/api/news/{id}/comments", NEWS_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(10)))
                .andExpectAll(
                        jsonPath("metadata.page").value(0),
                        jsonPath("metadata.size").value(20),
                        jsonPath("metadata.numberOfElements").value(10),
                        jsonPath("metadata.totalElements").value(10),
                        jsonPath("metadata.totalPages").value(1)
                );
    }

    @Test
    void findAllByNewsIdWithNonExistentNewsId() throws Exception {
        mockMvc.perform(get("/api/news/{id}/comments", NON_EXISTENT_NEWS_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/news/{id}/comments", NEWS_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Interesting text!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "text": "Interesting text!"
                        }
                        """))
                .andExpect(jsonPath("id").exists());
    }

    @Test
    void createWithNonExistentNewsId() throws Exception {
        mockMvc.perform(post("/api/news/{id}/comments", NON_EXISTENT_NEWS_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Interesting text!"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/news/{id}/comments", NEWS_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/api/comments/{id}", COMMENT_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Interesting text!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "text": "Interesting text!"
                        }
                        """));
    }

    @Test
    void updateWithNonExistentCommentId() throws Exception {
        mockMvc.perform(put("/api/comments/{id}", NON_EXISTENT_COMMENT_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Interesting text!"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateWithInvalidData() throws Exception {
        mockMvc.perform(put("/api/comments/{id}", COMMENT_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/{id}", COMMENT_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.findById(COMMENT_ID)).isEmpty();
    }

    @Test
    void deleteWithNonExistentCommentId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/{id}", NON_EXISTENT_COMMENT_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }
}
