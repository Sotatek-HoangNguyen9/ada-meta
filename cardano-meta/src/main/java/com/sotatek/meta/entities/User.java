package com.sotatek.meta.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "name")
    private String name;
    @Column(name = "password")
    private String password;
    @Column(name = "permission")
    private String permission;
    @Column(name = "phone_number")
    private String phone_number;
    @Column(name = "create_at")
    private String create_at;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "featureGroup")
    private Integer featureGroup;
//    private MapFeature mapFeature;

    public User() {
    }

    public User(String username, String name, String permission, Boolean active) {
        this.username = username;
        this.name = name;
        this.permission = permission;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Integer getFeatureGroup() {
        return featureGroup;
    }

    public void setFeatureGroup(Integer featureGroup) {
        this.featureGroup = featureGroup;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonIgnore
    @JsonProperty(value = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    @ManyToOne
//    @JoinColumn(name = "mapFeature_id")
//    public MapFeature getMapFeature() {
//        return mapFeature;
//    }
//
//    public void setMapFeature(MapFeature mapFeature) {
//        this.mapFeature = mapFeature;
//    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%s, username='%s', name='%s', permission='%s' , active='%s']",
                id, username, name, permission, active);
    }
}
