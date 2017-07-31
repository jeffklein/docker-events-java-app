package com.veeva.vault.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DockerPodDeployJavaApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DockerPodDeployJavaApplication.class, args);
	}

}
