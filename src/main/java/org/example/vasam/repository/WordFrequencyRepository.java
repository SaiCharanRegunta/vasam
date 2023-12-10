package org.example.vasam.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.example.vasam.model.WordFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WordFrequencyRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    @Autowired
    private AmazonDynamoDB dynamoDB;

    public WordFrequency save(WordFrequency wordFrequency) {
        dynamoDBMapper.save(wordFrequency);
        return wordFrequency;
    }

    public WordFrequency getWordFrequencyByUserIdAndWord(String userId, String word) {
        return dynamoDBMapper.load(WordFrequency.class, userId, word);
    }

    public void incrementFrequencyCounterByUserIdAndPhrase(String userId, String word) {
        String tableName = "WordFrequency";

        String updateExpression = "SET #counter = #counter + :increment";
        String counterAttribute = "frequency"; // Adjust to your attribute name


        Map<String, AttributeValue> expressionAttributeValues = new java.util.HashMap<>();
        expressionAttributeValues.put(":increment", new AttributeValue().withN("1"));

        Map<String, String> expressionAttributeNames = new java.util.HashMap<>();
        expressionAttributeNames.put("#counter", counterAttribute);

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(Map.of("userId", new AttributeValue().withS(userId),
                        "word", new AttributeValue().withS(word)))
                .withUpdateExpression(updateExpression)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withExpressionAttributeNames(expressionAttributeNames);
        dynamoDB.updateItem(updateItemRequest);
    }

    public List<WordFrequency> getAllWordsByUserId(String userId) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":userId", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<WordFrequency> queryExpression = new DynamoDBQueryExpression<WordFrequency>()
                .withConsistentRead(false)
                .withKeyConditionExpression("userId = :userId")
                .withExpressionAttributeValues(attributeValues);
        return dynamoDBMapper.query(WordFrequency.class, queryExpression);
    }

    public List<WordFrequency> getMostFrequentWordsByUserId(String userId) {
        DynamoDBQueryExpression<WordFrequency> queryRequest = new DynamoDBQueryExpression<WordFrequency>()
                .withIndexName("userId-frequency-index")
                .withKeyConditionExpression("userId = :v_title")
                .withExpressionAttributeValues(
                        new HashMap<String, AttributeValue>() {
                            {
                                put(":v_title", new AttributeValue().withS(userId));
                            }
                        }
                )
                .withProjectionExpression("userId, word, frequency")
                .withScanIndexForward(false)
                .withConsistentRead(false)
                .withLimit(10);
        return dynamoDBMapper.query(WordFrequency.class, queryRequest);
    }

    public List<WordFrequency> getWordFrequenciesByWord(String word) {
        Map<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":word", new AttributeValue().withS(word));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("word = :word")
                .withExpressionAttributeValues(attributeValues);

        return dynamoDBMapper.scan(WordFrequency.class, scanExpression);
    }


}
