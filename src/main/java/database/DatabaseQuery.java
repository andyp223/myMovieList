package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import api.MovieAPI;
import api.PosterAPI;
import movie.Movie;
import movie.MovieList;
import user.User;
import util.Bigram;

public final class DatabaseQuery {
  
  /**
   * private constructor.
   */
  private DatabaseQuery() {
  }
  
  /**
   * Get all rated movies for MovieTests specific login.
   * @param conn
   */
  public static List<Bigram<String,String>> getRatings(Connection conn, String userId) {
	  System.out.println(userId);
	  String query = "SELECT imdbId, rating FROM userRatings WHERE userId = ?;";
	  List<Bigram<String,String>> output = new ArrayList<Bigram<String,String>>();
	  try {
		  PreparedStatement prep;
		  prep = conn.prepareStatement(query);
		  prep.setString(1, userId);
		  ResultSet rs = prep.executeQuery();
		  while(rs.next()) {
			  System.out.println("INSERTED ENTRY");
			  output.add(new Bigram<String,String>(rs.getString(1),rs.getString(2)));
		  }
		  rs.close();
		  prep.close();
	  } catch (SQLException e) {
	      System.out.println("ERROR: Something wrong with updating rating.");
	  }
	  return output;
  }
  
  /**
   * Used to update MovieTests rating to the ratings table.
   * @param conn
   */
  public static boolean checkRating(Connection conn, String userId, String movieId) {
	  String query = "SELECT COUNT(*) FROM userRatings WHERE userId = ? AND imdbId = ?;";
	  try {
		  PreparedStatement prep;
		  prep = conn.prepareStatement(query);
		  prep.setString(1, userId);
		  prep.setString(2, movieId);
		  ResultSet rs = prep.executeQuery();
		  while(rs.next()) {
			  if (rs.getInt(1) > 0) {
				  return true;
			  } else {
				  return false;
			  }
		  }
		  prep.close();
	  } catch (SQLException e) {
	      System.out.println("ERROR: Something wrong with updating rating.");
	  }
	  return false;
  }
  
  /**
   * Used to update MovieTests rating to the ratings table.
   * @param conn
   */
  public static void updateRating(Connection conn, String userId, String movieId, int rating) {
	  String query = "UPDATE userRatings SET rating = ? WHERE userId = ? AND imdbId = ?;";
	  try {
		  PreparedStatement prep;
		  prep = conn.prepareStatement(query);
		  prep.setInt(1, rating);
		  prep.setString(2, userId);
		  prep.setString(3, movieId);
		  prep.executeUpdate();
		  prep.close();
	  } catch (SQLException e) {
	      System.out.println("ERROR: Something wrong with updating rating.");
	  }
	  return;
  }

  /**
   * Used to add MovieTests rating to the ratings table.
   * @param conn
   */
  public static void insertRating(Connection conn, String userId, String movieId, int rating) {
	  String query = "INSERT INTO userRatings VALUES (?, ?, ?);";
	  try {
		  PreparedStatement prep;
		  prep = conn.prepareStatement(query);
		  prep.setString(1, userId);
		  prep.setString(2, movieId);
		  prep.setInt(3, rating);
		  prep.execute();
		  prep.close();
	  } catch (SQLException e) {
	      System.out.println("ERROR: Something wrong with inserting rating.");
	  }
	  return;
  }

  public static void deleteRating(Connection conn, String userId, String movieId) {
    String query = "DELETE FROM userRatings WHERE userId = ? AND imdbId = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, userId);
      prep.setString(2, movieId);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Removing movie from listMovies error");
    }
  }
  
  /**
   * Used to return MovieTests mapping from imdbId's to the movie titles.
   * @param conn
   * @return MovieTests hashmap with MovieTests mapping from imdbIds to the movie titles
   */
  public static HashMap<String,String> getMovieToImdb(Connection conn) {
	  HashMap<String,String> output = new HashMap<String,String>();
	  String query = "SELECT title,imdbId FROM movies";
	  try {
		  PreparedStatement prep;
		  prep = conn.prepareStatement(query);
		  ResultSet rs = prep.executeQuery();
		  while(rs.next()) {
			  String title = rs.getString(1);
			  String imdbId = rs.getString(2);
			  output.put(imdbId, title);
		  }
		  rs.close();
		  prep.close();
	  } catch (SQLException e) {
	      System.out.println("ERROR: Something wrong with getting movie.");
	  }
	  return output;
  }
  
  /**
   * This query method returns the specific movie
   * @param conn Database SQL connection
   * @return A movie object with all data from database
   */
  public static Movie getMovie(Connection conn, String movieId) {
    Movie m = null;
    String query = "SELECT * FROM movies WHERE imdbId = ?";
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(query);
      prep.setString(1, movieId);
      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        String title = rs.getString(2);
        int year = rs.getInt(3);
        String rated = rs.getString(4);
        String runTime = rs.getString(5);
        String plot = rs.getString(6);
        String awards = rs.getString(7);
        double imdbRating = rs.getDouble(8);
        String imdbVotes = rs.getString(9);
        // get poster url
        String posterURL = PosterAPI.getImage(movieId);
        // set the movie temporarily without genres
        m = new Movie(movieId, title, year, rated, runTime, 
            new ArrayList<String>(), plot, awards, imdbRating, imdbVotes,
            posterURL);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Something wrong with getting movie.");
    }
    // if the movie does not exist in the database, use movie api
    if (m == null) {
      m = MovieAPI.searchById(movieId);
      // if it's still null, don't do anything.
      if (m != null) {
        insertMovie(conn, m);
      }
      return m;
    }
    // get all genres that this movie has
    List<String> genres = getGenres(conn, m.getImdbID());
    m.setGenre(genres);
    return m;
  }
  
  public static List<String> getGenres(Connection conn, String movieId) {
    // first get all the genre id's
    String query = "SELECT genreId FROM genreMovies WHERE movieId = ?";
    List<Integer> genreId = new ArrayList<>();
    try {
      PreparedStatement prep;
      prep = conn.prepareStatement(query);
      prep.setString(1, movieId);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        int genre = rs.getInt(1);
        genreId.add(genre);
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Something wrong with getting genre IDs.");
    }
    // now get all the names of genres through genre table
    String query2 = "SELECT genre FROM genres WHERE genreId = ?;";
    List<String> genreNames = new ArrayList<>();
    for (int genre : genreId) {
      try {
        PreparedStatement prep;
        prep = conn.prepareStatement(query2);
        prep.setInt(1, genre);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
          String genreName = rs.getString(1);
          genreNames.add(genreName);
        }
        rs.close();
        prep.close();
      } catch (SQLException e) {
        System.out.println("ERROR: Something wrong with getting genre names");
      }
    }
    return genreNames;
  }
  
  /**
   * This query method returns the specific movie
   * @param conn Database SQL connection
   * @return A movie object with all data from database
   */
  public static void insertMovie(Connection conn, Movie m) {
    String query = "INSERT INTO movies VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    String query2 = "INSERT INTO genreMovies VALUES (?, ?);";
    String query3 = "INSERT INTO genres VALUES (?, ?);";
    // getting specific movie data
    String imdbId = m.getImdbID();
    String title = m.getTitle();
    int year = m.getYear();
    String rated = m.getRated();
    String runTime = m.getRunTime();
    String plot = m.getPlot();
    String awards = m.getAwards();
    double imdbRating = m.getImdbRating();
    String imdbVotes = m.getImdbVotes();
    List<String> genres = m.getGenre();
    for (String genre : genres) {
      // unique id for each genre
      int hash = Objects.hashCode(genre);
      try {
        PreparedStatement prep;
        // insert each genre into the genre table
        if (!genreExists(conn, hash)) {
          prep = conn.prepareStatement(query3);
          prep.setInt(1, hash);
          prep.setString(2, genre);
          prep.execute();
          prep.close();
        }
        // associate each movie with genre in genreMovies table
        PreparedStatement prep2;
        prep2 = conn.prepareStatement(query2);
        prep2.setString(1, imdbId);
        prep2.setInt(2, hash);
        prep2.execute();
        prep2.close();
      } catch (SQLException e) {
        System.out.println("ERROR: Something wrong with inserting genres.");
      }
    }
    try {
      // insert each movie into the movies table
      PreparedStatement prep;
      prep = conn.prepareStatement(query);
      prep.setString(1, imdbId);
      prep.setString(2, title);
      prep.setInt(3, year);
      prep.setString(4, rated);
      prep.setString(5, runTime);
      prep.setString(6, plot);
      prep.setString(7, awards);
      prep.setDouble(8, imdbRating);
      prep.setString(9, imdbVotes);
      prep.execute();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Something wrong with inserting movie.");
    }
  }
  
  public static boolean genreExists(Connection conn, int hash) {
    boolean ret = true;
    String query = "SELECT COUNT(*) FROM genres WHERE genreId = ?;";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, hash);
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        int num = rs.getInt(1);
        if (num == 0) {
          ret = false;
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Counting genres failed.");
    }
    return ret;
  }

  public static Boolean validLogin(Connection conn, String login) {
    Boolean ret = false;
    String query = "SELECT COUNT(*) FROM users WHERE login = ?;";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, login);
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        int num = rs.getInt(1);
        if (num == 0) {
          ret = true;
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Finding login failed.");
    }
    return ret;
  }

  public static Boolean authenticateUser(Connection conn, String login, String pw) {
      Boolean toReturn = false;
	  String query = "SELECT COUNT(*) FROM users WHERE login = ? AND password = ?;";
	  try {
	      PreparedStatement prep = conn.prepareStatement(query);
	      prep.setString(1, login);
	      prep.setString(2, pw);
	      ResultSet rs = prep.executeQuery();
	      while (rs.next()) {
	    	  if (rs.getInt(1) == 1) {
	    		  toReturn = true;
	    	  }
	      }
	      rs.close();
	      prep.close();
	  } catch (SQLException e) {
		  System.out.println("invalid credentials");
	  }
	  return toReturn;
  }
  
  public static void insertNewUser(Connection conn, User u) {
    String login = u.getLogin();
    String password = u.getPassword();

    String query = "INSERT INTO users VALUES (?, ?);";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, login);
      prep.setString(2, password);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println("ERROR: Something wrong with inserting user.");
    }
  }

  /**
   * inserts MovieTests new list into the list table.
   * @param conn
   * @param owner
   * @param name
   */
  public static void insertNewList(Connection conn, String owner, String name) {
	  String query = "INSERT INTO lists VALUES (NULL, ?, ?);";
	  try {
		  PreparedStatement prep = conn.prepareStatement(query);
		  prep.setString(1, owner);
		  prep.setString(2, name);
		  prep.executeUpdate();
		  prep.close();
	  } catch (SQLException e) {
		  System.out.println("couldn't insert list for whatever reason");
	  }
  }

  /**
   * Given MovieTests list id returns MovieTests list of movie ids associated with that list id.
   * @param conn
   * @param id
   * @return
   */
  public static List<String> getMoviesForListId(Connection conn, int id) {
	  String query = "SELECT imdbId FROM listMovies WHERE listId = ?;";
	  List<String> toReturn = new ArrayList<>();
	  try {
		  PreparedStatement prep = conn.prepareStatement(query);
		  prep.setInt(1, id);
		  ResultSet rs = prep.executeQuery();
		  while (rs.next()) {
			  String imdbId = rs.getString(1);
			  toReturn.add(imdbId);
		  }
		  rs.close();
		  prep.close();
	  } catch (SQLException e) {
		  return toReturn;
	  }
	  return toReturn;
  }

  /**
   * Given MovieTests userid returns all the list ids and list names associated with
   * that user.
   * @param conn
   * @param login
   * @return
   */
  public static List<Bigram<Integer, String>> getListsFromUser(Connection conn, String login) {
	  String query = "SELECT * FROM lists WHERE curator = ?;";
	  List<Bigram<Integer, String>> toReturn = new ArrayList<>();
	  try {
		  PreparedStatement prep = conn.prepareStatement(query);
		  prep.setString(1, login);
		  ResultSet rs = prep.executeQuery();
		  while (rs.next()) {
			  int listId = rs.getInt(1);
			  String listName = rs.getString(3);
			  toReturn.add(new Bigram<>(listId, listName));
		  }
		  rs.close();
		  prep.close();
	  } catch (SQLException e) {
		  System.out.println("ERROR: Getting lists fatal.");
	  }
	  return toReturn;
  }
  
  public static List<String> getRatedMovies(Connection conn, String login) {
	  String query = "SELECT imdbId FROM userRatings WHERE userId = ?;";
	  List<String> toReturn = new ArrayList<>();
	  try {
		  PreparedStatement prep = conn.prepareStatement(query);
		  prep.setString(1, login);
		  ResultSet rs = prep.executeQuery();
		  while (rs.next()) {
			  toReturn.add(rs.getString(1));
		  }
		  rs.close();
		  prep.close();
	  } catch (SQLException e) {
		  System.out.println("something went wrong getting rated movies");
	  }
	  return toReturn;
  }
  
  /**
   * Given MovieTests userid returns all the list ids and list names associated with
   * that user.
   * @param conn
   * @return
   */
  public static void insertIntoList(Connection conn, String id, int listId) {
      String query = "INSERT INTO listMovies VALUES (?, ?)";
      try {
          PreparedStatement prep = conn.prepareStatement(query);
          prep.setInt(1, listId);
          prep.setString(2, id);
          prep.execute();
          prep.close();
      } catch (SQLException e) {
          System.out.println("ERROR: Inserting movie into listMovies error");
      }
  }

  public static Boolean listContainsMovie(Connection conn, String id, int listId) {
    String query = "SELECT COUNT(*) FROM listMovies WHERE listId = ? AND imdbId = ?";
    Boolean ret = true;
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, listId);
      prep.setString(2, id);
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        int num = rs.getInt(1);
        if (num == 0) {
          ret = false;
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Finding movie in listMovies error");
    }
    return ret;
  }
  
  public static void insertIntoWatchLater(Connection conn, String username,
      String id) {
    String query = "INSERT INTO userMovies VALUES (?, ?)";
    try {
        PreparedStatement prep = conn.prepareStatement(query);
        prep.setString(1, username);
        prep.setString(2, id);
        prep.execute();
        prep.close();
    } catch (SQLException e) {
        System.out.println("ERROR: Inserting movie into listMovies error");
    }
  }

  public static void deleteFromWatchLater(Connection conn, String username, String id) {
    String query = "DELETE FROM userMovies WHERE login = ? AND imdbId = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, username);
      prep.setString(2, id);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Removing movie from userMovies error");
    }
  }
  
  public static boolean watchLaterContains(Connection conn, String username,
      String id) {
    String query = "SELECT COUNT(*) FROM userMovies WHERE login = ? "
        + "AND imdbId = ?";
    boolean contains = true;
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, username);
      prep.setString(2, id);
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        int num = rs.getInt(1);
        if (num == 0) {
          contains = false;
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
        System.out.println("ERROR: Inserting movie into listMovies error");
    }
    return contains;
  }

  public static void removeFromList(Connection conn, String id, int listId) {
    String query = "DELETE FROM listMovies WHERE listId = ? AND imdbId = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, listId);
      prep.setString(2, id);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Removing movie from listMovies error");
    }
  }
  
  public static List<String> getWatchLaterList(Connection conn, 
      String username) {
    String query = "SELECT imdbId FROM userMovies WHERE login = ?";
    List<String> ids = new ArrayList<>();
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setString(1, username);
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        ids.add(rs.getString(1));
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
        System.out.println("ERROR: Getting curator, name fatal.");
    }
    return ids;
  }

  public static Bigram<String, String> getCuratorNameList(
      Connection conn, int id) {
    String query = "SELECT curator, name FROM lists WHERE id = ?";
    Bigram<String, String> curatorName = null;
    String curator = null;
    String name = null;
    try {
        PreparedStatement prep = conn.prepareStatement(query);
        prep.setInt(1, id);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
          curator = rs.getString(1);
          name = rs.getString(2);
        }
        rs.close();
        prep.close();
    } catch (SQLException e) {
        System.out.println("ERROR: Getting curator, name fatal.");
    }
    curatorName = new Bigram<>(curator, name);
    return curatorName;
  }

  public static List<String> searchUsers(Connection conn, String login) {
    List<String> ret = new ArrayList<>();
    login = login.toLowerCase();
    String query = "SELECT login FROM users;";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {

        String curr = rs.getString(1).toLowerCase();
        if (curr.contains(login)) {
          ret.add(rs.getString(1));
        }
      }
      rs.close();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Searching users failed.");
    }
    return ret;
  }

  public static void deleteList(Connection conn, int listId) {
    String query = "DELETE FROM listMovies WHERE listId = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query);
      prep.setInt(1, listId);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Removing movie from listMovies error");
    }

    String query2 = "DELETE FROM lists WHERE id = ?";
    try {
      PreparedStatement prep = conn.prepareStatement(query2);
      prep.setInt(1, listId);
      prep.executeUpdate();
      prep.close();
    } catch (SQLException e) {
      System.out.println("ERROR: Removing movie from listMovies error");
    }
  }
}
