package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/**
 * added feature for recurring user who benefit from a discount correction for
 * the calcul of duration(from int to double for in/outHour and getTime iso
 * getHours)
 */
public class FareCalculatorService {
	public TicketDAO ticketDAO;

	public void calculateFare(Ticket ticket, TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();
		// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = ((double) (outHour - inHour) / 3600000);

		if (duration < 0.50)
			ticket.setPrice(0);
		else {

			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR:
				TicketDAO ticketDao = new TicketDAO();
				int ticketQuantity = ticketDao.countTicketByVehiculeRegNumber(ticket.getVehicleRegNumber());
				if (ticketQuantity > 1) {
					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * (1 - Fare.CAR_RECURRING_DISCOUNT_COEF));
					System.out.println("As a recurring user of our parking lot, you benefit from a discount="
							+ duration * Fare.CAR_RATE_PER_HOUR * Fare.CAR_RECURRING_DISCOUNT_COEF);
				} else {

					ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				}
				break;

			case BIKE:
				TicketDAO ticketDaoBike = new TicketDAO();
				int ticketQuantityForBike = ticketDaoBike.countTicketByVehiculeRegNumber(ticket.getVehicleRegNumber());
				if (ticketQuantityForBike > 1) {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * (1 - Fare.BIKE_RECURRING_DISCOUNT_COEF));
					System.out.println("As a recurring user of our parking lot, you benefit from a discount="
							+ duration * Fare.BIKE_RATE_PER_HOUR * Fare.BIKE_RECURRING_DISCOUNT_COEF);
				} else {
					ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
					break;
				}

			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
	}
}