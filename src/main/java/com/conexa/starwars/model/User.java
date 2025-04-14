package com.conexa.starwars.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "users")  // Aseguramos que el nombre coincide con la tabla
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Clave foránea
    private Role role;
}
