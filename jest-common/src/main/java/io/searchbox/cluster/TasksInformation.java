package io.searchbox.cluster;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * Currently only supports retrieving information for a particular task
 */
public class TasksInformation extends GenericResultAbstractAction {

    protected String taskId;

    protected TasksInformation(Builder builder) {
        super(builder);
        taskId = builder.taskId;
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "_tasks/" + taskId;
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<TasksInformation, Builder> {

        protected String taskId;

        public Builder(String taskId) {
            this.taskId = taskId;
        }

        @Override
        public TasksInformation build() {
            return new TasksInformation(this);
        }
    }

}
