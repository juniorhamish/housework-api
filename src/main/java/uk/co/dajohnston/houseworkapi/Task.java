package uk.co.dajohnston.houseworkapi;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Task {

  @Id
  private String id;
  private String name;
}
