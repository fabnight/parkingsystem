package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void processExitingVehicleTypeCarUpdateParkingTest() {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTypeCarUpdateParkingTest() throws Exception {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

		parkingService.processIncomingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTypeBikeUpdateParkingTest() throws Exception {
		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);

		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(4);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);

		parkingService.processIncomingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTypeBikeSaveTicketTest() {
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
		parkingService.processIncomingVehicle();
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}

	@Test
	public void processIncomingVehicleTypeCarSaveTicketTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		parkingService.processIncomingVehicle();
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}
}