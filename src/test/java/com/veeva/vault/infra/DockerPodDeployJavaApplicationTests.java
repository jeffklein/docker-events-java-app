package com.veeva.vault.infra;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.EventsResultCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DockerPodDeployJavaApplicationTests {

	@Test
	public void dockerJavaTest() throws Throwable {

		DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
        String containerName = "whodat";
		EventsResultCallback callback = new EventsResultCallback() {
			@Override
			public void onNext(Event event) {
				System.out.println("Event: " + event);
                if (event.getAction().equals("die") && event.getActor().getAttributes().get("name").equals(containerName)) {
                    try {
                        System.out.println("got a match. try to close()");
                        super.close();
                    }
                    catch (IOException ioe) {
                        System.err.println("caught ioe");
                    }
                }
				super.onNext(event);
			}
		};

		dockerClient.eventsCmd().exec(callback).awaitCompletion().close();
        System.out.println("after callback");

	}

}
