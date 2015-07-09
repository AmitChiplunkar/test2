package com.example.chess;

import java.util.ArrayList;


public class Chess {
	private enum pieceType {
		pawn, bishop, knight, rook, queen, king
	};

	public enum objectColour {
		black, white
	};

	private enum pieceState {
		alive, dead
	};

	private enum gameState {
		allClear, whiteCheck, whiteMate, blackCheck, blackMate, stalemate
	};

	private enum moveStatus {
		success, fail, promote
	};
//Player Class Begins
	private class player {
		private objectColour colour;
		private piece[] pieces;
		
		public objectColour getColour() {
			return colour;
		}

		public piece[] getPieces() {
			return pieces;
		}

		public boolean move(piece piece, cell moveTo) throws Exception {
			if (piece == null || piece.getPieceColour() != this.colour || moveTo == null|| moveTo == deadCell)
				return false;
			
			ArrayList<cell> availableMoves = piece.getAvailableMoves();
			if (availableMoves.contains(moveTo)) {
				moveStatus status = piece.tryMove(moveTo);
				if (status != moveStatus.promote) {
					if (status == moveStatus.success) {
						return true;
					} else
						return false;
				} else {
					try {
						pieceType type = pieceType.queen;
						piece.setPieceType(type);
						if(piece.getImageResource()==R.drawable.wpawn)
							piece.imageResource=R.drawable.wqueen;
						else
							piece.imageResource=R.drawable.bqueen;
						return true;
					} catch (incompatiblePieceTypeConversionException ex) {
						throw new Exception("Exception");
					}
				}
			}
			return false;
		}

		player(objectColour colour, piece[] pieces) {
			this.colour = colour;
			this.pieces = pieces;
		}
	}
//Player Class Ends
	
//Cell Class Begins
	public class cell {
		private int x;
		private int y;
		private piece piece;

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
		public piece getPiece() {
			return piece;
		}

		public void setPiece(piece piece) {
			if (this.piece != null)
				this.piece.setPieceState(pieceState.dead);
			this.piece = piece;
		}

		cell(int x, int y) {
			this.x = x;
			this.y = y;
			this.piece = null;
		}
	}
//Cell class Ends
	
//incompatiblePieceTypeConversionException begins
	public class incompatiblePieceTypeConversionException extends Exception {
		public incompatiblePieceTypeConversionException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
	}
//incompatiblePieceTypeConversionException ends

//class piece begins 
	public class piece {

		private cell location;
		
		public cell getLocation() {
			return location;
		}

		private pieceState state;

		public pieceState getPieceState() {
			return state;
		}

		public void setPieceState(pieceState newState) {
			state = newState;
			if (newState == pieceState.dead) {
				location.piece  = null;
				location = deadCell;
			}
		}

		private int imageResource;

		public int getImageResource() {
			return imageResource;
		}

		protected objectColour colour;

		public objectColour getPieceColour() {
			return colour;
		}

		private pieceType type;

		public pieceType getPieceType() {
			return type;
		}

		public void setPieceType(pieceType newType)
				throws incompatiblePieceTypeConversionException {
			if (type != pieceType.pawn)
				throw new incompatiblePieceTypeConversionException(
						"Error: can only convert pawns");
			else {
				switch (newType) {
				case bishop:
					availMoves = new bishop();
					break;
				case knight:
					availMoves = new knight();
					break;
				case rook:
					availMoves = new rook();
					break;
				case queen:
					availMoves = new queen();
					break;
				case pawn:
				case king:
					throw new incompatiblePieceTypeConversionException(
							"Error: invalid conversion from pawn.");
				}

			}
		}


		public boolean isValidMove(cell moveTo) {
			
			if(moveTo.getPiece() != null)
				if(moveTo.getPiece().getPieceColour() == this.colour)
					return false;
			
			cell targetCell = board[moveTo.getX()][moveTo.getY()];
			piece oldPiece = targetCell.getPiece();
			cell sourceCell = this.getLocation();
			targetCell.setPiece(this);
			sourceCell.setPiece(null);
			this.location = targetCell;
			this.setPieceState(pieceState.alive);
			
			gameState tryState = checkForCheck(board, this.colour);
			
			sourceCell.setPiece(this);
			targetCell.setPiece(oldPiece);
			this.location = sourceCell;
			this.setPieceState(pieceState.alive);
			if(oldPiece!= null)
			{
				oldPiece.location = targetCell;
				oldPiece.setPieceState(pieceState.alive);
			}
			
			if ((colour == objectColour.white && tryState == gameState.whiteCheck)
					|| (colour == objectColour.black && tryState == gameState.blackCheck))
				return false;
			return true;
		}

		public moveStatus tryMove(cell moveTo) {

			if (!isValidMove(moveTo))
				return moveStatus.fail;
		
			this.location.setPiece(null);
			
			moveTo.setPiece(this);
			
			this.location = moveTo;

			if (type == pieceType.pawn) {
				return (location.y == 0 || location.y == 7) ? moveStatus.promote
						: moveStatus.success;
			}

			return moveStatus.success;
		}

		private availableMoves availMoves;

		public ArrayList<cell> getAvailableMoves() {
			return availMoves.getAvailableMoves(this);
		}

		piece(objectColour colour, pieceType type, cell location,
				availableMoves movementPattern, int imageResource) {
			this.colour = colour;
			this.type = type;
			this.location = location;
			this.availMoves = movementPattern;
			this.imageResource = imageResource;
		}

	}
//class piece ends

//interface availableMoves begins
	private interface availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece);
	}
//interface availableMoves Ends
	
//Pawn Begins
	private class pawn implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();

			if (piece.getPieceColour() == objectColour.white) {
				if (board[currX][currY - 1].getPiece() == null)
					retList.add(board[currX][currY - 1]);
				if (currX > 0 && board[currX - 1][currY - 1].getPiece() != null && board[currX - 1][currY - 1].getPiece().getPieceColour() == objectColour.black)
					retList.add(board[currX - 1][currY - 1]);
				if (currX < 7 && board[currX + 1][currY - 1].getPiece() != null && board[currX + 1][currY - 1].getPiece().getPieceColour() == objectColour.black)
					retList.add(board[currX + 1][currY - 1]);
				if (currY == 6 && board[currX][currY - 1].getPiece() == null && board[currX][currY - 2].getPiece() == null)
					retList.add(board[currX][currY - 2]);
			}
			if (piece.getPieceColour() == objectColour.black) {
				if (board[currX][currY + 1].getPiece() == null)
					retList.add(board[currX][currY + 1]);
				if (currX > 0 && board[currX - 1][currY + 1].getPiece() != null && board[currX - 1][currY + 1].getPiece().getPieceColour() == objectColour.white)
					retList.add(board[currX - 1][currY + 1]);
				if (currX < 7 && board[currX + 1][currY + 1].getPiece() != null && board[currX + 1][currY + 1].getPiece().getPieceColour() == objectColour.white)
					retList.add(board[currX + 1][currY + 1]);
				if (currY == 1 && board[currX][currY + 1].getPiece() == null && board[currX][currY + 2].getPiece() == null)
					retList.add(board[currX][currY + 2]);
			}
			return retList;
		}
	}
//Pawn Ends
	
//Bishop Begins
	private class bishop implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			int i = 1;
			while ((currX + i < 8 && currY + i < 8) && (board[currX + i][currY + i].getPiece() == null || board[currX + i][currY + i].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX + i][currY + i].getPiece() != null && board[currX + i][currY + i].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX + i][currY + i]);
					break;
				}
				retList.add(board[currX + i][currY + i]);
				i++;
			}

			i = 1;
			while ((currX + i < 8 && currY - i > -1) && (board[currX + i][currY - i].getPiece() == null || board[currX + i][currY - i].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX + i][currY - i].getPiece() != null && board[currX + i][currY - i].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX + i][currY - i]);
					break;
				}
				retList.add(board[currX + i][currY - i]);
				i++;
			}

			i = 1;
			while ((currX - i > -1 && currY + i < 8) && (board[currX - i][currY + i].getPiece() == null || board[currX - i][currY + i].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX - i][currY + i].getPiece() != null && board[currX - i][currY + i].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX - i][currY + i]);
					break;
				}
				retList.add(board[currX - i][currY + i]);
				i++;
			}

			i = 1;
			while ((currX - i > -1 && currY - i > -1) && (board[currX - i][currY - i].getPiece() == null || board[currX - i][currY - i].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX - i][currY - i].getPiece() != null && board[currX - i][currY - i].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX - i][currY - i]);
					break;
				}
				retList.add(board[currX - i][currY - i]);
				i++;
			}

			return retList;
		}
	}
//Bishop Ends
	
//Knight Begins
	private class knight implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();

			if (currX > 1 && currY > 0 && (board[currX - 2][currY - 1].getPiece() == null || board[currX - 2][currY - 1].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX - 2][currY - 1]);

			if (currX > 0 && currY > 1 && (board[currX - 1][currY - 2].getPiece() == null || board[currX - 1][currY - 2].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX - 1][currY - 2]);

			if (currX < 7 && currY > 1 && (board[currX + 1][currY - 2].getPiece() == null || board[currX + 1][currY - 2].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX + 1][currY - 2]);

			if (currX < 6 && currY > 0 && (board[currX + 2][currY - 1].getPiece() == null || board[currX + 2][currY - 1].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX + 2][currY - 1]);

			if (currX < 6 && currY < 7 && (board[currX + 2][currY + 1].getPiece() == null || board[currX + 2][currY + 1].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX + 2][currY + 1]);

			if (currX < 7 && currY < 6 && (board[currX + 1][currY + 2].getPiece() == null || board[currX + 1][currY + 2].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX + 1][currY + 2]);

			if (currX > 0 && currY < 6 && (board[currX - 1][currY + 2].getPiece() == null || board[currX - 1][currY + 2].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX - 1][currY + 2]);

			if (currX > 1 && currY < 7 && (board[currX - 2][currY + 1].getPiece() == null || board[currX - 2][currY + 1].getPiece().getPieceColour() != piece.getPieceColour()))
				retList.add(board[currX - 2][currY + 1]);

			return retList;
		}
	}
//Knight Ends
	
//Rook Begins
	private class rook implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();
			
			int i = 1;
			while (currX + i < 8 && (board[currX + i][currY].getPiece() == null || board[currX + i][currY].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX + i][currY].getPiece() != null && board[currX + i][currY].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX + i][currY]);
					break;
				}
				retList.add(board[currX + i][currY]);
				i++;
			}

			i = 1;
			while (currX - i > -1 && (board[currX - i][currY].getPiece() == null || board[currX - i][currY].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX - i][currY].getPiece() != null && board[currX - i][currY].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX - i][currY]);
					break;
				}
				retList.add(board[currX - i][currY]);
				i++;
			}

			i = 1;
			while (currY + i < 8 && (board[currX][currY + i].getPiece() == null || board[currX][currY + i].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX][currY + i].getPiece() != null && board[currX][currY + i].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX][currY + i]);
					break;
				}
				retList.add(board[currX][currY + i]);
				i++;
			}

			i = 1;
			while (currY - i > -1 && (board[currX][currY - i].getPiece() == null || board[currX][currY - i].getPiece().getPieceColour() != piece.getPieceColour())) {
				if (board[currX][currY - i].getPiece() != null && board[currX][currY - i].getPiece().getPieceColour() != piece.getPieceColour()) {
					retList.add(board[currX][currY - i]);
					break;
				}
				retList.add(board[currX][currY - i]);
				i++;
			}
			return retList;
		}
	}
//Rook Ends

//Queen Begins 
	private class queen implements availableMoves {
		private rook horizontalVerical;
		private bishop diagonal;

		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			horizontalVerical = new rook();
			diagonal = new bishop();
			ArrayList<cell> retList = horizontalVerical
					.getAvailableMoves(piece);
			ArrayList<cell> moreMoves = diagonal.getAvailableMoves(piece);
			for (int i = 0; i < moreMoves.size(); i++) {
				retList.add(moreMoves.get(i));
			}
			return retList;
		}
	}
//Queen Ends
	
//King Begins
	private class king implements availableMoves {
		public ArrayList<cell> getAvailableMoves(piece piece) {
			if (piece.getLocation() == deadCell)
				return null;
			ArrayList<cell> retList = new ArrayList<cell>();
			int currX = piece.getLocation().getX();
			int currY = piece.getLocation().getY();

			if (currX > 0 && currY > 0 && (piece.isValidMove(board[currX - 1][currY - 1])))
				retList.add(board[currX - 1][currY - 1]);

			if (currY > 0 && (piece.isValidMove(board[currX][currY - 1])))
				retList.add(board[currX][currY - 1]);

			if (currX < 7 && currY > 0 && (piece.isValidMove(board[currX + 1][currY - 1])))
				retList.add(board[currX + 1][currY - 1]);

			if (currX < 7 && (piece.isValidMove(board[currX + 1][currY])))
				retList.add(board[currX + 1][currY]);

			if (currX < 7 && currY < 7 && (piece.isValidMove(board[currX + 1][currY + 1])))
				retList.add(board[currX + 1][currY + 1]);

			if (currY < 7 && (piece.isValidMove(board[currX][currY + 1])))
				retList.add(board[currX][currY + 1]);

			if (currX > 0 && currY < 7 && (piece.isValidMove(board[currX - 1][currY + 1])))
				retList.add(board[currX - 1][currY + 1]);

			if (currX > 0 && piece.isValidMove(board[currX - 1][currY]))
				retList.add(board[currX - 1][currY]);

			return retList;
		}
	}
//King Ends
	
//user interface board Class Begins
	private class userInterfaceBoard {
		public cell getTargetCell() {
			cell retCell = deadCell;
			return retCell;
		}

		public cell getSourceCell() {
			cell retCell = deadCell;
			return retCell;
		}

		public piece getPiece() {
			piece retPiece = getSourceCell().getPiece();
			return retPiece;
		}

		userInterfaceBoard() {
		}
	}
//user interface board Class Ends
	
	private cell deadCell;
	private cell board[][];
	private player white, black;
	private objectColour turn;
	private gameState currentGameState;
	private userInterfaceBoard UIboard;

	public cell[][] getBoard() {
		return board;
	}

	public objectColour getTurn() {
		return turn;
	}

	
	private gameState checkForCheck(cell board[][], objectColour whoseCheck) {
		piece own[];
		piece opponent[];
		if (whoseCheck == objectColour.white) {
			own = white.getPieces();
			opponent = black.getPieces();
		} else {
			own = black.getPieces();
			opponent = white.getPieces();
		}
		
		for (int i = 0; i < 15; i++) {
			ArrayList<cell> moves = opponent[i].getAvailableMoves();
			if (moves != null && (!moves.isEmpty() && moves.contains(own[15].getLocation()))) 
			{
				if (whoseCheck == objectColour.white)
					return gameState.whiteCheck;
				else
					return gameState.blackCheck;
			}
		}
		return gameState.allClear;
	}

	private gameState checkForMate() {
		ArrayList<cell> whiteMoves = new ArrayList<cell>();
		ArrayList<cell> blackMoves = new ArrayList<cell>();
		
		for (int i = 0; i < 15; i++) {
			ArrayList<cell> moves = white.getPieces()[i].getAvailableMoves();
			for(int j = 0; j < moves.size(); j++)
				whiteMoves.add(moves.get(j));
			moves = black.getPieces()[i].getAvailableMoves();
			for(int j = 0; j < moves.size(); j++)
				blackMoves.add(moves.get(j));
		}
		if(blackMoves.size() == 0)
		{
			if(checkForCheck(board, objectColour.black) == gameState.blackCheck)
				return gameState.blackMate;
			else
				return gameState.stalemate;
		}
		if(whiteMoves.size() == 0)
		{
			if(checkForCheck(board, objectColour.white) == gameState.whiteCheck)
				return gameState.whiteMate;
			else
				return gameState.stalemate;
		}
		if (checkForCheck(board, objectColour.white) == gameState.whiteCheck)
			return gameState.whiteCheck;
		if (checkForCheck(board, objectColour.black) == gameState.blackCheck)
			return gameState.blackCheck;
		return gameState.allClear;
	}

	
//move begins
	public boolean move(int fromX, int fromY, int toX, int toY)
			throws Exception {
		player currentPlayer = turn == objectColour.white ? white : black;
		piece piece = board[fromX][fromY].getPiece();
		boolean success = currentPlayer.move(piece, board[toX][toY]);
		if (success)
			turn = turn == objectColour.white ? objectColour.black: objectColour.white;
		return success;
	}
//move ends
	
//populate board begins
	void populateBoard(cell[][] board) {
		piece blackPieces[] = new piece[16];
		piece whitePieces[] = new piece[16];

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				board[x][y] = new cell(x, y);
			}
		}

		for (int i = 0; i < 8; i++) {
			piece whitePawn = new piece(objectColour.white, pieceType.pawn,
					board[i][6], new pawn(), R.drawable.wpawn);
			whitePieces[i] = whitePawn;
			board[i][6].setPiece(whitePawn);
			piece blackPawn = new piece(objectColour.black, pieceType.pawn,
					board[i][1], new pawn(), R.drawable.bpawn);
			blackPieces[i] = blackPawn;
			board[i][1].setPiece(blackPawn);
		}

		whitePieces[8] = new piece(objectColour.white, pieceType.rook,
				board[0][7], new rook(), R.drawable.wrook);
		board[0][7].setPiece(whitePieces[8]);
		whitePieces[9] = new piece(objectColour.white, pieceType.rook,
				board[7][7], new rook(), R.drawable.wrook);
		board[7][7].setPiece(whitePieces[9]);
		blackPieces[8] = new piece(objectColour.black, pieceType.rook,
				board[0][0], new rook(), R.drawable.brook);
		board[0][0].setPiece(blackPieces[8]);
		blackPieces[9] = new piece(objectColour.black, pieceType.rook,
				board[7][0], new rook(), R.drawable.brook);
		board[7][0].setPiece(blackPieces[9]);
		whitePieces[10] = new piece(objectColour.white, pieceType.knight,
				board[1][7], new knight(), R.drawable.wknight);
		board[1][7].setPiece(whitePieces[10]);
		whitePieces[11] = new piece(objectColour.white, pieceType.knight,
				board[6][7], new knight(), R.drawable.wknight);
		board[6][7].setPiece(whitePieces[11]);
		blackPieces[10] = new piece(objectColour.black, pieceType.knight,
				board[1][0], new knight(), R.drawable.bknight);
		board[1][0].setPiece(blackPieces[10]);
		blackPieces[11] = new piece(objectColour.black, pieceType.knight,
				board[6][0], new knight(), R.drawable.bknight);
		board[6][0].setPiece(blackPieces[11]);
		whitePieces[12] = new piece(objectColour.white, pieceType.bishop,
				board[2][7], new bishop(), R.drawable.wbishop);
		board[2][7].setPiece(whitePieces[12]);
		whitePieces[13] = new piece(objectColour.white, pieceType.bishop,
				board[5][7], new bishop(), R.drawable.wbishop);
		board[5][7].setPiece(whitePieces[13]);
		blackPieces[12] = new piece(objectColour.black, pieceType.bishop,
				board[2][0], new bishop(), R.drawable.bbishop);
		board[2][0].setPiece(blackPieces[12]);
		blackPieces[13] = new piece(objectColour.black, pieceType.bishop,
				board[5][0], new bishop(), R.drawable.bbishop);
		board[5][0].setPiece(blackPieces[13]);
		whitePieces[14] = new piece(objectColour.white, pieceType.queen,
				board[3][7], new queen(), R.drawable.wqueen);
		board[3][7].setPiece(whitePieces[14]);
		blackPieces[14] = new piece(objectColour.black, pieceType.queen,
				board[3][0], new queen(), R.drawable.bqueen);
		board[3][0].setPiece(blackPieces[14]);
		whitePieces[15] = new piece(objectColour.white, pieceType.king,
				board[4][7], new king(), R.drawable.wking);
		board[4][7].setPiece(whitePieces[15]);
		blackPieces[15] = new piece(objectColour.black, pieceType.king,
				board[4][0], new king(), R.drawable.bking);
		board[4][0].setPiece(blackPieces[15]);

		white = new player(objectColour.white, whitePieces);
		black = new player(objectColour.black, blackPieces);
	}
//populate board  ends
	
	Chess() {
		deadCell = new cell(-1, -1);
		board = new cell[8][8];
		populateBoard(board);
		turn = objectColour.white;
		currentGameState = gameState.allClear;
	}
}
