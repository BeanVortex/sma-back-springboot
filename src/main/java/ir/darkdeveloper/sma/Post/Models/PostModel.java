package ir.darkdeveloper.sma.Post.Models;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import ir.darkdeveloper.sma.Users.Models.UserModel;
import ir.darkdeveloper.sma.Utils.ImageUtil;
import lombok.Data;

@Data
@Entity
@Table(name = "posts")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PostModel implements ImageUtil {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  private Long likes;

  // this wont be added to database it is just for operation
  @Transient
  private MultipartFile file;

  private String image;

  @ManyToOne(fetch = FetchType.EAGER)
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
  @OneToMany(mappedBy = "post")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<CommentModel> comments;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Override
  public String getImage() {
    return image;
  }

}
