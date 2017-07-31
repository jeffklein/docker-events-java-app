package com.veeva.vault.infra.model;

/**
 * Created by Jeffrey Klein on 7/29/2017.
 */
public class Job {

    public enum Status {
        READY, RUNNING, FINISHED_FAILED, FINISHED_SUCCESS
    }

    private String name;
    private String command = "/bin/sleep 10 && /bin/true";
    private Status status = Status.READY;
    private String containerId;
    private Integer returnCode;
    private boolean selected = false;

    public Job() {
    }

    public Job(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRunning() {
        return this.status == Status.RUNNING ? true : false;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "Job{" +
                "name='" + name + '\'' +
                ", command='" + command + '\'' +
                ", status=" + status +
                ", containerId='" + containerId + '\'' +
                ", returnCode=" + returnCode +
                ", selected=" + selected +
                '}';
    }
}
