# PopularMovies-Udacity-

May 14, 2016 [Sat]

- moved MovieFragment to a new java file
- added FetchMovieDataTask (which extends AysncTask [not optimal way of fetching data in the background, but will change later]) 
    to fetch data in the background
- implemented doInBackground method for the FetchMovieDataTask class
- used Uribuilder to build the URL paths to add parameters for sorting the movies (either by popular or by top_rated)
- added getMoviePostersURlsfromJson to parse the Json string received from themoviedb.org 
- implemented onPostExecute (in FetchMovieDataTask class) to update the MovieAdapter with the data from themoviedb.org database

May 12, 2016 [Thu]]

- added MovieViewHolder, which extends RecyclerView.ViewHolder
- added MovieAdapter, which extends RecyclerView.Adapter and used Picasso to load images to the views from the given urls
- added GridAutofitLayoutManager to auto fit/position the number of columns to display on the screen, depending on the screen size and/or orientation
- note "AutofitRecyclerView" added to be used in fragment_main.xml

Apr 27, 2016 [Wed]
- Finished Custom (Image) Array Adapter to be used in gridview
-- the images don't keep aspect ratio (has to be fixed)

