package ch.heigvd.amt.gamification.spec.steps;

import ch.heigvd.gamification.ApiException;
import ch.heigvd.gamification.ApiResponse;
import ch.heigvd.gamification.api.DefaultApi;
import ch.heigvd.gamification.api.dto.Measure;
import ch.heigvd.gamification.api.dto.Resource;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 */
public class ReportMeauresSteps {

  private final DefaultApi api = new DefaultApi();

  private Measure measure;
  private ApiResponse lastApiResponse;
  private ApiException lastApiException;
  private boolean lastApiCallThrewException;


  @Given("^there is an OpenAffect server")
  public void there_is_an_OpenAffect_server() throws Throwable {
    Properties properties = new Properties();
    properties.load(this.getClass().getClassLoader().getResourceAsStream("environment.properties"));
    String url = properties.getProperty("io.openaffect.server.url");
    api.getApiClient().setBasePath(url);
  }

  @Given("^I have an affective payload$")
  public void i_have_an_affective_payload() throws Throwable {
    Resource sensor = new Resource();
    sensor.setHref("https://cucumber.io");
    sensor.setType("testing tool");

    Resource subject = new Resource();
    subject.setHref("http://thesubject");
    subject.setType("software agent");

    Resource trigger = new Resource();
    subject.setHref("http://thetrigger");
    subject.setType("arbitrary trigger");

    measure = new Measure();
    measure.setSensor(sensor);
    measure.setSubject(subject);
    measure.setTimestamp(DateTime.now());
    measure.setTrigger(trigger);
  }

  @When("^I POST it to the /measures endpoint$")
  public void i_POST_it_to_the_measures_endpoint() throws Throwable {
    try {
      lastApiResponse = api.reportMeasureWithHttpInfo(measure);
      lastApiCallThrewException = false;
    } catch (ApiException e) {
      lastApiCallThrewException = true;
      lastApiException = e;
    }
  }

  @When("^I do a GET on the /measures endpoint$")
  public void i_do_a_GET_on_the_measures_endpoint() throws Throwable {
    try {
      lastApiResponse = api.listMeasuresWithHttpInfo();
      lastApiCallThrewException = false;
    } catch (ApiException e) {
      lastApiCallThrewException = true;
      lastApiException = e;
    }
  }

  @Then("^I receive a (\\d+) status code$")
  public void i_receive_a_status_code(int expectedStatusCode) throws Throwable {
    if (lastApiCallThrewException) {
      assertEquals(expectedStatusCode, lastApiException.getCode());
    } else {
      assertEquals(expectedStatusCode, lastApiResponse.getStatusCode());
    }
  }

  @Then("^the payload is a non-empty list$")
  public void the_payload_is_a_non_empty_list() throws Throwable {
    List list = (List)lastApiResponse.getData();
    assertNotEquals(0, list.size());
  }
}