package sparkHandlers;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.google.gson.Gson;
import database.DatabaseHandler;
import database.DatabaseQuery;
import spark.*;

public class DeleteListHandler implements Route {

  private static final Gson GSON = new Gson();

  @Override
  public String handle(Request request, Response response) throws Exception {
    QueryParamsMap qm = request.queryMap();
    int listId = Integer.parseInt(qm.value("listId"));
    Connection conn = DatabaseHandler.getDatabaseHandler().getConnection();
    DatabaseQuery.deleteList(conn, listId);
    Map<String, Object> variables = ImmutableMap.of("success", true);
    return GSON.toJson(variables);
  }
}
