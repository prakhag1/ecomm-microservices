const ZB = require('zeebe-node')

;
(async () => {
    const zbc = new ZB.ZBClient(process.env.ZEEBE_SERVICE_ENDPOINT);
    zbc.createWorker('test-worker', 'make-payment', makePayment);
    zbc.createWorker('test-worker', 'cancel-payment', cancelPayment);
})()


function makePayment(job, complete) {
    console.log('Inside make payment');
    let params;

    // Simulate payment exception
    console.log('Payment declined');
    params = updatePaymentStatus(job, false);
    
    complete.success(params);
}

function updatePaymentStatus(job, status) {
    return Object.assign({}, job.variables, {
        paymentsuccess: status
    })
}

function cancelPayment(job, complete) {
    console.log('Inside cancel payment. Placeholder for payment canceled cleanup.');
    complete.success();
}
