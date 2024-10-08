package uk.co.dajohnston.tasktrackerapi.users;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.dajohnston.tasktrackerapi.exceptions.DuplicateResourceException;
import uk.co.dajohnston.tasktrackerapi.security.SecurityConfig;
import uk.co.dajohnston.tasktrackerapi.security.WithMockJWT;
import uk.co.dajohnston.tasktrackerapi.users.controller.User;
import uk.co.dajohnston.tasktrackerapi.users.controller.UsersController;

@WebMvcTest(UsersController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockJWT
class UsersControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UsersService usersService;

  @Test
  void post_returnsCreatedUser() throws Exception {
    when(usersService.create(any()))
        .thenReturn(new User("First", "Last", "first.last@example.com"));

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
        .andExpect(
            content()
                .json(
                    """
                    {
                      "firstName": "First",
                      "lastName": "Last",
                      "emailAddress": "first.last@example.com"
                    }
                    """,
                    true));
  }

  @Test
  void post_usesServiceToCreateUser() throws Exception {
    mockMvc.perform(
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
            .contentType(APPLICATION_JSON));

    verify(usersService).create(new User("David", "Johnston", "david.johnston@example.com"));
  }

  @Test
  void post_duplicateEmailAddress_returns409Response() throws Exception {
    when(usersService.create(any())).thenThrow(DuplicateResourceException.class);

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
        .andExpect(status().isConflict());
  }

  @Test
  @WithMockJWT
  void get_returnsAllUsers() throws Exception {
    when(usersService.findAll())
        .thenReturn(List.of(new User("David", "Johnston", "david.johnston@example.com")));

    mockMvc
        .perform(get("/users"))
        .andExpect(
            content()
                .json(
                    """
                    [
                      {
                        "firstName": "David",
                        "lastName": "Johnston",
                        "emailAddress": "david.johnston@example.com"
                      }
                    ]
                    """));
  }

  @Test
  @WithMockJWT(subject = "MyUserID")
  void get_userHasReadUsersScope_findsAllUsersFromService() throws Exception {
    mockMvc.perform(get("/users"));

    verify(usersService).findAll();
  }
}
