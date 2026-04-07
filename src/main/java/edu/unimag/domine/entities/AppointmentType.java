package edu.unimag.domine.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "appointment_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "appointmentType", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Appointment> appointments = new HashSet<>();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setAppointmentType(this);
    }

}
