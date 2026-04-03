package edu.unimag;

import org.springframework.boot.SpringApplication;

public class TestMedicalReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.from(MedicalReservationsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
