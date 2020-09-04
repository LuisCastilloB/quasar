
package com.meli.quasar.controller;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

//import com.in28minutes.springboot.model.Course;
//import com.in28minutes.springboot.service.StudentService;
import com.meli.quasar.controllers.QuasarAPI;
import com.meli.quasar.models.Position;
import com.meli.quasar.models.SateliteRequest;
import com.meli.quasar.models.TopSecretRequest;
import com.meli.quasar.models.TopSecretResponse;
import com.meli.quasar.services.TopSecretService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = QuasarAPI.class)
public class QuasarAPITest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TopSecretService topSecretService;

	String requestTopSecret = "{\"satelites\":[{\"distance\":5,\"message\":"
                + "[\"este\",\"es\",\"\",\"\",\"\"],\"name\":\"kenobi\"},"
                + "{\"distance\":5,\"message\":[\"\",\"\",\"un\",\"mensaje\",\"\"],"
                + "\"name\":\"skywalker\"},{\"distance\":2,\"message\":"
                + "[\"\",\"\",\"\",\"\",\"secreto\"],\"name\":\"sato\"}]}";
        String requestTopSecretSplit = "{\"distance\":5,\"message\":[\"\",\"\",\"un"
                + "\",\"mensaje\",\"\"]}";
        String responseTopSecret = "{\"position\":{\"x\":-3.0,\"y\":-1.0},\"message\":"
                + "\"este es un mensaje secreto\"}";
        
	@Test
	public void topSecretTest() throws Exception {
                TopSecretResponse mockCourse = new TopSecretResponse();
                Position position = new Position();
                position.setX(-3.0);
                position.setY(-1.0);
                mockCourse.setMessage("este es un mensaje secreto");
                mockCourse.setPosition(position);

		Mockito.when(topSecretService.topSecret(Mockito.any(TopSecretRequest.class)))
                        .thenReturn(mockCourse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/topsecret")
				.accept(MediaType.APPLICATION_JSON).content(requestTopSecret)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
                assertEquals(HttpStatus.OK.value(), response.getStatus());
                assertEquals(responseTopSecret, response.getContentAsString());
	}
        
        
        
        @Test
	public void topSecretSplitTest() throws Exception {
                TopSecretResponse mockCourse = new TopSecretResponse();
                Position position = new Position();
                position.setX(-3.0);
                position.setY(-1.0);
                mockCourse.setMessage("este es un mensaje secreto");
                mockCourse.setPosition(position);

		Mockito.when(topSecretService.topSecretSplit(Mockito.anyString(),Mockito.any(SateliteRequest.class)))
                        .thenReturn(mockCourse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/topsecret_split/kenobi")
				.accept(MediaType.APPLICATION_JSON).content(requestTopSecretSplit)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
                assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
}
