$(document).ready(function() {
	$("#get-deposit-address-button").click(generateDepositAddress);
});

function generateDepositAddress() {
	var depositAddresses = $("textarea[id=output-addresses]").val();
	if (!depositAddresses) {
		alert("Please enter at least 1 deposit address!");
		return;
	}
	$("#mixer-form-container").slideUp(200);
	// parse them here and check for min number && non-blankness
	var parsedDepositAddresses = depositAddresses.split(",");
	var generateDepositAddressRequest = new Object();
	generateDepositAddressRequest.addresses = parsedDepositAddresses;
	var submission = $.ajax({
		url : "/generate-deposit-address",
		type: "post",
		data: JSON.stringify(generateDepositAddressRequest),
		headers : {
			"Accept":"application/json",
			"Content-Type":"application/json"
		},
		success :function(data) {
			$("#deposit-address-text").text(data.deposit_address);
			$("#deposit-address-container").slideDown(200);
			// clear form
			// and 'Mix More Coins' Button
		}, 
		error : function(data) {
			alert("An Error Occured While Generating Your Deposit Address!");
			// show error message
		}
	});
}