package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
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
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        private final String MOVIE_INFO = "movie info";
        private final String VIDEO_INFO = "video info";
        private ArrayAdapter<String> mTrailerAdapter;

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            if (intent != null && extras != null){
                String[] movieInfo = extras.getStringArray(MOVIE_INFO);
                ((TextView) rootView.findViewById(R.id.detail_title)).setText(movieInfo[1]);
                ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movieInfo[3]);
                ((TextView) rootView.findViewById(R.id.detail_rating)).setText(getString(R.string.detail_user_ratings)+ " " + movieInfo[4]+"/10");
                ((TextView) rootView.findViewById(R.id.detail_date)).setText(movieInfo[5]);
                ImageView imgV = (ImageView) rootView.findViewById(R.id.detail_image);
                Picasso.with(getActivity())
                        .load(movieInfo[2])
                        .placeholder(R.color.loadingColor)
                        .into(imgV);

                final ArrayList<String> videoInfo = extras.getStringArrayList(VIDEO_INFO);
                ArrayList<String> videoTitles = new ArrayList<>();
                int i = 0;
                while (i < videoInfo.size()) {
                    videoTitles.add(videoInfo.get(i)); // trailer name is at even positions
                    i += 2;
                }
                mTrailerAdapter =
                        new ArrayAdapter<String>(
                                getActivity(),
                                R.layout.list_item_detail,
                                R.id.list_item_detail_textview,
                                videoTitles
                        );
                // Get a reference to the ListView, and attach this adapter to it.
                ListView listView = (ListView) rootView.findViewById(R.id.detail_listview);
                listView.setAdapter(mTrailerAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // https://www.youtube.com/watch?v=
//                        final String YOUTUBE_BASE_URL = "https://www.youtube.com/";
//                        final String VIDEO_KEY_PARAM = "v";
//                        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
//                                .appendPath("watch")
//                                .appendQueryParameter(VIDEO_KEY_PARAM, videoInfo.get(position+1))
//                                .build();
//                        URL url = new URL(builtUri.toString());
                        String videoKey = videoInfo.get(position+1);
                        String url = "https://www.youtube.com/watch?v=" + videoKey;
                        Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }

            return rootView;
        }
    }
}
