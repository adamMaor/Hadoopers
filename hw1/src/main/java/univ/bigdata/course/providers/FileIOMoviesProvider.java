package univ.bigdata.course.providers;

import univ.bigdata.course.movie.Movie;
import univ.bigdata.course.movie.MovieReview;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Date;

public class FileIOMoviesProvider implements MoviesProvider {
    private final LineNumberReader lineNumberReader;
    // flag to tell if we've read the current line
    private boolean readLineFlag;
    // the line most recently read from file
    private String currentLine;
    /**
     * Constructor function for FileIOMoviesProvider
     * @param inputFileStr the input file
     */
    public FileIOMoviesProvider(String inputFileStr) {
        readLineFlag = false;
        try{
            lineNumberReader = new LineNumberReader(new FileReader(inputFileStr));
        } catch (Exception e){
            throw new RuntimeException("File open error");
        }
    }

    @Override
    public boolean hasMovie() {
        if (!readLineFlag) {
            readNextLine();
        }
        if (currentLine == null){
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
        if (!readLineFlag) {
            readNextLine();
        }
        // We've finished reading the next currentLine, reader can continue
        readLineFlag = false;
        String[] reviewParamsList = currentLine.split("\\t");
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
     * read the nextLineNumber currentLine from LineNumberReader
     */
    private void readNextLine(){
        try {
            currentLine = lineNumberReader.readLine();
            // We've read the current currentLine, reader should not continue reading until getMovie has been called
            readLineFlag = true;
        } catch (Exception e){
            throw new RuntimeException("Can't read current currentLine");
        }
    }
}
