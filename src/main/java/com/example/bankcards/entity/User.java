package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "cards")
@ToString(exclude = "cards")
@Builder
@Entity
@Table(name = "users")
@Schema(name = "User", description = "System user, has username, password and role")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User DB identifier", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    @NotNull
    @Schema(description = "Unique username", example = "alice")
    private String username;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "Password (hashed)", example = "$2a$10$...", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Schema(description = "Role of the user", example = "USER")
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Schema(description = "Cards belonging to the user", accessMode = Schema.AccessMode.READ_ONLY)
    private Set<Card> cards = new HashSet<>();
}
