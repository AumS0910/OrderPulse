package org.orderpulse.orderpulsebackend.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Elasticsearch document representing a searchable order projection.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "orders")
public class OrderDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String customerName;

    @Field(type = FieldType.Text)
    private String customerEmail;

    @Field(type = FieldType.Text)
    private String productDescription;

    @Field(type = FieldType.Keyword)
    private String productSku;

    @Field(type = FieldType.Integer)
    private Integer quantity;

    @Field(type = FieldType.Double)
    private BigDecimal totalPrice;

    @Field(type = FieldType.Keyword)
    private OrderStatus status;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction)
    private LocalDateTime createdAt;
}
