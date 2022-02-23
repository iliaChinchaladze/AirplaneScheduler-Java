
package solution;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;
import baseclasses.Schedule;

/**
 * The Scheduler class is responsible for deciding which aircraft and crew will
 * be used for each of an airline's flights in a specified period of time,
 * referred to as a "scheduling horizon". A schedule must have an aircraft, two
 * pilots, and sufficient cabin crew for the aircraft allocated to every flight
 * in the horizon to be valid.
 */
public class Scheduler implements IScheduler {
	int count = 0;

	/**
	 * Generates a schedule, providing you with ready-loaded DAO objects to get your
	 * data from
	 * 
	 * @param aircraftDAO         the DAO for the aircraft to be used when
	 *                            scheduling
	 * @param crewDAO             the DAO for the crew to be used when scheduling
	 * @param routeDAO            the DAO to use for routes when scheduling
	 * @param passengerNumbersDAO the DAO to use for passenger numbers when
	 *                            scheduling
	 * @param startDate           the start of the scheduling horizon
	 * @param endDate             the end of the scheduling horizon
	 * @return The generated schedule - which must happen inside 2 minutes
	 */
	@Override
	public Schedule generateSchedule(IAircraftDAO aircraftDAO, ICrewDAO crewDAO, IRouteDAO routeDAO,
			IPassengerNumbersDAO passengerNumbersDAO, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub
		Schedule scheduler = new Schedule(routeDAO, startDate, endDate);
		// List<CabinCrew> crew = crewDAO.getAllCabinCrew();
		// List<Pilot> pilots = crewDAO.getAllPilots();
		// List<Aircraft> aircraft = aircraftDAO.getAllAircraft();

		for (FlightInfo i : scheduler.getRemainingAllocations()) {
			try {
				// String arrivalAirpot = i.getFlight().getArrivalAirport();
				// LocalTime duration =i.getFlight().getDepartureTime();
				String departureAirport = i.getFlight().getDepartureAirportCode();
				int filghtNo = i.getFlight().getFlightNumber();
				LocalDate DepartureDate = i.getDepartureDateTime().toLocalDate();

				// allocate aircraft
				int loadEstimate = passengerNumbersDAO.getPassengerNumbersFor(filghtNo, DepartureDate);
				List<Aircraft> allAircraft = aircraftDAO.getAllAircraft();
				List<Aircraft> enoughSpace = aircraftDAO.findAircraftBySeats(loadEstimate);
				
				
				for (Aircraft l : allAircraft) {
					if (scheduler.hasConflict(l, i) != true && i.getFlight().getArrivalAirportCode().equals(l.getStartingPosition())) {				
							scheduler.unAllocate(i);
							scheduler.allocateAircraftTo(l, i);
							break;
					}
				}
				for (Aircraft k : enoughSpace) {
					if (scheduler.hasConflict(k, i) != true) {
							scheduler.unAllocate(i);
							scheduler.allocateAircraftTo(k, i);
							break;
					}
				}
				for (Aircraft l : enoughSpace) {
					if (scheduler.hasConflict(l, i) != true && i.getFlight().getArrivalAirportCode().equals(l.getStartingPosition())) {						
							scheduler.unAllocate(i);
							scheduler.allocateAircraftTo(l, i);
							break;
					}
				}
				if(scheduler.getAircraftFor(i)==null) {
					for (Aircraft j : allAircraft) {
						if (scheduler.hasConflict(j, i) != true) {
							scheduler.allocateAircraftTo(j, i);
							break;
						}
					}
				}
				

				/////////////////////////////////////////// allocate crew

				List<CabinCrew> suitableCrewByHomeAndType = crewDAO.findCabinCrewByHomeBaseAndTypeRating(
						scheduler.getAircraftFor(i).getTypeCode(), departureAirport);
				List<CabinCrew> suitableCrewByType = crewDAO
						.findCabinCrewByTypeRating(scheduler.getAircraftFor(i).getTypeCode());
				List<CabinCrew> allCrew = crewDAO.getAllCabinCrew();
				int crewRequired = scheduler.getAircraftFor(i).getCabinCrewRequired();

				// check if suitable by home and type are not empty if not assign one from that
				// list

				if (crewRequired >= 0) {
					for (CabinCrew j : suitableCrewByHomeAndType) {
						if (crewRequired >= 0 && scheduler.hasConflict(j, i) != true) {
							scheduler.allocateCabinCrewTo(j, i);
							j = null;
							crewRequired--;
						}
					}

				}
				suitableCrewByType.removeAll(suitableCrewByHomeAndType);
				if (crewRequired >= 0) {
					for (CabinCrew j : suitableCrewByType) {
						if (crewRequired >= 0 && scheduler.hasConflict(j, i) != true) {
							scheduler.allocateCabinCrewTo(j, i);
							j = null;
							crewRequired--;
						}
					}
				}

				// if there is no suitable by home and type just assign random
				if (crewRequired >= 0) {
					for (CabinCrew l : allCrew) {
						if (crewRequired >= 0 && scheduler.hasConflict(l, i) != true) {
							scheduler.allocateCabinCrewTo(l, i);
							l = null;
							crewRequired--;
						}
					}
				}

				/////// allocate captain and first officer
				List<Pilot> eligiblePilotByHomeBaseAndTypeRating = crewDAO.findPilotsByHomeBaseAndTypeRating(
						scheduler.getAircraftFor(i).getTypeCode(), scheduler.getAircraftFor(i).getStartingPosition());
				List<Pilot> eligiblePilotByTypeRating = crewDAO
						.findPilotsByTypeRating(scheduler.getAircraftFor(i).getTypeCode());
				List<Pilot> eligiblePilot = crewDAO.getAllPilots();

				////// allocate captain

				// search in first list
				for (Pilot j : eligiblePilotByHomeBaseAndTypeRating) {
					if (j.getRank().equals(Rank.CAPTAIN) && scheduler.hasConflict(j, i) != true) {
						scheduler.allocateCaptainTo(j, i);
						j = null;
						break;
					}
				}
				// search in second list if nothing has been found
				if (scheduler.getFirstOfficerOf(i) == null) {
					for (Pilot j : eligiblePilotByTypeRating) {
						if (j.getRank().equals(Rank.CAPTAIN) && scheduler.hasConflict(j, i) != true) {
							scheduler.allocateCaptainTo(j, i);
							j = null;
							break;
						}
					}
				}
				// allocate random one if nothing has been found
				if (scheduler.getFirstOfficerOf(i) == null) {
					for (Pilot j : eligiblePilot) {
						if (j.getRank().equals(Rank.CAPTAIN) && scheduler.hasConflict(j, i) != true) {
							scheduler.allocateCaptainTo(j, i);
							j = null;
							break;
						}
					}
				}

				////////// Allocate Firs officer

				// trying to find sutiable officer by home and type
				if (eligiblePilot != null) {
					for (Pilot j : eligiblePilotByHomeBaseAndTypeRating) {
						if (j.getRank().equals(Rank.FIRST_OFFICER) && scheduler.hasConflict(j, i) != true) {
							scheduler.allocateFirstOfficerTo(j, i);
							j = null;
							break;
						}
					}
					// trying to find officer by type
					if (scheduler.getFirstOfficerOf(i) == null) {
						for (Pilot j : eligiblePilotByTypeRating) {
							if (j.getRank().equals(Rank.FIRST_OFFICER) && scheduler.hasConflict(j, i) != true) {
								scheduler.allocateFirstOfficerTo(j, i);
								j = null;
								break;
							}
						}
					}
					// tring to assign random first officer
					if (scheduler.getFirstOfficerOf(i) == null) {
						for (Pilot j : eligiblePilot) {
							if (j.getRank().equals(Rank.FIRST_OFFICER) && scheduler.hasConflict(j, i) != true) {
								scheduler.allocateFirstOfficerTo(j, i);
								j = null;
								break;
							}
						}
					}
					// if first officer is not available allocate captain as a first officer
					if (scheduler.getFirstOfficerOf(i) == null) {
						for (Pilot j : eligiblePilotByHomeBaseAndTypeRating) {
							if (j.getRank().equals(Rank.CAPTAIN) && scheduler.hasConflict(j, i) != true) {
								scheduler.allocateFirstOfficerTo(j, i);
								j = null;
								break;
							}
						}
					}

					// alocate captian as officer with same type
					if (scheduler.getFirstOfficerOf(i) == null) {
						for (Pilot j : eligiblePilotByTypeRating) {
							if (j.getRank().equals(Rank.CAPTAIN) && scheduler.hasConflict(j, i) != true) {
								scheduler.allocateFirstOfficerTo(j, i);
								j = null;
								break;
							}
						}
					}

					if (scheduler.getFirstOfficerOf(i) == null) {
						for (Pilot j : eligiblePilot) {
							if (j.getRank().equals(Rank.CAPTAIN) && scheduler.hasConflict(j, i) != true) {
								scheduler.allocateFirstOfficerTo(j, i);
								j = null;
								break;
							}
						}
					}
				}

				scheduler.completeAllocationFor(i);
				System.out.println("FlightAdded" + count);
				count++;

			} catch (DoubleBookedException | InvalidAllocationException e) {
				e.printStackTrace();

			}
		}
		return scheduler;

	}

}
