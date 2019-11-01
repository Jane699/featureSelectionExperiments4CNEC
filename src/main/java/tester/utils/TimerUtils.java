package tester.utils;

import basic.procedure.timer.TimeCountStatus;
import basic.procedure.timer.TimeCounted;

public class TimerUtils {
	public static double nanoTimeToMillis(long nanoTime) {
		//return TimeUnit.MILLISECONDS.convert(nanoTime, TimeUnit.NANOSECONDS);
		return nanoTime / 1000.0 / 1000.0;
	}
	
	public static double nanoTimeToMillis(double nanoTime) {
		//return TimeUnit.MILLISECONDS.convert(nanoTime, TimeUnit.NANOSECONDS);
		return nanoTime / 1000.0 / 1000.0;
	}
	
	public static void timeStart(TimeCounted timeCounted) {
		if (/*timeCounted.getTimeStatus()!=TimeCountStatus.PAUSE && */
			timeCounted.getTimeStatus()!=TimeCountStatus.OFF
		) {
			throw new RuntimeException("Illegal time count status : "+timeCounted.getTimeStatus());
		}else {
			timeCounted.setTimeStatus(TimeCountStatus.COUNTING);
			timeCounted.setStart(System.nanoTime());
		}
	}
	
	public static void timePause(TimeCounted timeCounted) {
		long pause = System.nanoTime();
		if (timeCounted.getTimeStatus()!=TimeCountStatus.COUNTING) {
			throw new RuntimeException("Illegal time count status : "+timeCounted.getTimeStatus());
		}else {
			timeCounted.setTime(timeCounted.getTime()+(pause - timeCounted.getStart()));
			timeCounted.setTimeStatus(TimeCountStatus.PAUSE);
		}
	}
	
	public static void timeContinue(TimeCounted timeCounted) {
		if (timeCounted.getTimeStatus()!=TimeCountStatus.PAUSE) {
			throw new RuntimeException("Illegal time count status : "+timeCounted.getTimeStatus());
		}else {
			timeCounted.setTimeStatus(TimeCountStatus.COUNTING);
			timeCounted.setStart(System.nanoTime());
		}
	}
	
	public static void timeStop(TimeCounted timeCounted) {
		long stop = System.nanoTime();
		if (timeCounted.getTimeStatus()==TimeCountStatus.OFF) {
			throw new RuntimeException("Illegal time count status : "+timeCounted.getTimeStatus());
		}else if (timeCounted.getTimeStatus()==TimeCountStatus.COUNTING){
			timeCounted.setTime(timeCounted.getTime()+(stop - timeCounted.getStart()));
			timeCounted.setTimeStatus(TimeCountStatus.OFF);
		}else {	// TimeCountStatus.PAUSE
			timeCounted.setTimeStatus(TimeCountStatus.OFF);
		}
	}

	public static void timeAdd(TimeCounted timeCounted, long nanoTime) {
		boolean continueTimer = timeCounted.getTimeStatus()==TimeCountStatus.COUNTING;
		if (continueTimer)	timePause(timeCounted);
		long time = timeCounted.getTime();
		timeCounted.setTime(time+nanoTime);
		if (continueTimer)	timeContinue(timeCounted);
	}
	
	public static void timeMinus(TimeCounted timeCounted, long nanoTime) {
		boolean continueTimer = timeCounted.getTimeStatus()==TimeCountStatus.COUNTING;
		if (continueTimer)	timePause(timeCounted);
		long time = timeCounted.getTime();
		timeCounted.setTime(time - nanoTime);
		if (continueTimer)	timeContinue(timeCounted);
	}
}