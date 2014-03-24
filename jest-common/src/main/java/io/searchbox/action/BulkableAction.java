package io.searchbox.action;

import io.searchbox.client.JestResult;

import java.util.Collection;

/**
 * Represents an Action that can be included in a Bulk request.
 *
 * @author cihat keser
 */
public interface BulkableAction<T extends JestResult> extends DocumentTargetedAction<T> {

    String getBulkMethodName();

    Collection<Object> getParameter(String key);

}
