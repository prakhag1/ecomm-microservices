const ZB = require('zeebe-node')
var redis = require('redis');
var redisClient = redis.createClient({
    port      : process.env.REDIS_PORT,               
    host      : process.env.REDIS_ENDPOINT
});

;(async () => {
    const zbc = new ZB.ZBClient(process.env.ZEEBE_SERVICE_ENDPOINT);
    zbc.createWorker('test-worker', 'make-payment', makePayment);
    zbc.createWorker('test-worker', 'cancel-payment', cancelPayment);
    redisClient.on("error", function(error) {
  	  console.error(error);
    });
})()

process.on('uncaughtException', function (err) {
	  console.log('Caught exception: ', err);
});

function makePayment(job, complete) {
    console.log('Inside make payment');
    let params;    
    var random = Math.random() >= 0.5;
    
    // Check duplicate requests
    redisClient.get(job.key, function(error, result) {
    	if(result) {
    		console.log('Duplicate request. Payment status from earlier run: ', result);
    		params = updatePaymentStatus(job, JSON.parse(result));
    	} else {
	    	// Simulate successful payment
	        if(random) {
	        	console.log('Payment successful')
	        	params = updatePaymentStatus(job, true);
	        }
	        // Simulate errorenous payment
	        else {
	        	console.log('Payment declined');
	        	params = updatePaymentStatus(job, false);
	        }
	        // Simulate service exceptions, e.g.: timeout or network failure
	        if(!random) {
	        	console.log('Service exception');
	        	throw new Error('Service exception');
	        }
    	}
        complete.success(params);
    });
}

function updatePaymentStatus(job, status) {
	redisClient.set(job.key, status);
	return Object.assign({}, job.variables, {
		paymentsuccess: status
	})
}

function cancelPayment(job, complete) {
    console.log('Inside cancel payment');
    complete.success();
}
