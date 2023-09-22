package com.fltron.server.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fltron.server.entities.Coords;
import com.fltron.server.entities.Move;
import com.fltron.server.entities.Room;
import com.fltron.server.utils.GameConstants;
import com.fltron.server.utils.GenericUtils;

import lombok.Getter;

@Getter
public class RoomThread extends Thread {
	
	private Room room;
	private HashMap<String, Boolean> usersReady = new HashMap<>();
	private HashMap<String, ArrayList<Move>> usersMoves = new HashMap<>();
	private HashMap<String, Coords> usersCoords = new HashMap<>(); // TO-DO fill it
	private HashMap<String, Short> usersStatus = new HashMap<>(); // TO-DO fill it
	private boolean started;
	
	public RoomThread(Room newRoom) {
		super();
		this.room = newRoom;
		List<String> players = this.room.getPlayers();
		for (String player : players) {
			usersReady.put(player, false);
			usersMoves.put(player, new ArrayList<>());
			usersStatus.put(player, GameConstants.PLAYER_STATUS_ALIVE);
		}
				
		
	}
	
	private RoomThread () {
		
	}

	//@Override
	public void run() {
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (true) {
        	List<String> players = room.getPlayers();
        	String status = this.started ? " STARTED " : " PENDING ";
            System.out.println(this.getName() + "Room " + room.getId() + " ( " + room.getSize() + ") for " + players.toString() + status);
            try {
                //Wait for one sec so it doesn't print too fast
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void userReady (String userName) {
		usersReady.put(userName, true);
		
		Set<Boolean> values = new HashSet<Boolean>(usersReady.values());
		boolean allPlayersReady = values.size() == 1 
				&& Boolean.TRUE.equals((Boolean)values.toArray()[0]) 
				&& usersReady.size() == this.room.getPlayers().size();
		if (allPlayersReady) {
			this.started = true;
		}
	}

	public void userMove(String userName, short sMove, Coords coord) {
		if (!started) {
			return;
		}
		
		if (!isPlayerAlive(userName)) {
			return;
		}
		
		//TO-DO check coords are valid
		Move move = new Move(sMove, coord);
		usersMoves.get(userName).add(move);
		
		
	}

	private boolean isPlayerAlive(String userName) {
		return usersStatus.get(userName) == GameConstants.PLAYER_STATUS_ALIVE;
	}

	// TO-DO use in routine
	private void checkCollides() {
		List<String> playersToKill = new ArrayList<>();
		
		
		
		for (String player : getRoomPlayers()) {
			boolean offSideCollide = checkOffsideCollide(usersCoords.get(player));
			if (offSideCollide) {
				playersToKill.add(player);
				continue;
			}
			
			boolean collide = checkCollide(player, usersCoords.get(player));
			if (collide) {
				playersToKill.add(player);
			}
		}		
		for (String player : playersToKill) {
			usersStatus.put(player, GameConstants.PLAYER_STATUS_DEAD);
			checkWin();
		}
	}
	// TO-DO put in rutine
	private boolean checkWin() {
		ArrayList<Short> allStatus = (ArrayList<Short>) usersStatus.values();
		short aliveCount = 0;
		for (Short status : allStatus) {
			if (status.equals( GameConstants.PLAYER_STATUS_ALIVE)) {
				aliveCount++;
			}			
		}
		if (aliveCount == 1) {
			return true;
		}
		return false;		
	}

	private boolean checkOffsideCollide(Coords coords) {
		if (coords.getX() <= 0 || coords.getX() >= GameConstants.MAP_WIDTH) {
			return true;
		}
		
		if (coords.getY() <= 0 || coords.getY() >= GameConstants.MAP_HEIGHT) {
			return true;
		}
		return false;
	}

	private boolean checkCollide(String playerCheck, Coords coords) {
		boolean collide = false;
		for (String player : getRoomPlayers()) {
			if (player.equals(playerCheck)) {
				continue;
			}
			
			List<Move> moves = usersMoves.get(player);
			int i = 0;
			for (Move move : moves) {
				if (moves.size()-1 == i) {
					continue; // last move
				}
				Move nextMove = moves.get(i);
				Coords nmBigCoords = GenericUtils.getBiggerCoords(move.getCoords(), nextMove.getCoords());
				Coords nmMinorCoords = GenericUtils.getMinorCoords(move.getCoords(), nextMove.getCoords());
				
				int playerX = coords.getX();
				int playerY = coords.getY();
				
				if (playerX == nmBigCoords.getX() && playerX == nmMinorCoords.getX() 
						&& playerY <= nmBigCoords.getY() && playerY >= nmMinorCoords.getY()) {
					return true;
				} else if (playerY == nmBigCoords.getY() && playerY == nmMinorCoords.getY() 
						&& playerX <= nmBigCoords.getX() && playerX >= nmMinorCoords.getX()) {
					return true;
				}
				i++;
			}
			
		}
		return collide;
	}
	
	private List<String> getRoomPlayers() {
		return this.getRoom().getPlayers();
	}
}
