package com.example.application.data.service;


import com.example.application.data.entity.SampleTest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SampleTestRepository extends MongoRepository<SampleTest, String> {

}