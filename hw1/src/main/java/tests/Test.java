package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import univ.bigdata.course.IMoviesStorage;
import univ.bigdata.course.MoviesStorage;
import univ.bigdata.course.movie.MovieReview;

public class Test {
	
	private MoviesStorage storage;
	private HashMap <String, ArrayList<MovieReview>> reviewList;
	
	public Test(IMoviesStorage iStorageFromMain){
		storage = (MoviesStorage) iStorageFromMain;
		reviewList = storage.getReviewList();
	}
	
	public void checkFunction2point2(){
		Set <String> movies = reviewList.keySet();
		for (String movieId : movies){
            double x = storage.totalMovieAverage(movieId);
            System.out.println("the average of movie " + movieId + " is:" + x);
        }
		
	}
	
	
}
