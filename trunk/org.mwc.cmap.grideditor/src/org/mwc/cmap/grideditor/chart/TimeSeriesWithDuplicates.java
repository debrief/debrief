package org.mwc.cmap.grideditor.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.mwc.cmap.gridharness.data.FormatDateTime;

import MWC.GUI.TimeStampedDataItem;

/**
 * Workaround for the problem that {@link GriddableSeries} may contain duplicate
 * timestamps (e.g, after adding rows in the table) but the {@link TimeSeries}
 * can not.
 * <p>
 * Each item which is duplicate for some existing chart item in the series, will
 * NOT be added to the series but stored internally. If some future changes will
 * resolve the duplication, corresponding chart items will be will be added or
 * removed.
 */
class TimeSeriesWithDuplicates {

	/**
	 * For every {@link TimeStampedDataItem} which is the
	 * <strong>duplicate</strong> maps it to the its counterpart {@link
	 * BackedTimeSeriesDataItem} which is actually in the series
	 */
	private final IdentityHashMap<TimeStampedDataItem, BackedTimeSeriesDataItem> myDomainDuplicate2Existing;

	/**
	 * For every {@link TimeStampedDataItem} which is the
	 * <strong>duplicate</strong> maps it to the pre-created {@link
	 * BackedTimeSeriesDataItem} which is NOT in the series but stores the
	 * original time for the case when item's time will be changed.
	 */
	private final IdentityHashMap<TimeStampedDataItem, BackedTimeSeriesDataItem> myDomainDuplicate2Deferred;

	/**
	 * For every {@link TimeStampedDataItem} which actually presents in the
	 * series, maps it to its {@link BackedTimeSeriesDataItem}.
	 */
	private final IdentityHashMap<TimeStampedDataItem, BackedTimeSeriesDataItem> myDomain2Existing;

	private final TimeSeries myTimeSeries;

	private final TimeSeriesCollection myDataSet;

	public TimeSeriesWithDuplicates(String seriesKey) {
		myDataSet = new TimeSeriesCollection(getDefaultTimeZone());
		myTimeSeries = new TimeSeries(seriesKey);
		myDataSet.addSeries(myTimeSeries);
		myDomainDuplicate2Deferred = new IdentityHashMap<TimeStampedDataItem, BackedTimeSeriesDataItem>();
		myDomainDuplicate2Existing = new IdentityHashMap<TimeStampedDataItem, BackedTimeSeriesDataItem>();
		myDomain2Existing = new IdentityHashMap<TimeStampedDataItem, BackedTimeSeriesDataItem>();
	}

	public XYDataset getDataSet() {
		return myDataSet;
	}

	public void addDomainItem(TimeStampedDataItem addedItem, double itemValue) {
		BackedTimeSeriesDataItem newChartItem = createChartItem(addedItem, itemValue);
		BackedTimeSeriesDataItem existing = searchForExistingDuplicate(newChartItem);
		if (existing == null) {
			//safe to add then 
			addToSeries(newChartItem);
		} else {
			addDuplicate(newChartItem, existing);
		}
	}

	public void removeDomainItem(TimeStampedDataItem removedItem) {
		//NOTE: we can't be sure that removedItem's time is correct
		if (isDuplicate(removedItem)) {
			removeDuplicate(removedItem);
		} else {
			BackedTimeSeriesDataItem existing = myDomain2Existing.get(removedItem);
			if (existing == null) {
				//wow
				return;
			}
			assert existing.getDomainItem() == removedItem;
			List<TimeStampedDataItem> duplicates = findRegisteredDuplicatesFor(existing);
			myTimeSeries.delete(existing.getPeriod());
			if (!duplicates.isEmpty()) {
				TimeStampedDataItem luckyDomainItem = duplicates.get(0);
				BackedTimeSeriesDataItem toBeAddedToSeries = myDomainDuplicate2Existing.get(luckyDomainItem);
				for (TimeStampedDataItem next : duplicates) {
					BackedTimeSeriesDataItem precreatedChartItem = myDomainDuplicate2Deferred.get(luckyDomainItem);
					removeDuplicate(next);
					if (next == luckyDomainItem) {
						addToSeries(precreatedChartItem);
					} else {
						//now it becomes the duplicate for the "lucky" one which is about to be added to series 
						addDuplicate(precreatedChartItem, toBeAddedToSeries);
					}
				}
			}
		}
	}

	public void updateDomainItem(TimeStampedDataItem changedItem, double currentValue) {
		if (isDuplicate(changedItem)) {
			BackedTimeSeriesDataItem deferred = getDeferredFor(changedItem);
			Date oldTime = deferred.getPeriod().getTime();
			if (oldTime.equals(changedItem.getDTG().getDate())) {
				//only value have changed, its still duplicate
				return;
			}
			//it may still be duplicate for something else but definitely not for this one
			removeDuplicate(changedItem);
			//now its just an "add" operation, 
			addDomainItem(changedItem, currentValue);
			return;
		}

		BackedTimeSeriesDataItem existing = myDomain2Existing.get(changedItem);
		if (existing == null) {
			//wow
			return;
		}
		Date oldTime = existing.getPeriod().getTime();
		if (oldTime.equals(changedItem.getDTG().getDate())) {
			myTimeSeries.update(existing.getPeriod(), currentValue);
			//time haven't changed, so duplciates state is the same
			return;
		}

		//so the time have changed for existing item
		//its the same as it would it be deleted and then added back
		removeDomainItem(changedItem);
		addDomainItem(changedItem, currentValue);
	}

	private void addDuplicate(BackedTimeSeriesDataItem duplicate, BackedTimeSeriesDataItem original) {
		myDomainDuplicate2Existing.put(duplicate.getDomainItem(), original);
		myDomainDuplicate2Deferred.put(duplicate.getDomainItem(), duplicate);
	}
	
	private void addToSeries(BackedTimeSeriesDataItem toBeAdded){
		assert !isDuplicate(toBeAdded.getDomainItem());
		myDomain2Existing.put(toBeAdded.getDomainItem(), toBeAdded);
		myTimeSeries.add(toBeAdded);
	}

	private void removeDuplicate(TimeStampedDataItem domainItem) {
		myDomainDuplicate2Deferred.remove(domainItem);
		myDomainDuplicate2Existing.remove(domainItem);
	}

	/**
	 * Linear for duplicates count, we assuming that there duplicates are rare
	 */
	private List<TimeStampedDataItem> findRegisteredDuplicatesFor(BackedTimeSeriesDataItem original) {
		List<TimeStampedDataItem> result = null;
		for (Map.Entry<TimeStampedDataItem, BackedTimeSeriesDataItem> next : myDomainDuplicate2Existing.entrySet()) {
			if (next.getValue() == original) {
				if (result == null) {
					result = new ArrayList<TimeStampedDataItem>();
				}
				result.add(next.getKey());
			}
		}
		return result == null ? Collections.<TimeStampedDataItem> emptyList() : result;
	}

	private boolean isDuplicate(TimeStampedDataItem domainItem) {
		return myDomainDuplicate2Deferred.containsKey(domainItem);
	}

	private BackedTimeSeriesDataItem getDeferredFor(TimeStampedDataItem domainItem) {
		return myDomainDuplicate2Deferred.get(domainItem);
	}

	private BackedTimeSeriesDataItem searchForExistingDuplicate(BackedTimeSeriesDataItem chartItem) {
		//we should consider the possibility that chartItem is duplicate for some existing period 
		//(because duplicates are not supported by TimeSeries)
		//but we don't want to call binarySearch for EVERY domain item added (or being constructed on setInput) 
		//so, we will check for most frequent case when we can state for sure that there are no duplicates
		int seriesSize = myTimeSeries.getItemCount();
		if (seriesSize == 0) {
			return null;
		}
		TimeSeriesDataItem lastChartItem = myTimeSeries.getDataItem(seriesSize - 1);
		if (lastChartItem.compareTo(chartItem) < 0) {
			//thats hopefully most frequent case
			return null; //because series is sorted
		}
		//ok, bad luck, hopefully rare case, but we have to check anyway by binarySearch
		int existingIndex = myTimeSeries.getIndex(chartItem.getPeriod());
		if (existingIndex < 0) {
			return null;
		}
		return (BackedTimeSeriesDataItem) myTimeSeries.getDataItem(existingIndex);
	}

	private BackedTimeSeriesDataItem createChartItem(TimeStampedDataItem domainItem, double value) {
		Date time = domainItem.getDTG().getDate();
		return new BackedTimeSeriesDataItem(new FixedMillisecond(time), value, domainItem);
	}

	static TimeZone getDefaultTimeZone() {
		return TimeZone.getTimeZone(FormatDateTime.DEFAULT_TIME_ZONE_ID);
	}

}