package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author ferhat
 */


public class Status extends AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(Status.class);

    final private LinkedHashSet<String> indexSet = new LinkedHashSet<String>();

    public Status() {
    }

    public Status addIndex(String index) {
        indexSet.add(index);
        return this;
    }

    public void addIndex(Collection<String> index) {
        indexSet.addAll(index);
    }

    public boolean removeIndex(String index) {
        return indexSet.remove(index);
    }

    public void clearAllIndex() {
        indexSet.clear();
    }

    public boolean isIndexExist(String index) {
        return indexSet.contains(index);
    }

    public int indexSize() {
        return indexSet.size();
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getName() {
        return "STATUS";
    }

    @Override
    public String getURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(createCommaSeparatedItemList(indexSet))
                .append("/_status");
        log.debug("Created URI for status action is :" + sb.toString());
        return sb.toString();
    }
}