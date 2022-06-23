package ir.darkdeveloper.sma.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import ir.darkdeveloper.sma.utils.ImageUtil;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Builder
public class UserModel implements UserDetails, ImageUtil, UpdateModel<UserModel> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(unique = true)
    @NotEmpty
    @Size(min = 5)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Transient
    @Column(nullable = false)
    private String passwordRepeat;

    @Transient
    private String prevPassword;

    private Boolean enabled;

    @Transient
    private MultipartFile profileFile;

    @Column(name = "profile")
    private String profilePicture;

    @OneToMany(mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<PostModel> posts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role"))
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<UserRoles> roles;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public UserModel(Long id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var auth = new ArrayList<GrantedAuthority>();
        roles.forEach(e -> auth.addAll(e.getAuthorities()));
        return auth;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getImage() {
        return profilePicture;
    }

    @Override
    public void update(UserModel model) {
        id = model.id != null || id == null ? model.id : id;
        userName = model.userName != null || userName == null ? model.userName : userName;
        profilePicture = model.profilePicture != null || profilePicture == null ? model.profilePicture : profilePicture;
        password = model.password != null || password == null ? model.password : password;
        createdAt = model.createdAt != null || createdAt == null ? model.createdAt : createdAt;
        updatedAt = model.updatedAt != null || updatedAt == null ? model.updatedAt : updatedAt;
    }
}
