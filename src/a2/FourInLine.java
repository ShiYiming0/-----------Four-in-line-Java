package a2;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.print.CancelablePrintJob;

import static a2.FourInLine.showGameState;
import static java.util.stream.Collectors.*;

import java.awt.font.NumericShaper.Range;

public class FourInLine {


    // Declare some constants

    static int NColumns = 7;
    static int NRows = 6;

    // A player is either the red player, or the blue player

    public static Player redPlayer = Player.redPlayer;
    public static Player bluePlayer = Player.bluePlayer;


    // A piece is either a red piece or a blue piece

    public static Piece redPiece = Piece.redPiece;
    public static Piece bluePiece = Piece.bluePiece;


    // A column is a list of Pieces.  The first element of the list represents the top of
    // the column, e.g.
    // row 6 --
    // row 5 --
    // row 4 -- RedPiece   <- first element of the list
    // row 3 -- RedPiece
    // row 2 -- BluePiece
    // row 1 -- RedPiece   <- last element in the list
    // The list for this column would be [redPiece, redPiece, bluePiece, redPiece]
    // Now, to add a piece to the TOP of a column, just create a new column
    // with that piece and append the rest of the old column to it

    // a Column is a list of pieces

    public static class Column extends ArrayList<Piece> {
        public Column() {
        }
        public Column(List<Piece> l) {
            this.addAll(l);
        }
    }

    // The GameState is a list of Columns

     public static class GameState extends ArrayList<Column> implements Cloneable{
        public GameState() {
        }
        public GameState(List<List<Piece>> g) {
            List<Column> c = g.stream().map(Column::new).collect(toList());
            this.addAll(c);
        }
        @Override  
        public Object clone() {  
            GameState g = null;  
            g = (GameState)super.clone();  
            return g;  
        }  
     }


    // ColumnNums are 1-based, but list indices are 0-based.  indexOfColumn converts
    // a ColumnNum to a list index.

    public static class ColumnNum {
        int index;
        public ColumnNum(int index) {
            GameState s;
            this.index = index;
        }
        public int indexOfColumn() {
            return index - 1;
        }
        public String toString() {
            return "" + index;
        }
    }

    //
    //   Convert a column to a string of the form "rBrrBB", or "   rrB".  The string
    //   must have length 6.  If the column is not full, then the list should be
    //   prefixed with an appropriate number of spaces
    //

    //   Convert a column to a string of the form "rBrrBB", or "   rrB".  The string
    //   must have length 6.  If the column is not full, then the list should be
    //   prefixed with an appropriate number of spaces

    public static String showColumn(Column xs) {
    	String col = "";
    	for (Piece p : xs) {
    		col += p.toString();
    	}
    	int space = 6 - col.length();
    	for (int i = 0; i < space; i++) {
    		col = " " + col;
    	}
        return col; // replace this with implementation
    }


    //
    //  Convert a GameState value to a string of the form:
    //  "    r        \n
    //   r   r   B   r\n
    //   B B r   B r B\n
    //   r B r r B r r\n
    //   r B B r B B r\n
    //   r B r r B r B"
    //   Useful functions:
    //     showColumn
    //       (which you already defined)
    //     and transposes a list of lists using streams,
    //       so List(List(1,2,3), List(4,5,6)) becomes List(List(1,4), List(2,5), List(3,6))

    public static String showGameState(GameState xs) {
    	String gs = "";
    	for (int i = 0; i < NRows; i ++) {
	    	for (Column col : xs) {
	    		gs += showColumn(col).charAt(i) + " ";
	    	}
	    	gs = gs.substring(0,gs.length() -1); // remove last space
	    	gs += "\n";
    	}
    	gs = gs.substring(0,gs.length() -1);	// remove last \n
    	return gs;
    }

    // Which pieces belong to which players?

    public static Piece pieceOf(Player player)  {
    	if (player == redPlayer) { return redPiece; }
    	else { return bluePiece; }
    }

    // Given a player, who is the opposing player?

    public static Player otherPlayer(Player player) {
    	if (player == redPlayer) { return bluePlayer; }
    	else { return redPlayer; }
    }


    // Given a piece, what is the colour of the other player's pieces?

    public static Piece otherPiece(Piece piece) {
    	if (piece == redPiece) { return bluePiece; }
    	else if (piece == bluePiece) { return redPiece; }
    	else {return null;}
    }


    // The initial GameState, all columns are empty.  Make sure to create the proper
    // number of columns

    public static GameState initGameState() {
    	GameState g = new GameState();
    	for (int i = 0; i < NColumns; i++) {
    		Column column = new Column();
    		g.add(new Column());
    	}
    	return g;
    }


    // Check if a column number is valid (i.e. in range)

    public static boolean isValidColumn(ColumnNum c) {
    	if ( 0 <= c.indexOfColumn() && c.indexOfColumn() < NColumns ) { return true; }
    	else { return false; }
    }


    // Check if a column is full (a column can hold at most nRows of pieces)

    public static boolean isColumnFull(Column column) {
    	if (column.size() < NRows) { return false; }
    	else { return true; }
    }


    // Return a list of all the columns which are not full (used by the AI)

    public static List<ColumnNum> allViableColumns(GameState game) {
    	List<ColumnNum> list = new ArrayList<>();
    	for (int i = 0; i < game.size(); i++) {
    		if (!isColumnFull(game.get(i))) { list.add(new ColumnNum(i+1)); }
    	}
    	return list;
    }

    // Check if the player is able to drop a piece into a column

    public static boolean canDropPiece(GameState game, ColumnNum columnN) {
    	for (ColumnNum cn : allViableColumns(game)) {
    		if (columnN.indexOfColumn() == cn.indexOfColumn()) { return true; }
    	}
    	return false;
    }

    // Drop a piece into a numbered column, resulting in a new gamestate

    public static GameState dropPiece(GameState game, ColumnNum columnN, Piece piece) {
    	GameState g = initGameState();
    	for (int i = 0; i < game.size(); i ++) {
    		Column c = new Column();
        	for (int j = 0; j < game.get(i).size(); j ++) { c.add(game.get(i).get(j)); }
    		g.set(i, c);
    	}
    	
    	if (canDropPiece(g, columnN)) {
    		Column c = g.get(columnN.indexOfColumn());
    		c.add(0, piece);
    		//g.set(columnN.indexOfColumn(), c);
    	}
//    	System.err.println(showGameState(g));
//    	System.err.println(showGameState(game));
    	return g;
    }

    // Are there four pieces of the same colour in a column?

    static boolean fourInCol(Piece piece, Column col) {
    	int count = 1;
    	for (int i = 0; i < col.size()-1; i ++) {
    		if (col.get(i) == col.get(i+1) && col.get(i) == piece) { count++; }
    		else { count = 1; }
    		if (count == 4) { return true; }
    	}
    	return false;
    }

    public static boolean fourInColumn(Piece piece, GameState game) {
    	for (Column c : game) {
    		if (fourInCol(piece, c)) { return true; }
    	}
    	return false;
    }


    // transposes gameboard, assumes all columns are full
    static GameState transpose(GameState g) {
        return new GameState(IntStream.range(0, g.get(0).size())
                .mapToObj(i -> g.stream()
                        .map(l -> l.get(i))
                        .collect(toList()))
                .collect(toList()));
    }
    // A helper function that fills up a column with pieces of a certain colour.  It
    // is used to fill up the columns with pieces of the colour that
    // fourInRow/fourInDiagonal is not looking for.  This will make those functions
    // easier to define.

    static Column fillBlank(Piece piece, Column column) {
    	Column c = new Column();
    	for (int i = 0; i < column.size(); i ++) { c.add(column.get(i)); }
    	while (!isColumnFull(c)) {
    		c.add(0, piece);
    	}
    	return c;
    }

    // Are there four pieces of the same colour in a row?  Hint: use fillBlanks and
    // transpose to reduce the problem to fourInColumn

    public static boolean fourInRow(Piece piece, GameState game) {
    	GameState g = initGameState();
    	for (int i = 0; i < game.size(); i ++) {
    		g.set(i, fillBlank(otherPiece(piece), game.get(i)));
    	}
    	g = transpose(g);
    	if (fourInColumn(piece, g)) { return true; }
    	else { return false; }
    }


    // Another helper function for fourInDiagonal.  Remove n pieces from the top of
    // a full column and add blanks (of the colour we're not looking for) to the
    // bottom to make up the difference.  This makes fourDiagonal easier to define.

    static Column shift(int n, Piece piece, Column column) {
    	Column c = new Column();
    	for (int i = 0; i < column.size(); i ++) { c.add(column.get(i)); }
    	for (int i = 0; i < n; i ++) {
    		Piece p0 = c.get(0);
    		for (int j = 0; j < NRows-1; j ++) { c.set(j, c.get(j+1)); }
    		c.set(NRows-1, p0);
    	}
    	//System.err.println(column);
    	return c;
    }

    // Are there four pieces of the same colour diagonally?  Hint: define a helper
    // function using structural recursion over the gamestate, and using shift and fourInRow.

    static boolean fourDiagonalHelper(GameState g, Piece piece) {
    	GameState g1 = initGameState();
    	for (int i = 0; i < g.size(); i ++) {
    		g1.set(i, fillBlank(otherPiece(piece), g.get(i)));
    		g1.set(i, shift(i, otherPiece(piece), g1.get(i)));
    	}
    	if (fourInRow(piece, g1)) { return true; }
    	GameState g2 = initGameState();
    	for (int i = 0; i < g.size(); i ++) {
    		g2.set(i, fillBlank(otherPiece(piece), g.get(i)));
    		g2.set(i, shift(NRows - i, otherPiece(piece), g2.get(i)));
    	}
    	if (fourInRow(piece, g2)) { return true; }
    	return false;
    }

    public static boolean fourDiagonal(Piece piece, GameState game) {
    	return fourDiagonalHelper(game, piece);
    }

    // Are there four pieces of the same colour in a line (in any direction)

    public static boolean fourInALine(Piece piece, GameState game) {
    	if (fourInColumn(piece, game) || fourInRow(piece, game) || fourDiagonal(piece, game)) { 
    		//System.err.println(showGameState(game));
    		return true; 
    	
    	}
    	else { return false; }
    }

    // Who won the game.  Returns an Optional since it could be that no one has won the
    // game yet.

    public static Optional<Player> winner(GameState game) {
    	Optional<Player> win;
    	if (fourInALine(pieceOf(bluePlayer), game)) { win = Optional.ofNullable(bluePlayer); }
    	else if (fourInALine(pieceOf(redPlayer), game)) { win = Optional.ofNullable(redPlayer); }
    	else { win = Optional.empty(); }
    	return win;
    }

}
