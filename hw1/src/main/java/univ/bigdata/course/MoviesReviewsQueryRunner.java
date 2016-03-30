package univ.bigdata.course;

import univ.bigdata.course.providers.FileIOMoviesProvider;
import univ.bigdata.course.providers.MoviesProvider;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;

public class MoviesReviewsQueryRunner {

    public static void main(String[] args){

        String outputFile = "";
        String inputFile = "";
        // Go over arguments (set in pom.xml) and find our input and output files
        for (int i = 0; i < args.length; i++){
            if (args[i].contains("inputFile")){
                String inputFileName = args[i].substring(args[i].indexOf("=") + 1);
                // get the file full path from resources - actually the target
                ClassLoader classLoader = MoviesReviewsQueryRunner.class.getClassLoader();
                URL fileURL = classLoader.getResource(inputFileName);
                if (fileURL == null) {
                    throw new RuntimeException("Input file open error");
                }
                inputFile = fileURL.getFile();
            }
            else if (args[i].contains("outputFile")){
                outputFile = args[i].substring(args[i].indexOf("=") + 1);
            }
        }
        // init. printer
        final PrintStream printer = initPrinter(outputFile);

        try{
            final MoviesProvider provider = new FileIOMoviesProvider(inputFile);
            final IMoviesStorage storage = new MoviesStorage(provider);

            printer.println("Getting list of total movies average.");
            // 1.
            printer.println();
            printer.println("TOP2.");
            storage.getTopKMoviesAverage(2).stream().forEach(printer::println);
            printer.println();
            printer.println("TOP4.");
            storage.getTopKMoviesAverage(4).stream().forEach(printer::println);

            // 2.
            printer.println("Total average: " + storage.totalMoviesAverageScore());

            // 3.
            printer.println();
            printer.println("The movie with highest average:  " + storage.movieWithHighestAverage());

            // 4.

            printer.println();
            storage.reviewCountPerMovieTopKMovies(4)
                    .entrySet()
                    .stream()
                    .forEach(pair -> printer.println("Movie product id = [" + pair.getKey() + "], reviews count [" + pair.getValue() + "]."));

            // 5.
            printer.println();
            printer.println("The most reviewed movie product id is " + storage.mostReviewedProduct());

            // 6.
            printer.println();
            printer.println("Computing 90th percentile of all movies average.");
            storage.getMoviesPercentile(90).stream().forEach(printer::println);

            printer.println();
            printer.println("Computing 50th percentile of all movies average.");
            storage.getMoviesPercentile(50).stream().forEach(printer::println);

            // 7.
            printer.println();
            printer.println("Computing TOP100 words count");
            storage.moviesReviewWordsCount(100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            // 8.
            printer.println();
            printer.println("Computing TOP100 words count for TOP100 movies");
            storage.topYMoviewsReviewTopXWordsCount(100, 100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            printer.println("Computing TOP100 words count for TOP10 movies");
            storage.topYMoviewsReviewTopXWordsCount(100, 10)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            // 9.
            printer.println();
            printer.println("Most popular movie with highest average score, reviewed by at least 20 users " + storage.mostPopularMovieReviewedByKUsers(20));
            printer.println("Most popular movie with highest average score, reviewed by at least 15 users " + storage.mostPopularMovieReviewedByKUsers(15));
            printer.println("Most popular movie with highest average score, reviewed by at least 10 users " + storage.mostPopularMovieReviewedByKUsers(10));
            printer.println("Most popular movie with highest average score, reviewed by at least 5 users " + storage.mostPopularMovieReviewedByKUsers(5));

            // 10.
            printer.println();
            printer.println("Compute top 10 most helpful users.");
            storage.topKHelpfullUsers(10)
                    .entrySet()
                    .forEach(pair -> printer.println("User id = [" + pair.getKey() + "], helpfulness [" + pair.getValue() + "]."));

            printer.println();
            printer.println("Compute top 100 most helpful users.");
            storage.topKHelpfullUsers(100)
                    .entrySet()
                    .forEach(pair -> printer.println("User id = [" + pair.getKey() + "], helpfulness [" + pair.getValue() + "]."));

            // 11.
            printer.println();
            printer.println("Total number of distinct movies reviewed [" +storage.moviesCount() + "].");
            printer.println("THE END.");
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * this static method initializes the printer.
     * it handles the FileNotFoundException
     * @param outputFile - the file to write to
     * @return the initialized printer
     */
    private static PrintStream initPrinter(String outputFile) {
        PrintStream printer = null;
        try {
            printer = new PrintStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Output file open error");
        }
        return printer;
    }
}
