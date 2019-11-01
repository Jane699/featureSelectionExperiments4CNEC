package featureSelection.repository.frame.model.universe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * An {@link UniverseInstance}s generator. (Improved version)
 * 
 * @author Benjamin_L
 */
@Slf4j
public class UniverseGeneratorImp {	
	@Getter @Setter private UniverseGeneratingGuidance generatingInfo;
	/**
	 * The universe list
	 */
	private LinkedList<UniverseInstance> universeInsList = new LinkedList<>();

	public UniverseGeneratorImp() {
		reset();
	}
	
	/**
	 * Reset the generator;
	 */
	public void reset() {
		universeInsList.clear();
		UniverseInstance.resetID();
		generatingInfo = null;
	}

	/**
	 * Input and set the data set from file line by line.
	 * 
	 * @param file 
	 * 		The file to read.
	 * @param regex
	 * 		The symbol to separate each column.
	 * @param decisionIndex
	 * 		The index of the decision value(starts from 1, -1 as the last one).
	 * @throws Exception if file can't be read successfully. Or the first/last index is illegal.
	 */
	public void setDataSetWithFileByLines(File file, String regex, int decisionIndex) throws Exception {
		if (!file.exists())	throw new FileNotFoundException(file.getAbsolutePath());
		
		if (generatingInfo!=null && generatingInfo.getLastUniverseInstanceID()>1) {
			UniverseInstance.setID(generatingInfo.getLastUniverseInstanceID()+1);
			log.info("Universe instance ID -> "+UniverseInstance.getNumCounter());
		}
		
		universeInsList.clear();
		String line;
		LineIterator lineIterator = IOUtils.lineIterator(FileUtils.openInputStream(file), "UTF-8");
		while (lineIterator.hasNext()) {
			line = lineIterator.next();
			if (line!=null)	addUniverse(line.split(regex), decisionIndex);
		}
		generatingInfo.setLastUniverseInstanceID(universeInsList.getLast().getNum());
	}
	
	/**
	 * Return a list of universes
	 */
	public List<UniverseInstance> universeInsList(){
		UniverseInstance.resetID();
		return universeInsList;
	}

	/**
	 * Add an {@link UniverseInstance} by the given parameters. <code>columnMap</code> will be updated if 
	 * necessary.
	 * 
	 * @param data
	 * 		A line of data in {@link String[]}.
	 * @param decisionIndex
	 * 		The index of decision at column.(Starts from 1, -1 as the last column).
	 * @throws Exception if the column number of <code>data</code> is un-compatible with current {@link UniverseInstance}.
	 */
	public void addUniverse(String[] data, int decisionIndex)
			throws Exception 
	{
		int column = previousUniverseColumnNumber();
		if (column!=0 && data.length!=column) {
			throw new Exception("Invalid column number, "+column+" is expected: "+data.length);
		}else {
			if (column==0) {
				column = data.length;
				generatingInfo = new UniverseGeneratingGuidance(column);
			}
			if (decisionIndex<0)	decisionIndex = column+decisionIndex+1;

			int[] instanceArray = new int[column];
			Map<String, Integer>[] columnMap = generatingInfo.getColumnValueMap();
			for (int col=1; col<=column; col++) {
				Integer value = columnMap[col-1].get(data[col-1]);
				if (value==null)	columnMap[col-1].put(data[col-1], value=columnMap[col-1].size());
				
				columnValue2UniverseData(
					col, 
					instanceArray, 
					decisionIndex, 
					value
				);
			}
			universeInsList.add(new UniverseInstance(instanceArray));
		}
	}
		
	/**
	 * Fill column value into universe data.
	 * 
	 * @param col
	 * 		The column number. (Starts from 1)
	 * @param instance
	 * 		The instance/data in {@link UniverseInstance}.
	 * @param decisionIndex
	 * 		The decision index. (Starts from 1)
	 * @param value
	 * 		The value to be filled.
	 */
	private void columnValue2UniverseData(int col, int[] instance, int decisionIndex, int value) {
		if (col==decisionIndex)		instance[0] = value;
		else if (col>decisionIndex)	instance[col-1] = value;
		else						instance[col] = value;
	}
	
	private int previousUniverseColumnNumber() {
		return (generatingInfo==null? 0: generatingInfo.getColumnSize());
	}
}