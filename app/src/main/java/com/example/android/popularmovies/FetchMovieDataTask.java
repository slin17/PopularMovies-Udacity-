package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.MovieFragment.MoviesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by SawS on 8/9/16.
 */
public class FetchMovieDataTask extends AsyncTask<String,Void,List<String[]>> {

    private MoviesAdapter mPosterAdapter;
    private final Context mContext;
    private Map<String, ArrayList<String>> mMoviesIdToVideos;

    private int getMovieList = 0;
    private int getTrailer = 1;


    public FetchMovieDataTask(Context context, MoviesAdapter posterAdapter, Map<String, ArrayList<String>> moviesIdToVideos) {
        mContext = context;
        mPosterAdapter = posterAdapter;
        mMoviesIdToVideos = moviesIdToVideos;
    }

    private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

    @Override
    protected List<String[]> doInBackground (String... params){

        String movieListJsonStr = getMovieListJson(params[0], getMovieList);
        List<String[]> movieInfoFromJson = new ArrayList<>();

        try {
            movieInfoFromJson = getMovieInfofromJson(movieListJsonStr);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        String movieVideoJsonStr;

        for (int i = 0; i < movieInfoFromJson.size(); i ++){
            String movieID = movieInfoFromJson.get(i)[0];
            movieVideoJsonStr = getMovieListJson(movieID, getTrailer);
            try {
                mMoviesIdToVideos.put(movieID, getMovieTrailersFromJson(movieVideoJsonStr));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return movieInfoFromJson;
    }

    @Override
    protected void onPostExecute(List<String[]> results) {
        if (results != null && mPosterAdapter != null){
//            Log.i(LOG_TAG, "mPosterAdapter is not null");
            mPosterAdapter.clear();
            mPosterAdapter.setMovieList(results);
            //mForecastAdapter.addAll(results); for API level above 10, current API lvl = 10
        }
    }

    private String getMovieListJson(String param, int i) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        // Will contain the raw JSON response as a string.
        String returnJsonStr = null;

        try {
            final String FETCHMOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String APPID_PARAM = "api_key";

            Uri builtUri;
            if (i == getMovieList) {
                builtUri = Uri.parse(FETCHMOVIE_BASE_URL).buildUpon()
                        .appendPath(param)
                        .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            } else {
                builtUri = Uri.parse(FETCHMOVIE_BASE_URL).buildUpon()
                        .appendPath(param)
                        .appendPath("videos")
                        .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();
            }

            // Construct the URL for themoviedb query
            //URL url = new URL("http://api.themoviedb.org/3/movie/popular?api_key=b9c41728fe6750764cf6d3c12b0d7e7a");

            URL url = new URL(builtUri.toString());
            //Log.v(LOG_TAG, "Built URI " + builtUri.toString());
            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            returnJsonStr = buffer.toString();
            //Log.v(LOG_TAG, "movieListJsonStr "+ movieListJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return returnJsonStr;
    }

    private ArrayList<String> getMovieTrailersFromJson(String movieTrailerJsonStr)
            throws JSONException {
        final String TMD_LIST = "results";
        final String TMD_KEY = "key";
        final String TMD_NAME = "name";
        JSONObject trailerJson = new JSONObject(movieTrailerJsonStr);
        JSONArray resultsArray = trailerJson.getJSONArray(TMD_LIST);

        ArrayList<String> resultArrList = new ArrayList<String>();
        for (int i = 0; i < resultsArray.length(); i ++) {
            JSONObject currJsonObj = resultsArray.getJSONObject(i);
            String youtubeKey = currJsonObj.getString(TMD_KEY);
            String trailerName = currJsonObj.getString(TMD_NAME);
            resultArrList.add(trailerName);
            resultArrList.add(youtubeKey);

        }
        return  resultArrList;
    }

    private List<String[]> getMovieInfofromJson(String movieListJsonStr)
            throws JSONException {

        final String TMD_LIST = "results";
        final String TMD_ID = "id";
        final String TMD_ORIGINAL_TITLE = "original_title";
        final String TMD_POSTER = "poster_path";
        final String TMD_OVERVIEW = "overview";
        final String TMD_RATING = "vote_average";
        final String TMD_DATE = "release_date";
        final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

        JSONObject movieJson = new JSONObject(movieListJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(TMD_LIST);

        List<String[]> resultArrList = new ArrayList<String[]>();
        for (int i = 0; i < resultsArray.length(); i++) {
            String movieId = resultsArray.getJSONObject(i).getString(TMD_ID);
            String rawPosterPath = resultsArray.getJSONObject(i).getString(TMD_POSTER);
            String originalTitle = resultsArray.getJSONObject(i).getString(TMD_ORIGINAL_TITLE);
            String posterPath = POSTER_BASE_URL.concat(rawPosterPath.substring(1));
            String overView = resultsArray.getJSONObject(i).getString(TMD_OVERVIEW);
            String rating = resultsArray.getJSONObject(i).getString(TMD_RATING);
            String date = resultsArray.getJSONObject(i).getString(TMD_DATE);
            String[] movieInfo = { movieId, originalTitle,
                    posterPath,
                    overView,
                    rating,
                    date };
            resultArrList.add(movieInfo);
        }

        //String[] resultStrs = new String[resultArrList.size()];
        //resultStrs = resultArrList.toArray(resultStrs);
        return  resultArrList;
    }
}
