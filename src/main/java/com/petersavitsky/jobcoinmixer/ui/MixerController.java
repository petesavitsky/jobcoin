package com.petersavitsky.jobcoinmixer.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
public class MixerController {

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getMixerHome() {
		return "mixer-home";
	}

	@RequestMapping(value = "generate-deposit-address", method = RequestMethod.POST)
	public @ResponseBody GenerateDepositAddressResponse generateDepositAddress(
			@RequestBody GenerateDepositAddressRequest request) {

		return new GenerateDepositAddressResponse();
	}

}
