package com.sotatek.meta.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "meta_data")
public class MetaData {

    @Id
    private String subject;
    private String policy;

    private ChildEntry url;
    private ChildEntry name;
    private ChildEntry ticker;
    private ChildEntry decimals;
    private ChildEntry logo;
    private ChildEntry description;

    @Override
    public String toString() {
        return "MetaData{" +
                "subject='" + subject + '\'' +
                ", policy='" + policy + '\'' +
                ", url=" + url +
                ", name=" + name +
                ", ticker=" + ticker +
                ", decimals=" + decimals +
                ", logo=" + logo +
                ", description=" + description +
                '}';
    }
}
