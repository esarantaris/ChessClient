import java.util.ArrayList;
import java.util.Random;


public class World
{
	private String[][] board = null;
	private int rows = 7;
	private int columns = 5;
	private int myColor = 0;
	private ArrayList<String> availableMoves = null;
	private int rookBlocks = 2;		// rook can move towards <rookBlocks> 
                                               //blocks in any vertical or horizontal direction
	private int nTurns = 0;
	private int nBranches = 0;
	private int noPrize = 9;
        
       //myVariables-------------------------------
	//my Weights
        //material weights
        private int pawnWeight=30;
        private int rookWeight=90;
        private int kingWeight=1000;
        
        
        //Threat weights
        private int wpp=1;
        private int wpr=1;
        private int wpk=20;
        private int wrp=1;
        private int wrr=1;
        private int wrk=20;
        private int wkp=1;
        private int wkr=1;
        private int wkk=20;
        
        
        private int sdepth=5 ;                //my search depth********************************************************
	//-----------------------------------------
        
        public World()
	{
		board = new String[rows][columns];
		
		/* represent the board
		
		BP|BR|BK|BR|BP 
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP
		*/
		
		// initialization of the board
		for(int i=0; i<rows; i++)
			for(int j=0; j<columns; j++)
				board[i][j] = " ";
		
		// setting the black player's chess parts
		
		// black pawns
		for(int j=0; j<columns; j++)
			board[1][j] = "BP";
		
		board[0][0] = "BP";
		board[0][columns-1] = "BP";
		
		// black rooks
		board[0][1] = "BR";
		board[0][columns-2] = "BR";
		
		// black king
		board[0][columns/2] = "BK";
		
		// setting the white player's chess parts
		
		// white pawns
		for(int j=0; j<columns; j++)
			board[rows-2][j] = "WP";
		
		board[rows-1][0] = "WP";
		board[rows-1][columns-1] = "WP";
		
		// white rooks
		board[rows-1][1] = "WR";
		board[rows-1][columns-2] = "WR";
		
		// white king
		board[rows-1][columns/2] = "WK";
		
		// setting the prizes
		for(int j=0; j<columns; j++)
			board[rows/2][j] = "P";
		
		availableMoves = new ArrayList<String>();
                //successors = new ArrayList<String[][]>();
	}
	
	public void setMyColor(int myColor)
	{
		this.myColor = myColor;
	}
	public int getMyColor()
        {
                return this.myColor;
        }
               
        
	public String selectAction()
	{
		availableMoves = new ArrayList<String>();
				
		if(myColor == 0)		// I am the white player
			this.whiteMoves();
		else					// I am the black player
			this.blackMoves();
		
		// keeping track of the branch factor
		nTurns++;
		nBranches += availableMoves.size();
		//int mMax =minMax(board,myColor,0);
                int abmMax=alphaBetaMinMax(board,myColor,0,Integer.MIN_VALUE,Integer.MAX_VALUE);
                
                //System.out.println("MinMax-->"+mMax+"  ABminMax--->"+abmMax);  //check if minmax and Ab-pruning lead to the same results
                
                return availableMoves.get(abmMax);
                //return availableMoves.get(minMax(board,myColor,0));
                //return availableMoves.get(alphaBetaMinMax(board,myColor,0,Integer.MIN_VALUE,Integer.MAX_VALUE));
		//return this.selectRandomAction();
	}
	
	private void whiteMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					// check if it can move towards the last row
					if(i-1 == 0 && (Character.toString(board[i-1][j].charAt(0)).equals(" ") 
							         || Character.toString(board[i-1][j].charAt(0)).equals("P")))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i-1) + Integer.toString(j);
						
						availableMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i-1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j-1);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j+1);
						
						availableMoves.add(move);
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}
	
	private void blackMoves()
	{
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					// check if it is at the last row
					if(i+1 == rows-1 && (Character.toString(board[i+1][j].charAt(0)).equals(" ")
										  || Character.toString(board[i+1][j].charAt(0)).equals("P")))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i+1) + Integer.toString(j);
						
						availableMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i+1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j-1);
						
						availableMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j+1);
						
						availableMoves.add(move);
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						availableMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							availableMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							availableMoves.add(move);	
						}
					}
				}			
			}	
		}
	}
	
	private String selectRandomAction()
	{		
		Random ran = new Random();
		int x = ran.nextInt(availableMoves.size());
		
		return availableMoves.get(x);
	}
	
	public double getAvgBFactor()
	{
		return nBranches / (double) nTurns;
	}
	
	public void makeMove(int x1, int y1, int x2, int y2, int prizeX, int prizeY)
	{
		String chesspart = Character.toString(board[x1][y1].charAt(1));
		
		boolean pawnLastRow = false;
		
		// check if it is a move that has made a move to the last line
		if(chesspart.equals("P"))
			if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
			{
				board[x2][y2] = " ";	// in case an opponent's chess part has just been captured
				board[x1][y1] = " ";
				pawnLastRow = true;
			}
		
		// otherwise
		if(!pawnLastRow)
		{
			board[x2][y2] = board[x1][y1];
			board[x1][y1] = " ";
		}
		
		// check if a prize has been added in the game
		if(prizeX != noPrize)
			board[prizeX][prizeY] = "P";
	}
        
        //MyFunctions---------------------------------------------------------------------------------------------------------------------------
        private ArrayList<String>  getWhiteMoves(String[][] board)
	{       
                ArrayList<String> avMoves = new ArrayList<String>();  
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if there is not a white chess part in this position then keep on searching
				if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					// check if it can move towards the last row
					if(i-1 == 0 && (Character.toString(board[i-1][j].charAt(0)).equals(" ") 
							         || Character.toString(board[i-1][j].charAt(0)).equals("P")))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i-1) + Integer.toString(j);
						
						avMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i-1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j-1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j-1);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=0)
					{
						firstLetter = Character.toString(board[i-1][j+1].charAt(0));
						
						if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-1) + Integer.toString(j+1);
						
						avMoves.add(move);
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("W"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("B") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("W"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							avMoves.add(move);	
						}
					}
				}			
			}	
		}
            return avMoves;
	}
        
        private ArrayList<String> getBlackMoves(String[][] board)
	{
                ArrayList<String> avMoves = new ArrayList<String>();
		String firstLetter = "";
		String secondLetter = "";
		String move = "";
				
		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				firstLetter = Character.toString(board[i][j].charAt(0));
				
				// if it there is not a black chess part in this position then keep on searching
				if(firstLetter.equals("W") || firstLetter.equals(" ") || firstLetter.equals("P"))
					continue;
				
				// check the kind of the white chess part
				secondLetter = Character.toString(board[i][j].charAt(1));
				
				if(secondLetter.equals("P"))	// it is a pawn
				{
					// check if it is at the last row
					if(i+1 == rows-1 && (Character.toString(board[i+1][j].charAt(0)).equals(" ")
										  || Character.toString(board[i+1][j].charAt(0)).equals("P")))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
						       Integer.toString(i+1) + Integer.toString(j);
						
						avMoves.add(move);
						continue;
					}
					
					// check if it can move one vertical position ahead
					firstLetter = Character.toString(board[i+1][j].charAt(0));
					
					if(firstLetter.equals(" ") || firstLetter.equals("P"))
					{
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the left
					if(j!=0 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j-1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j-1);
						
						avMoves.add(move);
					}
					
					// check if it can move crosswise to the right
					if(j!=columns-1 && i!=rows-1)
					{
						firstLetter = Character.toString(board[i+1][j+1].charAt(0));
						
						if(firstLetter.equals("B") || firstLetter.equals(" ") || firstLetter.equals("P"))
							continue;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+1) + Integer.toString(j+1);
						
						avMoves.add(move);
					}
				}
				else if(secondLetter.equals("R"))	// it is a rook
				{
					// check if it can move upwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i-(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i-(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move downwards
					for(int k=0; k<rookBlocks; k++)
					{
						if((i+(k+1)) == rows)
							break;
						
						firstLetter = Character.toString(board[i+(k+1)][j].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i+(k+1)) + Integer.toString(j);
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check if it can move on the left
					for(int k=0; k<rookBlocks; k++)
					{
						if((j-(k+1)) < 0)
							break;
						
						firstLetter = Character.toString(board[i][j-(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j-(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
					
					// check of it can move on the right
					for(int k=0; k<rookBlocks; k++)
					{
						if((j+(k+1)) == columns)
							break;
						
						firstLetter = Character.toString(board[i][j+(k+1)].charAt(0));
						
						if(firstLetter.equals("B"))
							break;
						
						move = Integer.toString(i) + Integer.toString(j) + 
							   Integer.toString(i) + Integer.toString(j+(k+1));
						
						avMoves.add(move);
						
						// prevent detouring a chesspart to attack the other
						if(firstLetter.equals("W") || firstLetter.equals("P"))
							break;
					}
				}
				else // it is the king
				{
					// check if it can move upwards
					if((i-1) >= 0)
					{
						firstLetter = Character.toString(board[i-1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i-1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move downwards
					if((i+1) < rows)
					{
						firstLetter = Character.toString(board[i+1][j].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i+1) + Integer.toString(j);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the left
					if((j-1) >= 0)
					{
						firstLetter = Character.toString(board[i][j-1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j-1);
								
							avMoves.add(move);	
						}
					}
					
					// check if it can move on the right
					if((j+1) < columns)
					{
						firstLetter = Character.toString(board[i][j+1].charAt(0));
						
						if(!firstLetter.equals("B"))
						{
							move = Integer.toString(i) + Integer.toString(j) + 
								   Integer.toString(i) + Integer.toString(j+1);
								
							avMoves.add(move);	
						}
					}
				}			
			}	
		}
            return avMoves;
	}
        
     
        
            
        
        public int evaluationFunction(String[][] b) //evaluate each state
        {  
            String firstLetter = "";
            String secondLetter= "";
            int whitePieces=0;
            int blackPieces=0;
            int wp=0;
            int wr=0;
            int wk=0;
            int bp=0;
            int br=0;
            int bk=0;
            int rankA=0;
            int rankB=0;
            int whitePawnsFinished = Integer.parseInt(Character.toString(b[7][0].charAt(0)));
            int blackPawnsFinished = Integer.parseInt(Character.toString(b[7][1].charAt(0)));
            for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{   
                            firstLetter = Character.toString(b[i][j].charAt(0));
                            
                            if (firstLetter.equals("W")) //white player 
                            {	
                                secondLetter = Character.toString(b[i][j].charAt(1));
				if(secondLetter.equals("P")) //pawn
                                    rankA+=pawnWeight;
				else if(secondLetter.equals("R")) //rook
                                    rankA+=rookWeight;
				else if(secondLetter.equals("K")) //king
                                    rankA+=kingWeight;
				whitePieces++;
								
                            }
                            else if(firstLetter.equals("B")) //black player
                            {   
                                secondLetter = Character.toString(b[i][j].charAt(1));
				if(secondLetter.equals("P")) //pawn
                                    rankB+=pawnWeight;
				else if(secondLetter.equals("R")) //rook
                                    rankB+=rookWeight;
                                else if(secondLetter.equals("K")) //king
                                    rankB+=kingWeight;
                               blackPieces++;
                            }
                        }
                            
                            
                        }
           /* System.out.println("");
            printBoard(b);
            System.out.println("");
            System.out.println("rankA:"+rankA);
            System.out.println("rankB:"+rankB);
            System.out.println("whitePawnsFinished:"+whitePawnsFinished);
            System.out.println("blackPawnsFinished:"+blackPawnsFinished);
            System.out.println("mobility:"+mobility(b));
            System.out.println("threatFactor:"+threatFactor(b));
            */
            if(myColor==0)//I am white player
            {
                return (rankA-rankB)+(whitePawnsFinished-blackPawnsFinished)*31+mobility(b)+threatFactor(b);
            }
            else
            {
                return (rankB-rankA)+(blackPawnsFinished-whitePawnsFinished)*31+mobility(b)+threatFactor(b);
            }
            
     
            /*   BP|BR|BK|BR|BP
		BP|BP|BP|BP|BP
		--|--|--|--|--
		P |P |P |P |P 
		--|--|--|--|--
		WP|WP|WP|WP|WP
		WP|WR|WK|WR|WP */
        }
        
         public int threatFactor(String[][] b)
         {  
              String firstLetterA = "";
              String secondLetterA= "";
              //String firstLetterB = "";
              //String secondLetterB= "";
              int pp=0; //pawn threats pawn (2)
              int pr=0; //pawn threats rook (5)  
              int pk=0; //pawn threats king (20)  
              int rp=0; //rook threats pawn (1)
              int rr=0; //rook threats rook (5)
              int rk=0; //rook threats king (20)  
              int kp=0; //king threats pawn (0.5)
              int kr=0; //king threats rook (2)
              int kk=0; //king threats king (20)
              
              int threatPoints=0;
              if(isEven(sdepth))            //then i threat
              {
                  if(myColor==0)            //I am white and i look my threats-threatPoints
                  {
                            for(int i=0; i<rows; i++)
                            {
                                for(int j=0; j<columns; j++)
                                {   

                                    firstLetterA = Character.toString(b[i][j].charAt(0));

                                    if (firstLetterA.equals("W"))
                                    {	
                                        secondLetterA = Character.toString(b[i][j].charAt(1));
                                        if(secondLetterA.equals("P")) //i check possible threats with my WP (2 possible)
                                        {   
                                                
                                                if(i-1>=0 && j-1>=0)
                                                {
                                                    if(Character.toString(b[i-1][j-1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WP (left diagonal)
                                                    {
                                                        if(Character.toString(b[i-1][j-1].charAt(1)).equals("P"))  //i threat a BP with my WP
                                                            pp++;
                                                        else if(Character.toString(b[i-1][j-1].charAt(1)).equals("R"))//i threat a BR with my WP
                                                            pr++;
                                                        else if(Character.toString(b[i-1][j-1].charAt(1)).equals("K"))//i threat a BK with my WP
                                                           pk++;

                                                    }
                                                }
                                                if(i-1>=0 && (j+1<=columns-1) )
                                                {
                                                    if(Character.toString(b[i-1][j+1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WP (right diagonal)
                                                    {
                                                        if(Character.toString(b[i-1][j+1].charAt(1)).equals("P"))  //i threat a BP with my WP
                                                             pp++;
                                                        else if(Character.toString(b[i-1][j+1].charAt(1)).equals("R"))//i threat a BR with my WP
                                                            pr++;
                                                        else if(Character.toString(b[i-1][j+1].charAt(1)).equals("K"))//i threat a BK with my WP
                                                            pk++;

                                                    }
                                                }
                                        }
                                        else if(secondLetterA.equals("R")) //i check possible threats with my WR (8 possible- but 4 max)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the front square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                            rp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(i-2>=0)          //check 2 squares forward
                                                {
                                                    if(squareIsEmpty(b,i-1,j)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i-2][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                        {
                                                            if(Character.toString(b[i-2][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j-1>=0)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                             rp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(j-2>=0)          //check 2 left
                                                {
                                                    if(squareIsEmpty(b,i,j-1)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i][j-2].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                        {
                                                            if(Character.toString(b[i][j-2].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 square back
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                             rp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(i+2<=rows-1)          //check 2 square back
                                                {
                                                    if(squareIsEmpty(b,i+1,j))
                                                    {
                                                        if(Character.toString(b[i+2][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR 
                                                        {
                                                            if(Character.toString(b[i+2][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j+1<=columns-1)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR 
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                             rp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(j+2<=columns-1)          //check 2 square right
                                                {
                                                    if(squareIsEmpty(b,i,j+1))
                                                    {
                                                        if(Character.toString(b[i][j+2].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR 
                                                        {
                                                            if(Character.toString(b[i][j+2].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                
                                                
                                  

                                        }
                                        else if(secondLetterA.equals("K")) //i check possible threats with my WK (4 possible)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the front square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                            kp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                                if(j-1>=0)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                             kp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 back
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                             kp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                                 if(j+1<=columns-1)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                             kp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                            
                                        }



                                    }
                                }


                            }
                            threatPoints=(pp*wpp)+(pr*wpr)+(pk*wpk)+(rp*wrp)+(rr*wrr)+(rk*wrk)+(kp*wkp)+(kr*wkr)+(kk*wkk);
                            return threatPoints;
                       
                    }
                    else //if my color is black
                    {
                            for(int i=0; i<rows; i++)
                            {
                                for(int j=0; j<columns; j++)
                                {   

                                    firstLetterA = Character.toString(b[i][j].charAt(0));

                                    if (firstLetterA.equals("B"))
                                    {	
                                        secondLetterA = Character.toString(b[i][j].charAt(1));
                                        if(secondLetterA.equals("P")) //i check possible threats with my BP (2 possible)
                                        {   
                                                
                                                if(i+1<=rows-1 && j-1>=0)  
                                                {
                                                    if(Character.toString(b[i+1][j-1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BP (left diagonal)
                                                    {
                                                        if(Character.toString(b[i+1][j-1].charAt(1)).equals("P"))  //i threat a WP with my BP
                                                            pp++;
                                                        else if(Character.toString(b[i+1][j-1].charAt(1)).equals("R"))//i threat a WR with my BP
                                                            pr++;
                                                        else if(Character.toString(b[i+1][j-1].charAt(1)).equals("K"))//i threat a WK with my BP
                                                           pk++;

                                                    }
                                                }
                                                if(i+1<=rows-1  && (j+1<=columns-1) )  
                                                {
                                                    if(Character.toString(b[i+1][j+1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BP (right diagonal)
                                                    {
                                                        if(Character.toString(b[i+1][j+1].charAt(1)).equals("P"))  //i threat a WP with my BP
                                                             pp++;
                                                        else if(Character.toString(b[i+1][j+1].charAt(1)).equals("R"))//i threat a WR with my BP
                                                            pr++;
                                                        else if(Character.toString(b[i+1][j+1].charAt(1)).equals("K"))//i threat a WK with my BP
                                                            pk++;

                                                    }
                                                }
                                        }
                                        else if(secondLetterA.equals("R")) //i check possible threats with my BR (8 possible- but 4 max)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the back square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                            rp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(i-2>=0)          //check 2 squares back
                                                {
                                                    if(squareIsEmpty(b,i-1,j)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i-2][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                        {
                                                            if(Character.toString(b[i-2][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j-1>=0)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                             rp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(j-2>=0)          //check 2 right
                                                {
                                                    if(squareIsEmpty(b,i,j-1)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i][j-2].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                        {
                                                            if(Character.toString(b[i][j-2].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 square forward
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                             rp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(i+2<=rows-1)          //check 2 squares forward
                                                {
                                                    if(squareIsEmpty(b,i+1,j))
                                                    {
                                                        if(Character.toString(b[i+2][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR 
                                                        {
                                                            if(Character.toString(b[i+2][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j+1<=columns-1)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR 
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                             rp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(j+2<=columns-1)          //check 2 square left
                                                {
                                                    if(squareIsEmpty(b,i,j+1))
                                                    {
                                                        if(Character.toString(b[i][j+2].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR 
                                                        {
                                                            if(Character.toString(b[i][j+2].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                
                                                
                                  

                                        }
                                        else if(secondLetterA.equals("K")) //i check possible threats with my BK (4 possible)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the back square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                            kp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                                if(j-1>=0)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                             kp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 front
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                             kp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                                 if(j+1<=columns-1)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("B")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                             kp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                            
                                        }



                                    }
                                }


                            }
                           threatPoints=(pp*wpp)+(pr*wpr)+(pk*wpk)+(rp*wrp)+(rr*wrr)+(rk*wrk)+(kp*wkp)+(kr*wkr)+(kk*wkk);
                            return threatPoints;
                       
                    }
       
                    
              
            }
              else //I am THREATENED!!!
              {
                    if(myColor==1)            //I am black and i look my threats-threatPoints
                  {
                            for(int i=0; i<rows; i++)
                            {
                                for(int j=0; j<columns; j++)
                                {   

                                    firstLetterA = Character.toString(b[i][j].charAt(0));

                                    if (firstLetterA.equals("W"))
                                    {	
                                        secondLetterA = Character.toString(b[i][j].charAt(1));
                                        if(secondLetterA.equals("P")) //i check possible threats with my WP (2 possible)
                                        {   
                                                
                                                if(i-1>=0 && j-1>=0)
                                                {
                                                    if(Character.toString(b[i-1][j-1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WP (left diagonal)
                                                    {
                                                        if(Character.toString(b[i-1][j-1].charAt(1)).equals("P"))  //i threat a BP with my WP
                                                            pp++;
                                                        else if(Character.toString(b[i-1][j-1].charAt(1)).equals("R"))//i threat a BR with my WP
                                                            pr++;
                                                        else if(Character.toString(b[i-1][j-1].charAt(1)).equals("K"))//i threat a BK with my WP
                                                           pk++;

                                                    }
                                                }
                                                if(i-1>=0 && (j+1<=columns-1) )
                                                {
                                                    if(Character.toString(b[i-1][j+1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WP (right diagonal)
                                                    {
                                                        if(Character.toString(b[i-1][j+1].charAt(1)).equals("P"))  //i threat a BP with my WP
                                                             pp++;
                                                        else if(Character.toString(b[i-1][j+1].charAt(1)).equals("R"))//i threat a BR with my WP
                                                            pr++;
                                                        else if(Character.toString(b[i-1][j+1].charAt(1)).equals("K"))//i threat a BK with my WP
                                                            pk++;

                                                    }
                                                }
                                        }
                                        else if(secondLetterA.equals("R")) //i check possible threats with my WR (8 possible- but 4 max)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the front square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                            rp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(i-2>=0)          //check 2 squares forward
                                                {
                                                    if(squareIsEmpty(b,i-1,j)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i-2][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                        {
                                                            if(Character.toString(b[i-2][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j-1>=0)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                             rp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(j-2>=0)          //check 2 left
                                                {
                                                    if(squareIsEmpty(b,i,j-1)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i][j-2].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                        {
                                                            if(Character.toString(b[i][j-2].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 square back
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                             rp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(i+2<=rows-1)          //check 2 square back
                                                {
                                                    if(squareIsEmpty(b,i+1,j))
                                                    {
                                                        if(Character.toString(b[i+2][j].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR 
                                                        {
                                                            if(Character.toString(b[i+2][j].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j+1<=columns-1)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR 
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                             rp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a BR with my WR
                                                            rr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a BK with my WR
                                                            rk++;

                                                    }
                                                }
                                                if(j+2<=columns-1)          //check 2 square right
                                                {
                                                    if(squareIsEmpty(b,i,j+1))
                                                    {
                                                        if(Character.toString(b[i][j+2].charAt(0)).equals("B")) //i threat a BP/BR/BK with my WR 
                                                        {
                                                            if(Character.toString(b[i][j+2].charAt(1)).equals("P"))  //i threat a BP with my WR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("R"))//i threat a BR with my WR
                                                                rr++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("K"))//i threat a BK with my WR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                
                                                
                                  

                                        }
                                        else if(secondLetterA.equals("K")) //i check possible threats with my WK (4 possible)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the front square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                            kp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                                if(j-1>=0)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                             kp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 back
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                             kp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                                 if(j+1<=columns-1)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("B")) //i threat a WP/WR/WK with my WK
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a BP with my WK
                                                             kp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a BR with my WK
                                                            kr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a BK with my WK
                                                            kk++;

                                                    }
                                                }
                                            
                                        }



                                    }
                                }


                            }
                            threatPoints=(pp*wpp)+(pr*wpr)+(pk*wpk)+(rp*wrp)+(rr*wrr)+(rk*wrk)+(kp*wkp)+(kr*wkr)+(kk*wkk);
                            return (-threatPoints);
                    }
                    else //if my color is black
                    {
                            for(int i=0; i<rows; i++)
                            {
                                for(int j=0; j<columns; j++)
                                {   

                                    firstLetterA = Character.toString(b[i][j].charAt(0));

                                    if (firstLetterA.equals("B"))
                                    {	
                                        secondLetterA = Character.toString(b[i][j].charAt(1));
                                        if(secondLetterA.equals("P")) //i check possible threats with my BP (2 possible)
                                        {   
                                                
                                                if(i+1<=rows-1 && j-1>=0)  
                                                {
                                                    if(Character.toString(b[i+1][j-1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BP (left diagonal)
                                                    {
                                                        if(Character.toString(b[i+1][j-1].charAt(1)).equals("P"))  //i threat a WP with my BP
                                                            pp++;
                                                        else if(Character.toString(b[i+1][j-1].charAt(1)).equals("R"))//i threat a WR with my BP
                                                            pr++;
                                                        else if(Character.toString(b[i+1][j-1].charAt(1)).equals("K"))//i threat a WK with my BP
                                                           pk++;

                                                    }
                                                }
                                                if(i+1<=rows-1  && (j+1<=columns-1) )  
                                                {
                                                    if(Character.toString(b[i+1][j+1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BP (right diagonal)
                                                    {
                                                        if(Character.toString(b[i+1][j+1].charAt(1)).equals("P"))  //i threat a WP with my BP
                                                             pp++;
                                                        else if(Character.toString(b[i+1][j+1].charAt(1)).equals("R"))//i threat a WR with my BP
                                                            pr++;
                                                        else if(Character.toString(b[i+1][j+1].charAt(1)).equals("K"))//i threat a WK with my BP
                                                            pk++;

                                                    }
                                                }
                                        }
                                        else if(secondLetterA.equals("R")) //i check possible threats with my BR (8 possible- but 4 max)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the back square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                            rp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(i-2>=0)          //check 2 squares back
                                                {
                                                    if(squareIsEmpty(b,i-1,j)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i-2][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                        {
                                                            if(Character.toString(b[i-2][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i-2][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j-1>=0)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                             rp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(j-2>=0)          //check 2 right
                                                {
                                                    if(squareIsEmpty(b,i,j-1)) //check if the previous square is empty
                                                    {
                                                        if(Character.toString(b[i][j-2].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                        {
                                                            if(Character.toString(b[i][j-2].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i][j-2].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 square forward
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                             rp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(i+2<=rows-1)          //check 2 squares forward
                                                {
                                                    if(squareIsEmpty(b,i+1,j))
                                                    {
                                                        if(Character.toString(b[i+2][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR 
                                                        {
                                                            if(Character.toString(b[i+2][j].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i+2][j].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                if(j+1<=columns-1)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR 
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                             rp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a WR with my BR
                                                            rr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a WK with my BR
                                                            rk++;

                                                    }
                                                }
                                                if(j+2<=columns-1)          //check 2 square left
                                                {
                                                    if(squareIsEmpty(b,i,j+1))
                                                    {
                                                        if(Character.toString(b[i][j+2].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BR 
                                                        {
                                                            if(Character.toString(b[i][j+2].charAt(1)).equals("P"))  //i threat a WP with my BR
                                                                 rp++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("R"))//i threat a WR with my BR
                                                                rr++;
                                                            else if(Character.toString(b[i][j+2].charAt(1)).equals("K"))//i threat a WK with my BR
                                                                rk++;

                                                        }
                                                    }
                                                }
                                                
                                                
                                  

                                        }
                                        else if(secondLetterA.equals("K")) //i check possible threats with my BK (4 possible)
                                        {
                                                if(i-1>=0)          //check if i threat anything in the back square
                                                {
                                                    if(Character.toString(b[i-1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i-1][j].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                            kp++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i-1][j].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                                if(j-1>=0)          //check 1 right
                                                {
                                                    if(Character.toString(b[i][j-1].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i][j-1].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                             kp++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i][j-1].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                                if(i+1<=rows-1)          //check 1 front
                                                {
                                                    if(Character.toString(b[i+1][j].charAt(0)).equals("W")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i+1][j].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                             kp++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i+1][j].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                                 if(j+1<=columns-1)          //check 1 left
                                                {
                                                    if(Character.toString(b[i][j+1].charAt(0)).equals("B")) //i threat a WP/WR/WK with my BK
                                                    {
                                                        if(Character.toString(b[i][j+1].charAt(1)).equals("P"))  //i threat a WP with my BK
                                                             kp++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("R"))//i threat a WR with my BK
                                                            kr++;
                                                        else if(Character.toString(b[i][j+1].charAt(1)).equals("K"))//i threat a WK with my BK
                                                            kk++;

                                                    }
                                                }
                                            
                                        }



                                    }
                                }


                            }
                             threatPoints=(pp*wpp)+(pr*wpr)+(pk*wpk)+(rp*wrp)+(rr*wrr)+(rk*wrk)+(kp*wkp)+(kr*wkr)+(kk*wkk);
                            return (-threatPoints);
                       
                       
                    }
                  
              }
              
         }
         
        public boolean isEven(int x)
        {
            return x%2==0;
        }
                
        
        public boolean squareIsEmpty(String[][] b,int x,int y)
        {
            return (Character.toString(b[x][y].charAt(0)).equals(" ")||Character.toString(b[x][y].charAt(0)).equals("P"));
        }
        
        public ArrayList<String[][]> generateSuccessors(String[][] fatherBoard, int player)  //generates the successors of MAX (level=0) or MIN(level=1)
        {
            ArrayList<String> avMoves = new ArrayList<String>();
            ArrayList<String[][]> successors = new ArrayList<String[][]>();  
            //generate Successors for white 
            if(player==0)
                avMoves=getWhiteMoves(fatherBoard);
            else //black
                avMoves=getBlackMoves(fatherBoard);
            for(int c=0;c<avMoves.size();c++)
            {
                String[][] childBoard = new String[rows+1][columns];
                
                if(fatherBoard.length==rows) //in case the father is the root
                {
                    for(int i=0; i<rows; i++)
                    {
                        for(int j=0; j<columns; j++)
                        {

                            childBoard[i][j]=fatherBoard[i][j];
                        }   

                    }
                    childBoard[7][0]="0"; //White pawns reached the last line
                    childBoard[7][1]="0"; //Black pawns reached the last line
                }
                else  // in case the father is an other root's child(in this case i have already create the extra row)
                {
                    for(int i=0; i<rows+1; i++)
                    {
                        for(int j=0; j<columns; j++)
                        {

                            childBoard[i][j]=fatherBoard[i][j];
                        }   

                    }
                }
                    
                int x1 = Integer.parseInt(Character.toString(avMoves.get(c).charAt(0)));
                int y1 = Integer.parseInt(Character.toString(avMoves.get(c).charAt(1)));
                int x2 = Integer.parseInt(Character.toString(avMoves.get(c).charAt(2)));
                int y2 = Integer.parseInt(Character.toString(avMoves.get(c).charAt(3)));
               
                successors.add(moveChild(childBoard,player,x1,y1,x2,y2));
            }
            return ( successors.size() == 0 ) ? null : successors ; 
            //return successors;
            //printSuccessors();
        }
       

        
        public String[][] moveChild(String[][] child,int player,int x1, int y1, int x2, int y2)
	{   
            String chesspart = Character.toString(child[x1][y1].charAt(1));
		
            boolean pawnLastRow = false;
             
		// check if it is a move that has made a move to the last line
            if(chesspart.equals("P"))
		if( (x1==rows-2 && x2==rows-1) || (x1==1 && x2==0) )
		{   
                    if(player==0) //white player
                    {
                        int whitePawnsFinished = Integer.parseInt(Character.toString(child[7][0].charAt(0)))+1;
                        child[7][0]=Integer.toString(whitePawnsFinished);
                        
                    }
                    else
                    {
                        int blackPawnsFinished = Integer.parseInt(Character.toString(child[7][1].charAt(0)))+1;
                        child[7][1]=Integer.toString(blackPawnsFinished);
                        
                    }    
                    child[x2][y2] = " ";	// in case an opponent's chess part has just been captured
                    child[x1][y1] = " ";
                    pawnLastRow = true;
		}
             //otherwise
            
            if(!pawnLastRow)
            {
                child[x2][y2] = child[x1][y1];
                child[x1][y1] = " ";
            }
       	    return child;			  
	}
       
        public void printSuccessors(ArrayList<String[][]> successors)
        {
           System.out.println(successors.size());
            for(int k=0;k<successors.size();k++)
            {   
               // String[][] succ= new String[rows][columns];
                //succ=successors.get(k);
                System.out.println("");
                System.out.println("Board number "+k);
                for(int i=0; i<rows; i++)
                {   
                    System.out.println("");
                    for(int j=0; j<columns; j++)
                        System.out.print(" "+successors.get(k)[i][j]+" ");
            
                }
        
            }
        }
        
        public void printIntegerList( ArrayList<Integer> list)
        {
             for(int i=0;i<list.size();i++)
            {   
                System.out.println(" "+list.get(i)+" ");
            }
        }
        
        public void printBoard (String[][] b)
        {
            for(int i=0;i<b.length;i++)
            {   
                System.out.println(" ");
                for(int j=0;j<columns;j++)
                {
                    System.out.print(" "+b[i][j]);
                }
            }
        }
        public void printAvMoves()
        {
            for(int k=0;k<availableMoves.size();k++)
            {   
                System.out.print(" "+availableMoves.get(k)+" ");
            }
            
        }
        
          public void printExactMove(int pos)
        {
              System.out.println(" "+availableMoves.get(pos)+" ");
        }
        
        public String[][] getBoard()
        {
            return board;
        }
        
        public int nextPlayer(int player)
        {
            if(player==0)
                return 1;
            else
                return 0;
        }
         

    public int nextMaxORmin(int maxORmin)
        {
            if(maxORmin==0)
                return 1;
            else
                return 0;
        }
         
    
         
    
         public int kingCaptured(String[][] b,int player)
         {   
             boolean iWon=true;
             boolean iLost=true;
            
             
          
                    if(myColor==0) //I am white
                    {
                        for(int i=0;i<rows;i++)
                        {
                            for(int j=0;j<columns;j++)
                            {
                                if(b[i][j].equals("BK")) //BK not captured
                                    //return 0;
                                    iWon=false;
                                else if(b[i][j].equals("WK")) //WK not captured
                                    iLost=false;
                            }
                        }
                        
                        //printBoard(b);
                        //return 1;
                        
                    }
                    else //i am black
                    {
                         for(int i=0;i<rows;i++)
                        {
                            for(int j=0;j<columns;j++)
                            {
                                if(b[i][j].equals("WK")) //WK not captured
                                   iWon=false;
                                else if(b[i][j].equals("BK")) //BK not captured
                                    iLost=false;
                            }
                        }
                         //printBoard(b);
                         //return 1;
                    }
                    if(iWon)
                      return 1;
                    else if(iLost) 
                      return -1;
                    else
                        return 0;
             }
          
         
         public int mobility(String[][] b)
         {  
                if(myColor==0) //I am white
                 return (getWhiteMoves(b).size()-getBlackMoves(b).size());
                else //I am black
                  return (getBlackMoves(b).size()-getWhiteMoves(b).size()); 
         }
         
         public int getBest(ArrayList<Integer> gameList,int player)
        {   
            int rank=gameList.get(0);
            if(player==myColor) //MAX
            {
                for(int i=1; i<gameList.size(); i++) 
                {
                    if(gameList.get(i) > rank) 
                        rank=gameList.get(i);
                }  
                
            }
            else
            {
               for(int i=1; i<gameList.size(); i++) 
                {
                    if(gameList.get(i) < rank) 
                        rank=gameList.get(i);
                }   
            }
            return rank;
        }
		
		
        public int getBestMove(ArrayList<Integer> gameList)
        {   
            int position=0;
            int rank=gameList.get(0);

            for(int i=1; i<gameList.size(); i++) 
            {
                if(gameList.get(i) > rank)
		{
                    rank=gameList.get(i);
                    position=i;
		}
            }  
				
            return position;
        }		

        public int minMax(String[][] b,int player,int depth)
        {
            //generate successors
             ArrayList<String[][]> successors=generateSuccessors(b,player);
           
             if((depth<=sdepth-1)&& successors!=null)
            {
     
                ArrayList<Integer> gameList= new ArrayList<Integer>();
           
                for(int i=0;i<successors.size();i++)
                    gameList.add(minMax(successors.get(i),nextPlayer(player),depth+1));
			
                        //System.out.println("minimaxlist:");
                        //printIntegerList(gameList);
			if(depth!=0)
			{
                            int r=getBest(gameList,player);
                            return r;
			}
			else
                            return getBestMove(gameList);
            }
            else
                return evaluationFunction(b);
        } 
         
        
         public int alphaBetaMinMax(String[][] b,int player,int depth,int alpha,int beta)
        {   
            //I check if there is any terminal board till leafs .
             if(depth>0&&depth<sdepth)
             {
                int checkKing=kingCaptured(b,player);
                if(checkKing==1) //then this board is a terminal
                  return 10000; //1000
                else if(checkKing==-1)
                    return -10000; //-1000
             }
            //generate successors
             ArrayList<String[][]> successors=generateSuccessors(b,player);
           
             if((depth<=sdepth-1)&& successors!=null)
            {
               
                //int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;
     
                ArrayList<Integer> gameList= new ArrayList<Integer>();
                if(player==myColor)
                {
                    
                    int currentScore=Integer.MIN_VALUE;
                    int v=Integer.MIN_VALUE;
                    for(int i=0;i<successors.size();i++)
                    {   
                        v=alphaBetaMinMax(successors.get(i),nextPlayer(player),depth+1,alpha,beta);
                        currentScore=Math.max(currentScore,v);
                        alpha=Math.max(alpha,currentScore);
                        gameList.add(v);
                        if(beta<=alpha)
                        {   
                            //System.out.println("Pruned"+"Alpha="+alpha+"Beta="+beta);
                            //return v;
                            break;
                        }
                       
                    }
                }
                else
                {
                   int currentScore=Integer.MAX_VALUE;
                   int v=Integer.MAX_VALUE;
                   for(int i=0;i<successors.size();i++)
                    {   
                        v=alphaBetaMinMax(successors.get(i),nextPlayer(player),depth+1,alpha,beta);
                        currentScore=Math.min(currentScore,v);
                        beta=Math.min(beta,currentScore);
                        gameList.add(v);
                        if(beta<=alpha)
                        {
                            //System.out.println("Pruned"+"Alpha="+alpha+"Beta="+beta);
                            //return v;
                            break;
                        }
                        
                       
                    }
                  
                 }      
                        //System.out.println("ABlist:");
                        //printIntegerList(gameList);
			if(depth!=0)
			{
                            int r=getBest(gameList,player);
                            return r;
			}
			else
                            return getBestMove(gameList);
            }
            else
             {
                
                 //printBoard(b);
                 return evaluationFunction(b);//+threat;
                
             }
        } 
       
        //---------------------------------------------------
               
	
     
       /* public static void main(String[] args)
	{
           
            World w=new World();
            
           // w.printBoard(w.getBoard());
            w.tesTminMax(w.getBoard(),0,0);
            //w.tesTalphaBetaMinMax(w.getBoard(),0,0,Integer.MIN_VALUE,Integer.MAX_VALUE);
           // w.printSuccessors(w.generateSuccessors(w.getBoard(),0));
            //w.printAvMoves();
           
               
	}*/
}