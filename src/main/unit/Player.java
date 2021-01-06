package main.unit;

public class Player
{
	public enum Num{PLAYER1, PLAYER2};
	
	private Num mPlayerNum = null;
	private int mScore = 0;
	private int mLives = 0;
	
	public Player(Num playerNum)
	{
		this.mPlayerNum = playerNum;
		return;
	}
	
	public Num getPlayerNum()
	{
		return this.mPlayerNum;
	}
	
	public void setScore(int score)
	{
		this.mScore = score;
		return;
	}
	
	public void addScore(int score)
	{
		this.mScore += score;
		return;
	}
	
	public int getScore()
	{
		return this.mScore;
	}
	
	public void setLives(int lives)
	{
		this.mLives = lives;
		return;
	}
	
	public void addLife()
	{
		this.mLives++;
		return;
	}
	
	public void removeLife()
	{
		this.mLives--;
		this.mLives = this.mLives < 0 ? 0 : this.mLives;
		return;
	}
	
	public int getLives()
	{
		return this.mLives;
	}
}
