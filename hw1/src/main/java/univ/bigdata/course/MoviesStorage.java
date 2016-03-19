package univ.bigdata.course;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;
import univ.bigdata.course.providers.MoviesProvider;

import java.util.*;

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

    HashMap <String, ArrayList<MovieReview>> reviewList;

    public MoviesStorage(final MoviesProvider provider) {
        this.reviewList = new HashMap<String, ArrayList<MovieReview>>();
        while (provider.hasMovie()) {
            MovieReview res = provider.getMovie();
            if (!reviewList.containsKey(res.getMovie().getProductId())){
                ArrayList<MovieReview> l = new ArrayList<MovieReview>();
                l.add(res);
                reviewList.put(res.getMovie().getProductId(), l);
            }
            else {
                reviewList.get(res.getMovie().getProductId()).add(res);
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
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Movie movieWithHighestAverage() {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public List<Movie> getMoviesPercentile(double percentile) {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public String mostReviewedProduct() {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
    }

    @Override
    public Map<String, Long> reviewCountPerMovieTopKMovies(int topK) {
        throw new UnsupportedOperationException("You have to implement this method on your own.");
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
}
