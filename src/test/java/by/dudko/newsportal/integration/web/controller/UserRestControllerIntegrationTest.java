package by.dudko.newsportal.integration.web.controller;

import by.dudko.newsportal.integration.IntegrationTest;
import by.dudko.newsportal.model.User;
import by.dudko.newsportal.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class UserRestControllerIntegrationTest {
    private static final long USER_ID = 1L;
    private static final long NON_EXISTENT_USER_ID = -1L;
    private final MockMvc mockMvc;
    private final UserRepository userRepository;

    @Test
    void findAllActiveUsers() throws Exception {
        mockMvc.perform(get("/api/users").with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("content", hasSize(3)))
                .andExpectAll(
                        jsonPath("metadata.page").value(0),
                        jsonPath("metadata.size").value(20),
                        jsonPath("metadata.numberOfElements").value(3),
                        jsonPath("metadata.totalElements").value(3),
                        jsonPath("metadata.totalPages").value(1)
                );
    }

    @Test
    void findById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", USER_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "username": "admin",
                            "name": "Ivan",
                            "surname": "Ivanov",
                            "parentName": "Ivanovich",
                            "role": "ADMIN"
                        }
                        """));
    }

    @Test
    void findByIdWithNonExistentUserId() throws Exception {
        mockMvc.perform(get("/api/users/{id}", NON_EXISTENT_USER_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "Snake",
                                    "name": "Sergey",
                                    "surname": "Berlin",
                                    "role": "SUBSCRIBER"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "username": "Snake",
                            "name": "Sergey",
                            "surname": "Berlin",
                            "parentName": null,
                            "role": "SUBSCRIBER"
                        }
                        """))
                .andExpect(jsonPath("id").exists());
    }

    @Test
    void createTryToAssignTakenUsername() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "admin",
                                    "role": "SUBSCRIBER"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": null,
                                    "role": "SUBSCRIBER"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/api/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "cobra",
                                    "name": "Mark"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "username": "cobra",
                            "name": "Mark",
                            "surname": null,
                            "parentName": null,
                            "role": "ADMIN"
                        }
                        """));
    }

    @Test
    void updateTryTyAssignTakenUserName() throws Exception {
        mockMvc.perform(put("/api/users/{id}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": 1,
                                    "username": "journalist",
                                    "name": "Ivan",
                                    "surname": "Ivanov",
                                    "parentName": "Ivanovich"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateWithNonExistentUserId() throws Exception {
        mockMvc.perform(put("/api/users/{id}", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "cobra"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePassword() throws Exception {
        mockMvc.perform(post("/api/users/{id}/password", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "1111",
                                    "newPassword": "7777"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePasswordWithNonExistentUserId() throws Exception {
        mockMvc.perform(post("/api/users/{id}/password", NON_EXISTENT_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "1111",
                                    "newPassword": "7777"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePasswordWithInvalidOldPassword() throws Exception {
        mockMvc.perform(post("/api/users/{id}/password", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "oldPassword": "2222",
                                    "newPassword": "7777"
                                }
                                """)
                        .with(user(ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", USER_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNoContent());

        Optional<User> deletedUser = userRepository.findById(USER_ID);
        assertThat(deletedUser).isPresent();
        deletedUser.ifPresent(user -> assertTrue(user.isDeleted()));
    }

    @Test
    void deleteWithNonExistentUserId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", NON_EXISTENT_USER_ID)
                        .with(user(ADMIN)))
                .andExpect(status().isNotFound());
    }
}
