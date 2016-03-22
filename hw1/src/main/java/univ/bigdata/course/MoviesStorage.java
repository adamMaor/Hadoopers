package univ.bigdata.course;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;
import univ.bigdata.course.providers.MoviesProvider;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;


/**
 * Main class which capable to keep all information regarding movies review.
 * Has to implements all methods from @{@link IMoviesStorage} interface.
 * Also presents functionality to answer different user queries, such as:
 * <p>
 * 1. Total number of distinct movies reviewed.
 * 2. Total number of distinct users that produces the review.
 * 3. Average review score for all movies.
 * 4. Average review score per single movie.
 * 5. Most popular movie reviewed by at least "K" unique users
 * 6. Word count for movie review, select top "K" words
 * 7. K most helpful users
 */
public class MoviesStorage implements IMoviesStorage {
    // Hashmap of movies, key is movie and value is a list of movie reviews for that movie
    private HashMap <String, ArrayList<MovieReview>> reviewList;
    // Map with key movieId and value of number of reviews for that movie
    private List<Movie> moviesSortedByScore;
    private Map<String, Long> moviesSortedByNumOfReviews; 
    
    public Map<String, Long> getMoviesSortedByNumOfReviews() {
		return moviesSortedByNumOfReviews;
	}

	public HashMap<String, ArrayList<MovieReview>> getReviewList() {
		return reviewList;
	}

	public MoviesStorage(final MoviesProvider provider) {
        this.reviewList = new HashMap<>();
        this.moviesSortedByScore = new ArrayList<>();
        this.moviesSortedByNumOfReviews = new LinkedHashMap<>();
        while (provider.hasMovie()) {
            MovieReview review = provider.getMovie();
            if (!reviewList.containsKey(review.getMovie().getProductId())){
                ArrayList<MovieReview> movieReviewList = new ArrayList<>();
                movieReviewList.add(review);
                reviewList.put(review.getMovie().getProductId(), movieReviewList);
            }
            else {
                reviewList.get(review.getMovie().getProductId()).add(review);
            }
        }
    }

    @Override
    public double totalMoviesAverageScore() {
        int count = 0, sum = 0;
        Set <String> movies = reviewList.keySet();
        for (String movieId : movies){
            ArrayList <MovieReview> reviews = reviewList.get(movieId);
            for (MovieReview review : reviews){
                sum += review.getMovie().getScore();
                count++;
            }
        }
        return (double)sum/count;
    }

    @Override
    public double totalMovieAverage(String productId) {
        int count = 0, sum = 0;
        ArrayList <MovieReview> list = reviewList.get(productId);
        for (MovieReview review : list){
            sum += review.getMovie().getScore();
            count++;
        }
        return (double)sum/count;
    }

    @Override
    public List<Movie> getTopKMoviesAverage(long topK) {
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        return moviesSortedByScore.subList(0, toIntExact(topK));
    }

    @Override
    public Movie movieWithHighestAverage() {
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        return moviesSortedByScore.get(0);
    }

    @Override
    public List<Movie> getMoviesPercentile(double percentile) {
        int numOfMoviesToReturn = (int)Math.ceil((((100 -  percentile) / 100) * moviesSortedByScore.size()));
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        return moviesSortedByScore.subList(0, numOfMoviesToReturn);
    }

    @Override
    public String mostReviewedProduct() {
    	if (moviesSortedByNumOfReviews.isEmpty()){
    		populateMovieReviewCounts();
        }

        return (String) moviesSortedByNumOfReviews.keySet().toArray()[0]; 	
    }

    @Override
    public Map<String, Long> reviewCountPerMovieTopKMovies(int topK) {
    	Map<String, Long> topKMoviesSortedByNumOfReviews = new LinkedHashMap<>();
    	if (moviesSortedByNumOfReviews.isEmpty()){
    		populateMovieReviewCounts();
        }
    	// TODO retrun top K from the original Map to topK map
    	return topKMoviesSortedByNumOfReviews;
    }

    @Override
    public String mostPopularMovieReviewedByKUsers(int numOfUsers) {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Map<String, Long> moviesReviewWordsCount(int topK) {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Map<String, Long> topYMoviewsReviewTopXWordsCount(int topMovies, int topWords) {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Map<String, Double> topKHelpfullUsers(int k) {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public long moviesCount() {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    // method for populating the array of movies sorted by their score
    private void populateMoviesSortedByScore() {
        Set<String> movies = reviewList.keySet();
        for (String movieId : movies){
            Movie movie = new Movie(movieId, totalMovieAverage(movieId));
            moviesSortedByScore.add(movie);
        }
        Collections.sort(moviesSortedByScore);
        Collections.reverse(moviesSortedByScore);
    }
    
    // method for populating the map of product id(of a movie) and the number of reviews of the movie
    private void populateMovieReviewCounts() {
        for (Map.Entry<String, ArrayList<MovieReview>> entry : reviewList.entrySet()){
        	moviesSortedByNumOfReviews.put(entry.getKey(), (long)entry.getValue().size() );
        } 
        /*TODO sort the Map by #(reviews) and by lex' order*/
    }
    
    
}
