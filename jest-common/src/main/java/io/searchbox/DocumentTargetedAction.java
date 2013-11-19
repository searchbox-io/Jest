package io.searchbox;

/**
 * Represents an Action that <b>can <i>(but NOT necessarily does)</i></b> operate on a targeted single document on Elasticsearch.
 *
 * @author cihat keser
 */
public interface DocumentTargetedAction extends Action {

    String getIndex();

    String getType();

    String getId();
}
