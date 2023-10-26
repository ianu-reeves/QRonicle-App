package com.qronicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.qronicle.enums.PrivacyStatus;
import com.qronicle.enums.UserType;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private long id;

    @Column
    private String username;

    @Column
    @JsonIgnore
    String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "privacy_status")
    private PrivacyStatus privacyStatus;

    @Column(name = "signup_date", updatable = false)
    private LocalDate signupDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    public User() {
        this.signupDate = LocalDate.now();
    }

    public User(String username, String password, String firstName, String lastName, String email, UserType userType, PrivacyStatus privacyStatus) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userType = userType;
        this.privacyStatus = privacyStatus;
        this.signupDate = LocalDate.now();
    }

    public User(long id, String username, String firstName, String lastName, String email, String bio, UserType userType, PrivacyStatus privacyStatus) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.bio = bio;
        this.userType = userType;
        this.privacyStatus = privacyStatus;
        this.signupDate = LocalDate.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public PrivacyStatus getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(PrivacyStatus privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public LocalDate getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDate signupDate) {
        this.signupDate = signupDate;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && firstName.equals(user.firstName) && lastName.equals(user.lastName) && email.equals(user.email) && Objects.equals(bio, user.bio) && privacyStatus == user.privacyStatus && Objects.equals(items, user.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, bio, privacyStatus, items);
    }
}
