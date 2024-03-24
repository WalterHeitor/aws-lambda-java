package br.com.softwalter.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class Pessoa implements Serializable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("ip_address")
    private String ipAddress;


    public Pessoa() {
    }

    public Pessoa(String id, String firstName, String lastName, String email, String gender, String ipAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.ipAddress = ipAddress;
    }

    // Getters e Setters omitidos para brevidade

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(id, pessoa.id) && Objects.equals(firstName, pessoa.firstName) && Objects.equals(lastName, pessoa.lastName) && Objects.equals(email, pessoa.email) && Objects.equals(gender, pessoa.gender) && Objects.equals(ipAddress, pessoa.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, gender, ipAddress);
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "id='" + id + '\'' +
                ", first_name='" + firstName + '\'' +
                ", last_name='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", ip_address='" + ipAddress + '\'' +
                '}';
    }
}
