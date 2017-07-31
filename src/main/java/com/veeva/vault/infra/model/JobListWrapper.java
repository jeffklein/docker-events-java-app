package com.veeva.vault.infra.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey Klein on 7/29/2017.
 */
public class JobListWrapper {
    List<Job> jobList = new ArrayList<>();

    public List<Job> getJobList() {
        return jobList;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }
}
