package edu.unimag.domine.repositories;

import edu.unimag.domine.entities.Office;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OfficeRepositoryTest extends AbstractIntegrationDBTest {

    @Autowired
    private OfficeRepository officeRepository;

    @BeforeEach
    void clean() {
        officeRepository.deleteAll();
    }

    private Office createOffice(boolean active, String location) {
        Office office = Office.builder()
                .name("Office " + UUID.randomUUID())
                .location(location)
                .description("Description")
                .active(active)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return officeRepository.save(office);
    }

    @Test
    void shouldReturnTrueWhenActiveOfficeExists() {
        // arrange
        createOffice(true, "Building A");

        // act
        boolean result = officeRepository.existsByActive(true);

        // assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoActiveOfficeExists() {
        createOffice(false, "Building A");

        boolean result = officeRepository.existsByActive(true);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenLocationExists() {
        createOffice(true, "Building A");

        boolean result = officeRepository.existsByLocation("Building A");

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenLocationNotExists() {
        createOffice(true, "Building A");

        boolean result = officeRepository.existsByLocation("Building B");

        assertThat(result).isFalse();
    }
}