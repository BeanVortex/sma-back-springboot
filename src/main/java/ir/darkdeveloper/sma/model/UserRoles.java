package ir.darkdeveloper.sma.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="roles")
public class UserRoles implements Serializable {
    


    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private String name;

    @ElementCollection(targetClass = Authority.class, fetch=FetchType.EAGER)
    @CollectionTable(name="authorities", joinColumns=@JoinColumn(name="role_id", referencedColumnName="id"))
    @Enumerated(EnumType.STRING)
    private List<Authority> authorities;

}
