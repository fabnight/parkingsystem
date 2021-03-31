package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
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
public class ParkingSpotDaoTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static String vehicleRegNumber = "ABCDEF";
	private static Ticket ticket;
	private static ParkingService parkingService;
	private static DataBasePrepareService dataBasePrepareService;
	private static TicketDAO ticketDAO;
	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeEach
	private void setUpPerTest() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
		ticket = new Ticket();
		ticket.getId();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(Fare.CAR_RATE_PER_HOUR);

	}

	@AfterAll
	private static void tearDown() {

	}

	/** The first available slot should be the minimum slot number=1 */
	@Test
	public void TestGetNextAvailableSlot() {
		int slotNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		assertEquals(1, slotNumber);
	}

	/** if parking type is null, available slot should return -1 */
	@Test
	public void TestGetNextAvailableSlotWhenParkingTypeIsNull() {

		int slotNumber = parkingSpotDAO.getNextAvailableSlot(null);
		assertEquals(-1, slotNumber);
	}

	/**
	 * inputing "3" by the user in the option menu for vehicle type should return
	 * exception
	 */
	@Test
	public void TestExceptiongetVehichleType() throws Exception {
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(inputReaderUtil.readSelection()).thenReturn(3);
		try {

			parkingService.processIncomingVehicle();

		} catch (Throwable e) {
			assertEquals("Error parsing user input for type of vehicle", e.getMessage());
		}
	}

	@Test
	public void TestExceptionUpdateParkingWhenParkingSpotIsNull() throws Exception {

		try {

			parkingSpotDAO.updateParking(null);

		} catch (Throwable ex) {
			assertEquals("Error updating parking info", ex.getMessage());
		}
	}
}