package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	/**
	 * Corrected test to check that a ticket is saved and that parking table is
	 * updated in DB
	 */
	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		assertNotNull(ticket.getId());
		ParkingSpot parkingSpot = ticket.getParkingSpot();
		assertFalse(parkingSpot.isAvailable());

		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
	}

	/**
	 * added "getClosedTicket" method to check if fields in ticket are effectively
	 * updated in database
	 */
	@Test
	public void testParkingLotExit() throws InterruptedException {

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getClosedTicket("ABCDEF");
		assertNotNull(ticket.getPrice());
		assertNotNull(ticket.getOutTime());

		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

	/**
	 * To verify that no available slot is return when full for the parking type CAR
	 */
	@Test
	public void afterThreeIncomingCarsNoAvailableSlotShouldBeReturned() throws Exception {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		parkingService.processIncomingVehicle();
		parkingService.processIncomingVehicle();

		assertEquals(0, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
	}

	/**
	 * To check that the next available slot of parking spot is return for the
	 * parking type CAR
	 */
	@Test
	public void afterTwoIncomingCarsSlot3AvailableSlotShouldBeReturned() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processIncomingVehicle();
		assertEquals(3, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
	}

	@Test
	public void TestMethodGetTicket() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		assertNotNull(ticket.getInTime());
	}

	@Test
	public void TestMethodGetClosedTicket() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getClosedTicket("ABCDEF");
		assertNotNull(ticket.getOutTime());
	}
}