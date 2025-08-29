package com.example.bajajfinserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.boot.CommandLineRunner;
import org.json.JSONObject;

import java.util.*;

import org.springframework.web.client.RestClientException;


@SpringBootApplication
public class BajajfinservApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BajajfinservApplication.class, args);
	}

	@Override
	public void run(String... args) {

		try {
			
			RestTemplate restTemplate = new RestTemplate();

			String url1 = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
			
			Map<String, String> body = new HashMap<>();
			body.put("name", "Parijat Somani");
			body.put("regNo", "22BBS0023");
			body.put("email", "parijat.somani2022@vitstudent.ac.in");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Map<String, String>> reqEntity = new HttpEntity<>(body, headers);

			ResponseEntity<String> response = restTemplate.exchange(url1, HttpMethod.POST, reqEntity, String.class);

			JSONObject jsonResponse = new JSONObject(response.getBody());
			String webhook = jsonResponse.getString("webhook");
			String accessToken = jsonResponse.getString("accessToken");

			///////////////////////////
			System.out.println("Webhook URL: " + webhook);
			System.out.println("Access Token: " + accessToken);
			///////////////////////////

			String finalSqlQuery =
				"SELECT p.AMOUNT AS SALARY, " +
				"CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
				"TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
				"d.DEPARTMENT_NAME " +
				"FROM PAYMENTS p " +
				"INNER JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
				"INNER JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
				"WHERE DAY(p.PAYMENT_TIME) != 1 " +
				"ORDER BY p.AMOUNT DESC " +
				"LIMIT 1";


			JSONObject payload = new JSONObject();
			payload.put("finalQuery", finalSqlQuery);

			HttpHeaders headers2 = new HttpHeaders();
			headers2.setContentType(MediaType.APPLICATION_JSON);
			headers2.setBearerAuth(accessToken);

			HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers2);

			System.out.println("Webhook: " + webhook);
			System.out.println("Headers: " + headers2);
			System.out.println("Payload: " + payload);

			ResponseEntity<String> resEntity2 = restTemplate.exchange(
					webhook,
					HttpMethod.POST,
					request,
					String.class
			);

			System.out.println("Response: " + resEntity2.getBody());

		
		} catch (RestClientException e) {

			e.printStackTrace();
		}
	}

}
