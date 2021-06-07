package a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import a2.FourInLine.Column;
import a2.FourInLine.GameState;

import static a2.FourInLine.*;
import static java.util.stream.Collectors.toList;

public class GameTree {
    static class Tree {
         GameState game;
         List<Move> moves;
         public Tree(GameState g, List<Move> m) {
             game = g;
             moves = m;
         }
    }
    static class Move {
        ColumnNum move;
        Tree tree;
        public Move(ColumnNum m, Tree t) {
            move = m;
            tree = t;
        }
    }

    static Move subGameTree(GameState game, ColumnNum c, Player player, int depth) {
        return new Move(c, gameTree(otherPlayer(player),depth - 1, dropPiece(game, c, pieceOf(player))));
    }

//    static Move subGameTree(GameState game, ColumnNum c, Player player, int depth) {
//        //deep copy.not alter original game state
//        List<Column> copy = game.stream()
//                        .map(col -> new Column(col.stream().map(p -> new Piece(p.toString()) {}).collect(Collectors.toList())))
//                        .collect(Collectors.toList());
//        GameState gs = new GameState();
//        gs.addAll(copy);
//        return new Move(c, gameTree(otherPlayer(player),depth - 1, dropPiece(gs, c, pieceOf(player))));
//    }

    // Recursively build the game tree using allViableColumns to get all possible
    // moves (introduce depth as the function is not lazy).  Note that the tree bottoms out once the game is won

    static Tree gameTree(Player player, int depth, GameState game)  {

        Optional<Player> w = winner(game);
        if (w.isPresent()) {
            return new Tree(game, new ArrayList<>());
        } else if (depth == 0) {
            return new Tree(game, new ArrayList<>());
        } else {
            List<Move> moves = allViableColumns(game).stream().map(n -> {
                return subGameTree(game, n, player, depth);
            }).collect(toList());
            return new Tree(game, moves);

        }

    }

    //Estimate the value of a position for a player. This implementation only
    //assigns scores based on whether or not the player has won the game.  This is
    //the simplest possible way of doing it, but it results in an
    //overly-pessimistic AI.
    //
    //The "cleverness" of the AI is determined by the sophistication of the
    //estimate function.
    //Some ideas for making the AI smarter:
    //1) A win on the next turn should be worth more than a win multiple turns
    //later.  Conversely, a loss on the next turn is worse than a loss several
    //turns later.
    //2) Some columns have more strategic value than others.  For example, placing
    //pieces in the centre columns gives you more options.
    //3) It's a good idea to clump your pieces together so there are more ways you
    //could make four in a line.
//    static int estimate(Player player, GameState game) {
//        if (fourInALine(pieceOf(player), game))
//            return 100;
//        else if (fourInALine(pieceOf(otherPlayer(player)), game))
//            return -100;
//        else
//            return 0;
//    }
    
    static int estimate(Player player, GameState game) {
//    	System.out.println("++++++++++++++");
//    	System.out.println(showGameState(game));
    	int score = 0;
        if (fourInALine(pieceOf(player), game)) {
            score = 100;
        }
        else if (fourInALine(pieceOf(otherPlayer(player)), game)) {
        	score = -100;
        }     
        
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(4))) score = 90;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(4))) score = -90;
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(3))) score = 80;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(3))) score = -80;
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(5))) score = 80;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(5))) score = -80;
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(2))) score = 70;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(2))) score = -70;
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(6))) score = 70;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(6))) score = -70;
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(1))) score = 60;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(1))) score = -60;
        else if (hasPieceNearby(game, pieceOf(player), new ColumnNum(7))) score = 60;
        else if (hasPieceNearby(game, pieceOf(otherPlayer(player)), new ColumnNum(7))) score = -60;
        
        else if (inColumn(game, pieceOf(player), new ColumnNum(4)))  score = 50;
        else if (inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(4)))  score = -50;
        else if (inColumn(game, pieceOf(player), new ColumnNum(3)) || inColumn(game, pieceOf(player), new ColumnNum(5))) score = 40;
        else if (inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(3)) || inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(5))) score = -40;
        else if (inColumn(game, pieceOf(player), new ColumnNum(2)) || inColumn(game, pieceOf(player), new ColumnNum(6))) score = 30;
        else if (inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(2)) || inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(6))) score = -30;
        else if (inColumn(game, pieceOf(player), new ColumnNum(1)) || inColumn(game, pieceOf(player), new ColumnNum(7))) score = 20;
        else if (inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(1)) || inColumn(game, pieceOf(otherPlayer(player)), new ColumnNum(7))) score = -20;
//        System.err.println(score);
        return score;
    }
    
    static boolean inColumn(GameState game, Piece piece, ColumnNum cNum) {
    	try {
			if (game.get(cNum.indexOfColumn()).size() == 1 && game.get(cNum.indexOfColumn()).get(0).equals(piece)) return true;
		} catch (Exception e) {
			return false;
		}
    	return false;
    }
    
    static boolean hasPieceNearby(GameState game, Piece piece, ColumnNum cNum) {
    	GameState g = initGameState();
    	for (int i = 0; i < game.size(); i ++) {
    		Column c = new Column();
        	for (int j = 0; j < game.get(i).size(); j ++) { c.add(game.get(i).get(j)); }
    		g.set(i, c);
    	}
    	int hight = g.get(cNum.indexOfColumn()).size();
    	if (hight == 0) {
    		return false;
    	}
    	else {
	    	for (int i = 0; i < g.size(); i ++) {
	    		g.set(i, fillBlank(otherPiece(piece), g.get(i)));
	    	}
	    	if (hight == 1 && g.get(cNum.indexOfColumn()).get(hight-1).equals(piece)) {
	    		if (cNum.indexOfColumn() == 0) {
	    			if (g.get(cNum.indexOfColumn() + 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() + 1).get(NRows-hight).equals(piece)) return true;
	    		}
	    		else if (cNum.indexOfColumn() == NColumns-1) {
	    			if (g.get(cNum.indexOfColumn() - 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight).equals(piece)) return true;
	    		}
	    		else {
	    			if ( g.get(cNum.indexOfColumn() + 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() + 1).get(NRows-hight).equals(piece) ||
	    					g.get(cNum.indexOfColumn() - 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight).equals(piece)) return true;
	    		}
	    	}
	    	else if (hight > 1 && hight < NRows && g.get(cNum.indexOfColumn()).get(hight-1).equals(piece)) {
	    		if (cNum.indexOfColumn() == 0) {
	    			if (g.get(cNum.indexOfColumn() + 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() + 1).get(NRows-hight).equals(piece) ||
	    					g.get(cNum.indexOfColumn() + 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn()).get(NRows-hight).equals(piece)) return true;
	    		}
	    		else if (cNum.indexOfColumn() == NColumns-1) {
	    			if (g.get(cNum.indexOfColumn() - 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight).equals(piece) ||
	    					g.get(cNum.indexOfColumn() - 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn()).get(NRows-hight).equals(piece)) return true;
	    		}
	    		else {
	    			if ( g.get(cNum.indexOfColumn() + 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() + 1).get(NRows-hight).equals(piece) ||
	    					g.get(cNum.indexOfColumn() - 1).get(NRows-hight -1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight).equals(piece) ||
	    					g.get(cNum.indexOfColumn() + 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight +1).equals(piece) ||
	    					g.get(cNum.indexOfColumn()).get(NRows-hight).equals(piece)) return true;
	    		}
	    	}
	    	else if (hight == NRows && g.get(cNum.indexOfColumn()).get(hight-1).equals(piece))  {
	    		if (cNum.indexOfColumn() == 0) {
	    			if (g.get(cNum.indexOfColumn() + 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn() + 1).get(NRows-hight).equals(piece)) return true;
	    		}
	    		else if (cNum.indexOfColumn() == NColumns-1) {
	    			if (g.get(cNum.indexOfColumn() - 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight).equals(piece)) return true;
	    		}
	    		else {
	    			if ( g.get(cNum.indexOfColumn() + 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn() + 1).get(NRows-hight).equals(piece) ||
	    					g.get(cNum.indexOfColumn() - 1).get(NRows-hight +1).equals(piece) || g.get(cNum.indexOfColumn() - 1).get(NRows-hight).equals(piece)) return true;
	    		}
	    	}
    	}
//    	System.err.println(showGameState(game));
//    	System.err.println(showGameState(g));
    	return false;
    }

    static ColumnNum maxmini(Player player, Tree tree)  {
        if (tree.moves.isEmpty())
            throw new RuntimeException("The AI was asked to make a move, but there are no moves possible.  This cannot happen");
        else {

            return  tree.moves.stream()
                    .collect(Collectors.maxBy((Move a, Move b) -> {
                        return minimaxP(player, a.tree) - minimaxP(player, b.tree);
                    })).get().move;
        }

    }


    // Maximise the minimum utility of player making a move.  Do this when it is the
    // player's turn to find the least-bad move, assuming the opponent will play
    // perfectly.

    static int maxminiP(Player player, Tree tree) {
        if (tree.moves.isEmpty())
            return estimate(player, tree.game);
        else {
            return Collections.max(tree.moves.stream().map(m -> minimaxP(player, m.tree)).collect(toList()));
        }
    }

    // Minimise the maximum utility of player making a move.  Do this when it is the
    // opponent's turn, to simulate the opponent choosing the move that results in
    // the least utility for the player.

    static int minimaxP(Player player, Tree tree) {
        if (tree.moves.isEmpty())
            return estimate(player, tree.game);
        else {
            return Collections.min(tree.moves.stream().map(m -> maxminiP(player, m.tree)).collect(toList()));
        }
    }

    // Determine the best move for computer player

    public static Function<GameState, ColumnNum> aiMove(int lookahead, Player player) {
        return x -> {
            return maxmini(player,gameTree(player, lookahead, x));
        };
    }

}
