package uk.co.dajohnston.houseworkapi.users;

import static java.util.Collections.singletonList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.co.dajohnston.houseworkapi.exceptions.DuplicateResourceException;

@Service
@RequiredArgsConstructor
public class UsersService {

  private final UsersRepository usersRepository;

  public User create(User user) {
    try {
      return usersRepository.save(user);
    } catch (DuplicateKeyException e) {
      throw new DuplicateResourceException(e);
    }
  }

  public List<User> findAll() {
    return stream(usersRepository.findAll().spliterator(), false).toList();
  }

  public List<User> findScopedUsers(String emailAddress) {
    return singletonList(usersRepository.findByEmailAddress(emailAddress));
  }
}
