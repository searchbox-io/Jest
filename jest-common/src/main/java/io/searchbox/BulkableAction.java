package io.searchbox;

import java.util.Collection;

/**
 * Represents an Action that can be included in a Bulk request.
 *
 * @author cihat keser
 */
public interface BulkableAction extends DocumentTargetedAction {

    String getBulkMethodName();

    Collection<Object> getParameter(String key);

}
