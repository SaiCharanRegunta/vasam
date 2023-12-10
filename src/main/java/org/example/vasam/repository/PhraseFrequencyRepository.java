package org.example.vasam.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.example.vasam.model.PhraseFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PhraseFrequencyRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private AmazonDynamoDB dynamoDB;

    public PhraseFrequency save(PhraseFrequency phraseFrequency) {
        dynamoDBMapper.save(phraseFrequency);
        return phraseFrequency;
    }

    public PhraseFrequency getPhraseFrequencyByUserIdAndPhrase(String userId, String phrase) {
        return dynamoDBMapper.load(PhraseFrequency.class, userId, phrase);
    }

    public void incrementFrequencyCounterByUserIdAndPhrase(String userId, String phrase) {
        String tableName = "PhraseFrequency";

        String updateExpression = "SET #counter = #counter + :increment";
        String counterAttribute = "frequency"; // Adjust to your attribute name


        Map<String, AttributeValue> expressionAttributeValues = new java.util.HashMap<>();
        expressionAttributeValues.put(":increment", new AttributeValue().withN("1"));

        Map<String, String> expressionAttributeNames = new java.util.HashMap<>();
        expressionAttributeNames.put("#counter", counterAttribute);

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(Map.of("userId", new AttributeValue().withS(userId),
                        "phrase", new AttributeValue().withS(phrase)))
                .withUpdateExpression(updateExpression)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withExpressionAttributeNames(expressionAttributeNames);
        dynamoDB.updateItem(updateItemRequest);
    }

    public List<PhraseFrequency> getAllPhrasesByUserId(String userId) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":userId", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<PhraseFrequency> queryExpression = new DynamoDBQueryExpression<PhraseFrequency>()
                .withConsistentRead(false)
                .withKeyConditionExpression("userId = :userId")
                .withExpressionAttributeValues(attributeValues);
        return dynamoDBMapper.query(PhraseFrequency.class, queryExpression);
    }

    public List<PhraseFrequency> getMostFrequentPhraseByUserId(String userId) {
        DynamoDBQueryExpression<PhraseFrequency> queryRequest = new DynamoDBQueryExpression<PhraseFrequency>()
                .withIndexName("userId-frequency-index")
                .withKeyConditionExpression("userId = :v_title")
                .withExpressionAttributeValues(
                        new HashMap<String, AttributeValue>() {{
                            put(":v_title", new AttributeValue().withS(userId));
                        }}
                )
                .withProjectionExpression("userId, phrase, frequency")
                .withScanIndexForward(false)
                .withConsistentRead(false)
                .withLimit(3);
        return dynamoDBMapper.query(PhraseFrequency.class, queryRequest);
    }
}
