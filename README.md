# Discover-Movies

Description
----
This is an Android app to show the most popular movies or highest rated movies based on information from themoviedb.org. The selection can be made from the actionbar. Clicking on a thumbnail launches movie details in a new activity.

Technical Details
----
- Uses AsyncTask to retrieve information from themoviedb API
- Fragment supports screen rotation
- Uses Picasso to load images into ImageView
- Uses a custom GridViewAdapter to display movies in a grid

Note: The API Key is removed in strings.xml
```
<string name="themoviedb_api_key">api_key=XXX</string>
```

Walkthrough
---
![Video Walkthrough](Discover-movies.gif)

