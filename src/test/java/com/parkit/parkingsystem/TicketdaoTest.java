package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class TicketdaoTest {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static TicketDAO ticketDAO;
	private static String vehicleRegNumber = "ABCDEF";
	private static Ticket ticket;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;

	@BeforeEach
	private void setUpPerTest() throws Exception {
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
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

	@Test
	public void TestUpdateTicket() {

		ticketDAO.saveTicket(ticket);
		Date outTime = new Date();
		ticket.setOutTime(outTime);

		boolean isUpdated = ticketDAO.updateTicket(ticket);

		assertTrue(isUpdated);
	}

	@Test
	public void TestGetClosedTicket() {

		ticketDAO.saveTicket(ticket);
		Date outTime = new Date();
		ticket.setOutTime(outTime);

		Ticket ticket = ticketDAO.getClosedTicket(vehicleRegNumber);

		assertNotNull(ticket);
	}

	@Test
	public void TestExceptionUpdateClosedTicketWhenVehicleRegNumberIsNull() throws Exception {
		try {
			ticketDAO.saveTicket(ticket);
			Date outTime = new Date();
			ticket.setOutTime(outTime);
			ticket.setVehicleRegNumber(null);
			ticket = ticketDAO.getClosedTicket(vehicleRegNumber);
		} catch (Throwable ex) {
			assertEquals("Error fetching the last slot", ex.getMessage());
		}
	}

	@Test
	public void TestGetTicket() {

		ticketDAO.getTicket(vehicleRegNumber);

		assertNotNull(ticket.getParkingSpot());
	}

	@Test
	public void TestExceptionSaveTicketConnectionEqualsNull() throws Exception {

		Assertions.assertThrows(java.sql.SQLException.class, () -> {
			Connection con = null;
			con = dataBaseTestConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			con = null;
			ps.setInt(3, ticket.getId());
			ps.execute();

			dataBaseTestConfig.closeConnection(con);
		});

	}
}