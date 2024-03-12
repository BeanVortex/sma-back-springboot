package ir.darkdeveloper.sma.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ir.darkdeveloper.sma.utils.ImageUtil;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Builder
public class PostModel implements ImageUtil, UpdateModel<PostModel> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  private Long likes;

  // this won't be added to database it is just for operation
  @Transient
  private MultipartFile file;

  private String image;

  @ManyToOne
  // @JoinTable(joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
  //  inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private UserModel user;

  // mappedBy is read-only. can't add comment while creating post
  /*
   * In order to delete a record, first, delete the record with the foreign key,
   * and then delete the original that you wanted to delete. SHOULD
   * USE @OnDelete(action = OnDeleteAction = CASCADE)
   */
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<CommentModel> comments;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public PostModel(Long id) {
    this.id = id;
  }

  @Override
  public String getImage() {
    return image;
  }

  @Override
  public void update(PostModel model) {
    id = model.id != null || id == null ? model.id : id;
    title = model.title != null || title == null ? model.title : title;
    content = model.content != null || content == null ? model.content : content;
    likes = model.likes != null || likes == null ? model.likes : likes;
    image = model.image != null || image == null ? model.image : image;
    file = model.file != null || file == null ? model.file : file;
    createdAt = model.createdAt != null || createdAt == null ? model.createdAt : createdAt;
    updatedAt = model.updatedAt != null || updatedAt == null ? model.updatedAt : updatedAt;
  }
}
