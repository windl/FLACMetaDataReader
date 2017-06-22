package mediaGate.generalFrontend.flac.metadata.Tags;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mediaGate.generalFrontend.flac.metadata.FlacUtils;

public class PictureTag {

	
	/** hält die komplette Headersize*/
	private int headerSize = 0;
	
	/** hält die INT-Nummer der Typebeschreibung. Diese muss später gegen die Aufzählung pictureType gemappt werden*/
	private int picType = 0;
	
	/** wie z.B. image/jpeg*/
	private String picTypeDesc = "";
	
	/** hält eine Beschreibung zum Bild*/
	private String description = "";
	
	/** hält das eigentliche Cover*/
	private ImageIcon coverIcon = null;
	
	private String[] pictureType = {
		    "Other",
		    "32x32 pixels 'file icon' (PNG only)",
		    "Other file icon",
		    "Cover (front)",
		    "Cover (back)",
		    "Leaflet page",
		    "Media (e.g. label side of CD)",
		    "Lead artist/lead performer/soloist",
		    "Artist/performer",
		    "Conductor",
		    "Band/Orchestra",
		    "Composer",
		    "Lyricist/text writer",
		    "Recording Location",
		    "During recording",
		    "During performance",
		    "Movie/video screen capture",
		    "A bright coloured fish",
		    "Illustration",
		    "Band/artist logotype",
		    "Publisher/Studio logotype"
		};
	
	public PictureTag(FlacUtils flacReader)throws Exception{
		
		headerSize = flacReader.readBlock(3);
		//Einlesen des gesamten Blocks
		byte[] block = new byte[headerSize];
		flacReader.readBytes(block);
		
		//Liest den Typ des Pictures ein
		byte[] picTypeBlock = {block[0],block[1],block[2],block[3]};
		picType = flacReader.convertByteToInt(picTypeBlock);
		
		//Liest die Länge des "Image-Headers" ein und auch den dazugehörigen String
		byte[] picDes = {block[4],block[5],block[6],block[7]};
		int len = flacReader.convertByteToInt(picDes);
		
		byte[]value = new byte[len];
		System.arraycopy(block, 8, value, 0, len);
		picTypeDesc = new String(value);
		
		//Ab jetzt sind die Positionen variable....
		int pos = 8 + len;
		byte[] descLenByte = {block[pos],block[pos+1],block[pos+2],block[pos+3]};
		int decLen = flacReader.convertByteToInt(descLenByte);
		pos = pos + 4;
		
		value = new byte[decLen];
		System.arraycopy(block, pos, value, 0, decLen);
		description = new String(value);
		pos = pos + decLen;
		//Skip the next 16 Byte
		pos = pos + 16;
		
		//Anzahl an Bytes für das Bild einlesen..
		byte[] picLenByte = {block[pos],block[pos+1],block[pos+2],block[pos+3]};
		int picLen = flacReader.convertByteToInt(picLenByte);
		pos = pos + 4;
		
		byte[] pictureByte = new byte[picLen];
		System.arraycopy(block, pos, pictureByte, 0, picLen);
		coverIcon = new ImageIcon(pictureByte);
		
	}
	
	public String getPictureType(){
		return pictureType[picType];
	}
	
	public String getPictureTypeDescription(){
		return picTypeDesc;
	}
	
	public String getPictureDescription(){
		return description;
	}
	
	public ImageIcon getIcon(){
		return coverIcon;
	}
	
	public String toString(){
		String ret = "PICTURETAG\r\n-----------------------------\r\n"+ 
					"PictureType : " + getPictureType() + "\r\n" + 
					"Picture type description : " + picTypeDesc + "\r\n" + 
					"Picture description : " + description + "\r\n" + 
					"Picture available? " + (coverIcon == null?"false":"true") + "\r\n";
		return ret;
	}
	
}
