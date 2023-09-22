package com.fltron.server.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Room {
	List<String> players = new ArrayList<>();
	Integer size = 2;
}
