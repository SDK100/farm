package game;

public class GameTime {
	
	private final float dayLengthRealSeconds;
	
	private double realAccum = 0.0;
	private double gameMinutes = 8 * 60;
	
	public GameTime(float dayLengthRealSeconds) {
		this.dayLengthRealSeconds = Math.max(10f, dayLengthRealSeconds);
	}
	
	public void tick(double dtRealSeconds) {
		realAccum += dtRealSeconds;
		
		double minutesPerRealSec = (24.0* 60) / dayLengthRealSeconds;
		gameMinutes += dtRealSeconds * minutesPerRealSec;
		
		if(gameMinutes >= 24 * 60) {
			gameMinutes -= 24 * 60;
		}
	}
	
	public int hour() {return (int)(gameMinutes / 60.0) % 24;}
	public int minute() {return (int)(gameMinutes % 60.0);}
	public double minutesTotal() {return gameMinutes;}
	
	
	public float dayFactor() {return (float)(gameMinutes / (24.0 * 60.0));}

}
