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
        // Configuramos los datos de prueba
        createAppointmentType("General");
        createAppointmentType("Pediatrics");

        // Ejecutamos el método del repositorio (asegúrate de que se llame existsByName)
        boolean exists = appointmentTypeRepository.existsByName("General");

        // Verificamos que el resultado sea verdadero
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNameNotFound() {
        // Configuramos los datos de prueba
        createAppointmentType("General");

        // Buscamos un nombre que no hemos creado
        boolean exists = appointmentTypeRepository.existsByName("Nonexistent");

        // Verificamos que el resultado sea falso
        assertThat(exists).isFalse();
    }
}