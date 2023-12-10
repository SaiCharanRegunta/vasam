package org.example.vasam.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.ScanResultPage;
import com.amazonaws.services.dynamodbv2.model.Select;
import org.example.vasam.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public void save(String userId) {
        dynamoDBMapper.save(User.builder().userId(userId).build());
    }

    public boolean doesUserExists(String userId) {
        return !ObjectUtils.isEmpty(dynamoDBMapper.load(User.class, userId));
    }

    public int getTotalUserCount() {
        // Set up a scan expression to count all items
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withSelect(Select.COUNT)
                .withConsistentRead(false);

        // Perform a scan operation on the DynamoDB table for WordFrequency
        ScanResultPage<User> scanResult = dynamoDBMapper.scanPage(User.class, scanExpression);

        // Return the total count of items in the table
        return scanResult.getScannedCount(); // or scanResult.getCount() depending on your DynamoDB version
    }


}
