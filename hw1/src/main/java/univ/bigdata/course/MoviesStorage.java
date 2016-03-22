package univ.bigdata.course;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;
import univ.bigdata.course.providers.MoviesProvider;

import java.util.*;
import java.util.Map.Entry;

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
    private Map <String, List<MovieReview>> reviewMap;
    // Map with key movieId and value of number of reviews for that movie
    private List<Movie> moviesSortedByScore;
    private Map<String, Long> moviesSortedByNumOfReviews;

    public Map<String, Long> getMoviesSortedByNumOfReviews() {
		return moviesSortedByNumOfReviews;
	}

	public MoviesStorage(final MoviesProvider provider) {
        this.reviewMap = new HashMap<>();
        this.moviesSortedByScore = new ArrayList<>();
        this.moviesSortedByNumOfReviews = new LinkedHashMap<>();
        while (provider.hasMovie()) {
            MovieReview review = provider.getMovie();
            if (!reviewMap.containsKey(review.getMovie().getProductId())){
                List<MovieReview> movieReviewList = new ArrayList<>();
                movieReviewList.add(review);
                reviewMap.put(review.getMovie().getProductId(), movieReviewList);
            }
            else {
                reviewMap.get(review.getMovie().getProductId()).add(review);
            }
        }
    }

    //2.1
    @Override
    public double totalMoviesAverageScore() {
        int count = 0, sum = 0;
        Set <String> movies = reviewMap.keySet();
        for (String movieId : movies){
            List <MovieReview> reviews = reviewMap.get(movieId);
            for (MovieReview review : reviews){
                sum += review.getMovie().getScore();
                count++;
            }
        }
        return (double)sum/count;
    }

    //2.2
    @Override
    public double totalMovieAverage(String productId) {
        int count = 0, sum = 0;
        List<MovieReview> list = reviewMap.get(productId);
        for (MovieReview review : list){
            sum += review.getMovie().getScore();
            count++;
        }
        return (double)sum/count;
    }

    //2.3
    @Override
    public List<Movie> getTopKMoviesAverage(long topK) {
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        return moviesSortedByScore.subList(0, toIntExact(topK));
    }

    //2.4
    @Override
    public Movie movieWithHighestAverage() {
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        return moviesSortedByScore.get(0);
    }

    //2.5
    @Override
    public List<Movie> getMoviesPercentile(double percentile) {
        int numOfMoviesToReturn = (int)Math.ceil((((100 -  percentile) / 100) * moviesSortedByScore.size()));
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        return moviesSortedByScore.subList(0, numOfMoviesToReturn);
    }

    //2.6
    @Override
    public String mostReviewedProduct() {
        Map<String, Long> topKMoviesSortedByNumOfReviews = reviewCountPerMovieTopKMovies(1);
        return topKMoviesSortedByNumOfReviews.keySet().iterator().next();
    }

    //2.7
    @Override
    public Map<String, Long> reviewCountPerMovieTopKMovies(int topK) {
    	if (moviesSortedByNumOfReviews.isEmpty()){
            populateMovieReviewCounts();
        }
        return getKElementsFromMap(moviesSortedByNumOfReviews, topK);
    }

    //2.8
    @Override
    public String mostPopularMovieReviewedByKUsers(int numOfUsers) {
        if (moviesSortedByScore.isEmpty()){
            populateMoviesSortedByScore();
        }
        for (Movie movie : moviesSortedByScore){
            String productID = movie.getProductId();
            if(reviewMap.get(productID).size() >= numOfUsers){
                return  productID;
            }
        }
        return ""; //no such movie with at least numOfUsers user reviews
    }

    //2.9
    @Override
    public Map<String, Long> moviesReviewWordsCount(int topK) {
        return countWordsInGivenMovies(reviewMap, topK);
    }

    //2.10
    @Override
    public Map<String, Long> topYMoviewsReviewTopXWordsCount(int topMovies, int topWords) {
        Map<String, Long> topYMoviesReviewCountMap = reviewCountPerMovieTopKMovies(topMovies);
        Map<String, List<MovieReview>> topYMovieMap = new LinkedHashMap<>();
        for(String key : topYMoviesReviewCountMap.keySet()) {
            topYMovieMap.putIfAbsent(key, reviewMap.get(key));
        }
        return countWordsInGivenMovies(topYMovieMap, topWords);
    }

    //used by 2.10 and 2.9 querys. count all words in a map of movies reviews.
    private Map<String,Long> countWordsInGivenMovies(Map<String, List<MovieReview>> movieMap, int topK) {
        int x = 0;
        Map<String, Long> wordsCountMap = new LinkedHashMap<>();
        for (List<MovieReview> movieReviews : movieMap.values()) {
            for(MovieReview review : movieReviews) {
                String[] words = review.getReview().split("\\s");
//                words[0] = words[0].replaceAll("[\"]","");
//                words[words.length-1] = words[words.length-1].replaceAll("[\"]","");
                for (String word : words) {
                    if(word.contains("original")) {
                        x++;
                    }
                    wordsCountMap.putIfAbsent(word, (long) 0);
                    wordsCountMap.put(word, wordsCountMap.get(word) + 1);
                }
            }
        }
        return getKElementsFromMap(sortMapByValueAndKey(wordsCountMap), topK);
    }

    //2.11
    @Override
    public Map<String, Double> topKHelpfullUsers(int topK) {
        Map<String, Long> helpfulOpinionsSum = new HashMap<>();
        Map<String, Long> totalOpinionsSum = new HashMap<>();
        Map<String, Double> usersHelpfulnessMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<MovieReview>> reviewEntry : reviewMap.entrySet()){
            for(MovieReview review: reviewEntry.getValue()) {
                helpfulOpinionsSum.putIfAbsent(review.getUserId(), (long) 0);
                totalOpinionsSum.putIfAbsent(review.getUserId(), (long) 0);
                String helpfulness = review.getHelpfulness();
                long helpfulOpinions = Long.parseLong(helpfulness.substring(0, helpfulness.lastIndexOf("/")));
                long totalOpinions = Long.parseLong(helpfulness.substring(helpfulness.lastIndexOf("/") + 1, helpfulness.length()));
                helpfulOpinionsSum.put(review.getUserId(), helpfulOpinionsSum.get(review.getUserId()) + helpfulOpinions);
                totalOpinionsSum.put(review.getUserId(), totalOpinionsSum.get(review.getUserId()) + totalOpinions);
            }
        }
        helpfulOpinionsSum.keySet().stream()
                .filter(user -> totalOpinionsSum.get(user) != 0)
                .forEach(user -> {
            double avg = (double) helpfulOpinionsSum.get(user) / totalOpinionsSum.get(user);
            usersHelpfulnessMap.put(user, (double)Math.round(avg * 100000d) / 100000d);
        });
        return getKElementsFromMap(sortMapByValueAndKey(usersHelpfulnessMap), topK);
    }

    //2.12
    @Override
    public long moviesCount() {
        return reviewMap.size();
    }

    // method for populating the array of movies sorted by their score
    private void populateMoviesSortedByScore() {
        Set<String> movies = reviewMap.keySet();
        for (String movieId : movies){
            double avg = totalMovieAverage(movieId);
            avg = (double)Math.round(avg * 100000d) / 100000d;

            Movie movie = new Movie(movieId, avg);
            moviesSortedByScore.add(movie);
        }
        Collections.sort(moviesSortedByScore);
        Collections.reverse(moviesSortedByScore);
    }
    
    // method for populating the map of product id(of a movie) and the number of reviews of the movie
    private void populateMovieReviewCounts() {
        Map<String, Long> unsortedMovieReviewCountsMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<MovieReview>> entry : reviewMap.entrySet()) {
            unsortedMovieReviewCountsMap.put(entry.getKey(), (long)entry.getValue().size());
        }
        moviesSortedByNumOfReviews = sortMapByValueAndKey(unsortedMovieReviewCountsMap);
    }

    // generic method for reverse sorting map by value, if value equals then by key
    private static <K extends Comparable<K>, V extends Comparable<V>> Map<K, V> sortMapByValueAndKey(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().compareTo(o2.getKey());
                } else {
                    return o1.getValue().compareTo(o2.getValue()) * -1;
                }
            }
        });
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    // generic method for getting first k entries from map to new map
    private <K, V> Map<K, V> getKElementsFromMap(Map<K, V> map, int k) {
        Map<K, V> topKEntriesMap = new LinkedHashMap<>();
        Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
        for (int i = 0; i < k; i++) {
            if (iterator.hasNext()) {
                Entry<K, V> currEntry = iterator.next();
                topKEntriesMap.put(currEntry.getKey(), currEntry.getValue());
            }
        }
        return topKEntriesMap;
    }
}

