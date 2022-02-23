package solution;

import java.nio.file.Paths;
import java.time.LocalDate;

import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) throws DataLoadingException {					
		try {
			IAircraftDAO aircraft1 = new AircraftDAO();
	        CrewDAO crew = new CrewDAO();
	        RouteDAO routes = new RouteDAO();
	        PassengerNumbersDAO passengers = new PassengerNumbersDAO();

//			Tells your Aircraft DAO to load this particular data file
			aircraft1.loadAircraftData(Paths.get("./data/aircraft.csv"));
			crew.loadCrewData(Paths.get("./data/crew.json"));
			routes.loadRouteData(Paths.get("./data/routes.xml"));
			passengers.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));
//			aircraft.loadAircraftData(Paths.get("./data/mini_aircraft.csv"));
// 			aircraft1.loadAircraftData(Paths.get("./data/malformed_aircraft1.csv"));
			
//			for(int i=0;i<aircraft.findAircraftBySeats(200).size();i++)
//            {
//                System.out.println(aircraft.findAircraftBySeats(200).get(i).getSeats());
//                //System.out.println(aircraft.findAircraftByStartingPosition("LGW").get(i).getStartingPosition());
//            }
//			System.out.println("------------");
//			for(int i=0;i<aircraft.findAircraftByStartingPosition("LGW").size();i++) {
//				System.out.println(aircraft.findAircraftByStartingPosition("LGW").get(i).getStartingPosition());
//			}
//			System.out.println("------------");
//			System.out.println(aircraft.findAircraftByTailCode("G-TCDP").getTailCode());
//			System.out.println("------------");
//			for(int i=0;i<aircraft.findAircraftByType("A320").size();i++) {
//				System.out.println(aircraft.findAircraftByType("A320").get(i).getTypeCode());
//			}
		
			PassengerNumbersDAO passenger = new PassengerNumbersDAO();
			passenger.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));
			System.out.println(passenger.getNumberOfEntries()); 
			System.out.println("------------");
			Scheduler sch = new Scheduler();
			sch.generateSchedule(aircraft1, crew, routes, passengers, LocalDate.parse("2020-07-01"), LocalDate.parse("2020-07-08"));
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data"); 
			dle.printStackTrace(); 
		}
	} 

}
