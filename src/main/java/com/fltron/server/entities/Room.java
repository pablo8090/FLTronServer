package com.fltron.server.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Room {
	Long id;
	List<User> players = new ArrayList<>();
	Integer size = 2;
}
