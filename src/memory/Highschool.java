package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class Highschool{
	private double id;
	private int board, region, county;
	
	/**
	 * Initializes a new <code>Highschool</code> with the given parameters.
	 * 
	 * @param id - This highschool's id.
	 * @param board - This highschool's board.
	 * @param region - This highschool's region.
	 * @param county - This highschool's county.
	 */
	public Highschool(int id, int board, int region, int county){
		this.id = id;
		this.board = board;
		this.region = region;
		this.county = county;
	}//Constructor
	
	public void setID(double id){ this.id = id; }//setID()
	
	public double getID(){ return id; }//getID()
	public int getBoard(){ return board; }//getBoard()
	public int getRegion(){ return region; }//getRegion()
	public int getCounty(){ return county; }//getCounty
	
}//Highschool