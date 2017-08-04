$(document).ready(function() {
	
});

function generateDepositAddress() {
	var depositAddresses = $("input[id=addresses]").val();
	// parse them here and check for min number && non-blankness
	var emailAddress = $("input[id=email]").val();
	var timeframe;
	var generateDepositAddressRequest = new Object();
	generateDepositAddress.email = emailAddress;
	generateDepositAddress.deposit_addresses = depositAddresses;
	generateDepositAddress.timeframe_value = timeframeValue;
	generateDepositAddress.timeframe_unit = timeframeUnit;
	var submission = $.ajax({
		url : "/generate-deposit-address",
		type: "post",
		data: JSON.stringify(generateDepositAddress),
		headers : {
			"Accept":"application/json",
			"Content-Type":"application/json"
		},
		success :function(data) {
			// clear and hide form 
			// show deposit address
			// and 'Mix More Coins' Button
		}, 
		error : function(data) {
			// show error message
		}
	});
}