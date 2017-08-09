package com.petersavitsky.jobcoinmixer.client;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JobcoinClient {

	private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
	private static final String GET_ADDRESS_INFO_URL = "http://jobcoin.projecticeland.net/finery/api/addresses/";
	private static final String POST_TRANSACTION_URL = "http://jobcoin.projecticeland.net/finery/api/transactions";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String SEND_COINS_SUCCESS_STATUS = "OK";
	private static final String SEND_COINS_INSUFFICIENT_FUNDS_ERROR = "Insufficient Funds";

	public enum SendCoinsResult {
		SUCCESS, INSUFFICIENT_FUNDS, CLIENT_ERROR;
	}

	public AddressInfo getAddressInfo(String address) throws JobcoinClientException {
		HttpGet getAddressRequest = new HttpGet(GET_ADDRESS_INFO_URL + address);
		try (CloseableHttpResponse response = HTTP_CLIENT.execute(getAddressRequest)) {
			HttpEntity responseEntity = response.getEntity();
			AddressInfo addressInfo = OBJECT_MAPPER.readValue(responseEntity.getContent(), AddressInfo.class);
			return addressInfo;
		} catch (IOException e) {
			throw new JobcoinClientException("Failed to retrieve info for address [" + address + "]", e);
		}
	}

	public SendCoinsResult sendCoins(BigDecimal amount, String fromAddress, String toAddress) {
		try {
			SendCoinsRequest request = new SendCoinsRequest();
			request.setAmount(amount.toString());
			request.setFromAddress(fromAddress);
			request.setToAddress(toAddress);
			HttpPost sendCoinsPost = new HttpPost(POST_TRANSACTION_URL);
			StringEntity requestBody = new StringEntity(OBJECT_MAPPER.writeValueAsString(request));
			requestBody.setContentType("application/json");
			sendCoinsPost.setEntity(requestBody);
			CloseableHttpResponse response = HTTP_CLIENT.execute(sendCoinsPost);
			HttpEntity responseEntity = response.getEntity();
			SendCoinsResponse responseBody = OBJECT_MAPPER.readValue(responseEntity.getContent(),
					SendCoinsResponse.class);
			if (SEND_COINS_SUCCESS_STATUS.equals(responseBody.getStatus())) {
				return SendCoinsResult.SUCCESS;
			} else if (SEND_COINS_INSUFFICIENT_FUNDS_ERROR.equals(responseBody.getError())) {
				return SendCoinsResult.INSUFFICIENT_FUNDS;
			}
		} catch (IOException e) {
			// add logging in a normal app
			e.printStackTrace();
		}
		return SendCoinsResult.CLIENT_ERROR;
	}
}
