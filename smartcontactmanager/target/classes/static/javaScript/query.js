console.log("JavaScript is working");

const toggleBtn = () => {

	if ($(".sidebar").is(":visible")) {
		//doClose
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "7%");
		$(".cross").css("margin-left", "1%")

	}
	else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "32%");
		$(".cross").css("margin-left", "25%")


	}
};

/*const search = () => {
	let query = $("#search-input").val();

	if (query == '') {
		$(".search-result").hide();
	} else {
		
		console.log(query);
		
		let url = `http://localhost:100/search/${query}`;
		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				console.log(data);

				let text = `<div class='list-group'>`;
				data.forEach((contact) => {
					text += `<a href='user/contact/${contact.cid}' class='list-group-item list-group-action'>${contact.name}</a>`;
				});
				text += `</div>`;
				$(".search-result").html("text");
				$(".search-result").show();
			});
		$(".search-result").show();
	}

};*/


//payment gateway

const payementStart = () => {
	console.log("payment start")
	let amount = $("#paymentBar").val();
	console.log(amount);
	if (amount === "" || amount === null) {
		alert("amount is empty")
		return;
	}
	$.ajax({
		url: '/user/payment',
		dataType: "json",
		contentType: "application/json",
		type: "POST",
		data: JSON.stringify({ amount: amount, info: "order_request" }),
		success: function(response) {
			console.log(response);
			if (response.status == "created") {
				//open paymentform
				let options = {
					key: 'rzp_test_MEJlQkdvCK9cX6',
					amount: response.amount,
					currency: 'INR',
					name: 'Smart Contact Manager',
					description: 'Donation',
					image: "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.pngwing.com%2Fen%2Fsearch%3Fq%3Dcontact%2BManager&psig=AOvVaw0VNy6P-zJuKyXQx-38m9HK&ust=1690728629716000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCNj0o42VtIADFQAAAAAdAAAAABAF",
					order_id: response.id,
					handler: function(response) {
						console.log(response.razorpay_payment_id);
						console.log(response.razorpay_order_id);
						console.log(response.razorpay_signature);
						console.log('payment successfull !!')

						payemtUpdatedOnServer(response.razorpay_payment_id,response.razorpay_order_id,"paid");
					},
					prefill: {
						name: "",
						email: "",
						contact: "",
					},
					notes: {
						address: "Smart Contact Manager",
					},
					theme: {
						color: "#009688",
					},
				};
				let rzp = new Razorpay(options);

				rzp.on("payment.failed", function(response) {

					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					swal({
						title: "Payment Process",
						text: "Something went wrong!!",
						icon: "warning",
						buttons: true,
						dangerMode: true,
					})
				});
				rzp.open();
			}
		},
		error: function(error) {

			console.log(error);
			swal({
				title: "Payment Process",
				text: "Something went wrong!!",
				icon: "warning",
				buttons: true,
				dangerMode: true,
			})
		}


	})
};
function payemtUpdatedOnServer(payment_id,order_id,status) {
	$.ajax({
 url:'/user/payment_updated',
 data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:status}),
 type:"POST",
 contentType:'application/json',
 dataType:'json',
 success:function(response){
	swal({
		title: "Payment Process",
		text: "congrats !! payment successfull !!",
		icon: "success",
		dangerMode: true,
	});
	 },
 error:function(error){
		 swal({
		title: "Payment Process",
		text: "Internal Server Error !! payment successfull !! but did't catch in our server we will contact you in next 24hrs!!",
		icon: "warning",
		dangerMode: true,
	});
	 }
});
}
