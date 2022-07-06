package com.sotatek.meta.document;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Signature {
    private String signature;
    private String publicKey;

    @Override
    public String toString() {
        return "Signature{" +
                "signature='" + signature + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
