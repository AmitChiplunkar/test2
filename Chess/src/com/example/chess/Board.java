package com.example.chess;


import java.util.ArrayList;

import com.example.chess.Chess.cell;
import com.example.chess.Chess.piece;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;


public class Board extends View
{
	boolean pieceSelected;
	boolean reset;
	private int fromX, fromY, toX, toY, Yzero, width;
	private Chess core;
	public void setCore(Chess core)
	{
		if(core != null)
			this.core = core;
	}
	private Resources res;
	private Canvas canvas;
	
	private int getBoardX(int x)
	{
		return x * width / 8;
	}
	
	private int getBoardY(int y)
	{
		return y * width / 8 + Yzero;
	}
	
	private void drawBoard(cell[][] board)
	{	
		super.invalidate();
		Drawable boardImg = res.getDrawable(R.drawable.board);
		width = canvas.getWidth();
		Yzero =  (int)(canvas.getHeight() - width) / 2;
		boardImg.setBounds(0, Yzero, width, width + Yzero);
		boardImg.draw(canvas);
		for(int x = 0; x < 8; x++)
		{
			for(int y = 0; y < 8; y++)
			{
				piece piece = board[x][y].getPiece();
				if(piece != null)
				{
					Drawable figure = res.getDrawable(piece.getImageResource());
					figure.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
					figure.draw(canvas);
				}
			}
		}
	}
	
	private void drawAvailableMoves(cell[][] board, int x, int y)
	{
		piece piece = board[x][y].getPiece();
		if(piece != null && piece.getPieceColour() == core.getTurn())
		{
			Drawable selection = res.getDrawable(R.drawable.selected);
			selection.setBounds(getBoardX(x), getBoardY(y), getBoardX(x) + width/8, getBoardY(y) + width /8);
			selection.draw(canvas);
			
			ArrayList<cell> availMoves = piece.getAvailableMoves();
			for(int i = 0; i < availMoves.size(); i ++)
			{
				if(piece.isValidMove(availMoves.get(i)))
				{
					cell availMove = availMoves.get(i);
					Drawable circle = res.getDrawable(R.drawable.selectioncircle);
					circle.setBounds(getBoardX(availMove.getX()), getBoardY(availMove.getY()), getBoardX(availMove.getX()) + width/8, getBoardY(availMove.getY()) + width /8);
					circle.draw(canvas);
				}
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
		
		if(!pieceSelected)
		{	
			fromX = (int)(event.getX() / (width / 8.0));
			fromY = (int)((event.getY() - Yzero) / (width / 8.0));
			if( fromX < 0 || fromX > 7 || fromY < 0 || fromY > 7)
				return true;
		}
		else
		{
			toX = (int)(event.getX() / (width / 8.0));
			toY = (int)((event.getY() - Yzero) / (width / 8.0));
			if( toX < 0 || toX > 7 || toY < 0 || toY > 7 || (toX == fromX && toY == fromY));
			else
			{
				try
				{
					core.move(fromX, fromY, toX,toY);
					pieceSelected = false;
					return true;
				}catch( Exception ex)
				{
					pieceSelected = false;
					return true;
				}
			}
		}
		pieceSelected = !pieceSelected;
		
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{		
		this.canvas = canvas;
		this.drawBoard(core.getBoard());
		if(pieceSelected)
			drawAvailableMoves(core.getBoard(), fromX, fromY);
	}
	
	Board(Context context)
	{
		super(context);
		
		pieceSelected = false;
		reset = true;
		fromX =-1;
		fromY = -1;
		toX = -1;
		toY = -1;
		res = getResources();
		
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
}
