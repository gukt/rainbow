package com.codedog.rainbow.api.common;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import lombok.Getter;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * @author https://github.com/gukt
 * @date 2020/1/26 00:31
 * @version 1.0
 */
public class JsonViews {

  private static Map<String, Class<? extends ApiResultView>> viewClassesByName = new HashMap<>();

  static {
    for (Class<?> clazz : JsonViews.class.getClasses()) {
      if (ApiResultView.class.isAssignableFrom(clazz)) {
        viewClassesByName.put(clazz.getSimpleName(), (Class<ApiResultView>) clazz);
        JsonViewNameAlias anno = clazz.getAnnotation(JsonViewNameAlias.class);
        if (anno != null) {
          for (String alias : anno.value()) {
            viewClassesByName.put(alias, (Class<ApiResultView>) clazz);
          }
        }
      }
    }
  }

  // ==================== JsonView interfaces ====================

  @JsonViewNameAlias({"id-only"})
  public interface IdOnly extends ApiResultView {}

  @JsonViewNameAlias({"post-top"})
  public interface PostTopView extends IdOnly {}

  @JsonViewNameAlias({"post-simple"})
  public interface PostSimpleView extends PostTopView {}

  @JsonViewNameAlias({"post-rank"})
  public interface PostRankView extends PostSimpleView {}

  @JsonViewNameAlias({"post-detail"})
  public interface PostDetailView extends PostSimpleView {}

  @JsonViewNameAlias({"comment-simple"})
  public interface CommentSimpleView extends IdOnly {}

  @JsonViewNameAlias({"comment-detail"})
  public interface CommentDetailView extends CommentSimpleView {}

  @JsonViewNameAlias({"user-detail"})
  public interface UserDetailView extends IdOnly {}

  @JsonViewNameAlias({"notice-simple"})
  public interface NoticeSimpleView extends IdOnly {}

  // ==================== Static methods ====================

  static MappingJacksonValue wrap(Object data, String viewName) {
    if (!(data instanceof ApiResult)) {
      data = ApiResult.success(data);
    }
    MappingJacksonValue bodyContainer = new MappingJacksonValue(data);
    Class<?> viewClass = getJsonViewClass(viewName);
    if (viewClass != null) {
      bodyContainer.setSerializationView(viewClass);
    }
    return bodyContainer;
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends ApiResultView> getJsonViewClass(String viewName) {
    if (viewName == null) {
      return null;
    }
    Class<? extends ApiResultView> viewClass = viewClassesByName.get(viewName);
    if (viewClass == null) {
      String packageName = JsonViews.class.getName();
      String className = packageName + "$" + viewName;
      try {
        viewClass = (Class<ApiResultView>) Class.forName(className);
        JsonViewNameAlias alias = viewClass.getAnnotation(JsonViewNameAlias.class);
        if (alias != null) {
          for (String v : alias.value()) {
            viewClassesByName.put(v, viewClass);
          }
        }
        viewClassesByName.put(viewName, viewClass);
      } catch (ClassNotFoundException e) {
        throw new UnsupportedJsonView(viewName);
      }
    }
    return viewClass;
  }

  static class UnsupportedJsonView extends RuntimeException {
    @Getter private String view;

    UnsupportedJsonView(String view) {
      super("Unsupported view: " + view);
    }
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @JacksonAnnotation
  public @interface JsonViewNameAlias {
    String[] value() default {};
  }
}
