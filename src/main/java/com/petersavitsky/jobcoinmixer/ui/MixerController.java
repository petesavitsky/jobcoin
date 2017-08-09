package com.petersavitsky.jobcoinmixer.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.petersavitsky.jobcoinmixer.service.MixingService;

@Controller
@RequestMapping("")
public class MixerController {

	@Autowired
	private MixingService mixingService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getMixerHome() {
		return "mixer-home";
	}

	@RequestMapping(value = "generate-deposit-address", method = RequestMethod.POST)
	public @ResponseBody GenerateDepositAddressResponse generateDepositAddress(
			@RequestBody GenerateDepositAddressRequest request) {
		String depositAddress = mixingService.generateDepositAddress(request.getAddresses());
		GenerateDepositAddressResponse response = new GenerateDepositAddressResponse();
		response.setDepositAddress(depositAddress);
		return response;
	}

}
