package com.conexa.starwars.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "roles") // Asegúrate de que coincida con el nombre en la BD
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
