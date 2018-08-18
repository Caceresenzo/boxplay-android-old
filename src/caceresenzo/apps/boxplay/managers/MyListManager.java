package caceresenzo.apps.boxplay.managers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import caceresenzo.apps.boxplay.managers.XManagers.AbstractManager;
import caceresenzo.libs.boxplay.mylist.MyListItem;

public class MyListManager extends AbstractManager {
	
	public static final String TAG = MyListManager.class.getSimpleName();
	
	private File listFolder, rawListDataFile;
	
	private List<MyListItem<?>> watchLaterList;
	
	@Override
	public void initialize() {
		this.listFolder = new File(getManagers().getBaseApplicationDirectory() + "list/");
		this.rawListDataFile = new File(listFolder, "watchinglist.raw");
		
		this.watchLaterList = new ArrayList<>();
		
		load();
	}
	
	@Override
	protected void destroy() {
		save();
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		if (!checkFile()) {
			return;
		}
		
		try {
			InputStream file = new FileInputStream(rawListDataFile);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			
			try {
				List<MyListItem<?>> recoveredItems = (List<MyListItem<?>>) input.readObject();
				
				if (recoveredItems != null) {
					watchLaterList.addAll(recoveredItems);
				}
			} finally {
				input.close();
			}
		} catch (Exception exception) {
			Log.e(TAG, "Failed to load Watch Later list, first time ?", exception);
		}
		
		save(); // Will reset everything if incompatible or invalid loading
	}
	
	public void save() {
		if (!checkFile() && watchLaterList != null) {
			return;
		}
		
		try {
			OutputStream file = new FileOutputStream(rawListDataFile);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			
			try {
				output.writeObject(watchLaterList);
			} finally {
				output.close();
			}
		} catch (IOException exception) {
			Log.e(TAG, "Failed to save Watch Later list", exception);
		}
	}
	
	public boolean checkFile() {
		if (!rawListDataFile.exists() || rawListDataFile.isDirectory()) {
			try {
				rawListDataFile.mkdirs();
				rawListDataFile.delete();
				rawListDataFile.createNewFile();
				return true;
			} catch (IOException exception) {
				return false;
			}
		}
		
		return true;
	}
	
}