package sparkHandlers;

import java.sql.Connection;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import database.DatabaseHandler;
import database.DatabaseQuery;
import spark.*;

public class AddMovieHandler implements Route {

  private static final Gson GSON = new Gson();

  @Override
  public String handle(Request request, Response response) throws Exception {
    boolean success = true;
    QueryParamsMap qm = request.queryMap();
    String userName = qm.value("username");
    String password = qm.value("password");

    Map<String, Object> variables = ImmutableMap.of("success", success);
    return GSON.toJson(variables);
  }
}
