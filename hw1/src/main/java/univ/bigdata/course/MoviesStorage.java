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
    // TODO(vak): change name to reviewMap
    private Map <String, ArrayList<MovieReview>> reviewMap;
    // Map with key movieId and value of number of reviews for that movie
    private List<Movie> moviesSortedByScore;
    private Map<String, Long> moviesSortedByNumOfReviews;
    
    public Map<String, Long> getMoviesSortedByNumOfReviews() {
		return moviesSortedByNumOfReviews;
	}

	public Map<String, ArrayList<MovieReview>> getReviewMap() {
		return reviewMap;
	}

	public MoviesStorage(final MoviesProvider provider) {
        this.reviewMap = new HashMap<>();
        this.moviesSortedByScore = new ArrayList<>();
        this.moviesSortedByNumOfReviews = new LinkedHashMap<>();
        while (provider.hasMovie()) {
            MovieReview review = provider.getMovie();
            if (!reviewMap.containsKey(review.getMovie().getProductId())){
                ArrayList<MovieReview> movieReviewList = new ArrayList<>();
                movieReviewList.add(review);
                reviewMap.put(review.getMovie().getProductId(), movieReviewList);
            }
            else {
                reviewMap.get(review.getMovie().getProductId()).add(review);
            }
        }
    }


    @Override
    public double totalMoviesAverageScore() {
        int count = 0, sum = 0;
        Set <String> movies = reviewMap.keySet();
        for (String movieId : movies){
            ArrayList <MovieReview> reviews = reviewMap.get(movieId);
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
        ArrayList <MovieReview> list = reviewMap.get(productId);
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
        Map<String, Long> topKMoviesSortedByNumOfReviews = reviewCountPerMovieTopKMovies(1);
        return topKMoviesSortedByNumOfReviews.keySet().iterator().next();
    }

    @Override
    public Map<String, Long> reviewCountPerMovieTopKMovies(int topK) {
    	if (moviesSortedByNumOfReviews.isEmpty()){
            populateMovieReviewCounts();
        }
        Map<String, Long> topKMoviesSortedByNumOfReviews = new LinkedHashMap<>();
        Iterator<Entry<String, Long>> iterator = moviesSortedByNumOfReviews.entrySet().iterator();
        for (int i = 0 ; i < topK ; i++){
            if (iterator.hasNext()){
                Entry<String, Long> currEntry = iterator.next();
                topKMoviesSortedByNumOfReviews.put(currEntry.getKey(), currEntry.getValue());
            }
        }
    	return topKMoviesSortedByNumOfReviews;
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
        Map<String, Long> wordsCountMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<MovieReview>> entry : reviewMap.entrySet()){
            //moviesSortedByNumOfReviews.put(entry.getKey(), (long)entry.getValue().size() );
            for(MovieReview review: entry.getValue()){
                String[] words = review.getSummary().split(" ");
                for(int i=0; i<words.length;i++){
                    words[i] = words[i].replaceAll("[\"]","");
                    wordsCountMap.putIfAbsent(words[i], (long) 0);
                    wordsCountMap.put(words[i], wordsCountMap.get(words[i]) + 1);
                }
                /*words = review.getReview().split(" ");
                for(int i=0; i<words.length;i++){
                    words[i] = words[i].replaceAll("[\"]","");
                    wordsCountMap.putIfAbsent(words[i], (long) 0);
                    wordsCountMap.put(words[i], wordsCountMap.get(words[i]) + 1);
                }*/
            }
        }
        Map<String, Long> topKWordsSortedByCount = new LinkedHashMap<>();
        Iterator<Entry<String, Long>> iterator = wordsCountMap.entrySet().iterator();
        for (int i = 0 ; i < topK ; i++){
            if (iterator.hasNext()){
                Entry<String, Long> currEntry = iterator.next();
                topKWordsSortedByCount.put(currEntry.getKey(), currEntry.getValue());
            }
        }
        //System.out.println(wordsCountMap.get("the"));
        return topKWordsSortedByCount;
    }

    @Override
    public Map<String, Long> topYMoviewsReviewTopXWordsCount(int topMovies, int topWords) {
        Map<String, Long> topYMoviesReviewCountMap = reviewCountPerMovieTopKMovies(topMovies);
        HashMap<String, ArrayList<MovieReview>> topYMovieMap = new LinkedHashMap<>();
        for(String key : topYMoviesReviewCountMap.keySet())
        {
            topYMovieMap.putIfAbsent(key, reviewMap.get(key));
        }
        return countWordsInGivenMovies(topYMovieMap, topWords);
    }

    private Map<String,Long> countWordsInGivenMovies(HashMap<String, ArrayList<MovieReview>> movieMap, int k) {
        Map<String, Long> wordsCountMap = new LinkedHashMap<>();
        for (Map.Entry<String, ArrayList<MovieReview>> entry : movieMap.entrySet()){
            for(MovieReview review: entry.getValue()){
                String[] words = review.getSummary().split(" ");
                for(int i=0; i<words.length;i++){
                    words[i] = words[i].replaceAll("[\"]","");
                    wordsCountMap.putIfAbsent(words[i], (long) 0);
                    wordsCountMap.put(words[i], wordsCountMap.get(words[i]) + 1);
                }
                /*words = review.getReview().split(" ");
                for(int i=0; i<words.length;i++){
                    words[i] = words[i].replaceAll("[\"]","");
                    wordsCountMap.putIfAbsent(words[i], (long) 0);
                    wordsCountMap.put(words[i], wordsCountMap.get(words[i]) + 1);
                }*/
            }
        }
        Map<String, Long> topKWordsSortedByCount = new LinkedHashMap<>();//TODO sorting
        Iterator<Entry<String, Long>> iterator = wordsCountMap.entrySet().iterator();
        for (int i = 0 ; i < k ; i++){
            if (iterator.hasNext()){
                Entry<String, Long> currEntry = iterator.next();
                topKWordsSortedByCount.put(currEntry.getKey(), currEntry.getValue());
            }
        }
        //System.out.println(wordsCountMap.get("the"));
        return topKWordsSortedByCount;
    }



    @Override
    public Map<String, Double> topKHelpfullUsers(int k) {
        Map<String, Double> topKHelpfulUsers = new LinkedHashMap<>();
        return topKHelpfulUsers;
    }

    @Override
    public long moviesCount() {
        return reviewMap.size();
    }

    // method for populating the array of movies sorted by their score
    private void populateMoviesSortedByScore() {
        Set<String> movies = reviewMap.keySet();
        for (String movieId : movies){
            Movie movie = new Movie(movieId, totalMovieAverage(movieId));
            moviesSortedByScore.add(movie);
        }
        Collections.sort(moviesSortedByScore);
        Collections.reverse(moviesSortedByScore);
    }
    
    // method for populating the map of product id(of a movie) and the number of reviews of the movie
    private void populateMovieReviewCounts() {
        Map<String, Long> unsortedMovieReviewCountsMap = new LinkedHashMap<>();
        for (Map.Entry<String, ArrayList<MovieReview>> entry : reviewMap.entrySet()) {
            unsortedMovieReviewCountsMap.put(entry.getKey(), (long)entry.getValue().size());
        }
        moviesSortedByNumOfReviews = sortMapByValueAndKey(unsortedMovieReviewCountsMap);
    }

    private static <K extends Comparable<K>, V extends Comparable<V>> Map<K, V> sortMapByValueAndKey(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (o1.getValue().equals(o2.getValue())) {
                    return o1.getKey().compareTo(o2.getKey()) * -1;
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


}

