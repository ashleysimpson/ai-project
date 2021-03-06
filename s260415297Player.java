// first thing i did was find the best move and always choose that for only one level
// second i added the checking for multiple levels on the same move selection
// third implement minimax
// fourth implement alphabeta
// fifth i started with a specific move that ends with a high chance of winning
// sixth i updated my heuristic

package omweso;
//TODO this needs FIXXXXXIIIINNNNGGGGGGGG

import boardgame.Board;
import boardgame.BoardState;
import boardgame.Move;
import boardgame.Player;

import java.util.ArrayList;
import java.util.Random;

/** A random Omweso player. */
public class s260415297Player extends Player {
    Random rand = new Random();

    /** You must provide a default public constructor like this,
     * which does nothing but call the base-class constructor with
     * your student number. */
    public s260415297Player() { super("260415297"); }
    public s260415297Player(String s) { super(s); }

    public Board createBoard() { return new CCBoard(); }

    /** Use this method to take actions when the game is over. */
    public void gameOver( String msg, BoardState bs) {
        CCBoardState board_state = (CCBoardState) bs;

        if(board_state.haveWon()){
            System.out.println("I won!");
        }else if(board_state.haveLost()){
            System.out.println("I lost!");
        }else if(board_state.tieGame()){
            System.out.println("Draw!");
        }else{
            System.out.println("Undecided!");
        }
    }

    /** Implement a very stupid way of picking moves. */
    public Move chooseMove(BoardState bs)
    {
        // Cast the arguments to the objects we want to work with.
        CCBoardState board_state = (CCBoardState) bs;

        // Get the contents of the pits so we can use it to make decisions.
        int[][] pits = board_state.getBoard();

        // Our pits in first row of array, opponent pits in second row.
        int[] my_pits = pits[0];
        int[] op_pits = pits[1];

        if(!board_state.isInitialized()){
            // Code for setting up our initial position. Also, if your agent needs to be
            // set-up in any way (e.g. loading data from files) you should do it here.

            //CCBoardState.SIZE is the width of the board, in pits.
            int[] initial_pits = new int[2 * CCBoardState.SIZE];

            // Make sure your initialization move includes the proper number of seeds.
            int num_seeds = CCBoardState.NUM_INITIAL_SEEDS;

            if(board_state.playFirst()){
                // specific setup
            	initial_pits[8] = 7;
            	initial_pits[9] = 4;
            	initial_pits[10] = 3;
            	initial_pits[11] = 4;
            	initial_pits[12] = 3;
            	initial_pits[13] = 2;
            	initial_pits[14] = 9;
            }else{
            	// specific setup
            	initial_pits[8] = 7;
            	initial_pits[9] = 4;
            	initial_pits[10] = 3;
            	initial_pits[11] = 4;
            	initial_pits[12] = 3;
            	initial_pits[13] = 2;
            	initial_pits[14] = 9;
            }

            return new CCMove(initial_pits);
        }else{
            // Play a normal turn
            ArrayList<CCMove> moves = board_state.getLegalMoves();
            
            int[][] board = board_state.getBoard();
            int choice = bestMove(board);
            return moves.get(choice);

        }
    }
    
    // simple method to find the best move for only one seed sow
    private int bestMove(int[][] board) {
    	
    	int depth = 8;
    	int mostSeedsTaken = -1000;
		int bestMoveTaken = 0;
		int j = 0;
    	
    	// go through all seed pits once
    	for (int i = 0; i < board[0].length; i++) {
    		
    		// player must play specific move to increase winning chance
    		if (board[0][8] == 7 && board[0][9] == 4 && board[0][10] == 3 && board[0][11] == 4 &&
    				board[0][12] == 3 && board[0][13] == 2 && board[0][14] == 9) {
    			bestMoveTaken = 5;
    			break;
    		}
    		
    		// keep track of the next move location
    		int nextMove = i;
    		int cycleTracker = 0;
    		
    		// if invalid move then continue
    		if (board[0][i] < 2) {
    			continue;
    		}
    		
    		// create a board that can be updated
    		int[][] updatedBoard;
    		
    		// handles the most amount of seeds captured by a play
    		int captureMade = captureAmount(i, board, true);
    		int previousCapture = captureMade;
    		int prevMove = 0;
    		
    		// if a capture made then need to deal with different update of board
    		updatedBoard = updateBoard(i,board, true);
    		boolean previousWasACapture = captureMade > 0;
    		
    		// next move will be somewhere new in the board
    		if (!previousWasACapture) {
    			nextMove = (nextMove + board[0][i]) % 16;
    		}
    		
    		// go through the relay and capture loop until the end
    		while (updatedBoard[0][nextMove] > 1 && cycleTracker < 180) {
    			
    			// play the move and update the board
    			captureMade = captureMade + captureAmount(nextMove, updatedBoard, true);
    			previousCapture = captureMade - previousCapture;
    			prevMove = nextMove;
    			
    			// check for next move
    			previousWasACapture = previousCapture > 0;
    			if (!previousWasACapture) {
    				nextMove = (prevMove + updatedBoard[0][prevMove]) % 16;
    			}
    			
    			// update the board after all work computed
    			updatedBoard = updateBoard(prevMove, updatedBoard, true);

    			// set previous for next round
    			previousCapture = captureMade;
    			
    			// check for infinite issue
    			cycleTracker++;
    		} 		
    		
    		// check winning condition
    		boolean winCheck = true;
    		for (int a = 0; a < updatedBoard[1].length; a++) {
    			if (updatedBoard[1][a] > 1) {
    				winCheck = false;
    				break;
    			}
    		}
    		if (winCheck) {
    			bestMoveTaken = j;
    			break;
    		}
    		
    		// call minimax from this point
    		int bestAlphaBeta = alphabeta(depth,updatedBoard,-100000,100000,false);
    		
    		System.out.println("Without " + i + " with value " + bestAlphaBeta); 
    		
    		// make sure to make the best move
    		if (mostSeedsTaken < bestAlphaBeta + captureMade) {
    			mostSeedsTaken = bestAlphaBeta + captureMade;
    			
    			System.out.println("With " + i + " with value " + mostSeedsTaken); 
    			bestMoveTaken = j;
    		}
    		
    		// keeps track of the valid moves
    		j=j+1;
    	}
    	
        return bestMoveTaken;

    }
    
    // alphabeta search
    private int alphabeta(int depth, int[][] board, int alpha, int beta, boolean maxPlayer) {
    	    	
    	// setup the trackers
    	int bestMove = -2000;
    	int p1 = 0;
    	int p2 = 1;
    	if (!maxPlayer) {
    		p1 = 1;
    		p2 = 0;
    		bestMove = 2000;
    	}
    	
    	// check for winning
		boolean winCheck = true;
		for (int a = 0; a < board[p2].length; a++) {
			if (board[p2][a] > 1) {
				winCheck = false;
				break;
			}
		}
    	
    	// return the best or worse value that can be returned
    	if (depth == 0 || winCheck) {
    		
    		// if win check then signal good or bad scenario
    		if (winCheck) {
    			if (maxPlayer) {
    				return 1000;
    			} else {
    				return -1000;
    			}
    		}
    		return checkPositioning(board);
    	}
    	
    	// go through all seed pits once, same approach as before but a little different for minimax
    	for (int i = 0; i < board[p1].length; i++) {
    		
    		// keep track of the next move location
    		int nextMove = i;
    		int[][] updatedBoard;
    		int infiniteTracker = 0;
    		int move;
    		
    		// if invalid move then continue
    		if (board[p1][i] < 2) {
    			continue;
    		}
    		
    		// handles the most amount of seeds captured by a play
    		int captureMade = captureAmount(i, board, maxPlayer);
    		int previousCapture = captureMade;
    		int prevMove = 0;
    		
    		// if a capture made then need to deal with different update of board
    		updatedBoard = updateBoard(i,board, maxPlayer);
    		boolean previousWasACapture = captureMade > 0;
    		
    		// next move will be somewhere new in the board
    		if (!previousWasACapture) {
    			nextMove = (nextMove + board[p1][i]) % 16;
    		}   		  		
    		
    		// go through the relay and capture loop until the end
    		while (updatedBoard[p1][nextMove] > 1 && infiniteTracker < 180) {
    			
    			// play the move and update the board
    			captureMade = captureMade + captureAmount(nextMove, updatedBoard, maxPlayer);
    			previousCapture = captureMade - previousCapture;
    			prevMove = nextMove;
    			
    			// check for next move
    			previousWasACapture = previousCapture > 0;
    			if (!previousWasACapture) {
    				nextMove = (prevMove + updatedBoard[p1][prevMove]) % 16;
    			}
    			
    			// update the board after all work computed
    			updatedBoard = updateBoard(prevMove, updatedBoard, maxPlayer);

    			// set previous for next round
    			previousCapture = captureMade;
    			
    			// check for infinite issue
    			infiniteTracker++;
    		}
    		
    		// search other moves with minimax and make the appropriate move
    		if (maxPlayer) {
        		move =  alphabeta(depth-1, updatedBoard,alpha,beta,false) + captureMade;
        		
        		// if max player then want to make the best move
        		if (bestMove < move) {
        			bestMove = move;
        		}
        		
        		// alpha beta algorithm
        		if (alpha < bestMove) {
        			alpha = bestMove;
        		}  
        		// if beta equal or greater than alpha then can prune
        		if (beta <= alpha) {
        			break;
        		}
        		
        	} else {
        		move =  alphabeta(depth-1, updatedBoard,alpha,beta,true) - captureMade;
        		
        		// if min player then want to make the worst move
        		if (bestMove > move) {
        			bestMove = move;
        		}
        		
        		// alpha beta algorithm
        		if (beta > bestMove) {
        			beta = bestMove;
        		}
        		// if beta equal or greater than alpha then can prune
        		if (beta <= alpha) {
        			break;
        		}
        	}
    	}
    	
    	// if not at depth then just return the value
    	return bestMove;
    }
    
    
    // updates the board based on a move made that captures
    private int[][] updateBoard(int selection, int[][] oldBoard, boolean isMaxPlayer) {
    	
    	// setup the board conditions for max and min players
    	int p1 = 0;
    	int p2 = 1;
    	if (!isMaxPlayer) {
    		p1 = 1;
    		p2 = 0;
    	}
    	
    	// create a new board to return
    	int[][] board = new int[2][16];
    	for (int i = 0; i < board.length; i++) {
    		for (int j = 0; j < board[i].length; j++) {
    			board[i][j] = oldBoard[i][j];
    		} 		
    	}
    	// calculate the last pit
    	int lastPit = (selection + board[p1][selection]) % 16;
    	
    	// remove the pits from the selection pit
    	board[p1][selection] = 0;
    	
    	// fill in pieces that get added as the move is taken
    	for (int i = 1; i < oldBoard[p1][selection]; i++) {
    		board[p1][(i+selection) % 16] = board[p1][(i+selection) % 16] + 1;
    	}
    	
    	// check for each condition where capture occurs and update the board
    	// also update board where capture doesn't occur
		if (lastPit == 15) {
			if (board[p1][15] > 0 && board[p2][8] > 0 && board[p2][7] > 0) {
				board[p1][selection] = board[p2][8] + board[p2][7];
				board[p1][15] = board[p1][15] + 1;
				board[p2][8] = 0;
				board[p2][7] = 0;
			} else {
				board[0][15] = board[0][15] + 1;
			}
		} else if (lastPit == 14) {
			if (board[p1][14] > 0 && board[p2][6] > 0 && board[p2][9] > 0) {
				board[p1][selection] = board[p2][6] + board[p2][9];
				board[p1][14] = board[p1][14] + 1;
				board[p2][6] = 0;
				board[p2][9] = 0;
			} else {
				board[0][14] = board[0][14] + 1;
			}
		} else if (lastPit == 13) {
			if (board[p1][13] > 0 && board[p2][5] > 0 && board[p2][10] > 0) {
				board[p1][selection] = board[p2][5] + board[p2][10];
				board[p1][13] = board[p1][13] + 1;
				board[p2][5] = 0;
				board[p2][10] = 0;
			} else {
				board[0][13] = board[0][13] + 1;
			}
		} else if (lastPit == 12) {
			if (board[p1][12] > 0 && board[p2][4] > 0 && board[p2][11] > 0) {
				board[p1][selection] = board[p2][4] + board[p2][11];
				board[p1][12] = board[p1][12] + 1;
				board[p2][4] = 0;
				board[p2][11] = 0;
			} else {
				board[0][12] = board[0][12] + 1;
			}
		} else if (lastPit == 11) {
			if (board[p1][11] > 0 && board[p2][3] > 0 && board[p2][12] > 0) {
				board[p1][selection] = board[p2][3] + board[p2][12];
				board[p1][11] = board[p1][11] + 1;
				board[p2][3] = 0;
				board[p2][12] = 0;
			} else {
				board[0][11] = board[0][11] + 1;
			}
		} else if (lastPit == 10) {
			if (board[p1][10] > 0 && board[p2][2] > 0 && board[p2][13] > 0) {
				board[p1][selection] = board[p2][2] + board[p2][13];
				board[p1][10] = board[p1][10] + 1;
				board[p2][2] = 0;
				board[p2][13] = 0;
			} else {
				board[0][10] = board[0][10] + 1;
			}
		} else if (lastPit == 9) {
			if (board[p1][9] > 0 && board[p2][1] > 0 && board[p2][14] > 0) {
				board[p1][selection] = board[p2][1] + board[p2][14];
				board[p1][9] = board[p1][9] + 1;
				board[p2][1] = 0;
				board[p2][14] = 0;
			} else {
				board[0][9] = board[0][9] + 1;
			}
		} else if (lastPit == 8) {
			if (board[p1][8] > 0 && board[p2][0] > 0 && board[p2][15] > 0) {
				board[p1][selection] = board[p2][0] + board[p2][15];
				board[p1][8] = board[p1][8] + 1;
				board[p2][0] = 0;
				board[p2][15] = 0;
			} else {
				board[0][8] = board[0][8] + 1;
			}
		} else if (lastPit == 7) {
			board[p1][7] = board[p1][7] + 1;
		} else if (lastPit == 6) {
			board[p1][6] = board[p1][6] + 1;
		} else if (lastPit == 5) {
			board[p1][5] = board[p1][5] + 1;
		} else if (lastPit == 4) {
			board[p1][4] = board[p1][4] + 1;
		} else if (lastPit == 3) {
			board[p1][3] = board[p1][3] + 1;
		} else if (lastPit == 2) {
			board[p1][2] = board[p1][2] + 1;
		} else if (lastPit == 1) {
			board[p1][1] = board[p1][1] + 1;
		} else {
			board[p1][0] = board[p1][0] + 1;
		}
		
		return board;
    }
    
    // returns the amount of captured seeds from this pit (if any)
    private int captureAmount(int selection, int[][] board, boolean isMaxPlayer) {
    	
    	// setup the board conditions for max and min players
    	int p1 = 0;
    	int p2 = 1;
    	if (!isMaxPlayer) {
    		p1 = 1;
    		p2 = 0;
    	}
    	
    	int captured = 0;
    	int lastPit = (selection + board[p1][selection]) % 16;
    	
    	// check for each condition where capture occurs
		if (lastPit == 15) {
			if (board[p1][15] > 0 && board[p2][8] > 0 && board[p2][7] > 0) {
				captured = board[p2][8] + board[p2][7];
			}
		} else if (lastPit == 14) {
			if (board[p1][14] > 0 && board[p2][6] > 0 && board[p2][9] > 0) {
				captured = board[p2][6] + board[p2][9];
			}
		} else if (lastPit == 13) {
			if (board[p1][13] > 0 && board[p2][5] > 0 && board[p2][10] > 0) {
				captured = board[p2][5] + board[p2][10];
			}
		} else if (lastPit == 12) {
			if (board[p1][12] > 0 && board[p2][4] > 0 && board[p2][11] > 0) {
				captured = board[p2][4] + board[p2][11];
			}
		} else if (lastPit == 11) {
			if (board[p1][11] > 0 && board[p2][3] > 0 && board[p2][12] > 0) {
				captured = board[p2][3] + board[p2][12];
			}
		} else if (lastPit == 10) {
			if (board[p1][10] > 0 && board[p2][2] > 0 && board[p2][13] > 0) {
				captured = board[p2][2] + board[p2][13];
			}
		} else if (lastPit == 9) {
			if (board[p1][9] > 0 && board[p2][1] > 0 && board[p2][14] > 0) {
				captured = board[p2][1] + board[p2][14];
			}
		} else if (lastPit == 8) {
			if (board[p1][8] > 0 && board[p2][0] > 0 && board[p2][15] > 0) {
				captured = board[p2][0] + board[p2][15];
			}
		} else {
			captured = 0;
		}
		
    	return captured;
    }
    
    // private method that checks the position of the board, try to limit high value captures by opponent
    private int checkPositioning (int[][] board) {
    	
    	int p1 = 0;
    	int p2 = 1;
    	
    	// factors used in the program
    	int opponentFactor = 3;
    	int localFactor = 2;
    	int attackingFactor = 4;
    	int moveFactor = 6;
    	
    	// check how many seeds exists on the board
    	int totalSeeds = 0;
    	for (int j = 0; j < board[p1].length; j++) {
    		totalSeeds = totalSeeds + board[p1][j];
    	}
    	
    	// check how many seeds exists on the board, used to see the seed difference
    	int totalSeeds2 = 0;
    	for (int j = 0; j < board[p2].length; j++) {
    		totalSeeds2 = totalSeeds2 + board[p2][j];
    	}

    	// check through each column
    	double averageSeedsPerColumn = totalSeeds / 8;
    	
    	// check for protected pits, using a specific factor
    	// protected pits are ones that cannot be captured
    	int protectedPits = 0;
    	if (board[p1][0] ==  0 || board[p1][15] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][1] ==  0 || board[p1][14] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][2] ==  0 || board[p1][13] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][3] ==  0 || board[p1][12] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][4] ==  0 || board[p1][11] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][5] ==  0 || board[p1][10] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][6] ==  0 || board[p1][9] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	if (board[p1][7] ==  0 || board[p1][8] == 0) {
    		protectedPits = protectedPits + localFactor;
    	} else {
    		protectedPits = protectedPits - localFactor;
    	}
    	
    	// check for opponent pits that can be captured next round
    	// use a factor for configuration purposes
    	int opponentPits = 0;
    	if (board[p2][0] >  0 && board[p2][15] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][1] >  0 && board[p2][14] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][2] >  0 && board[p2][13] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][3] >  0 && board[p2][12] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][4] >  0 && board[p2][11] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][5] >  0 && board[p2][10] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][6] >  0 && board[p2][9] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	if (board[p2][7] >  0 && board[p2][8] > 0) {
    		opponentPits = opponentPits + opponentFactor;
    	}
    	
    	// check for pits that can capture
    	int attackingRow = 0;
    	for (int a = 0; a < board[p1].length; a++) {
    		if (board[p1][(a+board[p1][a]) % 16] > 0 && ((a+board[p1][a]) % 16) >= 8) {
    			attackingRow += attackingFactor;
    		} else {
    			attackingRow -= attackingFactor;
    		}
    	}
    	
    	// check for most available moves
    	int moves = 0;
    	for (int a = 0; a < board[p1].length; a++) {
    		if (board[p1][a] > 0) {
    			moves += moveFactor;
    		}
    	}
    	
    	// check for most available moves
    	int oppmoves = 0;
    	for (int a = 0; a < board[p2].length; a++) {
    		if (board[p2][a] > 0) {
    			oppmoves -= moveFactor;
    		}
    	}
    	
    	// return the heuristic value
    	return attackingRow + moves + oppmoves + (totalSeeds - totalSeeds2) + protectedPits + opponentPits;
    	
    }
}
