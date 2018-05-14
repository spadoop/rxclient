const AWS = require('aws-sdk');
const sns = new AWS.SNS();

const publishToTopic = (message, topicArn) => {
    const params = {
        Message: message,
        TopicArn: topicArn
    };
    sns.publish(params, (err, data) => {
        if (err) {
            console.log(err);
        } else {
            console.log(data);
        }
    });
};

exports.handler = async (event) => {
    const recordsArr = event.preScreeningRecords;
    const numberOfRecords = recordsArr.length;
    const batchSize = process.env.transformBatchSize;
    const concurrentNumber = Math.floor(numberOfRecords / batchSize);
    
    if (concurrentNumber) {
        for (let idx = 1; idx <= concurrentNumber; idx++) {
            const sequenceId = idx;
            const numberOfRecordsRemaining = numberOfRecords - idx * batchSize;
            let recordsToPass = null;
            if (idx === concurrentNumber) {
                recordsToPass = recordsArr.slice(numberOfRecordsRemaining, numberOfRecords);
            } else {
                recordsToPass = recordsArr.slice(numberOfRecordsRemaining, (numberOfRecordsRemaining + batchSize));
            }
            publishToTopic(JSON.stringify({
                records: recordsToPass
            }), process.env.topicArn);
        }
    } else {
        publishToTopic(JSON.stringify({
            records: recordsArr
        }), process.env.topicArn);
    }
    return 'done';
};
