package univ.bigdata.course.providers;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;

import java.io.File;
import java.util.Date;
import java.util.Scanner;

public class FileIOMoviesProvider implements MoviesProvider {
    // Scanner to read file
    private final Scanner inputScanner;
    /**
     * Constructor function for FileIOMoviesProvider
     * @param inputFileStr the input file path
     */
    public FileIOMoviesProvider(String inputFileStr) {
        try {
            File file = new File(inputFileStr);
            inputScanner = new Scanner(file);
        } catch (Exception e) {
            throw new RuntimeException("File open error");
        }
    }

    @Override
    public boolean hasMovie() {
        try {
            if (!inputScanner.hasNextLine()){
                inputScanner.close();
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public MovieReview getMovie() {
        if (hasMovie()) {
            String currentLine = inputScanner.nextLine();
            String[] reviewParamsList = currentLine.split("\\t");
            for (int i = 0; i < reviewParamsList.length; i++){
                reviewParamsList[i] = reviewParamsList[i].substring(reviewParamsList[i].indexOf(":") + 2);
            }
            Movie movie = new Movie(reviewParamsList[0], Double.parseDouble(reviewParamsList[4]));
            Date movieDate = new Date(Long.parseLong(reviewParamsList[5]));
            return new MovieReview(movie,
                    reviewParamsList[1],
                    reviewParamsList[2],
                    reviewParamsList[3],
                    movieDate,
                    reviewParamsList[6],
                    reviewParamsList[7]);
        }
        else {
            throw new RuntimeException("Illegal read from file");
        }
    }
}
