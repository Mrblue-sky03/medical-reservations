package edu.unimag.domine.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "specialty", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Doctor> doctors = new HashSet<>();

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        doctor.setSpecialty(this);
    }

}
