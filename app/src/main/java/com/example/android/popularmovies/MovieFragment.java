package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

/**
 * Created by SawS on 5/12/16.
 */
public class MovieFragment extends Fragment {

    private MoviesAdapter mPosterAdapter;
    private RecyclerView mRecyclerView;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { //where fragment is created
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateMovieList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //String[] testMovieInfo = {"","http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg","","",""};

        List<String[]> testPosters = new ArrayList<String[]>();

        /*for (int i = 0; i< 10; i++){
            testPosters.add(testMovieInfo);
        }*/

        mPosterAdapter = new MoviesAdapter(getActivity().getApplicationContext(),testPosters);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_posters_recyclerView);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity().getApplicationContext(),400));
        mRecyclerView.setAdapter(mPosterAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        String[] movieInfo = mPosterAdapter.getItem(position);
                        //Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),DetailActivity.class)
                                .putExtra(Intent.EXTRA_TEXT,movieInfo);
                        startActivity(intent);

                    }
                })
        );
        return rootView;
    }

    private void updateMovieList() {
        FetchMovieDataTask movieDataTask = new FetchMovieDataTask();
        //get an instance of sharedpreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //to retrieve the info we want, we use the key "sort_by"
        String sortType = prefs.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_popular));
        movieDataTask.execute(sortType);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovieList();
    }

    public class FetchMovieDataTask extends AsyncTask<String,Void,List<String[]>> {
        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

        @Override
        protected List<String[]> doInBackground (String... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            /*String sortType;
            if (params[0].equals("popular")){
                sortType = "popular";
            } else {sortType = "top_rated";}
            */

            // Will contain the raw JSON response as a string.
            String movieListJsonStr = null;

            try {
                // Construct the URL for themoviedb query
                //URL url = new URL("http://api.themoviedb.org/3/movie/popular?api_key=b9c41728fe6750764cf6d3c12b0d7e7a");

                final String FETCHMOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FETCHMOVIE_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                movieListJsonStr = buffer.toString();
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
            try {
                return getMovieInfofromJson(movieListJsonStr);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String[]> results) {
            if (results != null){
                mPosterAdapter.setMovieList(results);
                //mForecastAdapter.addAll(results); for API level above 10, current API lvl = 10
            }
        }

        private List<String[]> getMovieInfofromJson(String movieListJsonStr)
                throws JSONException {

            final String TMD_LIST = "results";
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
                String rawPosterPath = resultsArray.getJSONObject(i).getString(TMD_POSTER);
                String originalTitle = resultsArray.getJSONObject(i).getString(TMD_ORIGINAL_TITLE);
                String posterPath = POSTER_BASE_URL.concat(rawPosterPath.substring(1));
                String overView = resultsArray.getJSONObject(i).getString(TMD_OVERVIEW);
                String rating = resultsArray.getJSONObject(i).getString(TMD_RATING);
                String date = resultsArray.getJSONObject(i).getString(TMD_DATE);
                String[] movieInfo = { originalTitle,
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

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.movie_posters_imageView);
        }
    }

    public static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
        private List<String[]> mMovieList;
        private LayoutInflater mInflater;
        private Context mContext;

        public MoviesAdapter(Context context, List<String[]> imageUrls) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
            this.mMovieList = new ArrayList<String[]>();
            mMovieList.addAll(imageUrls);
        }

        public String[] getItem(int positon){
            return mMovieList.get(positon);
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.gridview_item_image, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            //binding viewholders to the data from the model layer
            String url = mMovieList.get(position)[1];

            // This is how we use Picasso to load images from the internet.
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.color.loadingColor)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return (mMovieList == null) ? 0 : mMovieList.size();
        }

        public void setMovieList(List<String[]> movieList) {
            this.mMovieList.clear();
            this.mMovieList.addAll(movieList);
            // The adapter needs to know that the data has changed. If we don't call this, app will crash.
            notifyDataSetChanged();
        }
    }

    public static class GridAutofitLayoutManager extends GridLayoutManager {

        private int mColumnWidth;
        private boolean mColumnWidthChanged = true;

        public GridAutofitLayoutManager(Context context, int columnWidth) {
            super(context, 1);
            setColumnWidth(checkedColumnWidth(context, columnWidth));
        }

        public GridAutofitLayoutManager(Context context, int columnWidth, int orientation, boolean reverseLayout) {
    /* Initially set spanCount to 1, will be changed automatically later. */
            super(context, 1, orientation, reverseLayout);
            setColumnWidth(checkedColumnWidth(context, columnWidth));
        }

        private int checkedColumnWidth(Context context, int columnWidth) {
            if (columnWidth <= 0) {
        /* Set default columnWidth value (48dp here). It is better to move this constant
        to static constant on top, but we need context to convert it to dp, so can't really
        do so. */
                columnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                        context.getResources().getDisplayMetrics());
            }
            return columnWidth;
        }

        public void setColumnWidth(int newColumnWidth) {
            if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
                mColumnWidth = newColumnWidth;
                mColumnWidthChanged = true;
            }
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            if (mColumnWidthChanged && mColumnWidth > 0) {
                int totalSpace;
                if (getOrientation() == VERTICAL) {
                    totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
                } else {
                    totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
                }
                int spanCount = Math.max(1, totalSpace / mColumnWidth);
                setSpanCount(spanCount);
                mColumnWidthChanged = false;
            }
            super.onLayoutChildren(recycler, state);
        }
    }
}
