package featureSelection.statistics.info;

import java.io.File;

import lombok.Data;

@Data
public class DatasetInfo {
	private String datasetName;
	private int universeSize;
	private int conditionAttributeSize;
	private int decisionAttributeSize;

	private File datsetFile;
	private long fileSie;
	
	private long initTime;
}
