package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SawS on 5/12/16.
 */
public class MovieFragment extends Fragment {

    private MoviesAdapter mPosterAdapter;
    private RecyclerView mRecyclerView;
    private Map<String, ArrayList<String>> movieIdToVideos;
    private final String MOVIE_INFO = "movie info";
    private final String VIDEO_INFO = "video info";

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

        List<String[]> testPosters = new ArrayList<String[]>();

        movieIdToVideos = new HashMap<String, ArrayList<String>>();
        mPosterAdapter = new MoviesAdapter(getActivity().getApplicationContext(),testPosters);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_posters_recyclerView);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity().getApplicationContext(),400));
        //A LayoutManager is responsible for measuring and positioning item views within a RecyclerView
        // as well as determining the policy for when to recycle item views that are no longer visible to the user.
        mRecyclerView.setAdapter(mPosterAdapter);
        //Add an RecyclerView.OnItemTouchListener to intercept touch events
        // before they are dispatched to child views or this view's standard scrolling behavior.
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Bundle extras = new Bundle();

                        String[] movieInfo = mPosterAdapter.getItem(position);
                        extras.putStringArray(MOVIE_INFO, movieInfo);
//                        Toast.makeText(getActivity(), String.valueOf(movieIdToVideos.size()), Toast.LENGTH_SHORT).show();

                        ArrayList<String> videoInfo = movieIdToVideos.get(movieInfo[0]);
                        extras.putStringArrayList(VIDEO_INFO, videoInfo);
                        Intent intent = new Intent(getActivity(),DetailActivity.class)
                                .putExtras(extras);
                        startActivity(intent);

                    }
                })
        );
        return rootView;
    }

    private void updateMovieList() {
        FetchMovieDataTask movieDataTask = new FetchMovieDataTask(getActivity(), mPosterAdapter, movieIdToVideos);
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
            //Your code might call findViewById() frequently during the scrolling of ListView, which can slow down performance
            // Even when the Adapter returns an inflated view for recycling, you still need to look up the elements and update them.
            //Holder design pattern is used for View caching - Holder (arbitrary) object holds child widgets of each row and
            // when row is out of View then findViewById() won't be called but View
            // will be recycled and widgets will be obtained from Holder.
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
            String url = mMovieList.get(position)[2];

            // This is how we use Picasso to load images from the internet.
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.color.loadingColor)
                    .into(holder.imageView);
        }

        public void clear(){
            mMovieList.clear();
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
