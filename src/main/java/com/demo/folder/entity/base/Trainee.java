package com.demo.folder.entity.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.util.List;
@Entity
@Table(name = "trainees")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Date_Of_Birth")
    private LocalDate dateOfBirth;
    @Column(name = "Address")
    private String address;

    @OneToMany(mappedBy = """
      trainee""", fetch = FetchType.EAGER)
    private List<Training> trainings;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "User_Id", referencedColumnName = "id")
    private User user;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers;


    public Trainee() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<Training> getTrainings() {
        return trainings;
    }
    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }
    public List<Trainer> getTrainers() {
        return trainers;
    }
    public void setTrainers(List<Trainer> trainers) {
        this.trainers = trainers;
    }

}