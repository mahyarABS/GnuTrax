/* 

Date: 14th of Februar 2014
version 0.2
All source under GPL version 2 
(GNU General Public License - http://www.gnu.org/)
contact traxplayer@gmail.com for more information about this code

*/

package org.traxgame.main;

import java.util.ArrayList;

public class Traxboard 
{
	// Piece description...
	//
	// 0 1 2 3 4 5 6
	//
	// . . . . o . . x . . o . . o . . x . . x .
	//
	// . . x x o o o / x x \ o o \ x x / o
	//
	// . . . . o . . x . . x . . x . . o . . o .
	// the direction of the white lines and their number

	private boolean boardEmpty;
	private int wtm;
	private int[][] board;
	private int gameover;
	private int num_of_tiles;
	private int firstrow, lastrow, firstcol, lastcol;

	private boolean boardEmpty_save;
	private int wtm_save;
	private int[][] board_save;
	private int gameover_save;
	private int num_of_tiles_save;
	private int firstrow_save, lastrow_save, firstcol_save, lastcol_save;
	public boolean debug = false;

	public static final int EMPTY = 0, INVALID = 7,
			NS = 1, SN = 1, WE = 2, EW = 2, NW = 3, WN = 3, NE = 4, EN = 4, WS = 5,
			SW = 5, SE = 6, ES = 6;
	public static final int WHITE = 0, BLACK = 1, DRAW = 2, NOPLAYER = 3, NORESULT = 3;

	private static String[][] col_row_array;
	
	static 
	{
		StringBuffer str;
		col_row_array = new String[9][9];
		for (char i = '@'; i <= 'H'; i++) {
			for (char j = '0'; j <= '8'; j++) {
				str = new StringBuffer();
				str.append(i);
				str.append(j);
				col_row_array[i - '@'][j - '0'] = new String(str);
			}
		}
	}

	public static final long serialVersionUID = 24362472L;

	public boolean blank(int piece) { return (piece == EMPTY); }

	/**
	 * Returns the numbers of used tiles. Can be used to determine if we are in
	 * the opening, middle or end-phase of the game
	 * 
	 * @return the number of used tiles
	 */
	public int getNumOfTiles() { return num_of_tiles; }

	public Traxboard rotate() 
	{
		Traxboard result=new Traxboard(this);
		for (int i=0; i<17; i++) {
			for (int j=0; j<17; j++) {
				switch (board[16-j][i]) {
				case NS:
					result.board[i][j]=WE;
					break;
				case WE:
					result.board[i][j]=NS;
					break;
				case EMPTY:
					result.board[i][j]=EMPTY;
					break;
				case NW:
					result.board[i][j]=NE;
					break;
				case NE:
					result.board[i][j]=SE;
					break;
				case SE:
					result.board[i][j]=SW;
					break;
				case SW:
					result.board[i][j]=NW;
					break;
				default:
					// This should never happen
					throw new RuntimeException("This should never happen. (044)");
				}
			}
		}
		result.setCorners();
		return result;
	}

	private void setCorners() 
	{
		firstrow=-1;
		firstcol=-1;
		lastcol=-1;
		lastrow=-1;
		for (int i=0; i<17; i++) {
			for (int j=0; j<17; j++) {
				if ((firstrow<0) && (board[i][j]!=EMPTY)) firstrow=i;
				if ((lastrow<0) && (board[16-i][j]!=EMPTY)) lastrow=16-i;
				if ((firstcol<0) && (board[j][i]!=EMPTY)) firstcol=i;
				if ((lastcol<0) && (board[j][16-i]!=EMPTY)) lastcol=16-i;
			}
		}
	}

	private void saveState() 
	{
		wtm_save = wtm;
		boardEmpty_save = boardEmpty;
		gameover_save = gameover;
		num_of_tiles_save = num_of_tiles;
		firstrow_save = firstrow;
		firstcol_save = firstcol;
		lastrow_save = lastrow;
		lastcol_save = lastcol;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 17; j++) {
				board_save[i][j] = board[i][j];
			}
		}
	}

	private void restoreState() 
	{
		wtm = wtm_save;
		boardEmpty = boardEmpty_save;
		gameover = gameover_save;
		num_of_tiles = num_of_tiles_save;
		firstrow = firstrow_save;
		firstcol = firstcol_save;
		lastrow = lastrow_save;
		lastcol = lastcol_save;
		for (int i = 0; i < 17; i++) {
			for (int j = 0; j < 17; j++) {
				board[i][j] = board_save[i][j];
			}
		}
	}

	public Traxboard() 
	{
		int i, j;

		wtm = WHITE;
		gameover = NOPLAYER;
		num_of_tiles = 0;
		board = new int[17][17];
		board_save = new int[17][17];
		for (i = 0; i < 17; i++)
			for (j = 0; j < 17; j++)
				board[i][j] = EMPTY;
		boardEmpty = true;

	}

	public Traxboard(Traxboard org) 
	{
		int i, j;

		wtm = org.wtm;
		gameover = org.gameover;
		num_of_tiles = org.num_of_tiles;
		board = new int[17][17];
		board_save = new int[17][17];
		for (i = 0; i < 17; i++) {
			for (j = 0; j < 17; j++) {
				this.board[i][j] = org.board[i][j];
				this.board_save[i][j] = org.board_save[i][j];
			}
		}
		firstrow = org.firstrow;
		firstcol = org.firstcol;
		lastrow = org.lastrow;
		lastcol = org.lastcol;
		firstrow_save = org.firstrow_save;
		firstcol_save = org.firstcol_save;
		lastrow_save = org.lastrow_save;
		lastcol_save = org.lastcol_save;
		boardEmpty = org.boardEmpty;
	}

	public int getRowSize() { return ((getNumOfTiles() == 0)?0:1+(lastrow-firstrow)); }
	public int getColSize() { return ((getNumOfTiles() == 0)?0:1+(lastcol-firstcol)); }

	public void dump() 
	{
		System.out.println(this);
		System.out.println("num_of_tiles=" + getNumOfTiles());
		System.out.println("rowsize=" + getRowSize());
		System.out.println("colsize=" + getColSize());
		System.out.println("firstrow=" + firstrow);
		System.out.println("firstcol=" + firstcol);
		System.out.println("lastrow=" + lastrow);
		System.out.println("lastcol=" + lastcol);
		System.out.print("gameover=");
		switch (gameover) {
		case WHITE:
			System.out.println("WHITE");
			break;
		case BLACK:
			System.out.println("BLACK");
			break;
		case DRAW:
			System.out.println("DRAW");
			break;
		case NOPLAYER:
			System.out.println("NOPLAYER");
			break;
		default:
			// This should never happen
			throw new RuntimeException("This should never happen. (001)");
		}
		if (boardEmpty)
			System.out.println("boardEmpty=true");
		else
			System.out.println("boardEmpty=false");
		System.out.print("wtm=");
		switch (wtm) {
		case WHITE:
			System.out.println("WHITE");
			break;
		case BLACK:
			System.out.println("BLACK");
			break;
		case DRAW:
			System.out.println("DRAW");
			break;
		case NOPLAYER:
			System.out.println("NOPLAYER");
			break;
		default:
			// This should never happen
			throw new RuntimeException("This should never happen. (002)");
		}
		System.out.println("   0123456789ABCDEFG");
		for (int i = 0; i < 17; i++) {
			if (i>9) {
				System.out.print(i+":");
			}
			else {
				System.out.print("0"+i+":");
			}
			for (int j = 0; j < 17; j++) {
				System.out.print(board[i][j]);
			}
			System.out.println();
		}

	}

	/**
	 * Given a row and column, the method checks if that (row,col) is occupied
	 * or free
	 * 
	 * @param row
	 *            the row number 1-8
	 * @param col
	 *            the column number 1-8
	 * @return true if the place (row,col) is free or false otherwise
	 */
	public boolean isBlank(int row, int col) { return (getAt(row, col) == EMPTY); }

	@Override
	public String toString() 
	{
		StringBuffer result = new StringBuffer(1000);
		int i, j, k;
		int leftpiece, uppiece, upleftpiece;
		String cols = "     A     B     C     D     E     F     G     H     ";
		String rows = "1 2 3 4 5 6 7 8 ";

		if (boardEmpty)
			return new String();
		result.append(cols.substring(0, 5 + 6 * getColSize()));
		// result.append(cols,0,5+6*getColSize());
		result.append('\n');
		for (i = 1; i <= getRowSize(); i++) {
			for (k = 0; k < 4; k++) {
				if (k == 2) {
					result.append(rows.substring(i * 2 - 2, i * 2));
					// result.append(rows,i*2-2,2);
				} else {
					result.append("  ");
				}
				for (j = 1; j <= getColSize(); j++) {
					switch (getAt(i, j)) {
					case NS:
						switch (k) {
						case 0:
							result.append("+--o--");
							break;
						case 1:
							result.append("|  o  ");
							break;
						case 2:
							result.append("######");
							break;
						case 3:
							result.append("|  o  ");
							break;
						}
						break;
					case WE:
						switch (k) {
						case 0:
							result.append("+--#--");
							break;
						case 1:
							result.append("|  #  ");
							break;
						case 2:
							result.append("ooo#oo");
							break;
						case 3:
							result.append("|  #  ");
							break;
						}
						break;
					case NW:
						switch (k) {
						case 0:
							result.append("+--o--");
							break;
						case 1:
							result.append("| o   ");
							break;
						case 2:
							result.append("oo   #");
							break;
						case 3:
							result.append("|   # ");
							break;
						}
						break;
					case NE:
						switch (k) {
						case 0:
							result.append("+--o--");
							break;
						case 1:
							result.append("|   o ");
							break;
						case 2:
							result.append("##   o");
							break;
						case 3:
							result.append("| #   ");
							break;
						}
						break;
					case SW:
						switch (k) {
						case 0:
							result.append("+--#--");
							break;
						case 1:
							result.append("|   # ");
							break;
						case 2:
							result.append("oo   #");
							break;
						case 3:
							result.append("| o   ");
							break;
						}
						break;
					case SE:
						switch (k) {
						case 0:
							result.append("+--#--");
							break;
						case 1:
							result.append("| #   ");
							break;
						case 2:
							result.append("##   o");
							break;
						case 3:
							result.append("|   o ");
							break;
						}
						break;
					case EMPTY:
						uppiece = getAt(i - 1, j);
						leftpiece = getAt(i, j - 1);
						upleftpiece = getAt(i - 1, j - 1);
						switch (k) {
						case 0:
							if ((uppiece == SN) || (uppiece == SW)
									|| (uppiece == SE)) {
								result.append("+--o--");
								break;
							}
							if ((uppiece == WE) || (uppiece == WN)
									|| (uppiece == EN)) {
								result.append("+--#--");
								break;
							}
							if ((upleftpiece != EMPTY) || (leftpiece != EMPTY)) {
								result.append("+     ");
								break;
							}
							result.append("      ");
							break;
						case 1:
							if (leftpiece == EMPTY)
								result.append("      ");
							else
								result.append("|     ");
							break;
						case 2:
							if (leftpiece == EMPTY)
								result.append("      ");
							if ((leftpiece == EW) || (leftpiece == EN)
									|| (leftpiece == ES))
								result.append("o     ");
							if ((leftpiece == NS) || (leftpiece == NW)
									|| (leftpiece == SW))
								result.append("#     ");
							break;
						case 3:
							if (leftpiece == EMPTY)
								result.append("      ");
							else
								result.append("|     ");
							break;
						}
						break;
					} // switch (getAt(i,j));
				} // for (j)

				upleftpiece = getAt(i - 1, j - 1);
				leftpiece = getAt(i, j - 1);
				switch (k) {
				case 0:
					if ((upleftpiece != EMPTY) || (leftpiece != EMPTY))
						result.append("+");
					break;
				case 1:
					if (leftpiece != EMPTY)
						result.append("|");
					break;
				case 2:
					if ((leftpiece == EW) || (leftpiece == EN)
							|| (leftpiece == ES))
						result.append("o");
					if ((leftpiece == NS) || (leftpiece == NW)
							|| (leftpiece == SW))
						result.append("#");
					break;
				case 3:
					if (leftpiece != EMPTY)
						result.append("|");
					break;
				}
				result.append("\n");
			}
		}
		result.append("  ");
		for (j = 1; j <= getColSize(); j++) {
			leftpiece = getAt(getRowSize(), j - 1);
			uppiece = getAt(getRowSize(), j);
			if ((uppiece == EMPTY) && (leftpiece == EMPTY))
				result.append("      ");
			if ((uppiece == EMPTY) && (leftpiece != EMPTY))
				result.append("+     ");
			if ((uppiece == SN) || (uppiece == SW) || (uppiece == SE))
				result.append("+--o--");
			if ((uppiece == WE) || (uppiece == WN) || (uppiece == NE))
				result.append("+--#--");
		}
		if (getAt(getRowSize(), getColSize()) != EMPTY)
			result.append("+");
		result.append("\n");
		return result.toString();
	}

	/**
	 * Try to make the specified move. If it is legal, then update the board.
	 * Accepts upper-case and lower-case letters, old and new notation but not
	 * the very old notation (used until 1986?) which is incompatibel with old
	 * notation
	 * 
	 * @param move
	 *            The move
	 */
	public void makeMove(String move) throws IllegalMoveException 
	{
		// updates the board etc. if it was a legal move
		// accepts upper-case & lower-case letters
		// and old&new notation but not the very old
		// notation (used until 1986?) which is incompatibel with old notation

		boolean oldNotation;
		int col, row, neighbor;
		char dir;
		int ohs_up = 0, ohs_down = 0, ohs_right = 0, ohs_left = 0, eks_up = 0, eks_down = 0, eks_right = 0, eks_left = 0;

		if (gameover != NOPLAYER)
			throw new IllegalMoveException("Game is over.");
		if (move.length() != 3)
			throw new IllegalMoveException("not a move.");
		move = move.toUpperCase();
		if (boardEmpty) {
			if (move.equals("A1C") || move.equals("@0/")) {
				putAt(1, 1, NW);
				switchPlayer();
				return;
			}
			if (move.equals("A1S") || move.equals("@0+")) {
				putAt(1, 1, NS);
				switchPlayer();
				return;
			}
			throw new IllegalMoveException(
					"Only A1C,A1S,@0/ and @0+ accepted as first move. Got "
							+ move);
		}

		// handle the old notation 1A special case (changing 1A -> A0)
		if (move.startsWith("1A")) {
			col = 1;
			row = 0;
			// col=(int)'A'-(int)'@';
			// row=(int)'0'-'0';
		} else {
			col = move.charAt(0) - '@';
			row = move.charAt(1) - '0';
			// col=(int)move[0]-(int)'@';
			// row=(int)move[1]-(int)'0';
		}
		if ((col < 0) || (col > 8))
			throw new IllegalMoveException("Illegal column.");
		if ((row < 0) || (row > 8))
			throw new IllegalMoveException("Illegal row.");
		if (col == 0 && row == 0)
			throw new IllegalMoveException("no neighbors.");

		dir = move.charAt(2);
		switch (dir) {
		case 'C':
		case 'S':
		case 'U':
		case 'L':
		case 'R':
		case 'D':
			oldNotation = true;
			break;
		case '/':
		case '+':
		case '\\':
			oldNotation = false;
			break;
		default:
			throw new IllegalMoveException("unknown direction.");
		}

		if (oldNotation) {
			if (!isBlank(row, col)) {
				if (col == 1)
					col--;
				else if (row == 1)
					row--;
			}
		}

		if (col == 0 && row == 0)
			throw new IllegalMoveException("no neighbors.");
		if ((row == 0) && (!canMoveDown()))
			throw new IllegalMoveException("illegal row.");
		if ((col == 0) && (!canMoveRight()))
			throw new IllegalMoveException("illegal column.");
		if (!isBlank(row, col))
			throw new IllegalMoveException("occupied." + move + " illegal");

		saveState();
		int up = getAt(row - 1, col), down = getAt(row + 1, col), left = getAt(
				row, col - 1), right = getAt(row, col + 1);

		if (up == SN || up == SE || up == SW)
			ohs_up = 1;
		if (up == EW || up == NW || up == NE)
			eks_up = 1;
		if (down == NS || down == NE || down == NW)
			ohs_down = 1;
		if (down == EW || down == SW || down == SE)
			eks_down = 1;
		if (left == EN || left == ES || left == EW)
			ohs_left = 1;
		if (left == WS || left == WN || left == NS)
			eks_left = 1;
		if (right == WN || right == WE || right == WS)
			ohs_right = 1;
		if (right == ES || right == NS || right == EN)
			eks_right = 1;
		neighbor = 1 * ohs_up + 2 * ohs_down + 4 * ohs_left + 8 * ohs_right
				+ 16 * eks_up + 32 * eks_down + 64 * eks_left + 128 * eks_right;

		switch (neighbor) {
		case 0:
			throw new IllegalMoveException("no neighbors.");
		case 1:
			switch (dir) {
			case '/':
			case 'L':
				putAt(row, col, NW);
				break;
			case '\\':
			case 'R':
				putAt(row, col, NE);
				break;
			case '+':
			case 'S':
				putAt(row, col, NS);
				break;
			case 'U':
			case 'C':
			case 'D':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (003)");
			}
			break;
		case 2:
			switch (dir) {
			case '/':
			case 'R':
				putAt(row, col, SE);
				break;
			case '\\':
			case 'L':
				putAt(row, col, SW);
				break;
			case '+':
			case 'S':
				putAt(row, col, NS);
				break;
			case 'C':
			case 'U':
			case 'D':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (026)");
			}
			break;
		case 4:
			switch (dir) {
			case '/':
			case 'U':
				putAt(row, col, WN);
				break;
			case '\\':
			case 'D':
				putAt(row, col, WS);
				break;
			case '+':
			case 'S':
				putAt(row, col, WE);
				break;
			case 'C':
			case 'R':
			case 'L':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (004)");
			}
			break;
		case 8:
			switch (dir) {
			case '/':
			case 'D':
				putAt(row, col, ES);
				break;
			case '\\':
			case 'U':
				putAt(row, col, EN);
				break;
			case '+':
			case 'S':
				putAt(row, col, EW);
				break;
			case 'C':
			case 'R':
			case 'L':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (005)");
			}
			break;
		case 16:
			switch (dir) {
			case '/':
			case 'L':
				putAt(row, col, SE);
				break;
			case '\\':
			case 'R':
				putAt(row, col, SW);
				break;
			case '+':
			case 'S':
				putAt(row, col, WE);
				break;
			case 'C':
			case 'U':
			case 'D':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (006)");
			}
			break;
		case 18:
			switch (dir) {
			case '/':
			case 'R':
				putAt(row, col, SE);
				break;
			case '\\':
			case 'L':
			case 'C':
				putAt(row, col, SW);
				break;
			case '+':
			case 'S':
			case 'U':
			case 'D':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (007)");
			}
			break;
		case 20:
			switch (dir) {
			case '/':
			case 'L':
			case 'U':
				throw new IllegalMoveException("illegal direction.");
			case '\\':
			case 'C':
			case 'R':
			case 'D':
				putAt(row, col, WS);
				break;
			case '+':
			case 'S':
				putAt(row, col, WE);
				break;
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (008)");
			}
			break;
		case 24:
			switch (dir) {
			case '/':
			case 'L':
			case 'C':
			case 'D':
				putAt(row, col, SE);
				break;
			case 'U':
			case '\\':
			case 'R':
				throw new IllegalMoveException("illegal direction.");
			case 'S':
			case '+':
				putAt(row, col, WE);
				break;
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (010)");
			}
			break;
		case 32:
			switch (dir) {
			case '/':
			case 'R':
				putAt(row, col, NW);
				break;
			case 'D':
			case 'U':
			case 'C':
				throw new IllegalMoveException("illegal direction.");
			case '\\':
			case 'L':
				putAt(row, col, NE);
				break;
			case 'S':
			case '+':
				putAt(row, col, WE);
				break;
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (011)");
			}
			break;
		case 33:
			switch (dir) {
			case '/':
			case 'L':
				putAt(row, col, NW);
				break;
			case 'R':
			case '\\':
				putAt(row, col, NE);
				break;
			case 'C':
			case 'S':
			case '+':
			case 'D':
			case 'U':
				throw new IllegalMoveException("illegal direction.");
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (012)");
			}
			break;
		case 36:
			if (dir == '/')
				putAt(row, col, NW);
			if (dir == '\\')
				throw new IllegalMoveException("illegal direction.");
			if (dir == '+')
				putAt(row, col, WE);
			if (dir == 'S')
				putAt(row, col, WE);
			if (dir == 'C')
				putAt(row, col, WN);
			if (dir == 'L')
				throw new IllegalMoveException(move + " illegal direction.");
			if (dir == 'R')
				putAt(row, col, WN);
			if (dir == 'U')
				putAt(row, col, WN);
			if (dir == 'D')
				throw new IllegalMoveException("illegal direction.");
			break;
		case 40:
			if (dir == '/')
				throw new IllegalMoveException("illegal direction.");
			if (dir == '\\')
				putAt(row, col, EN);
			if (dir == '+')
				putAt(row, col, EW);
			if (dir == 'S')
				putAt(row, col, WE);
			if (dir == 'C')
				putAt(row, col, NE);
			if (dir == 'L')
				putAt(row, col, NE);
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				putAt(row, col, NE);
			if (dir == 'D')
				throw new IllegalMoveException("illegal direction.");
			break;
		case 64:
			if (dir == '/')
				putAt(row, col, ES);
			if (dir == '\\')
				putAt(row, col, EN);
			if (dir == '+')
				putAt(row, col, NS);
			if (dir == 'S')
				putAt(row, col, NS);
			if (dir == 'C')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'L')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				putAt(row, col, SE);
			if (dir == 'D')
				putAt(row, col, NE);
			break;
		case 65:
			if (dir == '/')
				throw new IllegalMoveException("illegal direction.");
			if (dir == '\\')
				putAt(row, col, NE);
			if (dir == '+')
				putAt(row, col, NS);
			if (dir == 'S')
				putAt(row, col, NS);
			if (dir == 'C')
				putAt(row, col, NE);
			if (dir == 'L')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'R')
				putAt(row, col, NE);
			if (dir == 'U')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'D')
				putAt(row, col, NE);
			break;
		case 66:
			if (dir == '/')
				putAt(row, col, SE);
			if (dir == '\\')
				throw new IllegalMoveException("illegal direction.");
			if (dir == '+')
				putAt(row, col, SN);
			if (dir == 'S')
				putAt(row, col, SN);
			if (dir == 'C')
				putAt(row, col, SE);
			if (dir == 'L')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'R')
				putAt(row, col, SE);
			if (dir == 'U')
				putAt(row, col, SE);
			if (dir == 'D')
				throw new IllegalMoveException("illegal direction.");
			break;
		case 72:
			if (dir == '/')
				putAt(row, col, ES);
			if (dir == '\\')
				putAt(row, col, EN);
			if (dir == '+')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'S')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'C')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'L')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				putAt(row, col, NE);
			if (dir == 'D')
				putAt(row, col, SE);
			break;
		case 128:
			if (dir == '/')
				putAt(row, col, WN);
			if (dir == '\\')
				putAt(row, col, WS);
			if (dir == '+')
				putAt(row, col, NS);
			if (dir == 'S')
				putAt(row, col, NS);
			if (dir == 'C')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'L')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				putAt(row, col, WS);
			if (dir == 'D')
				putAt(row, col, WN);
			break;
		case 129:
			if (dir == '/')
				putAt(row, col, NW);
			if (dir == '\\')
				throw new IllegalMoveException("illegal direction.");
			if (dir == '+')
				putAt(row, col, NS);
			if (dir == 'S')
				putAt(row, col, NS);
			if (dir == 'C')
				putAt(row, col, NW);
			if (dir == 'L')
				putAt(row, col, NW);
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'D')
				putAt(row, col, NW);
			break;
		case 130:
			if (dir == '/')
				throw new IllegalMoveException("illegal direction.");
			if (dir == '\\')
				putAt(row, col, SW);
			if (dir == '+')
				putAt(row, col, SN);
			if (dir == 'S')
				putAt(row, col, SN);
			if (dir == 'C')
				putAt(row, col, SW);
			if (dir == 'L')
				putAt(row, col, SW);
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				putAt(row, col, SW);
			if (dir == 'D')
				throw new IllegalMoveException("illegal direction.");
			break;
		case 132:
			if (dir == '/')
				putAt(row, col, WN);
			if (dir == '\\')
				putAt(row, col, WS);
			if (dir == '+')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'S')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'C')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'L')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'R')
				throw new IllegalMoveException("illegal direction.");
			if (dir == 'U')
				putAt(row, col, WN);
			if (dir == 'D')
				putAt(row, col, WS);
			break;
		default:
			/* This should never happen */
			throw new RuntimeException("This should never happen. (013)");
		}
		if (row == 0)
			row++;
		if (col == 0)
			col++;
		if (!forcedMove(row - 1, col)) {
			restoreState();
			throw new IllegalMoveException("illegal filled cave.");
		}
		if (!forcedMove(row + 1, col)) {
			restoreState();
			throw new IllegalMoveException("illegal filled cave.");
		}
		if (!forcedMove(row, col - 1)) {
			restoreState();
			throw new IllegalMoveException("illegal filled cave.");
		}
		if (!forcedMove(row, col + 1)) {
			restoreState();
			throw new IllegalMoveException("illegal filled cave.");
		}
		/* note that switchPlayer() _must_ come before isGameOver() */
		switchPlayer();
		isGameOver(); // updates the gameOver attribute

	}

	public void switchPlayer() 
	{
		switch (wtm) {
		case WHITE:
			wtm = BLACK;
			break;
		case BLACK:
			wtm = WHITE;
			break;
		default:
			/* This should never happen */
			throw new RuntimeException("This should never happen. (014)");
		}
	}

	public int isGameOver() {
		boolean WhiteWins = false, BlackWins = false;
		int sp;

		if (gameover != NOPLAYER)
			return gameover;
		if (num_of_tiles < 4) {
			gameover = NOPLAYER;
			return gameover;
		}

		if (uniqueMoves().size() == 0) {
			gameover = DRAW;
			return gameover;
		}

		// check for line win.
		// check left-right line
		if (getColSize() == 8) {
			// check left-right line
			for (int row = 1; row <= 8; row++) {
				if (checkLine(row, 1, 'r', 'h')) {
					// Line win
					sp = getAt(row, 1);
					if (sp == NS || sp == NE || sp == ES)
						BlackWins = true;
					else
						WhiteWins = true;
				}
			}
		}
		// check up-down line
		if (getRowSize() == 8) {
			for (int col = 1; col <= 8; col++) {
				if (checkLine(1, col, 'd', 'v')) {
					// Line win
					sp = getAt(1, col);
					if (sp == WE || sp == WS || sp == SE)
						BlackWins = true;
					else
						WhiteWins = true;
				}
			}
		}

		// if (need_loop_check==true) {
		// Now check loop wins
		for (int i = 1; i < 8; i++) {
			for (int j = 1; j < 8; j++) {
				switch (getAt(i, j)) {
				case NW:
					if (checkLine(i, j, 'u', 'l'))
						BlackWins = true;
					break;
				case SE:
					if (checkLine(i, j, 'u', 'l'))
						WhiteWins = true;
					break;
				case EMPTY:
				case NS:
				case WE:
				case NE:
				case WS:
					break;
				default:
					/* This should never happen */
					throw new RuntimeException(
							"This should never happen. (015)");
				}
			}
		}
		// }

		if (WhiteWins && BlackWins) {
			gameover = whoDidLastMove();
			return gameover;
		}
		if (WhiteWins) {
			gameover = WHITE;
			return gameover;
		}
		if (BlackWins) {
			gameover = BLACK;
			return gameover;
		}
		return NOPLAYER;
	}

	public ArrayList<String> uniqueMoves() 
	{
		// complex throw away a lot of equal moves
		// and symmetries (hopefully)

		ArrayList<String> Moves = new ArrayList<String>(100); // 50 might be enough
		String AMove;
		int i, j, k;
		int dl, dr, ur, ul, rr;
		int[][] neighbors = new int[10][10]; // which neighbors - default all
		// values 0
		boolean[][][] dirlist = new boolean[10][10][3]; // which directions for
		// move
		// 0 /, 1 \, 2 +
		// true means already used
		// default all values false
		int ohs_up, ohs_down, ohs_right, ohs_left, eks_up, eks_down, eks_right, eks_left;
		int up, down, left, right;
		int ohs, eks;
		int iBegin, jBegin, iEnd, jEnd;
		boolean lrsym, udsym, rsym;
		final String col = "@ABCDEFGH";
		final String row = "012345678";

		if (gameover != NOPLAYER) {
			return new ArrayList<String>(0);
		}

		if (boardEmpty == true) { // empty board only these two moves
			Moves.add("@0/");
			Moves.add("@0+");
			Moves.trimToSize();
			return Moves;
		}
		if (getRowSize() * getColSize() == 1) {
			switch (getAt(1, 1)) {
			case NW:
				Moves.add("@1+");
				Moves.add("@1/");
				Moves.add("B1+");
				Moves.add("B1/");
				Moves.add("B1\\");
				break;
			case NS:
				Moves.add("@1+");
				Moves.add("@1/");
				Moves.add("A0/");
				Moves.add("A0+");
				break;
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (016)");
			}
			Moves.trimToSize();
			return Moves;
		}

		for (i = 0; i < 10; i++)
			for (j = 0; j < 10; j++)
				for (k = 0; k < 3; k++)
					dirlist[i][j][k] = false;

		lrsym = isLeftRightMirror();
		udsym = isUpDownMirror();
		rsym = isRotateMirror();
		iBegin = (canMoveDown() == true) ? 0 : 1;
		jBegin = (canMoveRight() == true) ? 0 : 1;
		iEnd = (getRowSize() < 8) ? getRowSize() + 1 : 8;
		jEnd = (getColSize() < 8) ? getColSize() + 1 : 8;
		if (lrsym)
			jEnd = (getColSize() + 1) / 2;
		if (rsym || udsym)
			iEnd = (getRowSize() + 1) / 2;

		for (i = iBegin; i <= iEnd; i++) {
			for (j = jBegin; j <= jEnd; j++) {
				if (!(isBlank(i, j))) {
					neighbors[i][j] = 0;
				} else {
					ohs_up = 0;
					ohs_down = 0;
					ohs_right = 0;
					ohs_left = 0;
					eks_up = 0;
					eks_down = 0;
					eks_right = 0;
					eks_left = 0;
					up = getAt(i - 1, j);
					down = getAt(i + 1, j);
					left = getAt(i, j - 1);
					right = getAt(i, j + 1);

					if (up == SN || up == SW || up == SE) {
						ohs_up = 1;
					} else if (up != EMPTY) {
						eks_up = 1;
					}

					if (down == NS || down == NW || down == NE) {
						ohs_down = 1;
					} else if (down != EMPTY) {
						eks_down = 1;
					}

					if (left == EN || left == ES || left == WE)
						ohs_left = 1;
					else if (left != EMPTY)
						eks_left = 1;

					if (right == WE || right == WS || right == WN)
						ohs_right = 1;
					else if (right != EMPTY)
						eks_right = 1;

					ohs = ohs_up + ohs_down + ohs_left + ohs_right;
					eks = eks_up + eks_down + eks_left + eks_right;
					neighbors[i][j] = 1 * ohs_up + 2 * ohs_down + 4 * ohs_left
							+ 8 * ohs_right + 16 * eks_up + 32 * eks_down + 64
							* eks_left + 128 * eks_right;
				}
			}
		}

		for (i = iBegin; i <= iEnd; i++) {
			for (j = jBegin; j <= jEnd; j++) {
				if (neighbors[i][j] != 0) {
					dl = getAt(i + 1, j - 1);
					dr = getAt(i + 1, j + 1);
					ur = getAt(i - 1, j + 1);
					ul = getAt(i - 1, j - 1);
					rr = getAt(i, j + 2);
					switch (neighbors[i][j]) {
					case 1: {
						if (dr == NS || dr == NW || dr == NE)
							dirlist[i][j + 1][1] = true;
						if (dr == WN || dr == WS || dr == WE)
							dirlist[i + 1][j][1] = true;
						if (dl == EW || dl == ES || dl == ES)
							dirlist[i + 1][j][0] = true;
						if (ur == SW || ur == SE || ur == SN)
							dirlist[i][j + 1][0] = true;
						break;
					}
					case 2: {
						if (dr == NS || dr == NW || dr == NE)
							dirlist[i][j + 1][1] = true;
						if (ur == SW || ur == SE || ur == SN)
							dirlist[i][j + 1][0] = true;
						break;
					}
					case 4: {
						if (dl == ES || dl == EN || dl == EW)
							dirlist[i + 1][j][0] = true;
						if (dr == WN || dr == WS || dr == WE)
							dirlist[i + 1][j][1] = true;
						if (ur == SW || ur == SN || ur == SE)
							dirlist[i][j + 1][0] = true;
						if (dr == NS || dr == NE || dr == NW)
							dirlist[i][j + 1][1] = true;
						break;
					}
					case 8: {
						if (dl == ES || dl == EN || dl == EW)
							dirlist[i + 1][j][0] = true;
						if (dr == WN || dr == WE || dr == WS)
							dirlist[i + 1][j][1] = true;
						break;
					}
					case 16: {
						if (dr == SW || dr == SE || dr == WE)
							dirlist[i][j + 1][1] = true;
						if (dr == SE || dr == SN || dr == EN)
							dirlist[i + 1][j][1] = true;
						if (dl == NW || dl == NS || dl == WS)
							dirlist[i + 1][j][0] = true;
						if (ur == NW || ur == NE || ur == WE)
							dirlist[i][j + 1][0] = true;
						break;
					}
					case 18:
					case 33: {
						dirlist[i][j + 1][1] = true;
						dirlist[i][j + 1][0] = true;
						dirlist[i][j][2] = true;
						break;
					}
					case 20:
					case 65: {
						if (rr != EMPTY)
							dirlist[i][j + 1][2] = true;
						dirlist[i + 1][j][0] = true;
						dirlist[i + 1][j][1] = true;
						dirlist[i][j + 1][0] = true;
						dirlist[i][j][0] = true;
						break;
					}
					case 24:
					case 129: {
						dirlist[i + 1][j][1] = true;
						dirlist[i][j][1] = true;
						break;
					}
					case 32: {
						if (dr == SE || dr == SW || dr == EW)
							dirlist[i][j + 1][1] = true;
						if (ur == NW || ur == NE || ur == WE)
							dirlist[i][j + 1][0] = true;
						break;
					}
					case 36: {
						if (ul == NW || ul == SW || ul == NS)
							dirlist[i - 1][j][1] = true;
						dirlist[i][j + 1][1] = true;
						dirlist[i][j + 1][0] = true;
						dirlist[i][j][1] = true;
						break;
					}
					case 40:
					case 130: {
						dirlist[i][j][0] = true;
						break;
					}
					case 64: {
						if (dl == WN || dl == WS || dl == NS)
							dirlist[i + 1][j][0] = true;
						if (dr == EN || dr == ES || dr == NS)
							dirlist[i + 1][j][1] = true;
						if (ur == NW || ur == NE || ur == WE)
							dirlist[i][j + 1][0] = true;
						if (dr == SE || dr == SW || dr == EW)
							dirlist[i][j + 1][1] = true;
						break;
					}
					case 66: {
						if (ul == EW || ul == ES || ul == EN)
							dirlist[i - 1][j][1] = true;
						dirlist[i][j + 1][1] = true;
						dirlist[i][j + 1][0] = true;
						dirlist[i][j][1] = true;
						break;
					}
					case 72:
					case 132: {
						dirlist[i + 1][j][0] = true;
						dirlist[i + 1][j][1] = true;
						dirlist[i][j][2] = true;
						break;
					}
					case 128: {
						if (dl == WS || dl == WN || dl == SN)
							dirlist[i + 1][j][0] = true;
						if (dr == EN || dr == ES || dr == NS)
							dirlist[i + 1][j][1] = true;
						break;
					}
					default:
						// This should never happen
						throw new RuntimeException(
								"This should never happen(029)");
					}
				}
			}
		}

		// remove left-right symmetry moves
		if (lrsym && getColSize() % 2 == 1) {
			for (i = iBegin; i <= iEnd; i++) {
				dirlist[i][jEnd][0] = true;
			}
		}
		// remove up-down symmetry moves
		if (udsym && getRowSize() % 2 == 1) {
			for (j = jBegin; j <= jEnd; j++) {
				dirlist[iEnd][j][1] = true;
			}
		}

		// collects the moves
		for (i = iBegin; i <= iEnd; i++)
			for (j = jBegin; j <= jEnd; j++) {
				// remove rotation symmetry moves
				if (rsym && getRowSize() % 2 == 1) {
					int jMiddle = (getColSize() + 1) / 2;
					if (j > jMiddle && i == iEnd) {
						continue;
					}
				}
				if (neighbors[i][j] != 0) {
					ohs_up = 0;
					ohs_down = 0;
					ohs_right = 0;
					ohs_left = 0;
					eks_up = 0;
					eks_down = 0;
					eks_right = 0;
					eks_left = 0;
					up = getAt(i - 1, j);
					down = getAt(i + 1, j);
					left = getAt(i, j - 1);
					right = getAt(i, j + 1);

					if (up == SN || up == SW || up == SE)
						ohs_up = 1;
					else if (up != EMPTY)
						eks_up = 1;
					if (down == NS || down == NW || down == NE)
						ohs_down = 1;
					else if (down != EMPTY)
						eks_down = 1;
					if (left == EN || left == ES || left == WE)
						ohs_left = 1;
					else if (left != EMPTY)
						eks_left = 1;
					if (right == WE || right == WS || right == WN)
						ohs_right = 1;
					else if (right != EMPTY)
						eks_right = 1;

					if (dirlist[i][j][0] == false) {
						saveState();
						if ((ohs_up + ohs_left > 0)
								|| (eks_right + eks_down > 0))
							putAt(i, j, NW);
						if ((eks_up + eks_left > 0)
								|| (ohs_right + ohs_down > 0))
							putAt(i, j, SE);
						if (forcedMove(i - 1, j) && forcedMove(i + 1, j)
								&& forcedMove(i, j - 1) && forcedMove(i, j + 1)) {
							Moves.add(col_row_array[j][i] + "/");
						}
						restoreState();
					}
					if (dirlist[i][j][1] == false) {
						saveState();
						if ((ohs_up + ohs_right > 0)
								|| (eks_left + eks_down > 0))
							putAt(i, j, NE);
						if ((eks_up + eks_right > 0)
								|| (ohs_left + ohs_down > 0))
							putAt(i, j, SW);
						if (forcedMove(i - 1, j) && forcedMove(i + 1, j)
								&& forcedMove(i, j - 1) && forcedMove(i, j + 1)) {
							Moves.add(col_row_array[j][i] + "\\");
						}
						restoreState();
					}
					if (dirlist[i][j][2] == false) {
						saveState();
						if ((ohs_up + ohs_down > 0)
								|| (eks_left + eks_right > 0))
							putAt(i, j, NS);
						if ((eks_up + eks_down > 0)
								|| (ohs_left + ohs_right > 0))
							putAt(i, j, WE);
						if (forcedMove(i - 1, j) && forcedMove(i + 1, j)
								&& forcedMove(i, j - 1) && forcedMove(i, j + 1)) {
							Moves.add(col_row_array[j][i] + "+");
						}
						restoreState();
					}
				}
			}
		Moves.trimToSize();
		return Moves;
	}

	private boolean checkLine(int row, int col, char direction, char type) 
	{
		// type can be _h_orizontal , _v_ertical, _l_oop

		int start_row = row;
		int start_col = col;
		int ix = 0;
		String newdir;

		newdir = " uurllr" // 'u' 1.. 6
				+ "ddlrrl" // 'd' 7..12
				+ "llduud" // 'l' 13..18
				+ "rruddu"; // 'r' 19..24

		for (;;) {
			if (isBlank(row, col))
				return false; // no line starts with a empty space
			// or we are out of range
			switch (direction) {
			case 'u':
				// newdir's first line
				ix = 0;
				break;
			case 'd':
				// newdir's second line
				ix = 6;
				break;
			case 'l':
				// newdir's third line
				ix = 12;
				break;
			case 'r':
				// newdir's fourth line
				ix = 18;
				break;
			}
			ix += getAt(row, col);
			direction = newdir.charAt(ix);
			switch (direction) {
			case 'u':
				row--;
				break;
			case 'd':
				row++;
				break;
			case 'l':
				col--;
				break;
			case 'r':
				col++;
				break;
			}
			if ((type == 'h') && (col == 9))
				return true; // left-right win
			if ((type == 'v') && (row == 9))
				return true; // top-buttom win
			if ((row == start_row) && (col == start_col)) {
				if (type == 'l')
					return true; // loop win
				else
					return false;
			}
		}
	}

	public boolean isLeftRightMirror() 
	{
		int piece, i, j, j2;

		for (i = 1; i <= getRowSize(); i++) {
			j2 = getColSize();
			for (j = 1; j <= ((getColSize() + 1) / 2); j++) {
				piece = getAt(i, j);
				switch (getAt(i, j2)) {
				case NW:
					if (piece != NE)
						return false;
					break;
				case NE:
					if (piece != NW)
						return false;
					break;
				case SW:
					if (piece != SE)
						return false;
					break;
				case SE:
					if (piece != SW)
						return false;
					break;
				case NS:
					if (piece != NS)
						return false;
					break;
				case WE:
					if (piece != WE)
						return false;
					break;
				case EMPTY:
					if (piece != EMPTY)
						return false;
					break;
				}
				j2--;
			}
		}
		return true;
	}

	public boolean isRightLeftMirror() { return isLeftRightMirror(); }

	public boolean isUpDownMirror() 
	{
		int piece, i, j, i2;

		i2 = getRowSize();
		for (i = 1; i <= ((getRowSize() + 1) / 2); i++) {
			for (j = 1; j <= getColSize(); j++) {
				piece = getAt(i, j);
				switch (getAt(i2, j)) {
				case NW:
					if (piece != SW)
						return false;
					break;
				case NE:
					if (piece != SE)
						return false;
					break;
				case SW:
					if (piece != NW)
						return false;
					break;
				case SE:
					if (piece != NE)
						return false;
					break;
				case NS:
					if (piece != NS)
						return false;
					break;
				case WE:
					if (piece != WE)
						return false;
					break;
				case EMPTY:
					if (piece != EMPTY)
						return false;
					break;
				}
			}
			i2--;
		}
		return true;
	}

	public boolean isDownUpMirror() { return isDownUpMirror(); }

	// 90 degree rotation
	public boolean isRotateMirror() 
	{
		int i, j, piece, i2, j2;

		i2 = getRowSize();
		for (i = 1; i <= ((getRowSize() + 1) / 2); i++) {
			j2 = getColSize();
			for (j = 1; j <= getColSize(); j++) {
				piece = getAt(i, j);
				switch (getAt(i2, j2)) {
				case NW:
					if (piece != SE)
						return false;
					break;
				case NE:
					if (piece != SW)
						return false;
					break;
				case SW:
					if (piece != NE)
						return false;
					break;
				case SE:
					if (piece != NW)
						return false;
					break;
				case NS:
					if (piece != NS)
						return false;
					break;
				case WE:
					if (piece != WE)
						return false;
					break;
				case EMPTY:
					if (piece != EMPTY)
						return false;
					break;
				}
				j2--;
			}
			i2--;
		}
		return true;
	}

	public int whoToMove() { return wtm; }

	public int whoDidLastMove() {
		if (boardEmpty)
			return NOPLAYER;
		switch (wtm) {
		case WHITE:
			return BLACK;
		case BLACK:
			return WHITE;
		default:
			// This should never happen
			throw new RuntimeException("This should never happen. (017)");
		}
	}

	public int getAt(int row, int col) {
		if ((row < 1) || (row > 8))
			return EMPTY;
		if ((col < 1) || (col > 8))
			return EMPTY;
		return board[firstrow + row - 1][firstcol + col - 1];
	}

	public void putAt(int row, int col, int piece) {
		assert (row + col > 0);
		if (piece == EMPTY) {
			if (board[firstrow + row - 1][firstcol + col - 1] != EMPTY)
				num_of_tiles--;
			board[firstrow + row - 1][firstcol + col - 1] = piece;
			/*
			 if ((row == getRowSize()) || (col == getColSize()) || (row == 1)
					|| (col == 1)) {
				recalcSize();
			}
			 */
			return;
		} else {
			if (boardEmpty) {
				boardEmpty = false;
				firstrow = 7;
				firstcol = 7;
				lastrow = 7;
				lastcol = 7;
				num_of_tiles = 1;
				board[firstrow][firstcol] = piece;
				return;
			}
			if (row == 0) {
				assert (firstrow > 0);
				firstrow--;
				row++;
			}
			if (col == 0) {
				assert (firstcol > 0);
				firstcol--;
				col++;
			}
			if (row > getRowSize()) {
				lastrow += row - getRowSize();
			}
			if (col > getColSize()) {
				lastcol += col - getColSize();
			}
			num_of_tiles++;
		}
		board[firstrow + row - 1][firstcol + col - 1] = piece;
	}

	private boolean canMoveDown() { return (getRowSize() < 8); }

	private boolean canMoveRight() { return (getColSize() < 8); }

	public boolean forcedMove(int brow, int bcol)
	{
		if (!isBlank(brow, bcol))
			return true;
		if ((brow < 1) || (brow > 8) || (bcol < 1) || (bcol > 8))
			return true;

		int up = getAt(brow - 1, bcol);
		int down = getAt(brow + 1, bcol);
		int left = getAt(brow, bcol - 1);
		int right = getAt(brow, bcol + 1);

		// boolean result=true;
		int neighbors = 0;

		if (!blank(up))
			neighbors++;
		if (!blank(down))
			neighbors++;
		if (!blank(left))
			neighbors++;
		if (!blank(right))
			neighbors++;

		if (neighbors < 2)
			return true; // Less than two pieces bordering

		int white_up = 0, black_up = 0, white_down = 0, black_down = 0, white_left = 0, black_left = 0, white_right = 0, black_right = 0;

		if (up == SN || up == SW || up == SE)
			white_up = 1;
		if (up == WE || up == NW || up == NE)
			black_up = 1;
		if (down == NS || down == NW || down == NE)
			white_down = 1;
		if (down == WE || down == SW || down == SE)
			black_down = 1;
		if (left == EW || left == EN || left == ES)
			white_left = 1;
		if (left == NS || left == NW || left == SW)
			black_left = 1;
		if (right == WE || right == WN || right == WS)
			white_right = 1;
		if (right == NS || right == NE || right == SE)
			black_right = 1;

		int white = white_up + white_down + white_left + white_right;
		int black = black_up + black_down + black_left + black_right;

		if ((white > 2) || (black > 2)) // Illegal filled cave
			return false;

		if ((white < 2) && (black < 2)) // Done
			return true;

		int piece = EMPTY;
		if (white == 2) {
			switch (white_up + 2 * white_down + 4 * white_left + 8
					* white_right) {
					case 3:
						piece = NS;
						break;
					case 12:
						piece = WE;
						break;
					case 5:
						piece = NW;
						break;
					case 9:
						piece = NE;
						break;
					case 6:
						piece = WS;
						break;
					case 10:
						piece = SE;
						break;
			}
		} else { // right==2
			switch (black_up + 2 * black_down + 4 * black_left + 8
					* black_right) {
					case 12:
						piece = NS;
						break;
					case 3:
						piece = WE;
						break;
					case 10:
						piece = NW;
						break;
					case 6:
						piece = NE;
						break;
					case 9:
						piece = WS;
						break;
					case 5:
						piece = SE;
						break;
			}
		}
		putAt(brow, bcol, piece);
		/*
		 * result&=(forcedMove(brow-1,bcol )); result&=(forcedMove(brow+1,bcol
		 * )); result&=(forcedMove(brow ,bcol-1)); result&=(forcedMove(brow
		 * ,bcol+1)); return result;
		 */
		if (forcedMove(brow - 1, bcol) == false) {
			return false;
		}
		if (forcedMove(brow + 1, bcol) == false) {
			return false;
		}
		if (forcedMove(brow, bcol - 1) == false) {
			return false;
		}
		if (forcedMove(brow, bcol + 1) == false) {
			return false;
		}
		return true;
	}

	private void updateLine(char colour, char entry, int row, int col) 
	{
		int theNum;

		while (true) {
			theNum = 0;
			if (colour == 'w')
				theNum = 1024;
			switch (entry) {
			case 'w':
				theNum += 512;
				break;
			case 'e':
				theNum += 256;
				break;
			case 's':
				theNum += 128;
				break;
			case 'n':
				theNum += 64;
				break;
			default:
				// This should never happen
				throw new RuntimeException("This should never happen. (018)");
			}
			switch (getAt(row, col)) {
			case NS:
				theNum += 32;
				break;
			case WE:
				theNum += 16;
				break;
			case NW:
				theNum += 8;
				break;
			case NE:
				theNum += 4;
				break;
			case SW:
				theNum += 2;
				break;
			case SE:
				theNum += 1;
				break;
			default:
				// This should never happen
				throw new RuntimeException("This should never happen. (019)");
			}
			switch (theNum) {
			case 1024 + 512 + 16:
				if (getAt(row, col + 1) == EMPTY)
					return;
			col++;
			break;
			case 1024 + 512 + 8:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			entry = 's';
			break;
			case 1024 + 512 + 2:
				if (getAt(row + 1, col) == EMPTY)
					return;
			row++;
			entry = 'n';
			break;
			case 1024 + 256 + 16:
				if (getAt(row, col - 1) == EMPTY)
					return;
			col--;
			break;
			case 1024 + 256 + 4:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			entry = 's';
			break;
			case 1024 + 256 + 1:
				if (getAt(row + 1, col) == EMPTY)
					return;
			row++;
			entry = 'n';
			break;
			case 1024 + 128 + 32:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			break;
			case 1024 + 128 + 2:
				if (getAt(row, col - 1) == EMPTY)
					return;
			col--;
			entry = 'e';
			break;
			case 1024 + 128 + 1:
				if (getAt(row, col + 1) == EMPTY)
					return;
			col++;
			entry = 'w';
			break;
			case 1024 + 64 + 32:
				if (getAt(row + 1, col) == EMPTY)
					return;
			row++;
			break;
			case 1024 + 64 + 8:
				if (getAt(row, col - 1) == EMPTY)
					return;
			col--;
			entry = 'e';
			break;
			case 1024 + 64 + 4:
				if (getAt(row, col + 1) == EMPTY)
					return;
			col++;
			entry = 'w';
			break;
			case 512 + 32:
				if (getAt(row, col + 1) == EMPTY)
					return;
			col++;
			break;
			case 512 + 4:
				if (getAt(row + 1, col) == EMPTY)
					return;
			row++;
			entry = 'n';
			break;
			case 512 + 1:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			entry = 's';
			break;
			case 256 + 32:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			break;
			case 256 + 8:
				if (getAt(row, col + 1) == EMPTY)
					return;
			row++;
			entry = 'n';
			break;
			case 256 + 2:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			entry = 's';
			break;
			case 128 + 16:
				if (getAt(row - 1, col) == EMPTY)
					return;
			row--;
			break;
			case 128 + 8:
				if (getAt(row, col + 1) == EMPTY)
					return;
			col++;
			entry = 'w';
			break;
			case 128 + 4:
				if (getAt(row, col - 1) == EMPTY)
					return;
			col--;
			entry = 'e';
			break;
			case 64 + 16:
				if (getAt(row + 1, col) == EMPTY)
					return;
			row++;
			break;
			case 64 + 2:
				if (getAt(row, col + 1) == EMPTY)
					return;
			col++;
			entry = 'w';
			break;
			case 64 + 1:
				if (getAt(row, col - 1) == EMPTY)
					return;
			col--;
			entry = 'e';
			break;
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (020)");
			}
		}
	}

	public String getBorder() { return this.getBorder(false); }

	public String getBorder(boolean needNumbers) 
	{
		String result = new String();
		char[] dummy = new char[4];
		int i, j, k, starti, startj, icopy, jcopy;
		char direction;
		int[][] wnum = new int[9][9]; // every white line gets a number
		int[][] bnum = new int[9][9];
		int currentWNum = 1;
		int currentBNum = 1;

		if (whoDidLastMove() == NOPLAYER)
			return result;
		for (i = 1; i < 9; i++) {
			for (j = 1; j < 9; j++) {
					wnum[i][j] = 0;
					bnum[i][j] = 0;
			}
		}
		starti = 1;
		startj = 1;
		while (getAt(starti, startj) == EMPTY)
			starti++;

		direction = 'd';
		i = starti;
		j = startj;
		while (true) {
			switch (direction) {
			case 'd':
				switch (getAt(i, j)) {
				case NW:
				case SW:
				case WE:
					result += 'W';
					/*
					 * if (needNumbers) { if (wnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('w','w',icopy,jcopy); wnum[i][j]=currentWNum;
					 * wnum[icopy][jcopy]=currentWNum; currentWNum++; }
					 * snprintf(dummy,4,"%d",wnum[i][j]); result+=dummy; }
					 */
					break;
				case NS:
				case NE:
				case SE:
					result += 'B';
					/*
					 * if (needNumbers) { if (bnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('b','w',icopy,jcopy); bnum[i][j]=currentBNum;
					 * bnum[icopy][jcopy]=currentBNum; currentBNum++; }
					 * result+=(char)bnum[icopy][jcopy];
					 * snprintf(dummy,4,"%d",bnum[i][j]); result+=dummy; }
					 */
					break;
				default:
					/* This should never happen */
					throw new RuntimeException(
							"This should never happen. (021)");
				}
				if (getAt(i + 1, j - 1) != EMPTY) {
					direction = 'l';
					result += '+';
					i++;
					j--;
					break;
				}
				if (getAt(i + 1, j) == EMPTY) {
					direction = 'r';
					result += '-';
					break;
				}
				i++;
				break;
			case 'u':
				switch (getAt(i, j)) {
				case EW:
				case NE:
				case ES:
					result += 'W';
					/*
					 * if (needNumbers) { if (wnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('w','e',icopy,jcopy); wnum[i][j]=currentWNum;
					 * wnum[icopy][jcopy]=currentWNum; currentWNum++; }
					 * snprintf(dummy,4,"%d",wnum[i][j]); result+=dummy; }
					 */
					break;
				case WS:
				case SN:
				case NW:
					result += 'B';
					/*
					 * if (needNumbers) { if (bnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('b','e',icopy,jcopy); bnum[i][j]=currentBNum;
					 * bnum[icopy][jcopy]=currentBNum; currentBNum++; }
					 * snprintf(dummy,4,"%d",bnum[i][j]); result+=dummy; }
					 */
					break;
				default:
					/* This should never happen */
					throw new RuntimeException(
							"This should never happen. (022)");
				}
				if (getAt(i - 1, j + 1) != EMPTY) {
					direction = 'r';
					result += '+';
					i--;
					j++;
					break;
				}
				if (getAt(i - 1, j) == EMPTY) {
					direction = 'l';
					result += '-';
					break;
				}
				i--;
				break;
			case 'l':
				switch (getAt(i, j)) {
				case NW:
				case SN:
				case NE:
					result += 'W';
					/*
					 * if (needNumbers) { if (wnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('w','n',icopy,jcopy); wnum[i][j]=currentWNum;
					 * wnum[icopy][jcopy]=currentWNum; currentWNum++; }
					 * snprintf(dummy,4,"%d",wnum[i][j]); result+=dummy; }
					 */
					break;
				case WE:
				case SE:
				case SW:
					result += 'B';
					/*
					 * if (needNumbers) { if (bnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('b','n',icopy,jcopy); bnum[i][j]=currentBNum;
					 * bnum[icopy][jcopy]=currentBNum; currentBNum++; }
					 * snprintf(dummy,4,"%d",bnum[i][j]); result+=dummy; }
					 */
					break;
				default:
					/* This should never happen */
					throw new RuntimeException(
							"This should never happen. (023)");
				}
				if (getAt(i - 1, j - 1) != EMPTY) {
					direction = 'u';
					result += '+';
					i--;
					j--;
					break;
				}
				if (getAt(i, j - 1) == EMPTY) {
					if ((i == starti) && (j == startj))
						return result;
					result += '-';
					direction = 'd';
					break;
				}
				j--;
				break;
			case 'r':
				switch (getAt(i, j)) {
				case NS:
				case SE:
				case SW:
					result += 'W';
					/*
					 * if (needNumbers) { if (wnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('w','s',icopy,jcopy); wnum[i][j]=currentWNum;
					 * wnum[icopy][jcopy]=currentWNum; currentWNum++; }
					 * snprintf(dummy,4,"%d",wnum[i][j]); result+=dummy; }
					 */
					break;
				case NE:
				case NW:
				case WE:
					result += 'B';
					/*
					 * if (needNumbers) { if (bnum[i][j]==0) { icopy=i; jcopy=j;
					 * updateLine('b','s',icopy,jcopy); bnum[i][j]=currentBNum;
					 * bnum[icopy][jcopy]=currentBNum; currentBNum++; }
					 * snprintf(dummy,4,"%d",bnum[i][j]); result+=dummy; }
					 */
					break;
				default:
					/* This should never happen */
					throw new RuntimeException(
							"This should never happen. (024)");
				}
				if (getAt(i + 1, j + 1) != EMPTY) {
					direction = 'd';
					result += '+';
					i++;
					j++;
					break;
				}
				if (getAt(i, j + 1) == EMPTY) {
					direction = 'u';
					result += '-';
					break;
				}
				j++;
				break;
			default:
				/* This should never happen */
				throw new RuntimeException("This should never happen. (025)");
			}
		}
	}

	private int neighbor_value(int x, int y) 
	{
		int value = 0;
		int up = getAt(x - 1, y), down = getAt(x + 1, y), left = getAt(x, y - 1), right = getAt(
				x, y + 1);
		if (up == Traxboard.SN || up == Traxboard.SE || up == Traxboard.SW) {
			value += 1;
		} // ohs_up
		if (up == Traxboard.EW || up == Traxboard.NW || up == Traxboard.NE) {
			value += 16;
		} // eks_up
		if (down == Traxboard.NS || down == Traxboard.NE
				|| down == Traxboard.NW) {
			value += 2;
		} // ohs_down
		if (down == Traxboard.EW || down == Traxboard.SW
				|| down == Traxboard.SE) {
			value += 32;
		} // eks_down
		if (left == Traxboard.EN || left == Traxboard.ES
				|| left == Traxboard.EW) {
			value += 4;
		} // ohs_left;
		if (left == Traxboard.WS || left == Traxboard.WN
				|| left == Traxboard.NS) {
			value += 64;
		} // eks_left;
		if (right == Traxboard.WN || right == Traxboard.WE
				|| right == Traxboard.WS) {
			value += 8;
		} // ohs.right
		if (right == Traxboard.ES || right == Traxboard.NS
				|| right == Traxboard.EN) {
			value += 128;
		} // eks.right
		return value;
	}

	public ArrayList<Integer> getLegalTiles(int x, int y) 
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (this.boardEmpty) {
			result.add(Traxboard.NW);
			result.add(Traxboard.NS);
		}
		switch (neighbor_value(x, y)) {
		case 0:
			return result;
		case 1:
			result.add(new Integer(Traxboard.NW));
			result.add(new Integer(Traxboard.NS));
			result.add(new Integer(Traxboard.NE));
			return result;
		case 128:
			result.add(new Integer(Traxboard.WS));
			result.add(new Integer(Traxboard.NS));
			result.add(new Integer(Traxboard.WN));
			return result;
		case 2:
			result.add(new Integer(Traxboard.SW));
			result.add(new Integer(Traxboard.SE));
			result.add(new Integer(Traxboard.SN));
			return result;
		case 32:
			result.add(new Integer(Traxboard.WE));
			result.add(new Integer(Traxboard.WN));
			result.add(new Integer(Traxboard.NE));
			return result;
		case 8:
			result.add(new Integer(Traxboard.EW));
			result.add(new Integer(Traxboard.ES));
			result.add(new Integer(Traxboard.EN));
			return result;
		case 4:
			result.add(new Integer(Traxboard.WE));
			result.add(new Integer(Traxboard.WS));
			result.add(new Integer(Traxboard.WN));
			return result;
		case 64:
			result.add(new Integer(Traxboard.NS));
			result.add(new Integer(Traxboard.NE));
			result.add(new Integer(Traxboard.SE));
			return result;
		case 16:
			result.add(new Integer(Traxboard.WE));
			result.add(new Integer(Traxboard.WS));
			result.add(new Integer(Traxboard.SE));
			return result;
		case 36:
			result.add(new Integer(Traxboard.WN));
			result.add(new Integer(Traxboard.WE));
			return result;
		case 66:
			result.add(new Integer(Traxboard.SN));
			result.add(new Integer(Traxboard.SE));
			return result;
		case 132:
			result.add(new Integer(Traxboard.WN));
			result.add(new Integer(Traxboard.WS));
			return result;
		case 72:
			result.add(new Integer(Traxboard.EN));
			result.add(new Integer(Traxboard.ES));
			return result;
		case 65:
			result.add(new Integer(Traxboard.NS));
			result.add(new Integer(Traxboard.NE));
			return result;
		case 20:
			result.add(new Integer(Traxboard.WE));
			result.add(new Integer(Traxboard.WS));
			return result;
		case 33:
			result.add(new Integer(Traxboard.NW));
			result.add(new Integer(Traxboard.NE));
			return result;
		case 18:
			result.add(new Integer(Traxboard.SW));
			result.add(new Integer(Traxboard.SE));
			return result;
		case 129:
			result.add(new Integer(Traxboard.NW));
			result.add(new Integer(Traxboard.NS));
			return result;
		case 24:
			result.add(new Integer(Traxboard.EW));
			result.add(new Integer(Traxboard.ES));
			return result;
		case 40:
			result.add(new Integer(Traxboard.EW));
			result.add(new Integer(Traxboard.EN));
			return result;
		case 130:
			result.add(new Integer(Traxboard.SN));
			result.add(new Integer(Traxboard.SW));
			return result;
		default:
			// This should never happen
			throw new RuntimeException("This should never happen. (009)");
		}
	}

	// Unit test
	public static void main(String[] args) 
	{
		Traxboard t;
		ArrayList<String> moves;
		ArrayList<String> aMove;
		boolean result;

		result = true;

		try {
			t = new Traxboard();
			t.makeMove("a1c");
			t.makeMove("b1s");
			moves = t.uniqueMoves();
			if (moves.size() != 17) {
				result = false;
				System.err.println("Test 1: FAILED\n");
			} else {
				System.err.println("Test 1: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("b1s");
			t.makeMove("a0/");
			if (t.getNumOfTiles() != 4) {
				result = false;
				System.err.println("Test 2: FAILED\n");
			} else {
				System.err.println("Test 2: OK\n");
			}

			t = new Traxboard();
			t.makeMove("@0/");
			t.makeMove("B1/");
			t.makeMove("@1/");
			t.makeMove("A2/");
			t.makeMove("C0+");
			t.makeMove("@3\\");
			t.makeMove("A2+");
			t.makeMove("A4\\");
			t.makeMove("B5+");
			t.makeMove("E5+");
			t.makeMove("E1/");
			t.makeMove("E0+");
			t.makeMove("E0+");
			t.makeMove("B8\\");
			moves = t.uniqueMoves();
			if (moves.size() != 43) {
				System.err.println("Test 3: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 3: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("@1+");
			t.makeMove("c1u");
			t.makeMove("b2\\");
			t.makeMove("d2u");
			t.makeMove("c3+");
			t.makeMove("c1r");
			t.makeMove("b0\\");
			t.makeMove("a1u");
			t.makeMove("@2+");
			t.makeMove("d1s");
			t.makeMove("f6\\");
			t.makeMove("d7r");
			t.makeMove("d0/");
			t.makeMove("e2d");
			t.makeMove("f2\\");
			t.makeMove("e4s");
			if (t.isGameOver() != WHITE) {
				System.err.println("Test 4: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 4: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("a1s");
			t.makeMove("a1s");
			t.makeMove("a1s");
			t.makeMove("a1s");
			t.makeMove("c2s");
			moves = t.uniqueMoves();
			if (moves.size() != 15) {
				System.err.println("Test 5: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 5: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1c");
			t.makeMove("1ar");
			t.makeMove("a1d");
			t.makeMove("1ar");
			moves = t.uniqueMoves();
			if (moves.size() != 11) {
				System.err.println("Test 6: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 6: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1c");
			t.makeMove("a1d");
			moves = t.uniqueMoves();
			if (moves.size() != 9) {
				System.err.println("Test 7: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 7: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("1as");
			t.makeMove("1as");
			t.makeMove("a1d");
			t.makeMove("c2d");
			System.out.println(t + t.getBorder());
			moves = t.uniqueMoves();
			if (moves.size() != 12) {
				System.err.println("Test 8: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 8: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1c");
			t.makeMove("1ar");
			t.makeMove("b2u");
			t.makeMove("b3l");
			moves = t.uniqueMoves();
			if (moves.size() != 11) {
				System.err.println("Test 9: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 9: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1c");
			t.makeMove("1ar");
			t.makeMove("b2u");
			t.makeMove("b3l");
			t.makeMove("c3u");
			t.makeMove("c4l");
			moves = t.uniqueMoves();
			if (moves.size() != 13) {
				System.err.println("Test 10: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 10: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("1al");
			t.makeMove("a3r");
			t.makeMove("b3s");
			t.makeMove("@1+");
			moves = t.uniqueMoves();
			if (moves.size() != 12) {
				System.err.println("Test 11: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 11: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("1as");
			t.makeMove("1as");
			t.makeMove("1as");
			t.makeMove("1as");
			moves = t.uniqueMoves();
			if (moves.size() != 9) {
				System.err.println("Test 12: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 12: OK\n");
			}

			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("1as");
			t.makeMove("1as");
			t.makeMove("1as");
			moves = t.uniqueMoves();
			if (moves.size() != 7) {
				System.err.println("Test 13: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 13: OK\n");
			}
			t = new Traxboard();
			t.makeMove("a1s");
			t.makeMove("b1s");
			t.makeMove("1as");
			if (!t.getBorder().equals("BB-WW-B-W+B-W")) {
				System.err.println("Test 14: FAILED\n");
				result = false;
			} else {
				System.err.println("Test 14: OK\n");
			}
			t = new Traxboard();
			t.makeMove("a1c");
			t.makeMove("b1d");
			t.makeMove("a2l");
			t.makeMove("a3l");
			t=t.rotate();
			System.out.println(t);
			t=t.rotate();
			System.out.println(t);
			t=t.rotate();
			System.out.println(t);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		if (result == false) {
			System.exit(-1);
		} else {
			System.out.println("All tests went OK.");
			System.exit(0);
		}
	}

}
