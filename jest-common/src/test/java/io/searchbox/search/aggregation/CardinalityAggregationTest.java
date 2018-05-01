package io.searchbox.search.aggregation;

import com.google.gson.JsonObject;
import io.searchbox.core.search.aggregation.CardinalityAggregation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CardinalityAggregationTest {
    @Test
    public void givenMissingValueProperty_cardinalityAggregationConstructor_doesNotAssignToCardinalityField() {
        JsonObject cardinalityAggregationJson = new JsonObject();
        cardinalityAggregationJson.addProperty("notValueField", "hi");
        cardinalityAggregationJson.addProperty("anotherField", "hello");
        CardinalityAggregation cardinalityAggregation = new CardinalityAggregation("aggName", cardinalityAggregationJson);
        assertNull("Cardinality field should default to null since value is unassigned", cardinalityAggregation.getCardinality());
    }

    @Test
    public void givenNullValueProperty_cardinalityAggregationConstructor_doesNotAssignToCardinalityField() {
        JsonObject cardinalityAggregationJson = new JsonObject();
        cardinalityAggregationJson.addProperty("notValueField", "hi");
        cardinalityAggregationJson.addProperty("anotherField", "hello");
        String nullString = null;
        cardinalityAggregationJson.addProperty("value", nullString);
        CardinalityAggregation cardinalityAggregation = new CardinalityAggregation("aggName", cardinalityAggregationJson);
        assertNull("Cardinality field should default to null since value is null", cardinalityAggregation.getCardinality());
    }

    @Test
    public void givenNonNullValueProperty_cardinalityAggregationConstructor_assignsToCardinalityField() {
        JsonObject cardinalityAggregationJson = new JsonObject();
        cardinalityAggregationJson.addProperty("notValueField", "hi");
        cardinalityAggregationJson.addProperty("anotherField", "hello");
        Long setValue = 100L;
        cardinalityAggregationJson.addProperty("value", setValue);
        CardinalityAggregation cardinalityAggregation = new CardinalityAggregation("aggName", cardinalityAggregationJson);
        assertEquals(setValue, cardinalityAggregation.getCardinality());
    }
}
