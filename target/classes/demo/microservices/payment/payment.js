const ZB = require('zeebe-node')
 
;(async () => {
    const zbc = new ZB.ZBClient(process.env.ZEEBE_SERVICE_ENDPOINT);
    const zbWorker = zbc.createWorker('test-worker', 'make-payment', makePayment);
    const zbWorker1 = zbc.createWorker('test-worker', 'cancel-payment', cancelPayment);
})()
 
function makePayment(job, complete) {
    console.log('Inside make payment', job.variables);
    let updatedVariables;
    
    var random = Math.random() >= 0.5;
    if(random) {
    	updatedVariables = Object.assign({}, job.variables, {
    		paymentsuccess: true
    	})
    }else {
    	updatedVariables = Object.assign({}, job.variables, {
    		paymentsuccess: false
    	})
    }
  
    complete.success(updatedVariables);
}

function cancelPayment(job, complete) {
    console.log('Inside cancel payment', job.variables);
    complete.success();
}
