/* Verifies if a film has been directed by the specified individual
 *
 * @NotTheRealOliverTwist
 * @version
 */

import java.util.*;

public class DirectorsFilter implements Filter{
	ArrayList<String>  myDirectors;
	public DirectorsFilter(String directors)
	{
		myDirectors = new ArrayList<String>();
		for(String s : directors.split(",")){
			myDirectors.add(s.trim());
		}
	}
	public boolean satisfies(String id){
		boolean hasDirector = false;
		for(String s : myDirectors){
			if(MovieDatabase.getDirector(id).contains(s)){
				hasDirector = true;
				break;
			}
		}
		return hasDirector == true;
	}
}
