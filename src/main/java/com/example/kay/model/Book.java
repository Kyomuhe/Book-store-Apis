package com.example.kay.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book {

    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //tells the database to automatically generate the unique id
    private Long id; //this stores the unique identifier

    @Column(nullable = false)
    private String title;

    @Column()
    private String author;

    @Column(precision = 10, scale = 2) // you can adjust precision/scale as needed
    private BigDecimal price;


    @Column()
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    public Book(){}

    public Book(String title, String author, BigDecimal price, String description, Integer quantity) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }
    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getAuthor(){
        return author;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public BigDecimal getPrice(){
        return price;
    }
    public void setPrice(BigDecimal price){
        this.price = price;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public Integer getQuantity(){
        return quantity;
    }
    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}