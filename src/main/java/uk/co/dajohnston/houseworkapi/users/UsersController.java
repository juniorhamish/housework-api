package uk.co.dajohnston.houseworkapi.users;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsersController {

  private final UsersService usersService;

  @PostMapping("/users")
  @ResponseStatus(CREATED)
  @PreAuthorize("hasAuthority('SCOPE_create:users')")
  public User create(@RequestBody User user) {
    return usersService.create(user);
  }

  @GetMapping("/users")
  @PreAuthorize("hasAnyAuthority('SCOPE_read:users', 'SCOPE_read:allusers')")
  public List<User> findAll(Authentication authentication) {
    if (authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList().contains("SCOPE_read:allusers")) {
      return usersService.findAll();
    }
    return usersService.findAll();
  }
}
