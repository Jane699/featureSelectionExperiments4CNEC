package tester;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

import basic.utils.NumberUtils;
import featureSelection.repository.frame.model.universe.UniverseGeneratorImp;
import featureSelection.repository.frame.model.universe.UniverseInstance;

public class BasicTester {
	protected static final int POS_SIG_DEVIATION = 0;
	protected static final double DEP_SIG_DEVIATION = NumberUtils.DECIMAL_WITH_12_ZERO;
	protected static final double SCE_SIG_DEVIATION = NumberUtils.DECIMAL_WITH_9_ZERO;
	protected static final double LCE_SIG_DEVIATION = NumberUtils.DECIMAL_WITH_12_ZERO;
	protected static final double CCE_SIG_DEVIATION = NumberUtils.DECIMAL_WITH_16_ZERO;
	
	// Import the dataset below.
	private static List<String[]> dataSet = Arrays.asList(
			"1, 1, 1, 1, 0".split(" "),
			"2, 2, 1, 1, 0".split(" "),
			"1, 1, 1, 1, 0".split(" "),
			"1, 3, 1, 3, 0".split(" "),
			"2, 2, 1, 1, 1".split(" "),
			"3, 1, 2, 1, 0".split(" "),
			"2, 2, 3, 2, 2".split(" "),
			"2, 3, 2, 2, 3".split(" "),
			"3, 1, 2, 1, 1".split(" "),
			"2, 2, 3, 2, 2".split(" "),
			"3, 1, 2, 1, 1".split(" "),
			"2, 3, 2, 2, 3".split(" "),
			"4, 3, 4, 2, 1".split(" "),
			"2, 2, 3, 2, 2".split(" "),
			"4, 3, 4, 2, 2".split(" ")
	);
	
	// Import dataset from a file
	private static File dataFile = new File("./dataset/demo.csv");
	
	public static List<UniverseInstance> universe;

	// if transfer field "dataSet" into an exact universe, set true;
	// else set false.
	private static boolean stringSource = false;
	
	@BeforeAll
	public static void initiateUniverse(){
		if (!stringSource) {
			UniverseGeneratorImp generator = new UniverseGeneratorImp();
			try {
//				for (String[] str : dataSet)	generator.addUniverse(str, -1);	// decision attribute at the last column.
				generator.setDataSetWithFileByLines(dataFile, ",", -1); // columns split by ",", decision attribute at the last column.
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			universe = generator.universeInsList();
		}else {
			stringIntArray2Universes();
		}
	}
	
	private static void stringIntArray2Universes() {
		UniverseInstance.resetID();
		
		int[] values;
		List<UniverseInstance> universeList = new LinkedList<>();
		for (String[] str : dataSet) {
			values = new int[str.length];
			values[0] = Integer.valueOf(str[str.length-1].replace(",", ""));
			for (int i=1; i<values.length; i++)	values[i] = Integer.valueOf(str[i-1].replace(",", ""));
			universeList.add(new UniverseInstance(values));
		}
		universe = universeList;
	}
}