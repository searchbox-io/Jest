package io.searchbox.core;

import java.util.List;

/**
 * @author Dogukan Sonmez
 */


public class Bulk extends AbstractAction implements Action {

    List<Action> requestList;

    public Bulk(List<Action> requestList){
         this.requestList = requestList;
    }

    public void addRequest(Action request){
        requestList.add(request);
    }
}
