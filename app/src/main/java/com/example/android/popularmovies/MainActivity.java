package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private MoviesAdapter mPosterAdapter;
        private RecyclerView mRecyclerView;
        private AutofitRecyclerView mAutofitRecyclerView;

        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            String[] testPosters = {
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"

            };

            mPosterAdapter = new MoviesAdapter(getActivity().getApplicationContext(),testPosters);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_posters_recyclerView);
            mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity().getApplicationContext(),500));
            mRecyclerView.setAdapter(mPosterAdapter);
            return rootView;
        }

        public static class MovieViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public MovieViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.movie_posters_imageView);
            }
        }

        public static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder>
        {
            private List<String> mMovieList;
            private LayoutInflater mInflater;
            private Context mContext;

            public MoviesAdapter(Context context, String[] imageUrls) {
                this.mContext = context;
                this.mInflater = LayoutInflater.from(context);
                this.mMovieList = new ArrayList<String>(Arrays.asList(imageUrls));
            }

            @Override
            public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = mInflater.inflate(R.layout.gridview_item_image, parent, false);
                return new MovieViewHolder(view);
            }

            @Override
            public void onBindViewHolder(MovieViewHolder holder, int position) {
                String url = mMovieList.get(position);

                // This is how we use Picasso to load images from the internet.
                Picasso.with(mContext)
                        .load(url)
                        .placeholder(R.color.colorAccent)
                        .into(holder.imageView);
            }

            @Override
            public int getItemCount()
            {
                return (mMovieList == null) ? 0 : mMovieList.size();
            }

            public void setMovieList(List<String> movieList) {
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
}
