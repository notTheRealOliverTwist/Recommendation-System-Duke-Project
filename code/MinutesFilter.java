/* Verifies if a movie is between and including the min and max values of time specified
 *
 * @NotTheRealOliverTwist
 * @version
 *
 */

import java.util.*;
import edu.duke.*;

public class MinutesFilter implements Filter{

	private int minTime;
	private int maxTime;

	public MinutesFilter(int min, int max){
		minTime = min;
		maxTime = max;
	}

	public boolean satisfies(String id){
		return MovieDatabase.getMinutes(id) >= minTime && MovieDatabase.getMinutes(id) <= maxTime;
	} 
} 
