package ir.darkdeveloper.sma.model;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class UserRoles implements Serializable {


    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Enumerated(EnumType.STRING)
    private List<Authority> authorities;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    private List<UserModel> users;

    public UserRoles(Long id, String name, List<Authority> authorities) {
        this.id = id;
        this.name = name;
        this.authorities = authorities;
    }
}
