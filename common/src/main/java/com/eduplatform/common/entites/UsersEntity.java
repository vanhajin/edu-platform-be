package com.eduplatform.common.entites;

import com.eduplatform.common.utils.enums.EnumRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_users_email", columnNames = "email")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersEntity {

    @Id
    @UuidGenerator
    @Column(name = "user_id", nullable = false, length = 40)
    private String userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private EnumRole role = EnumRole.STUDENT;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private int status = 1;

    @Column(name = "created_by", length = 40)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, columnDefinition = "timestamp(6)")
    private LocalDateTime createdDate;

    @Column(name = "updated_by", length = 40)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false, columnDefinition = "timestamp(6)")
    private LocalDateTime updatedDate;

    @Column(name = "year_of_birth", length = 4)
    private String yearOfBirth;

    @Column(name = "home_town", length = 200)
    private String homeTown;
}
