/* Filter used to narrow down movie list created from movie database 
 * based on desired genre
 *
 * @NotTheRealOliverTwist
 * @version
 */

import java.util.*;
import edu.duke.*;

public class GenreFilter implements Filter{
	private String myGenre;
	public GenreFilter(String genre){
		myGenre = genre;
	}

	@Override
	public boolean satisfies(String id)
	{
		return MovieDatabase.getGenres(id).contains(myGenre);
	}
}
