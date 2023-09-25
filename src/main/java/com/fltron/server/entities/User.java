package com.fltron.server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name="user",schema="fltron")
public class User {
	
	@Id
	//@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	Long id;
	
	@Column(name="username")
	String username;
	
	@Column(name="email")
	String email;
	
	@Column(name="password")
	String password;
	
	@Override
	public String toString() {
		return this.username;
	}
}
