package by.dudko.newsportal.integration.web.controller;

import by.dudko.newsportal.integration.IntegrationTest;
import by.dudko.newsportal.model.News;
import by.dudko.newsportal.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

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
class NewsRestControllerIntegrationTest {
    private static final long NEWS_ID = 1L;
    private static final long NON_EXISTENT_NEWS_ID = -1L;
    private final MockMvc mockMvc;
    private final NewsRepository newsRepository;
    
    @Test
    void findAllWithEmptyFilter() throws Exception {
        mockMvc.perform(get("/api/news")
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(20)))
                .andExpectAll(
                        jsonPath("metadata.page").value(0),
                        jsonPath("metadata.size").value(20),
                        jsonPath("metadata.numberOfElements").value(20),
                        jsonPath("metadata.totalElements").value(20),
                        jsonPath("metadata.totalPages").value(1)
                );
    }

    @Test
    void findAllByTitleAndTextFilter() throws Exception {
        mockMvc.perform(get("/api/news")
                        .param("title", "news2")
                        .param("text", "20")
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content[0].id").value(20))
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpectAll(
                        jsonPath("metadata.page").value(0),
                        jsonPath("metadata.size").value(20),
                        jsonPath("metadata.numberOfElements").value(1),
                        jsonPath("metadata.totalElements").value(1),
                        jsonPath("metadata.totalPages").value(1)
                );
    }

    @Test
    void findAllByUserId() throws Exception {
        long userId = 1L;
        mockMvc.perform(get("/api/users/{id}/news", userId)
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
    void findAllByUserIdWithNonExistentUserId() throws Exception {
        long nonExistentUserId = -1L;
        mockMvc.perform(get("/api/users/{id}/news", nonExistentUserId)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdWithComments() throws Exception {
        mockMvc.perform(get("/api/news/{id}", NEWS_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "title": "news1",
                            "text": "text1"
                        }
                        """))
                .andExpect(jsonPath("comments.content", hasSize(10)));
    }

    @Test
    void findByIdWithCommentsWithNonExistentNewsId() throws Exception {
        mockMvc.perform(get("/api/news/{id}", NON_EXISTENT_NEWS_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/news")
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Cats",
                                    "text": "Facts about cats"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "title": "Cats",
                            "text": "Facts about cats"
                        }
                        """))
                .andExpect(jsonPath("id").exists());
    }

    @Test
    void createWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/news")
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "text": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateNews() throws Exception {
        mockMvc.perform(put("/api/news/{id}", NEWS_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Test title",
                                    "text": "Test text"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "title": "Test title",
                            "text": "Test text"
                        }
                        """));

        Optional<News> updatedNews = newsRepository.findById(NEWS_ID);
        assertThat(updatedNews).isPresent();
        updatedNews.ifPresent(news -> assertThat(news.getUpdatedById()).isEqualTo(ADMIN.getId()));
    }

    @Test
    void updateNewsWithInvalidData() throws Exception {
        mockMvc.perform(put("/api/news/{id}", NEWS_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "",
                                    "text": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateNewsWithNonExistentNewsId() throws Exception {
        mockMvc.perform(put("/api/news/{id}", NON_EXISTENT_NEWS_ID)
                        .with(user(ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Test title",
                                    "text": "Test text"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/news/{id}", NEWS_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNoContent());

        assertThat(newsRepository.findById(NEWS_ID)).isEmpty();
    }

    @Test
    void deleteWithNonExistentNewsId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/news/{id}", NON_EXISTENT_NEWS_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }
}
