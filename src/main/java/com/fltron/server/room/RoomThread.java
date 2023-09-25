package com.fltron.server.room;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fltron.server.dto.CoordsDTO;
import com.fltron.server.dto.MoveDTO;
import com.fltron.server.entities.Room;
import com.fltron.server.entities.User;
import com.fltron.server.utils.GameConstants;
import com.fltron.server.utils.GenericUtils;

import lombok.Getter;

@Getter
public class RoomThread extends Thread {
	
	private Room room;
	private HashMap<Long, Boolean> usersReady = new HashMap<>();
	private HashMap<Long, ArrayList<MoveDTO>> usersMoves = new HashMap<>();
	private HashMap<Long, CoordsDTO> usersCoords = new HashMap<>();
	private HashMap<Long, Short> usersStatus = new HashMap<>(); 
	private HashMap<Long, Color> usersColors = new HashMap<>(); 
	private HashMap<Long, ArrayList<MoveDTO>> usersQueueMoves = new HashMap<>();
	
	private boolean started;
	private boolean finished;
	private boolean running = true;
	
	public RoomThread(Room newRoom) {
		super();
		this.room = newRoom;
		List<User> players = this.room.getPlayers();
		int i = 0;
		for (User player : players) {
			// Init player
			usersReady.put(player.getId(), false);			
			usersStatus.put(player.getId(), GameConstants.PLAYER_STATUS_ALIVE);			
			short initX = GameConstants.PLAYER_INIT_COORDS[i][0];
			short initY = GameConstants.PLAYER_INIT_COORDS[i][1];
			CoordsDTO initialCoords = new CoordsDTO(initX,initY);
			MoveDTO initialDirectionMove = new MoveDTO(GameConstants.PLAYER_INIT_MOVE[i], new CoordsDTO(initX,initY));			
			usersColors.put(player.getId(), GameConstants.PLAYER_COLOR[i]);
			usersCoords.put(player.getId(), initialCoords);
			
			// Add initial direction
			ArrayList<MoveDTO> initialPlayerMoves = new ArrayList<>();
			initialPlayerMoves.add(initialDirectionMove);
			usersMoves.put(player.getId(), initialPlayerMoves);
			usersQueueMoves.put(player.getId(), new ArrayList<>());
			i++;
		}
	}
	
	private RoomThread () {
		
	}

	//@Override
	public void run() {
		
		long lastTime = System.nanoTime();
		double amountOfTicks = 64.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1)
			{
				tick();
				delta--;
			}
			if (running)
			{
				render();
			}
			frames++;
			if (System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;

			}
			
			
			
			// Debug
			List<User> players = room.getPlayers();
        	String status = this.started ? " STARTED " : " PENDING ";
           // System.out.println(this.getName() + "Room " + room.getId() + " ( " + room.getSize() + ") for " + players.toString() + status);
//            try {
//                //Wait for one sec so it doesn't print too fast
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
        
    }
	
	private void tick() {
		
		if (started) {
			predictFrontalCollide();
			movePlayers();
			checkCollides();
			if(checkWin()) {
				started = false;
				finished = true;
			};
		} else {
			Set<Long> keys = usersStatus.keySet();
			for (Long key : new ArrayList<Long>(keys)) {
				System.out.println("Player " + (key % 2 == 1 ? " Red " :  " Black ") 
						+ " status: " + (usersStatus.get(key) == 0 ? " Alive" : " Dead")
						+ " moves : " + (usersMoves.get(key).toString())
						+ " current: " + (usersCoords.get(key).toString()));
			}
		}
		
	}
	

	

	private void movePlayers() {
		List<User> players = getRoomPlayers();
		
		for (User player : players) {
			if (usersStatus.get(player.getId()).equals(GameConstants.PLAYER_STATUS_DEAD)) {
				continue;
			}
			CoordsDTO currentPosition = usersCoords.get(player.getId());
			List<MoveDTO> movements = usersMoves.get(player.getId());
			movePlayerQueue(player.getId(), currentPosition, movements);
			
			MoveDTO lastMovement = movements.get(movements.size()-1);
			short currentDirection = lastMovement.getMoveDirection();
			incrementCoordsByDirection(currentPosition, currentDirection);
		}
		
	}

	private void movePlayerQueue(Long playerId, CoordsDTO currentPosition, List<MoveDTO> movements) {
		List<MoveDTO> queueMoves = usersQueueMoves.get(playerId);
		if (queueMoves.isEmpty()) {
			return;
		}
		
		MoveDTO lastMovement = movements.get(movements.size()-1);
		CoordsDTO lastMoveCoords = lastMovement.getCoords();
		boolean isHoritzontal = lastMovement.getMoveDirection() == GameConstants.MOVE_LEFT 
				|| lastMovement.getMoveDirection() == GameConstants.MOVE_RIGHT;
		
		if (isHoritzontal && Math.abs(lastMoveCoords.getX()-currentPosition.getX()) < GameConstants.PLAYER_SIZE) {
			return;
		}
		
		if (!isHoritzontal && Math.abs(lastMoveCoords.getY()-currentPosition.getY()) < GameConstants.PLAYER_SIZE) {
			return;
		} 
		
		MoveDTO queueMove = queueMoves.get(0);
		if (isHoritzontal && currentPosition.getX() % 4 != 0) {
			return;
		} else if (!isHoritzontal && currentPosition.getY() % 4 != 0) {
			return;
		}		
		
		queueMove.getCoords().setX(currentPosition.getX());
		queueMove.getCoords().setY(currentPosition.getY());		
		usersMoves.get(playerId).add(queueMove);
		queueMoves.remove(0);
		
	}

	private void incrementCoordsByDirection(CoordsDTO currentPosition, short currentDirection) {
		switch (currentDirection) {
			case GameConstants.MOVE_LEFT:
				currentPosition.setX((short)(currentPosition.getX()-GameConstants.MOVEMENT_SPEED));
				break;
			case GameConstants.MOVE_UP:
				currentPosition.setY((short)(currentPosition.getY()-GameConstants.MOVEMENT_SPEED));
				break;
			case GameConstants.MOVE_RIGHT:
				currentPosition.setX((short)(currentPosition.getX()+GameConstants.MOVEMENT_SPEED));
				break;
			case GameConstants.MOVE_DOWN:
				currentPosition.setY((short)(currentPosition.getY()+GameConstants.MOVEMENT_SPEED));
				break;
		}
		
	}

	private void render()
	{
		//this.window.render(usersCoords.entrySet());
	}
	
	public void userReady (Long userId) {
		usersReady.put(userId, true);
		
		Set<Boolean> values = new HashSet<Boolean>(usersReady.values());
		boolean allPlayersReady = values.size() == 1 
				&& Boolean.TRUE.equals((Boolean)values.toArray()[0]) 
				&& usersReady.size() == this.room.getPlayers().size();
		if (allPlayersReady) {
			this.started = true;
		}
	}

	public void userMove(Long userId, short sMove) {
		if (!started || finished) {
			return;
		}
		
		if (!isPlayerAlive(userId)) {
			return;
		}
		
		MoveDTO lastPlayerMove = usersMoves.get(userId).get(usersMoves.get(userId).size()-1);
		if (!GenericUtils.areMovesValid(lastPlayerMove.getMoveDirection(), sMove)) {
			return;
		}
		
		
		MoveDTO move = new MoveDTO(sMove, GenericUtils.copyCoords(usersCoords.get(userId)));
		usersQueueMoves.get(userId).add(move);
	}

	private boolean isPlayerAlive(Long userId) {
		return usersStatus.get(userId) == GameConstants.PLAYER_STATUS_ALIVE;
	}

	private void predictFrontalCollide() {

		for (User player : getRoomPlayers()) {
			if (usersStatus.get(player.getId()).equals(GameConstants.PLAYER_STATUS_DEAD)) {
				continue;
			}
			List<User> playersToKill = new ArrayList<>();		
			User frontalCollide = checkFrontalCollide(player, usersCoords.get(player.getId()));
			if (frontalCollide != null) {
				System.out.println("***FRONTAL COLLIDE");
				playersToKill.add(player);
				playersToKill.add(frontalCollide);
				for (User playerToKill : playersToKill) {
					usersStatus.put(playerToKill.getId(), GameConstants.PLAYER_STATUS_DEAD);
				}
			}
		}		
	}
	
	private User checkFrontalCollide(User player, CoordsDTO coords) {
		for (User roomPlayer : getRoomPlayers()) {
			if (roomPlayer.getId().equals(player.getId())) {
				continue;
			}
			
			MoveDTO lastPlayerMove = usersMoves.get(player.getId()).get(usersMoves.get(player.getId()).size()-1);
			MoveDTO lastOtherMove = usersMoves.get(roomPlayer.getId()).get(usersMoves.get(roomPlayer.getId()).size()-1);
			
			if (!GenericUtils.areMovesOpposite(lastPlayerMove.getMoveDirection(), lastOtherMove.getMoveDirection())) {
				return null;
			}

			boolean isHoritzontal = lastPlayerMove.getMoveDirection() == GameConstants.MOVE_LEFT 
					|| lastPlayerMove.getMoveDirection() == GameConstants.MOVE_RIGHT;
			
			CoordsDTO otherCoords = usersCoords.get(roomPlayer.getId());
			
			if (isHoritzontal && 
					Math.abs(coords.getX() - otherCoords.getX())-(GameConstants.MOVEMENT_SPEED*2) <= 0) {				
				return roomPlayer;				
			} else if (!isHoritzontal && Math.abs(coords.getY() - otherCoords.getY())-(GameConstants.MOVEMENT_SPEED*2) <= 0) {
				return roomPlayer;
			}
			
		}
		return null;
	}

	private void checkCollides() {
		List<User> playersToKill = new ArrayList<>();		
		
		for (User player : getRoomPlayers()) {
			if (usersStatus.get(player.getId()).equals(GameConstants.PLAYER_STATUS_DEAD)) {
				continue;
			}
			
			User userToKill = checkCornerCollide(player);
			if (userToKill != null) {
				playersToKill.add(userToKill);
				playersToKill.add(player);
				continue;
			}
			
			
			boolean offSideCollide = checkOffsideCollide(usersCoords.get(player.getId()));
			if (offSideCollide) {
				playersToKill.add(player);
				continue;
			}
			
			boolean collide = checkCollide(player, usersCoords.get(player.getId()));
			if (collide) {
				playersToKill.add(player);
			}
		}		
		
		for (User player : playersToKill) {
			usersStatus.put(player.getId(), GameConstants.PLAYER_STATUS_DEAD);
		}
	}
	
	private User checkCornerCollide(User player) {
		User kill = null;
		CoordsDTO playerCoords = usersCoords.get(player.getId());
		for (User roomPlayer : getRoomPlayers()) {
			if (roomPlayer.getId().equals(player.getId())) {
				continue;
			}
			
			CoordsDTO otherPlayerCoords = usersCoords.get(roomPlayer.getId());
			if (playerCoords.getX() == otherPlayerCoords.getX() && playerCoords.getY() == otherPlayerCoords.getY()) {
				kill = roomPlayer;
			}
		}
		
		return kill;
	}

	private boolean checkWin() {
		ArrayList<Short> allStatus = new ArrayList<>(usersStatus.values());;
		short aliveCount = 0;
		for (Short status : allStatus) {
			if (status.equals( GameConstants.PLAYER_STATUS_ALIVE)) {
				aliveCount++;
			}			
		}
		if (aliveCount == 1 || aliveCount == 0) {
			return true;
		}
		return false;		
	}

	private boolean checkOffsideCollide(CoordsDTO coords) {
		if (coords.getX() <= 0 || coords.getX() >= GameConstants.MAP_WIDTH) {
			return true;
		}
		
		if (coords.getY() <= 0 || coords.getY() >= GameConstants.MAP_HEIGHT) {
			return true;
		}
		return false;
	}

	private boolean checkCollide(User player, CoordsDTO coords) {
		boolean collide = false;
		for (User roomPlayer : getRoomPlayers()) {
			boolean itsMySelf = false;
			if (roomPlayer.getId().equals(player.getId())) {
				itsMySelf = true;
			}
			
			List<MoveDTO> moves = usersMoves.get(roomPlayer.getId());
			int i = 0;
			for (MoveDTO move : moves) {
				CoordsDTO nextMoveCoords = null;
				if (itsMySelf && (i+2) > (moves.size()-1)) {
					continue;
				} else if (moves.size()-1 == i) {					
					// last move
					nextMoveCoords = usersCoords.get(roomPlayer.getId());
				} else {
					MoveDTO nextMove = moves.get(i+1);
					nextMoveCoords = nextMove.getCoords();
				}
				
				CoordsDTO nmBigCoords = GenericUtils.getBiggerCoords(move.getCoords(), nextMoveCoords);
				CoordsDTO nmMinorCoords = GenericUtils.getMinorCoords(move.getCoords(), nextMoveCoords);
				
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
	
	private List<User> getRoomPlayers() {
		return this.getRoom().getPlayers();
	}
}
