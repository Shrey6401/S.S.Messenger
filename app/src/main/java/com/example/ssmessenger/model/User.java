package com.example.ssmessenger.model;

public class User {
    String userName;
     String userEmail;
     String userId;
     String imageUrl;
     public User(){

     }

 public User(String userName, String userEmail, String userId, String imageUrl) {
  this.userName = userName;
  this.userEmail = userEmail;
  this.userId = userId;
  this.imageUrl = imageUrl;
 }

 public String getUserName() {
  return userName;
 }

 public String getUserEmail() {
  return userEmail;
 }

 public String getUserId() {
  return userId;
 }

 public String getImageUrl() {
  return imageUrl;
 }
}
