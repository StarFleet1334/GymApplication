package com.demo.folder.controller;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature/trainingType",
        glue = "com.demo.folder.controller.step.trainingType",
        plugin = {"pretty", "html:target/new-reports.html", "json:target/cucumber.json"},
        monochrome = true,
        publish = true
)
public class TrainingCucumberTest {
}
