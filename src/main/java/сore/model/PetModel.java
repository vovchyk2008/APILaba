package сore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@ToString
public class PetModel {

  public Long id;
  public Category category;
  public String name;
  public List<String> photoUrls;
  public List<Tag> tags;
  public String status;


  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Tag {

    public Integer id;
    public String name;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Category {

    public Integer id;
    public String name;
  }
}