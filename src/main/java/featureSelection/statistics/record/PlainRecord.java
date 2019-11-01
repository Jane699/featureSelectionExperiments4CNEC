package featureSelection.statistics.record;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString
public class PlainRecord {
	@Getter private Collection<RecordFieldInfo> titles;
	@Getter private Map<String, PlainRecordItem<?, ?>> recordItems;
	
	public PlainRecord(Collection<RecordFieldInfo> titles) {
		recordItems = new HashMap<>();
		this.titles = titles;
	}
	
	public <DataValue, DBValue> boolean set(RecordFieldInfo field, DataValue value, DBValue dbValue) {
		if (titles.contains(field)) {
			recordItems.put(field.getField(), new PlainRecordItem<DataValue, DBValue>(field, value, dbValue));
			return true;
		}else {
			return false;
		}
	}
}