package basic.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExcelUtils {
	public static void saveFile(Collection<String[]> dataLines, File file, boolean rewrite) throws IOException {
		if (!file.exists())	file.createNewFile();
		try(
			CSVPrinter csvPrinter = new CSVPrinter(
										new BufferedWriter(
											new OutputStreamWriter(
												new FileOutputStream(file, !rewrite),
												"GBK"
											)
										),
										CSVFormat.EXCEL
									);
		){
			for (String[] data: dataLines)	csvPrinter.printRecord(data);
		}
	}
}
