package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

        ImageListAdapter mPosterAdapter;

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
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"

            };
            mPosterAdapter = new ImageListAdapter(getActivity().getApplicationContext(),testPosters);
            GridView gridView = (GridView) rootView.findViewById(R.id.poster_gridview);
            gridView.setAdapter(mPosterAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Toast.makeText(getActivity(), "" + position,
                            Toast.LENGTH_SHORT).show();
                }
            });

            return rootView;
        }

        public class ImageListAdapter extends ArrayAdapter {
            private Context context;
            private LayoutInflater inflater;

            private String[] imageUrls;

            public ImageListAdapter(Context context, String[] imageUrls) {
                super(context, R.layout.gridview_item_image, imageUrls);

                this.context = context;
                this.imageUrls = imageUrls;

                inflater = LayoutInflater.from(context);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.gridview_item_image, parent, false);
                }

                Picasso
                        .with(context)
                        .load(imageUrls[position])
                        .fit().centerInside()
                        .into((ImageView) convertView);

                return convertView;
            }
        }

        /*
        public class ImageAdapter extends BaseAdapter {
            //from https://github.com/square/picasso/blob/master/picasso-sample/src/main/java/com/example/picasso/SampleGridViewAdapter.java
            private final Context mContext;
            private final List<String> urls;

            public ImageAdapter(Context context, String[] imageUrls) {
                this.mContext = context;
                this.urls = new ArrayList<String>(Arrays.asList(imageUrls));
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //SquaredImageView view = (SquaredImageView) convertView;
                ImageView view = (ImageView) convertView;
                if (view == null) {
                    //view = new SquaredImageView(mContext);
                    //view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

                String url = getItem(position);

                // Trigger the download of the URL asynchronously into the image view.
                Picasso.with(mContext) //
                        .load(url) //
                        .fit() //
                        .centerInside()
                        .tag(mContext) //
                        .into(view);

                return view;
            }

            @Override
            public int getCount() {
                return urls.size();
            }

            @Override
            public String getItem(int position) {
                return urls.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }
        }

        public class SquaredImageView extends ImageView {
            public SquaredImageView(Context context) {
                super(context);
            }

            public SquaredImageView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public SquaredImageView(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
            }
        }
        */
    }
}
