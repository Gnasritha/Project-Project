package com.aaseya.momthathel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskVariables {

    private String id;
    private String name;
    private String processName;
    private String processInstanceKey;
    private String taskState;
    private String state;
    private String assignee;
    private String creationTime;
    private String completionTime;
    private List<Map<String, Object>> variables;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }

    public String getProcessInstanceKey() { return processInstanceKey; }
    public void setProcessInstanceKey(String processInstanceKey) { this.processInstanceKey = processInstanceKey; }

    public String getTaskState() { return taskState; }
    public void setTaskState(String taskState) { this.taskState = taskState; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public String getCreationTime() { return creationTime; }
    public void setCreationTime(String creationTime) { this.creationTime = creationTime; }

    public String getCompletionTime() { return completionTime; }
    public void setCompletionTime(String completionTime) { this.completionTime = completionTime; }

    public List<Map<String, Object>> getVariables() { return variables; }
    public void setVariables(List<Map<String, Object>> variables) { this.variables = variables; }
}
