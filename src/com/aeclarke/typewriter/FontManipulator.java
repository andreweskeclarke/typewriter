package com.aeclarke.typewriter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.CMapTable.CMapId;
import com.google.typography.font.tools.subsetter.CMapTableBuilder;
import com.google.typography.font.sfntly.Font.Builder;

public class FontManipulator {

	private String fontPath;

	public FontManipulator(String fontPath) {
		this.fontPath = fontPath;
	}

	public Font applyCaesarCypherToFont(Font font) throws IOException, FileNotFoundException {
		Builder builder = FontFactory.getInstance().loadFontsForBuilding(new FileInputStream(fontPath))[0];
		Map<Integer, Integer> characterMapping = new HashMap<Integer, Integer>();
		CMap cmap = font.<CMapTable>getTable(Tag.cmap).cmap(CMapId.getInstance(0,3));
		
		for (Iterator<Integer> cmapIterator = cmap.iterator(); cmapIterator.hasNext();) {
			int characterId = cmapIterator.next();
			int glyphId  = cmap.glyphId(characterId);
			if(characterId < 100) {
				characterMapping.put(characterId, glyphId + 1);
			} else {
				characterMapping.put(characterId, glyphId);
			}
		}
		
		CMapTableBuilder cMapTableBuilder = new CMapTableBuilder(builder, characterMapping);
		cMapTableBuilder.build();			
		return builder.build();
	}
	
	public Font[] loadFont(File file) throws IOException {
		FontFactory fontFactory = FontFactory.getInstance();
		fontFactory.fingerprintFont(true);
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			return fontFactory.loadFonts(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public void saveFontAs(Font font, String path, Activity activty) throws IOException {
		backupFont(path);
		String tempFontFile = activty.getFilesDir().getAbsolutePath() + "/temp_font.ttf";
		FontFactory fontFactory = FontFactory.getInstance();
		fontFactory.fingerprintFont(true);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(tempFontFile);
			fontFactory.serializeFont(font, os);
			runWithSu(new String[] {"rm " + path,
								  "mv " + tempFontFile + " " + path,
								  "chmod 644 " + path});
		} finally {
			if (os != null) {
				os.flush();
				os.close();
			}
		}
	}
	
	public void backupFont() {
		backupFont(fontPath);
	}

	private void backupFont(String pathToBackup) {
		try {
			runWithSu(new String[] {"mount -o remount,rw /system;",
								  "cp " + pathToBackup + " " + pathToBackup + ".bkp",
								  "chmod 644 " + pathToBackup});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runWithSu(String[] commands) throws IOException {
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());            
		for (String tmpCmd : commands) {
			os.writeBytes(tmpCmd+"\n");
		}           
		os.writeBytes("exit\n");  
		os.flush();
	}
}
