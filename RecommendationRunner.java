/* RecoomendationRunner is implemented with the users browser.
 * This method displays the movies to be rated
 * Based on the intial ratings, a set of new recommendations is made that the current user may enjoy
 *
 * @NotReallyOliverTwist
 * @version
 */

import java.util.*;
import edu.duke.*;

public class RecommendationRunner implements Recommender {

	/* getItemsToRate() is used to populate the movies to be rated in the users browser.
	 * This may be used in other means, however for the purpose of this project it is 
	 * implemented using HTML.
	 */

	public ArrayList<String> getItemsToRate(){
		Random random = new Random();
		ArrayList<String> all_movies = MovieDatabase.filterBy(new TrueFilter());
		var wrapper = new Object(){
			ArrayList<String> movie_ids = new ArrayList<String>();
			int count = 0;
		};

		all_movies.forEach(movie ->{
			int value = random.nextInt(all_movies.size());
			if(!wrapper.movie_ids.contains(all_movies.get(value)) && wrapper.count < 20){
				wrapper.movie_ids.add(all_movies.get(value));
				wrapper.count++;
			}
		});
		return wrapper.movie_ids;
	}
	/* printRecommendationsFor() is used to populate the browser with HTML code to display the resulting
	 * recommended movie titles. This may obviously be adjusted and styled more. However for basic functionality
	 * it works as it should.
	 *
	 * Users may feel free to modify this as they wish.
	 */ 

	public void printRecommendationsFor(String webRaterID){
		MovieDatabase.initialize("ratedmoviesfull.csv");
		RaterDatabase.initialize("ratings.csv");
		FourthRatings fRatings = new FourthRatings();
		int min = 5;
		int max = 20;
		ArrayList<Rating> recommendations = fRatings.getSimilarRatings(webRaterID, max, min);

		if(recommendations.size() == 0){
			System.out.println("<script type=\"text/javascript\">");
			System.out.println("alert('There are no recommended movies for you. Perhaps try refreshing the page.')");
			System.out.println("</script>");
		}
		else{
			System.out.println("Recommended movies for you based on past history: \n");
			String header = ("<table> <tr> <th>Movie Title</th> " + 
					"<th>Rating</th> <th>Genres</th> </tr>");
			var wrapper = new Object(){
				String body = "";
			};
			recommendations.forEach( rec -> {
				wrapper.body += "<tr> <td>" + MovieDatabase.getTitle(rec.getItem()) 
					+ "</td> </tr>" + Double.toString(rec.getValue())
					+ "</td> </tr>" + MovieDatabase.getGenres(rec.getItem())
					+ "</td></tr>";
			});
			System.out.println(header+wrapper.body+"</table>");
		}
	}

	public static void main(String[] args)
	{
		RecommendationRunner rr = new RecommendationRunner();
		rr.getItemsToRate();
		rr.printRecommendationsFor();
	}
}
