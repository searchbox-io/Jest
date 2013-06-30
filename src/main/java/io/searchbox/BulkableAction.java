package io.searchbox;

/**
 * Represents an Action that can be included in a Bulk request.
 *
 * @author cihat keser
 */
public interface BulkableAction extends DocumentTargetedAction {

    String getBulkMethodName();

    Object getParameter(String key);

}
