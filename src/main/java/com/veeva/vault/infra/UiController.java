package com.veeva.vault.infra;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.EventsResultCallback;
import com.veeva.vault.infra.model.Job;
import com.veeva.vault.infra.model.JobListWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey Klein
 * on 7/29/2017.
 */
@Controller
public class UiController implements Runnable {

    List<Job> jobs = new ArrayList<>();

    DockerClient dockerClient;

    public UiController() {
        for (int i = 1; i <= 4; i++) {
            jobs.add(new Job("VLT-US-EAST-1-USER-" + i));
        }
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
        Thread dockerEventThread = new Thread(this);
        dockerEventThread.start();
    }

    @GetMapping(path = "/jobs")
    public String jobsForm(Model model) {
        JobListWrapper wrapper = new JobListWrapper();
        // query docker - are any containers running that have these pod names? (update isRunning, containerId,
        wrapper.setJobList(jobs);
        model.addAttribute("wrapper", wrapper);
        return "jobs";
    }

    @PostMapping("/jobs")
    public String jobsSubmit(@ModelAttribute JobListWrapper wrapper, Model model) {
        this.jobs = wrapper.getJobList();
        int i = 1;
        for (Job job : this.jobs) {
            if (job.isSelected()) {
                CreateContainerResponse container = dockerClient.createContainerCmd("alpine")
                        .withCmd("sh", "-c", job.getCommand())
                        .withName(job.getName())
                        .exec();
                dockerClient.startContainerCmd(container.getId()).exec();
            }
            i++;
        }
        wrapper.setJobList(this.jobs);
        model.addAttribute("wrapper", wrapper);
        return "jobs";
    }

    public void run() {
        EventsResultCallback callback = new EventsResultCallback() {
            @Override
            public void onNext(Event event) {
                for (Job job : jobs) {
                    //System.out.println("Event: " + event);
                    if (event.getAction().equals("start") && event.getActor().getAttributes().get("name").equals(job.getName())) {
                        System.out.println("EVENT: Job for "+job.getName()+" just started. Container ID: "+ event.getId());
                        job.setStatus(Job.Status.RUNNING);
                        job.setContainerId(event.getId());
                        job.setReturnCode(null);
                        job.setSelected(false);
                    }
                    if (event.getAction().equals("die") && event.getActor().getAttributes().get("name").equals(job.getName())) {
                        Integer exitCode = Integer.valueOf(event.getActor().getAttributes().get("exitCode"));
                        System.out.println("EVENT: Job for "+job.getName()+" just finished. exit code: "+ exitCode);
                        if (exitCode == 0) {
                            job.setStatus(Job.Status.FINISHED_SUCCESS);
                        }
                        else {
                            job.setStatus(Job.Status.FINISHED_FAILED);
                        }
                        job.setContainerId(null);
                        job.setReturnCode(exitCode);
                        dockerClient.removeContainerCmd(event.getId()).exec();
                    }
                }
                super.onNext(event);
            }
        };

        try {
            dockerClient.eventsCmd().exec(callback).awaitCompletion().close();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("after callback");

    }
}
