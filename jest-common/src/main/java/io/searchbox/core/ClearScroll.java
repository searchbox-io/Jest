package io.searchbox.core;

import com.google.common.collect.ImmutableMap;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;


public class ClearScroll extends GenericResultAbstractAction {

  private final String uri;

  public ClearScroll(Builder builder) {

    if (builder.getScrollIds().size() == 0) {
      uri = "/_search/scroll/_all";
      this.payload = null;
    } else {
      uri = "/_search/scroll";
      this.payload = ImmutableMap.of("scroll_id", builder.getScrollIds());
    }
  }

  @Override
  public String getRestMethodName() {
    return "DELETE";
  }

  @Override
  protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
    return super.buildURI(elasticsearchVersion) + uri;
  }

  public static class Builder extends GenericResultAbstractAction.Builder<ClearScroll, Builder> {

    protected Collection<String> scrollIds = new LinkedHashSet<String>();

    public Builder addScrollId(String scrollId) {
      this.scrollIds.add(scrollId);
      return this;
    }

    public Builder addScrollIds(Set<String> scrollIds) {
      this.scrollIds.addAll(scrollIds);
      return this;
    }

    @Override
    public ClearScroll build() {
      return new ClearScroll(this);
    }

    public Collection<String> getScrollIds() {
      return scrollIds;
    }

  }
}
