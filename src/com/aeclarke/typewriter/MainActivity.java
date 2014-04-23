package com.aeclarke.typewriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import android.app.Activity;
import android.app.Fragment;
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
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.CMapTable.CMapId;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		FontFactory factory = FontFactory.getInstance();
		Font font = null;
		TextView contentView = (TextView) findViewById(R.id.main_content_text);
		contentView.setMovementMethod(new ScrollingMovementMethod());
		try {
			font = loadFont(new File("/system/fonts/Roboto-Regular.ttf"))[0];
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
			stringBuilder.append("\n\n\nBelow are the mapped characters:\n\n");
			
			CMap cmap = cmapTable.cmap(CMapId.getInstance(0,3));
			Iterator<Integer> cmapIterator = cmap.iterator();
			for (; cmapIterator.hasNext();) {
				int characterId = cmapIterator.next();
				int glyphId  = cmap.glyphId(characterId);
				
				stringBuilder.append(Integer.toHexString(characterId) + ":" + Integer.toString(glyphId) + "\n");
			}
			contentView.setText(stringBuilder);			
			
			
			
//			GlyphTable glyfTable = font.getTable(Tag.glyf);
//			ReadableFontData fontData = glyfTable.readFontData();
//			Font.Builder fontBuilder = factory.newFontBuilder();
//			Builder<? extends Table> newTableBuilder = fontBuilder.newTableBuilder(Tag.glyf);
//
//			WritableFontData writableFontData = WritableFontData.createWritableFontData(0);//newTableBuilder.data();
//			fontData.copyTo(writableFontData);
		} catch (IOException e) {
			contentView.setText(e.getMessage());
		}

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
	
	  public static Font[] loadFont(File file) throws IOException {
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

}
