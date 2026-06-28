package com.sahil.chatapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@DynamicUpdate
@Table(
        name = "contact",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_owner_&_contact_user",
                columnNames = {"owner_id", "contact_user_id"}
        )
)
public class Contact {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_user_id")
    private User contactUser;

    private String name;

    private LocalDateTime createdAt;
}
