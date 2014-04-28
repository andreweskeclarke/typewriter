package com.aeclarke.typewriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import android.R.string;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aeclarke.typewriter.R;
import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.Font.MacintoshEncodingId;
import com.google.typography.font.sfntly.Font.PlatformId;
import com.google.typography.font.sfntly.data.ReadableFontData;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.Header;
import com.google.typography.font.sfntly.table.Table;
import com.google.typography.font.sfntly.table.Table.Builder;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapFormat4;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.CMapTable.CMapId;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;
import com.google.typography.font.tools.subsetter.CMapTableBuilder;

public class MainActivity extends Activity {

	private static final String ROBOTO_PATH = "/system/fonts/Roboto-Regular.ttf";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		((TextView) findViewById(R.id.main_content_text_1)).setText("Starting...");
		try {
			FontManipulator fontManipulator = new FontManipulator(ROBOTO_PATH);
			fontManipulator.backupFont();
			Font font = fontManipulator.loadFont(new File(ROBOTO_PATH))[0];

			listFontPlatformInformation(font, (TextView) findViewById(R.id.main_content_text_2));
			listTablesTagListToView(font, (TextView) findViewById(R.id.main_content_text_4));
			listSampleCmapEntries(font, (TextView) findViewById(R.id.main_content_text_3));
			
//			fontManipulator.saveFontAs(fontManipulator.applyCaesarCypherToFont(font), ROBOTO_PATH, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private CMapTable listFontPlatformInformation(Font font, TextView findViewById) {
		CMapTable cmapTable = font.getTable(Tag.cmap);
		Iterator<CMap> cmapSubtableIterator = cmapTable.iterator();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("PlatformID/EncodingID combinations present in Roboto-Regular.ttf:");
		stringBuilder.append("\n");
		for (; cmapSubtableIterator.hasNext();) {
			CMap next = cmapSubtableIterator.next();
			stringBuilder.append(Integer.toString(next.platformId()) + " - " + Integer.toString(next.encodingId()) + "\n");
		}
		stringBuilder.append("\nID  Platform Description\n");
		stringBuilder.append("0   Unicode\n");
		stringBuilder.append("1   Macintosh\n");
		stringBuilder.append("2   ISO [deprecated]\n");
		stringBuilder.append("3   Windows\n");
		stringBuilder.append("4   Custom\n");
		stringBuilder.append("\nId Encoding Description\n");
		stringBuilder.append("0  Unicode 1.0 semantics" + "\n" +
							"1  Unicode 1.1 semantics" + "\n" +
							"2  ISO/IEC 10646 semantics" + "\n" +
							"3  Unicode 2.0 and onwards semantics, Unicode BMP only (cmap subtable formats 0, 4, 6)." + "\n" +
							"4  Unicode 2.0 and onwards semantics, Unicode full repertoire (cmap subtable formats 0, 4, 6, 10, 12)." + "\n" +
							"5  Unicode Variation Sequences (cmap subtable format 14)." + "\n" +
							"6  Unicode full repertoire (cmap subtable formats 0, 4, 6, 10, 12, 13).");
		stringBuilder.append("\n\n\nBelow are some mapped characters:\n");
		findViewById.setText(stringBuilder);
		return cmapTable;
	}

	private Map<Integer, Integer> listSampleCmapEntries(Font font,
			TextView cmappingTextView) {
		Map<Integer, Integer> characterMapping = new HashMap<Integer, Integer>();
		CMapTable cmapTable = font.getTable(Tag.cmap);
		CMap cmap = cmapTable.cmap(CMapId.getInstance(0,3));
		Iterator<Integer> cmapIterator = cmap.iterator();
		StringBuilder mappingStringBuilder = new StringBuilder();
		mappingStringBuilder.append("\nThe cmap table format is: " + cmap.format() + "\n");
		for (; cmapIterator.hasNext();) {
			int characterId = cmapIterator.next();
			int glyphId  = cmap.glyphId(characterId);
			if(characterId < 100) {
				characterMapping.put(characterId, glyphId + 1);
				mappingStringBuilder.append(Integer.toHexString(characterId) + ":" + Integer.toString(glyphId) + "\n");
			} else {
				characterMapping.put(characterId, glyphId);
			}
		}
		cmappingTextView.setText(mappingStringBuilder);
		return characterMapping;
	}

	private void listTablesTagListToView(Font font, TextView tableTagsView) {
		StringBuilder tableListBuilder = new StringBuilder();
		tableListBuilder.append("Below are the tag names of each table in the font:\n");
		for (Iterator<? extends Table> tableIterator = font.iterator(); tableIterator.hasNext();) {
			Table table = tableIterator.next();
			tableListBuilder.append(Tag.stringValue(table.headerTag()) + "\n");
		}
		tableTagsView.setText(tableListBuilder);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
}
