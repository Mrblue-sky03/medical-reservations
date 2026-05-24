package edu.unimag.domine.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.unimag.domine.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import edu.unimag.domine.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import edu.unimag.domine.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
@Validated

public class DoctorSchedulesController {

    private final DoctorScheduleService doctorSchedulesService;

    @PostMapping
    public ResponseEntity<DoctorScheduleResponse> create(@PathVariable UUID doctorId, @RequestBody @Valid CreateDoctorScheduleRequest request, UriComponentsBuilder uriBuilder) {

        var createdDoctorSchedule = doctorSchedulesService.create(doctorId, request);
        var location = uriBuilder.path("/api/doctors/{doctorId}/schedules/{scheduleId}")
                .buildAndExpand(doctorId, createdDoctorSchedule.id())
                .toUri();
        return ResponseEntity.created(location).body(createdDoctorSchedule);
    }

    @GetMapping
    public ResponseEntity<List<DoctorScheduleResponse>> getAllSchedules(@PathVariable UUID doctorId, UriComponentsBuilder uriBuilder) {
        var schedules = doctorSchedulesService.getAllSchedules(doctorId);
        return ResponseEntity.ok(schedules);
    }


}
