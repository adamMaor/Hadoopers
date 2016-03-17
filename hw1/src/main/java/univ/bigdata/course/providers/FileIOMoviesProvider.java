package univ.bigdata.course.providers;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Date;

public class FileIOMoviesProvider implements MoviesProvider {
    private final LineNumberReader lineNumberReader;
    private int nextLineNumber;
    /**
     * Constructor function for FileIOMoviesProvider
     * @param inputFileStr the input file
     */
    public FileIOMoviesProvider(String inputFileStr) {
        try{
            lineNumberReader = new LineNumberReader(new FileReader(inputFileStr));
            nextLineNumber = lineNumberReader.getLineNumber();
        } catch (Exception e){
            throw new RuntimeException("File open error");
        }
    }

    @Override
    public boolean hasMovie() {
        String line = readLine();
        if (line.equals("")){
            try{
                lineNumberReader.close();
            } catch (Exception e){
                throw new RuntimeException("Can't close reader");
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public MovieReview getMovie() {
        String[] reviewParamsList = readLine().split("\t");
        nextLineNumber++;
        for (int i = 0; i < reviewParamsList.length; i++){
            reviewParamsList[i] = reviewParamsList[i].substring(reviewParamsList[i].lastIndexOf(":") + 2);
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

    /**
     * read the nextLineNumber line from LineNumberReader
     * @return String of next line
     */
    private String readLine(){
        String line;
        lineNumberReader.setLineNumber(nextLineNumber);
        try {
            line = lineNumberReader.readLine();
        } catch (Exception e){
            throw new RuntimeException("Can't read line");
        }
        return line;
    }
}
