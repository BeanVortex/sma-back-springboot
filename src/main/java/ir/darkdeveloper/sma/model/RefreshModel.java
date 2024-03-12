package ir.darkdeveloper.sma.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tokens")
public class RefreshModel {

    
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String accessToken;
    private String refreshToken;
}
