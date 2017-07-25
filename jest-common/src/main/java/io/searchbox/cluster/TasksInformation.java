package io.searchbox.cluster;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * Currently only supports retrieving information for a particular task
 */
public class TasksInformation extends GenericResultAbstractAction {

    protected String task;

    protected TasksInformation(Builder builder) {
        super(builder);
        task = builder.task;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        String uri = super.buildURI() + "_tasks";
        if (task != null) {
            uri += "/" + task;
        }
        return uri;
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<TasksInformation, Builder> {

        protected String task;

        public Builder task(String task) {
            this.task = task;
            return this;
        }

        @Override
        public TasksInformation build() {
            return new TasksInformation(this);
        }
    }

}
