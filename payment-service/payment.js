const ZB = require('zeebe-node')

;
(async () => {
    const zbc = new ZB.ZBClient(process.env.ZEEBE_SERVICE_ENDPOINT);
    zbc.createWorker('test-worker', 'make-payment', makePayment);
    zbc.createWorker('test-worker', 'cancel-payment', cancelPayment);
})()


function makePayment(job, complete) {
    console.log('Inside make payment.');
    let params;
    var random = Math.random() >= 0.5;

    // Simulate successful payment
    if (random) {
        console.log('Payment successful.')
        params = updatePaymentStatus(job, true);
    }
    // Simulate payment exception
    else {
        console.log('Payment declined.');
        params = updatePaymentStatus(job, false);
    }
    complete.success(params);
}

function updatePaymentStatus(job, status) {
    return Object.assign({}, job.variables, {
        paymentsuccess: status
    })
}

function cancelPayment(job, complete) {
    console.log('Inside cancel payment. Placeholder for cleanup.');
    complete.success();
}
