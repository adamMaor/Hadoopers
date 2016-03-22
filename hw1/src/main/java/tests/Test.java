package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import univ.bigdata.course.IMoviesStorage;
import univ.bigdata.course.MoviesStorage;
import univ.bigdata.course.movie.MovieReview;

public class Test {
	
	private MoviesStorage storage;
	private HashMap <String, ArrayList<MovieReview>> reviewList;

	
	public void checkFunction2point2(){
		Set <String> movies = reviewList.keySet();
		for (String movieId : movies){
            double x = storage.totalMovieAverage(movieId);
            System.out.println("the average of movie " + movieId + " is:" + x);
        }
		
	}
	
	public void checkFunction2point6(){
		String ans = storage.mostReviewedProduct();
		Map<String, Long> moviesSortedByNumOfReviews = storage.getMoviesSortedByNumOfReviews();
		System.out.println("movies sorted by number of reviews:");
		for (Map.Entry<String, Long> entry : moviesSortedByNumOfReviews.entrySet()){
			System.out.println(entry.getKey() + " " + entry.getValue());
        }
		System.out.println(ans);
		//System.out.println("first item id: " + moviesSortedByNumOfReviews.keySet().toArray()[0] );
		//System.out.println("first item val: " + moviesSortedByNumOfReviews.values().toArray()[0] );
	}
}
