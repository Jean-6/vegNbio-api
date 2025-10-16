package org.example.vegnbioapi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Document
public class Drink extends MenuItem {
    private String volume;
    private boolean isAlcoholic;
    private boolean isGaseous;
}