package mediaGate.generalFrontend.flac.metadata.Tags;

import java.util.HashMap;

import mediaGate.generalFrontend.flac.metadata.FlacUtils;

public class VorbisTag {

	/** hält die komplette Headersize*/
	private int headerSize = 0;
	
	/** hält die extrahierten Tags*/
	private HashMap<String, String>tagMap = new HashMap<String,String>();
	
	public VorbisTag(FlacUtils flacReader)throws Exception{
		
		headerSize = flacReader.readBlock(3);
		//Einlesen des gesamten Blocks
		byte[] block = new byte[headerSize];
		flacReader.readBytes(block);
		
		int i = 0;
		while(true){
			int len = block[i];
			len = len + 4;
			byte[]value = new byte[len];
			System.arraycopy(block, i, value, 0, len);
			
			String sValue = prepareComment(value);
			saveValueTag(sValue);
			i = i + len;
			//Aus irgendeinem Grund stand in der Flac-Datei ein falscher Pointer...
			//Naja - wir nehmen ihn mal so mit!
			if(sValue.toUpperCase().startsWith("REFERENCE LIBFLAC")){
				i = i + 4;
			}
			if(i>=block.length){
				break;
			}
		}
		
	}
	
	private String prepareComment(byte[] value){
		String sValue = new String(value);
		return sValue.substring(4);
	}
	
	private void saveValueTag(String tag){
		
		if(tag.toUpperCase().startsWith("REFERENCE LIBFLAC")){
			//Diese Information will ich nun wirklich nicht speichern....
			return;
		}
		
		String TAGTITLE = getTag(tag);
		String TAGVALUE = getValue(tag);
		tagMap.put(TAGTITLE, TAGVALUE);
	}
	
	private String getTag(String tag){
		int index = tag.indexOf("=");
		if(index >-1){
			return tag.substring(0,index).toUpperCase();
		}
		return tag;
	}
	
	private String getValue(String tag){
		int index = tag.indexOf("=");
		if(index >-1){
			return tag.substring(index+1).toUpperCase();
		}
		return tag;
	}
	
	
	/**
	 * TITLE 	
	 * VERSION 	
	 * ALBUM 	
	 * TRACKNUMBER
	 * ARTIST 	
	 * PERFORMER
	 * COPYRIGHT
	 * LICENSE 
	 * ORGANISATION
	 * DESCRIPTION 
	 * GENRE 
	 * DATE 
	 * LOCATION 
	 * CONTACT 
	 * ISRC
	 * @param flacReader
	 * @throws Exception
	 */
	public String getTagValue(String key){
		return tagMap.get(key.toUpperCase());
	}
	
	public String toString(){
		String ret = "VORBISTAG\r\n" + 
				"-------------------------\r\n" +
				"TITLE : " + tagMap.get("TITLE") + "\r\n" + 
				"VERSION : " + tagMap.get("VERSION") + "\r\n" + 
				"ALBUM : " + tagMap.get("ALBUM") + "\r\n" + 
				"TRACKNUMBER : " + tagMap.get("TRACKNUMBER") + "\r\n" + 
				"ARTIST : " + tagMap.get("ARTIST") + "\r\n" + 
				"PERFORMER : " + tagMap.get("PERFORMER") + "\r\n" + 
				"COPYRIGHT : " + tagMap.get("COPYRIGHT") + "\r\n" + 
				"LICENSE : " + tagMap.get("LICENSE") + "\r\n" + 
				"ORGANISATION : " + tagMap.get("ORGANISATION") + "\r\n" + 
				"DESCRIPTION : " + tagMap.get("DESCRIPTION") + "\r\n" + 
				"GENRE : " + tagMap.get("GENRE") + "\r\n" + 
				"DATE : " + tagMap.get("DATE") + "\r\n" + 
				"LOCATION : " + tagMap.get("LOCATION") + "\r\n" + 
				"CONTACT : " + tagMap.get("CONTACT") + "\r\n" + 
				"ISRC : " + tagMap.get("ISRC") + "\r\n";
		return ret;
	}
}
