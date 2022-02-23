package solution;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	
	List<Route> paths =  new ArrayList<>();
	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		// TODO Auto-generated method stub
		List<Route> list = new ArrayList<>();

        for(int i=0;i<paths.size();i++)
        {

            if(paths.get(i).getDayOfWeek().equals(dayOfWeek))
            {
                list.add(paths.get(i));
            }

        }
        return list;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to searh for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		List<Route> list = new ArrayList<>();

        for(int i=0;i<paths.size();i++)
        {

            if(paths.get(i).getDayOfWeek().equals(dayOfWeek) && paths.get(i).getDepartureAirportCode().equals(airportCode))
            {
                list.add(paths.get(i)); 
            }

        }
        return list;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		List<Route> list = new ArrayList<>();

        for(int i=0;i<paths.size();i++)
        {

            if(paths.get(i).getDepartureAirportCode().equals(airportCode))
            {
                list.add(paths.get(i));
            }

        }
        return list;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		// TODO Auto-generated method stub
		List<Route> list = new ArrayList<>();
		//String newDate = new SimpleDateFormat("EE").format(date.toString());
		DayOfWeek nd = date.getDayOfWeek();
		String ndShort = nd.toString().substring(0, 3).toUpperCase();
		 for(int i=0;i<paths.size();i++){
			 if(paths.get(i).getDayOfWeek().toUpperCase().equals(ndShort)){
				 list.add(paths.get(i));
			 }	           
	      }
		return list;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		List<Route> allRoutes = paths;
		return allRoutes;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		// TODO Auto-generated method stub
		return paths.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		if(p ==null) {
			throw new DataLoadingException();
		}
		try
		{
			
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();			
			Document document = db.parse(p.toString());						
			Element root = document.getDocumentElement();
			NodeList children = root.getChildNodes();
			
			
			
			for(int i=0; i<children.getLength();i++) {
				Node child = children.item(i);
				if(child.getNodeName().equals("Route")) {
					
					Route a = new Route();
					NodeList children2 = child.getChildNodes();
					for(int j=0;j<children2.getLength();j++) {
						//at this point we are on same level as info
						Node child2 = children2.item(j);
						
						if(child2.getNodeName().equals("FlightNumber")  ) {
							if(child2.getChildNodes().item(0).getNodeValue().matches("[0-9]+")) {
								a.setFlightNumber(Integer.parseInt(child2.getChildNodes().item(0).getNodeValue()));
							}
							else {
								throw  new DataLoadingException();
							}
							
						}
						if(child2.getNodeName().equals("DayOfWeek")) {
							a.setDayOfWeek(child2.getChildNodes().item(0).getNodeValue());	
							if (a.getDayOfWeek().equals("Mon")
									||a.getDayOfWeek().equals("Tue")
                                    ||a.getDayOfWeek().equals("Wed")
                                    ||a.getDayOfWeek().equals("Thu")
                                    ||a.getDayOfWeek().equals("Fri") 
                                    ||a.getDayOfWeek().equals("Sat")
                                    ||a.getDayOfWeek().equals("Sun")){}                            
                           else {
                               throw new DataLoadingException();
                           }
							
						}
						if(child2.getNodeName().equals("DepartureTime")) {
							a.setDepartureTime(LocalTime.parse(child2.getChildNodes().item(0).getNodeValue()));
						}
						if(child2.getNodeName().equals("DepartureAirport")) {
							a.setDepartureAirport(child2.getChildNodes().item(0).getNodeValue());
						}
						if(child2.getNodeName().equals("DepartureAirportIATACode")) {
							a.setDepartureAirportCode(child2.getChildNodes().item(0).getNodeValue());
						}
						if(child2.getNodeName().equals("ArrivalTime")) {
							a.setArrivalTime(LocalTime.parse(child2.getChildNodes().item(0).getNodeValue()));
						}
						if(child2.getNodeName().equals("ArrivalAirport")) {
							a.setArrivalAirport(child2.getChildNodes().item(0).getNodeValue());
						}
						if(child2.getNodeName().equals("ArrivalAirportIATACode")) {
							a.setArrivalAirportCode(child2.getChildNodes().item(0).getNodeValue());
						}						
						if(child2.getNodeName().equals("Duration")) {
							try {
							a.setDuration(Duration.parse(child2.getChildNodes().item(0).getNodeValue()));
							}
							catch(DateTimeParseException e) {
								throw new DataLoadingException(e);
							}
						}
						
					}
					paths.add(a);
				}
				
			}
			
		}
		catch (IOException | NumberFormatException ioe ) {
			throw new DataLoadingException(ioe);
		} catch (SAXException e) {
			throw new DataLoadingException(e);
		} catch (ParserConfigurationException e) {
			throw new DataLoadingException(e);
		}
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		paths.removeAll(paths);
	}

}
