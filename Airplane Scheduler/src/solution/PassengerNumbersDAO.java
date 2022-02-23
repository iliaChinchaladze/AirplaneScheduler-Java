package solution;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {

	//HashMap hm=new HashMap(int initialCapacity, float loadFactor);  
	 HashMap<String, Integer> Hmap = new HashMap<>(); 
	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		// TODO Auto-generated method stub
		return Hmap.size();
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		// TODO Auto-generated method stub
		Integer Data = Hmap.get(date.toString()+flightNumber);
		if(Data == null) {
			return -1;
		}
		else {
			return Data;
		}		
	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		Connection c = null;
		if(p ==null) {
			throw new DataLoadingException();
		}
		try {
			// connect to DB
			c = DriverManager.getConnection("jdbc:sqlite:"+p.toString()); 
			// run query
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM PassengerNumbers");

			//ferch results
			while(rs.next()) {
				
				
				//LocalDate date = LocalDate.parse(rs.getString("Date"));
				String date = rs.getString("Date");
				String flightNumber = rs.getString("FlightNumber");
                int loadEstimate = Integer.parseInt(rs.getString("LoadEstimate"));
             
                Hmap.put(date + flightNumber, loadEstimate);
			}
		}
		catch(SQLException se){
			throw new DataLoadingException(se);
		}

	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		Hmap.clear();
	}

}
