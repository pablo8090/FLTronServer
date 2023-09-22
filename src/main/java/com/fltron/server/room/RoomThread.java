package com.fltron.server.room;

import java.util.HashMap;
import java.util.List;

import com.fltron.server.entities.Room;

import lombok.Getter;

@Getter
public class RoomThread extends Thread {
	
	private Room room;
	private HashMap<String, Boolean> usersReady = new HashMap<>();
	private boolean started;
	
	public RoomThread(Room newRoom) {
		super();
		this.room = newRoom;
	}
	
	private RoomThread () {
		
	}

	//@Override
	public void run() {
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (true) {
        	List<String> players = room.getPlayers();
            System.out.println(this.getName() + "Room( " + room.getSize() + ") for " + players.toString());
            try {
                //Wait for one sec so it doesn't print too fast
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void userReady (String userName) {
		
	}
}
