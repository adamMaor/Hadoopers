package univ.bigdata.course;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import univ.bigdata.course.providers.MoviesProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class GradingReviewsQueryRunner {

    public static final String INPUT_FILE = "inputFile";

    public static final String OUTPUT_FILE = "outputFile";

    private OptionSet options;

    private MoviesStorage storage;

    public GradingReviewsQueryRunner(String...args) {
        final OptionParser parser = new OptionParser();
        parser.accepts(INPUT_FILE).withRequiredArg();
        parser.accepts(OUTPUT_FILE).withRequiredArg();

        this.options = parser.parse(args);

        if (!options.has(INPUT_FILE) || !options.has(OUTPUT_FILE)) {
            System.out.println("Error, you should provide movies reviews" +
                    " input text file and output file to persist queries results.");
            System.out.println("-" + INPUT_FILE + "=<path_to_input_file> -" +
                    OUTPUT_FILE + "=<path_to_output_file>");
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws IOException {
        final GradingReviewsQueryRunner grader = new GradingReviewsQueryRunner(args);
        grader.initialize();
        grader.grade();
    }

    private void initialize() throws IOException {
        String inputFileName = options.valueOf(INPUT_FILE).toString();
        final MoviesProvider provider = ... //TODO: Your code goes here...;
        this.storage = new MoviesStorage(provider);
    }

    private void grade() throws IOException {
            gradingFirstPart();
            gradingSecondPart();
            gradingThirdPart();
            gradingFourthPart();
    }

    private void gradingFourthPart() {
        final String file = options.valueOf(OUTPUT_FILE).toString() + "_4.txt";
        try (final PrintStream printer = new PrintStream(file)) {
            printer.println("Most popular movie with highest average score, reviewed by at least 20 users " + mostPopularMovieReviewedByKUsers(storage, 20));
            printer.println("Most popular movie with highest average score, reviewed by at least 15 users " + mostPopularMovieReviewedByKUsers(storage, 15));
            printer.println("Most popular movie with highest average score, reviewed by at least 10 users " + mostPopularMovieReviewedByKUsers(storage, 10));
            printer.println("Most popular movie with highest average score, reviewed by at least 5 users " + mostPopularMovieReviewedByKUsers(storage, 5));

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

            printer.println();
            printer.println("Total number of distinct movies reviewed [" + storage.moviesCount() + "].");
            printer.println("THE END.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gradingThirdPart() throws FileNotFoundException {
        final String file = options.valueOf(OUTPUT_FILE).toString() + "_3.txt";
        try (final PrintStream printer = new PrintStream(file)) {
            printer.println("Computing TOP100 words count");
            storage.moviesReviewWordsCount(100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            printer.println();
            printer.println("Computing TOP100 words count for TOP100 movies");
            storage.topYMoviewsReviewTopXWordsCount(100, 100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));

            printer.println("Computing TOP100 words count for TOP10 movies");
            storage.topYMoviewsReviewTopXWordsCount(10, 100)
                    .entrySet()
                    .forEach(pair -> printer.println("Word = [" + pair.getKey() + "], number of occurrences [" + pair.getValue() + "]."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gradingSecondPart() {
        final String file = options.valueOf(OUTPUT_FILE).toString() + "_2.txt";
        try (final PrintStream printer = new PrintStream(file)) {
            storage.reviewCountPerMovieTopKMovies(4)
                    .entrySet()
                    .stream()
                    .forEach(pair -> printer.println("Movie product id = [" + pair.getKey() + "], reviews count [" + pair.getValue() + "]."));

            printer.println();
            printer.println("The most reviewed movie product id is " + storage.mostReviewedProduct());

            printer.println();
            printer.println("Computing 90th percentile of all movies average.");
            storage.getMoviesPercentile(90).stream().forEach(printer::println);

            printer.println();
            printer.println("Computing 50th percentile of all movies average.");
            storage.getMoviesPercentile(50).stream().forEach(printer::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gradingFirstPart() {
        final String file = options.valueOf(OUTPUT_FILE).toString() + "_1.txt";
        try (final PrintStream printer = new PrintStream(file)) {
            printer.println("Getting list of total movies average.");

            printer.println();
            printer.println("TOP2.");
            storage.getTopKMoviesAverage(2).stream().forEach(printer::println);
            printer.println();
            printer.println("TOP4.");
            storage.getTopKMoviesAverage(4).stream().forEach(printer::println);

            printer.println("Total average: " + storage.totalMoviesAverageScore());

            printer.println();
            printer.println("The movie with highest average:  " + storage.movieWithHighestAverage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String mostPopularMovieReviewedByKUsers(IMoviesStorage storage, int numOfUsers) {
        try {
            return storage.mostPopularMovieReviewedByKUsers(numOfUsers);
        } catch (Exception e) {
            return  "";
        }
    }
}
