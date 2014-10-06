package ca.fraggergames.ffxivextract.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Hashtable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

//Stores a master file list to check between updates.

public class CompareFile {
	
	String lastPatchDate;
	String currentPatchDate;
	
	Hashtable<Integer, String> files; //File Hash -> Patch Date
	
	String loadedIndexName;
	
	public CompareFile(String indexPath) {
		
		files = new Hashtable<Integer, String>();
		Calendar c = Calendar.getInstance();
		currentPatchDate = c.get(Calendar.YEAR)+""+c.get(Calendar.DAY_OF_YEAR)+""+c.get(Calendar.HOUR)+""+c.get(Calendar.MINUTE)+"2";
		loadedIndexName = indexPath;
	}

	public static CompareFile getCompareFile(String indexName)
	{		
		File file = new File("./" + indexName + ".cmp");
		if (file.exists())
		{
			Kryo kryo = new Kryo();
			Input in = null;
			try {
				in = new Input(new FileInputStream(new File("./" + indexName + ".cmp")));
			} catch (FileNotFoundException e) {
				return new CompareFile(indexName);
			}
			CompareFile obj = kryo.readObject(in, CompareFile.class);
			in.close();
			return obj;
		}	
		else
			return new CompareFile(indexName);
	}
	
	public boolean isNewFile(int hash)
	{
		if ((files.get(hash) == null) && lastPatchDate!=null&&!lastPatchDate.equals(currentPatchDate)) //New File
		{
			lastPatchDate = currentPatchDate;
			Calendar c = Calendar.getInstance();
			currentPatchDate = c.get(Calendar.YEAR)+""+c.get(Calendar.DAY_OF_YEAR)+""+c.get(Calendar.HOUR)+""+c.get(Calendar.MINUTE);
			files.put(hash, currentPatchDate);
			return true;
		}
		else if (files.get(hash) != null && files.get(hash).equals(currentPatchDate)  && lastPatchDate!=null&&!lastPatchDate.equals(currentPatchDate)) //New File, but already recorded
			return true;
		else //Old File
			return false;		
	}
	
	public void save() throws FileNotFoundException{
		Kryo kryo = new Kryo();
		Output out = new Output(new FileOutputStream(new File("./" + loadedIndexName + ".cmp")));
		kryo.writeObject(out, this);
		out.close();
	}
}
