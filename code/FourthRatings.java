/*
 * Ratings Class used to compile all user / movie ratings with various helper methods to access the information
 * These ratings are then used to recommend alterantive movies to a user based on a weighted average calculation from other users / movie ratings
 * 
 * @NotReallyOliverTwist 
 */

import java.util.*;
import edu.duke.*;
import org.apache.commons.csv.*;

public class FourthRatings {
	/* getAverageByID calculates the average ratiing for a specific movie
	 * it makes use of the minimalRaters parameter to only calculate averages if there
	 * are a minimum amount of raters for a movie.
	 */

	public double getAverageByID(String id, int minimalRaters)
	{
		var wrapper = new Object(){
			double avg = 0.00;
			double totalRaters = 0.00;
			ArrayList<String> currMovies = new ArrayList<String>();
			ArrayList<Rater> myRaters = RaterDatabase.getRaters();
		};
		wrapper.myRaters.forEach(rating ->{
			wrapper.currMovies = rating.getItemsRated();
			wrapper.currMovies.forEach(movie ->{
				if(movie.equals(id)){
					wrapper.avg += rating.getRating(movie);
					wrapper.totalRaters++;
				}
			});
		});
		if(wrapper.totalRaters >= minimalRaters){
			wrapper.avg = wrapper.avg / wrapper.totalRaters;
			return wrapper.avg;
		}
		return 0.00;
	}

	/* getAverageRatings calculates the average ratings for all movies in the moviedatabase
	 * taking into account the minimum amount of raters required.
	 */

	public ArrayList<Rating> getAverageRatings(int minimalRaters){
		var wrapper = new Object(){
			ArrayList<Rating> movieRatings = new ArrayList<Rating>();
			ArrayList<String> movieList = MovieDatabase.filterBy(new TrueFilter());
			double avg = 0.00;
		};

		wrapper.movieList.forEach(name ->{
			wrapper.avg = getAverageByID(name, minimalRaters);
			if(wrapper.avg != 0.00){
				Rating currRating = new Rating(name, wrapper.avg);
				wrapper.movieRatings.add(currRating);
			}
		});
		Collections.sort(wrapper.movieRatings);
		return wrapper.movieRatings;
	}

	/* getAverageRatingsByFilter calculates the average ratings for movies that meet the prescriped
	 * filter requirements, i.e: GenreFilter, YearAfterFilter, etc
	 */

	public ArrayList<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria){
		var wrapper = new Object(){
			ArrayList<Rating> movieRatings = new ArrayList<Rating>();
			ArrayList<String> movieList = MovieDatabase.filterBy(filterCriteria);
			double avg = 0.00;
		};
		wrapper.movieList.forEach(name ->{
			wrapper.avg = getAverageByID(name, minimalRaters);
			if(wrapper.avg != 0.00){
				Rating currRating = new Rating(name, wrapper.avg);
				wrapper.movieRatings.add(currRating);
			}
		});
		Collections.sort(wrapper.movieRatings);
		return wrapper.movieRatings;
	}

	/* dotProduct calculates the similarity rating between two raters
	 */

	private double dotProduct(Rater me, Rater r){
		String myID = me.getID();
		String userID = r.getID();
		Rater meAfter = RaterDatabase.getRater(myID);
		Rater rAfter = RaterDatabase.getRater(userID);
		var wrapper = new Object(){
			double dotProd = 0.00;
			int count = 0;
			ArrayList<String> movDatabase = MovieDatabase.filterBy(new TrueFilter());
		};
		wrapper.movDatabase.forEach(movie ->{
			if(meAfter.hasRating(movie) && rAfter.hasRating(movie)){
				double myRating = meAfter.getRating(movie) - (double)5;
				double userRating = rAfter.getRating(movie) - (double)5;
				//			wrapper.count++;
				wrapper.dotProd += myRating * userRating;
			}
		});
		//wrapper.dotProd = wrapper.dotProd;
		return wrapper.dotProd;
	}

	/* getSimilarities obtains an ArrayList of Ratings that are similar to the users
	 * in order to make recommendations for alternative titles at a later stage.
	 */

	private ArrayList<Rating> getSimilarities(String id){
		// Might need to add a new raterdatabase object here. However not certain
		var wrapper = new Object(){
			ArrayList<Rating> similarRaters = new ArrayList<Rating>();	
			ArrayList<Rater> raters = RaterDatabase.getRaters();
			Rater me = RaterDatabase.getRater(id);
		};
		wrapper.raters.forEach(rating ->{
			if(!rating.getID().equals(id)){
				double dotProd = dotProduct(wrapper.me, rating);
				if(dotProd > 0.0){
					Rating r = new Rating(rating.getID(), dotProd);
					wrapper.similarRaters.add(r);
				}
			}
		});
		Collections.sort(wrapper.similarRaters, Collections.reverseOrder());
		return wrapper.similarRaters;
	}
	/* getSimilarRatings calculates a weighted average of ratings to recommend
	 * users who have a similar taste to the current user.
	 * As they may be more likely to recommend a more ideal film history based on their past viewing
	 */

	public ArrayList<Rating> getSimilarRatings(String id, int numSimilarRaters, int minimalRaters){
		var wrapper = new Object(){
			ArrayList<Rating> similarRaters = getSimilarities(id);
			ArrayList<Rating> weightedRaters = new ArrayList<Rating>();
			ArrayList<String> movieList = MovieDatabase.filterBy(new TrueFilter());
		};

		wrapper.movieList.forEach(movie ->{
			double sum = 0.0;
			int count = 0;

			for (int i = 0; i < numSimilarRaters; i++) {
				Rating raterRating = wrapper.similarRaters.get(i);
				String raterID = raterRating.getItem();
				Rater currRater = RaterDatabase.getRater(raterID);
				if(currRater.hasRating(movie)) {
					count++;
					double movieRating = currRater.getRating(movie);
					double similarRating = raterRating.getValue() * movieRating;
					sum += similarRating;
				}
			}
			if (count >= minimalRaters) {
				double weightedAverage = sum / count;
				Rating rating = new Rating(movie, weightedAverage);
				wrapper.weightedRaters.add(rating);
			}
		});

		Collections.sort(wrapper.weightedRaters, Collections.reverseOrder());
		return wrapper.weightedRaters;
	}
	/* getSimilarRatingsByFilter does the same as getSimilarRatings with only taking into account 
	 * the type of filter a user would like to use in order to personalise their recommendations more
	 */
	public ArrayList<Rating> getSimilarRatingsByFilter(String id, int numSimilarRaters, int minimalRaters, Filter filterCriteria){
		var wrapper = new Object(){
			ArrayList<Rating> similarRaters = getSimilarities(id);
			ArrayList<Rating> weightedRaters = new ArrayList<Rating>();
			ArrayList<String> movieList = MovieDatabase.filterBy(filterCriteria);
		};

		wrapper.movieList.forEach(movie ->{
			double sum = 0.0;
			int count = 0;

			for (int i = 0; i < numSimilarRaters; i++) {
				Rating raterRating = wrapper.similarRaters.get(i);
				String raterID = raterRating.getItem();
				Rater currRater = RaterDatabase.getRater(raterID);
				if(currRater.hasRating(movie)) {
					count++;
					double movieRating = currRater.getRating(movie);
					double similarRating = raterRating.getValue() * movieRating;
					sum += similarRating;
				}
			}
			if (count >= minimalRaters) {
				double weightedAverage = sum / count;
				Rating rating = new Rating(movie, weightedAverage);
				wrapper.weightedRaters.add(rating);
			}
		});

		Collections.sort(wrapper.weightedRaters, Collections.reverseOrder());
		return wrapper.weightedRaters;
	}
	public static void main(String[] args){
		FourthRatings fRatings = new FourthRatings();
		fRatings.getAverageRatings(1);
	}
}
