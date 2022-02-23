package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;
import org.json.*;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {
	List<Pilot> pilotJob = new ArrayList<>();
	List<CabinCrew> cabinJob = new ArrayList<>();
	List<Crew> crewJob = new ArrayList<>();
	
	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		if(p ==null) {
			throw new DataLoadingException();
		}
		 try {
			 	BufferedReader reader = Files.newBufferedReader(p);
	            String json = ""; 
	            String line = "";
	            while((line = reader.readLine())!= null) {
	                json = json + line;
	            } 

	            
	            JSONObject root = new JSONObject (json);
	            
	            JSONArray pilots =root.getJSONArray("pilots");	 
	             
	            for(int i=0;i<pilots.length();i++) {
	                JSONObject crew = pilots.getJSONObject(i);
	                Pilot a = new Pilot();   
	                //Crew b = new Crew();
	                // setting forname and surname
	                a.setForename(crew.getString("forename"));
	                a.setSurname(crew.getString("surname"));
	                // setting ranks by checking if they equal one or another
	                if(crew.getString("rank").equals("CAPTAIN")) {a.setRank(Rank.CAPTAIN);}
	                if(crew.getString("rank").equals("FIRST_OFFICER")) {a.setRank(Rank.FIRST_OFFICER);}
	                //seting home airport
	                a.setHomeBase(crew.getString("home_airport"));
	                //as pilot can be eligible for several types we 
	                JSONArray raiting =crew.getJSONArray("type_ratings");
	                for(int j=0;j<raiting.length();j++)
	                 {
	                     a.setQualifiedFor(raiting.getString(j));
	                 }
	                pilotJob.add(a);
	                //crewJob.add(a);	                
	            }
	            crewJob.addAll(pilotJob);
	           
	            JSONArray CabinCrew =root.getJSONArray("cabincrew");	           
	            for(int i=0;i<CabinCrew.length();i++) {
	                JSONObject crew = CabinCrew.getJSONObject(i);
	                CabinCrew a = new CabinCrew();   
	                
	                a.setForename(crew.getString("forename"));
	                a.setSurname(crew.getString("surname"));	         
	                a.setHomeBase(crew.getString("home_airport"));
	                JSONArray raiting =crew.getJSONArray("type_ratings");
	                for(int j=0;j<raiting.length();j++)
	                 {
	                     a.setQualifiedFor(raiting.getString(j));
	                 }     
	                cabinJob.add(a);	              
	            }
	             crewJob.addAll(cabinJob);
	        }
		 	catch(org.json.JSONException e)
		 	{
		 		throw new DataLoadingException(e);
		 		} 
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new DataLoadingException(e);
			}
		 
	    }
	
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		List<CabinCrew> list = new ArrayList<>();
		
		 for(int i=0;i<cabinJob.size();i++){
			 if(cabinJob.get(i).getHomeBase().equals(airportCode)){
				 list.add(cabinJob.get(i));
			 }	           
	      }
		return list;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */

	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		List<CabinCrew> list = new ArrayList<>();
		 for(int i=0;i<cabinJob.size();i++){
			 if(cabinJob.get(i).getHomeBase().equals(airportCode)){
				 //list.add(cabinJob.get(i));
				 //JSONArray raiting =crew.getJSONArray("type_ratings");				  
				 for(int j=0;j<cabinJob.get(i).getTypeRatings().size();j++) {
					 if(cabinJob.get(i).getTypeRatings().get(j).equals(typeCode)) {
						 list.add(cabinJob.get(i));
					 }
				 }				 				 
			 }
	     }
		
		return list;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		// TODO Auto-generated method stub
		List<CabinCrew> list = new ArrayList<>();
		for(int i=0;i<cabinJob.size();i++) {
			for(int j=0;j<cabinJob.get(i).getTypeRatings().size();j++) {
				if(cabinJob.get(i).getTypeRatings().get(j).equals(typeCode)){
					list.add(cabinJob.get(i));
				}
			}
		}
		return list;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		List<Pilot> list = new ArrayList<>();
		 for(int i=0;i<pilotJob.size();i++){
			 if(pilotJob.get(i).getHomeBase().equals(airportCode)){
				 list.add(pilotJob.get(i));
			 }	           
	      }
		return list;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		List<Pilot> list = new ArrayList<>();
		 for(int i=0;i<pilotJob.size();i++){
			 if(pilotJob.get(i).getHomeBase().equals(airportCode)){
				 //list.add(cabinJob.get(i));
				 //JSONArray raiting =crew.getJSONArray("type_ratings");				 
				 for(int j=0;j<pilotJob.get(i).getTypeRatings().size();j++) {
					 if(pilotJob.get(i).getTypeRatings().get(j).equals(typeCode)) {
						 list.add(pilotJob.get(i));
					 }
				 }				 				 
			 }
	     }
		
		return list;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		// TODO Auto-generated method stub
		List<Pilot> list = new ArrayList<>();
		for(int i=0;i<pilotJob.size();i++) {
			for(int j=0;j<pilotJob.get(i).getTypeRatings().size();j++) {
				if(pilotJob.get(i).getTypeRatings().get(j).equals(typeCode)){
					list.add(pilotJob.get(i));
				}
			}
		}
		return list;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		// TODO Auto-generated method stub
		List<CabinCrew> allCabinCrew = cabinJob;
		return allCabinCrew;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override 
	public List<Crew> getAllCrew() {
		// TODO Auto-generated method stub
		List<Crew> allCrew = crewJob;
		return allCrew;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		// TODO Auto-generated method stub
		List<Pilot> allPilots = pilotJob;
		return allPilots;
	}

	@Override
	public int getNumberOfCabinCrew() {
		// TODO Auto-generated method stub
		return cabinJob.size();
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		// TODO Auto-generated method stub
		return pilotJob.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		pilotJob.removeAll(pilotJob);
		crewJob.removeAll(crewJob);
		cabinJob.removeAll(cabinJob);
	}

}
