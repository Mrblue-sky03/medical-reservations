package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.AppointmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentTypeRepositoryTest extends AbstractIntegrationDBTest {

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;

    @BeforeEach
    void clean() {
        appointmentTypeRepository.deleteAll();
    }

    private AppointmentType createAppointmentType(String name) {
        AppointmentType type = AppointmentType.builder()
                .name(name)
                .description("Description")
                .durationMinutes(30)
                .build();
        return appointmentTypeRepository.save(type);
    }

    @Test
    void shouldReturnTrueWhenNameExists() {
        createAppointmentType("General");
        createAppointmentType("Pediatrics");

        boolean exists = appointmentTypeRepository.existsByName("General");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNameNotFound() {
        createAppointmentType("General");

        boolean exists = appointmentTypeRepository.existsByName("Nonexistent");

        assertThat(exists).isFalse();
    }
}