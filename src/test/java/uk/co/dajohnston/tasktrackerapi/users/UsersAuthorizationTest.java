package uk.co.dajohnston.tasktrackerapi.users;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.tasktrackerapi.security.SecurityConfig;
import uk.co.dajohnston.tasktrackerapi.security.WithMockJWT;
import uk.co.dajohnston.tasktrackerapi.users.controller.UsersController;

@WebMvcTest(UsersController.class)
@Import(SecurityConfig.class)
class UsersAuthorizationTest {

  @Autowired private MockMvc mockMvc;
  @MockBean private UsersService usersService;

  @Test
  void post_noToken_returns401Response() throws Exception {
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .content(
                    """
                    {
                      "firstName": "David",
                      "lastName": "Johnston",
                      "emailAddress": "david.johnston@example.com"
                    }
                    """)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockJWT
  void post_withToken_returns201Response() throws Exception {
    mockMvc
        .perform(
            post("/users")
                .with(csrf())
                .content(
                    """
                    {
                      "firstName": "David",
                      "lastName": "Johnston",
                      "emailAddress": "david.johnston@example.com"
                    }
                    """)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  @WithMockJWT
  void post_requestWithoutCsrfToken_returns403Response() throws Exception {
    mockMvc
        .perform(
            post("/users")
                .content(
                    """
                                      {
                                        "firstName": "David",
                                        "lastName": "Johnston",
                                        "emailAddress": "david.johnston@example.com"
                                      }
                                      """)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void get_noToken_returns401Response() throws Exception {
    mockMvc.perform(get("/users")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockJWT
  void get_withToken_returns200Response() throws Exception {
    mockMvc.perform(get("/users")).andExpect(status().isOk());
  }
}
