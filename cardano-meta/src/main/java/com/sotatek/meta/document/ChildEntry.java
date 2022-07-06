package com.sotatek.meta.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildEntry {

    private Integer sequenceNumber;
    private String value;
    private List<Signature> signatures;

    @Override
    public String toString() {
        return "ChildEntry{" +
                "sequenceNumber=" + sequenceNumber +
                ", value='" + value + '\'' +
                ", signatures=" + signatures +
                '}';
    }
}
