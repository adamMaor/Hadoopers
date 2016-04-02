/**
 * Submitters information - Hadoopers team:
 * Vadim Khakham 	vadim.khakham@gmail.com	311890156
 * Michel Guralnik mikijoy@gmail.com 	306555822
 * Gilad Eini 	giladeini@gmail.com	034744920
 * Adam Maor 	maorcpa.adam@gmail.com	036930501
 */
package univ.bigdata.course.providers;

import univ.bigdata.course.movie.MovieReview;


public interface MoviesProvider {

    boolean hasMovie();

    MovieReview getMovie();
}
