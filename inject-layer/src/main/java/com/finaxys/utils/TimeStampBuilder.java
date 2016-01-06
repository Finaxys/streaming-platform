package com.finaxys.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampBuilder {
	public static final String TIME_FORMAT = "h:mm";

	public static final String DATE_FORMAT = "MM/dd/yyyy";

	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(TimeStampBuilder.class.getName());

	private int nbAgents;
	private int nbOrderBooks;
	private int nbTickMax;
	private int currentTick = 1;
	private int currentDay = 0;
	private long dateToSeconds = 0L;
	private long openHoursToSeconds;
	private long closeHoursToSeconds;
	private long ratio;
	private long timeStamp;
	private long nbMaxOrderPerTick;
	private long timePerOrder;
	private static final long nbMilliSecDay = 86400000;
	private static final long nbMilliSecHour = 3600000;

	public TimeStampBuilder(String dateBegin, String openHourStr,
                            String closeHourStr, int nbTickMax, int nbAgents, int nbOrderBooks) {
		this.nbTickMax = nbTickMax;
		this.nbAgents = nbAgents;
		this.nbOrderBooks = nbOrderBooks;
		resetFromString(dateBegin, openHourStr, closeHourStr);
	}
	
	public TimeStampBuilder() {
	}

	// @TODO ajouter une verification pour que ce ne soit fait qu'une fois
	public void init() {
		ratio = (closeHoursToSeconds - openHoursToSeconds) / (nbTickMax); 
		// +1 to not reach the closehour on the last tick or not +1 but begin at open hour

		LOGGER.info("ratio = " + ratio);

		// calc nb max order between 2 ticks
		nbMaxOrderPerTick = getNbAgents() * getNbOrderBooks() * 2;
		LOGGER.info("nbmaxorderpertick = " + nbMaxOrderPerTick);
		timePerOrder = (ratio / nbMaxOrderPerTick);
		LOGGER.info("timePerOrder is = " + timePerOrder);
		setTimeStamp(baseTimeStampForCurrentTick());
	}

	public long baseTimeStampForCurrentTick() {
		long baseTimeStampCurrentTick;
		if (currentTick == nbTickMax) {
			baseTimeStampCurrentTick = nbMilliSecHour + dateToSeconds
					+ (currentDay - 1) * nbMilliSecDay + openHoursToSeconds
					+ (currentTick - 1) * ratio;
		} else {
			baseTimeStampCurrentTick = nbMilliSecHour + dateToSeconds
					+ currentDay * nbMilliSecDay + openHoursToSeconds
					+ (currentTick - 1) * ratio;
		}
		return (baseTimeStampCurrentTick);
	}

	public long baseTimeStampForNextTick() {
		long baseTimeStampNextTick;
		if (currentTick == nbTickMax) {
			baseTimeStampNextTick = nbMilliSecHour + dateToSeconds
					+ (currentDay - 1) * nbMilliSecDay + openHoursToSeconds
					+ (currentTick) * ratio;
		} else {
			baseTimeStampNextTick = nbMilliSecHour + dateToSeconds + currentDay
					* nbMilliSecDay + openHoursToSeconds + (currentTick)
					* ratio;
		}
		return (baseTimeStampNextTick);
	}

	public long nextTimeStamp() {
		timeStamp += timePerOrder;
		return (timeStamp);
	}

	protected void resetFromString(String dateBegin, String openHourStr,
			String closeHourStr) throws InjectLayerException {

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			Date date = formatter.parse(dateBegin);

			// LOGGER.info("date = " + date);
			dateToSeconds = date.getTime();
			// LOGGER.info("timestamp à partir du fichier de conf : " +
			// dateToSeconds);

			DateFormat dateFormatter = new SimpleDateFormat(TIME_FORMAT);
			Date openHour = null;
			Date closeHour = null;
			openHour = (Date) dateFormatter.parse(openHourStr);
			assert openHour != null;
			closeHour = (Date) dateFormatter.parse(closeHourStr);
			assert closeHour != null;
			openHoursToSeconds = openHour.getTime();
			closeHoursToSeconds = closeHour.getTime();
		} catch (ParseException e) {
			throw new InjectLayerException(
					"cannot init TimeStampBuilder. Check configuration", e);
		}

	}

	public int getNbAgents() {
		return nbAgents;

	}

	public int getNbOrderBooks() {
		return nbOrderBooks;
	}

	public int getNbTickMax() {
		return nbTickMax;
	}

	public void setNbAgents(int nbAgents) {
		this.nbAgents = nbAgents;
	}

	public void setNbOrderBooks(int nbOrderBooks) {
		this.nbOrderBooks = nbOrderBooks;
	}

	public void setNbTickMax(int nbTickMax) {
		this.nbTickMax = nbTickMax;
	}
	
	public int getCurrentTick() {
		return currentTick;
	}

	public void setCurrentTick(int currentTick) {
		this.currentTick = currentTick;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(int currentDay) {
		this.currentDay = currentDay;
	}

	public long getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getTimePerOrder() {
		return timePerOrder;
	}

	public long getDateToSeconds() {
		return dateToSeconds;
	}

	public long getOpenHoursToSeconds() {
		return openHoursToSeconds;
	}

	public long getCloseHoursToSeconds() {
		return closeHoursToSeconds;
	}

	public long getNbMaxOrderPerTick() {
		return nbMaxOrderPerTick;
	}

}