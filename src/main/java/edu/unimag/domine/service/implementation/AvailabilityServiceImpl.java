package edu.unimag.domine.service.implementation;

import edu.unimag.domine.api.dto.AvailabilityDto.AvailabilitySlotResponse;
import edu.unimag.domine.entities.DoctorSchedule;
import edu.unimag.domine.entities.enums.DayOfWeek;
import edu.unimag.domine.entities.enums.Status;
import edu.unimag.domine.exceptions.ResourceNotFoundException;
import edu.unimag.domine.exceptions.ValidationException; // Usa tu excepción personalizada aquí
import edu.unimag.domine.repositories.AppointmentRepository;
import edu.unimag.domine.repositories.DoctorRepository;
import edu.unimag.domine.repositories.DoctorScheduleRepository;
import edu.unimag.domine.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final AppointmentRepository appointmentRepository;

    private static final long SLOT_DURATION_MINUTES = 30;

    @Override
    public List<AvailabilitySlotResponse> getAvailableSlots(UUID doctorId, UUID officeId, LocalDate date) {

        requireNonNull(doctorId, "El ID del doctor no puede ser nulo");
        requireNonNull(officeId, "El ID de la oficina no puede ser nulo");
        requireNonNull(date, "La fecha no puede ser nula");

        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("No se encontró un doctor con el ID: " + doctorId);
        }

        DayOfWeek day = DayOfWeek.valueOf(date.getDayOfWeek().name());

        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, day)
                .orElseThrow(() -> new ResourceNotFoundException("El doctor no tiene un horario configurado para el día " + day));


        List<LocalTime[]> doctorOccupied = appointmentRepository
                .findByDoctorIdAndDate(doctorId, date)
                .stream()
                .filter(a -> a.getStatus() != Status.CANCELLED)
                .map(a -> new LocalTime[]{a.getStartAt(), a.getEndAt()})
                .toList();


        List<LocalTime[]> officeOccupied = appointmentRepository
                .findByOfficeIdAndDateAndStartAtBetween(officeId, date, LocalTime.MIN, LocalTime.MAX)
                .stream()
                .filter(a -> a.getStatus() != Status.CANCELLED)
                .map(a -> new LocalTime[]{a.getStartAt(), a.getEndAt()})
                .toList();

        List<AvailabilitySlotResponse> slots = new ArrayList<>();
        LocalTime slotStart = schedule.getStartsAt();

        while (!slotStart.plusMinutes(SLOT_DURATION_MINUTES).isAfter(schedule.getEndsAt())) {
            LocalTime slotEnd = slotStart.plusMinutes(SLOT_DURATION_MINUTES);

            if (isSlotFree(slotStart, slotEnd, doctorOccupied) && isSlotFree(slotStart, slotEnd, officeOccupied)) {
                // Nota: Asumo que corregiste 'starsAt' por 'startsAt' en tu DTO
                slots.add(new AvailabilitySlotResponse(date, slotStart, slotEnd));
            }

            slotStart = slotEnd;
        }

        return slots;
    }

    private boolean isSlotFree(LocalTime slotStart, LocalTime slotEnd, List<LocalTime[]> occupiedRanges) {
        return occupiedRanges.stream().noneMatch(occupied ->
                // occupied[0] es startAt, occupied[1] es endAt
                slotStart.isBefore(occupied[1]) && slotEnd.isAfter(occupied[0])
        );
    }

    private static void requireNonNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            // Usa tu ValidationException aquí para mantener la consistencia de tu API
            throw new ValidationException(message);
        }
    }
}