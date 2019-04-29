package sparkHandlers;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class LoginPageHandler implements TemplateViewRoute {
  
  @Override
  public ModelAndView handle(Request request, Response response) {
    Map<String, Object> variables = ImmutableMap.of("title",
                "Login");
    return new ModelAndView(variables, "login.ftl");
  }
}
