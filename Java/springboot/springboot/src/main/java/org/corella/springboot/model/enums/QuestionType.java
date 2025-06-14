package org.corella.springboot.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {
    MULTIPLEOPTION("MO", "Opción Múltiple"),
    BOOLEAN("BOOL", "Verdadero/Falso"),
    LONGANSWER("LA", "Respuesta Larga"),
    SHORTANSWER("SA", "Respuesta Corta");

    @JsonProperty("code")
    private final String code;
    private final String description;

    @JsonCreator
    public static QuestionType fromCode(@JsonProperty("code") String code) {
        if (code == null) {
            return null;
        }

        for (QuestionType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo inválido de código: " + code);
    }


    @JsonValue
    public String toValue() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }
}
