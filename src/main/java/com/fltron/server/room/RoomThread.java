package com.fltron.server.room;

import com.fltron.server.entities.Room;

import lombok.Getter;

@Getter
public class RoomThread extends Thread {
	
	private Room room;
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
            System.out.println(this.getName() + "Room for " + room.getPlayer1() + " and " + room.getPlayer2());
            try {
                //Wait for one sec so it doesn't print too fast
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
