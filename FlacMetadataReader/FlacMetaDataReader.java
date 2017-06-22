package mediaGate.generalFrontend.flac.metadata;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.RandomAccessFile;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mediaGate.generalFrontend.flac.metadata.Tags.PictureTag;
import mediaGate.generalFrontend.flac.metadata.Tags.StreaminfoTag;
import mediaGate.generalFrontend.flac.metadata.Tags.VorbisTag;

/**
 * Diese Klasse liest die Informationen zu einem FLAC-File aus.
 * Der Aufruf ist denkbar einfach.
 * 
 * 			FlacMetaDataReader metaData = new FlacMetaDataReader(Pfadname zum FLACFILE);
 * 			String album = metaData.getVorbisTag(FlacMetaDataReader.ALBUM);
 * 
 * 			JFrame frame = new JFrame();
 *			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 *			int sizeX = 500;
 *			int sizeY = 500;
 *			frame.setSize(sizeX, sizeY);
 *			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
 *			frame.setLocation((dim.width - sizeX) / 2, (dim.height - sizeY) / 2);
 *			frame.add(new JLabel(metaData.getCoverIcon()));
 *			frame.setVisible(true);
 * 
 * 
 * 
 * Hinweis auf Webseiten:
 * https://xiph.org/flac/format.html
 * https://stackoverflow.com/questions/16653100/what-are-those-32-bits-near-the-start-of-a-flac-file
 * 
 * @author Heidinger, Uwe
 *
 */



public class FlacMetaDataReader {

	/**
	 * Die KeyTags für den VORBIS-TAG
	 */
	public static String TITLE 			= "TITLE";
	public static String VERSION		= "VERSION"; 	
	public static String ALBUM			= "ALBUM"; 	
	public static String TRACKNUMBER	= "TRACKNUMBER";
	public static String ARTIST 		= "ARTIST";
	public static String PERFORMER		= "PERFORMER";
	public static String COPYRIGHT		= "COPYRIGHT";
	public static String LICENSE 		= "LICENSE";
	public static String ORGANISATION	= "ORGANISATION";
	public static String DESCRIPTION 	= "DESCRIPTION";
	public static String GENRE			= "GENRE";
	public static String DATE 			= "DATE";
	public static String LOCATION 		= "LOCATION";
	public static String CONTACT 		= "CONTACT";
	public static String ISRC			= "ISRC";
	//-----------------------------------------------------------------------------
	
	/** Name und Pfad der Datei*/	
	private String _file = "";
	/** RandomAccessFile zum einlesen der Datei*/
	private RandomAccessFile raf = null;
	/** Klasse für einige wichtige Verarbeitungen*/
	private FlacUtils utils = null;
	/** Liest die Informationen zum Stream wie Anzahl der Kanäle/Bits per Sample/Sekunden zu spielen etc*/
	private StreaminfoTag streaminfoTag = null;
	/** hält die Informationen über Album/Interpret/etc...*/
	private VorbisTag vorbisTag = null;
	/** hält alle notwenigen Informationen zum hinterlegtem Bild sowie das Bild selber*/
	private PictureTag pictureTag = null;
	/** hält die Anzahl der Bytes des geöffneten Lieds*/
	private long fileBytes = 0;
	
	public FlacMetaDataReader(String file)throws Exception{
		_file = file;
		
		try{
			raf = new RandomAccessFile(_file,"r");
			fileBytes = raf.length();
			utils = new FlacUtils(raf);
			//Header auslesen
			byte[] header = new byte[4];
			raf.read(header);
			if(header[0] == 102 && header[1] == 76 && header[2] == 97 && header[3] == 67 ){
				//Flacheader gefunden!
				//Datei kann verarbeitet werden
			} else {
				throw new Exception("Kein FLAC Header...");
			}
			
			while(true){
				//Einlesen um welchen Header es sich handelt....
				/*
			     * 0 		: STREAMINFO
			     * 1 		: PADDING
			     * 2 		: APPLICATION
			     * 3 		: SEEKTABLE
			     * 4 		: VORBIS_COMMENT
			     * 5 		: CUESHEET
			     * 6 		: PICTURE
			     * 7-126 	: reserved
			     * 127 		: invalid, to avoid confusion with a frame sync code
				 */
				int headerType = raf.read();
				if(headerType >127){
					break;
				}
				switch(headerType){
				case 0:
					streaminfoTag = new StreaminfoTag(utils);
					break;
				case 4:
					vorbisTag = new VorbisTag(utils);
					break;
				case 6:
					pictureTag = new PictureTag(utils); 
					break;
				}
			}
			raf.close();
		}catch(Exception error){
			error.printStackTrace();
			try{
				raf.close();
			}catch(Exception er){}
		}
	}
	
	/** gibt die tatsächliche Länge der Datei in Bytes zurück*/
	public long getFileSize(){
		return fileBytes;
	}
	
	/** gibt den Pfad und Namen der eingelesenen Datei zurück*/
	public String getFileName(){
		return _file;
	}
	
	/** gibt die Anzahl der Kanäle wieder*/
	public int getChannels(){
		if(streaminfoTag != null){
			return streaminfoTag.get_channels();
		}
		return 0;
	}
	
	/**gibt die Khz des Liedes zurück*/
	public int getKhz(){
		if(streaminfoTag != null){
			return streaminfoTag.get_khz();
		}
		return 0;
	}
	
	/** gibt die Spieldauer des Liedes in Sekunden an*/
	public int getSecondsToPlay(){
		if(streaminfoTag != null){
			return streaminfoTag.getTimeToPlaySec();
		}
		return 0;
	}
	
	/** gibt die Bits per Sample zurück*/
	public int getBitsPerSample(){
		if(streaminfoTag != null){
			return streaminfoTag.getBitsPerSample();
		}
		return -1;
	}
	
	/**
	 * Diese Funktion gibt den Wert eines VorbisTags zurück.
	 * Dabei sind die folgenden Keys berücksichtigt.
	 * 
	 * TITLE
	 * VERSION	
	 * ALBUM	
	 * TRACKNUMBER
	 * ARTIST 
	 * PERFORMER
	 * COPYRIGHT;
	 * LICENSE ;
	 * ORGANISATION
	 * DESCRIPTION
	 * GENRE	
	 * DATE 	
	 * LOCATION 
	 * CONTACT 
	 * ISRC		
	 * 
	 * @param tag
	 * @return
	 */
	public String getVorbisTag(String tag){
		if(vorbisTag != null){
			return vorbisTag.getTagValue(tag);
		}
		return "unknown";
	}
	
	/**
	 * Diese Funktion gibt den Type des hinterlegten Bildes wieder.
	 * Hierbei können die folgenden Werte zurückgegeben werden.
	 * 
	 * Other
	 * 32x32 pixels 'file icon' (PNG only)
	 * Other file icon
	 * Cover (front)
	 * Cover (back)
	 * Leaflet page
	 * Media (e.g. label side of CD)
	 * Lead artist/lead performer/soloist
	 * Artist/performer
	 * Conductor
	 * Band/Orchestra
	 * Composer
	 * Lyricist/text writer
	 * Recording Location
	 * During recording
	 * During performance
	 * Movie/video screen capture
	 * A bright coloured fish
	 * Illustration
	 * Band/artist logotype
	 * Publisher/Studio logotype
	 * @return
	 */
	public String getPictureType(){
		if(pictureTag != null){
			return pictureTag.getPictureType();
		}
		return "unknown";
	}
	
	/**
	 * Gibt die Beschreibung des PictureTypes zurück.
	 * Hier aufgeführt ein paar Beispiele die möglich sind.
	 * 
	 * image/jpeg 	
	 * image/png
	 * image/tiff
	 * @return
	 */
	public String getPictureTypeDescription(){
		if(pictureTag != null){
			return pictureTag.getPictureTypeDescription();
		}
		return "unknown";
	}
	
	/**
	 * Gibt einen Beschreibung zum Bild zurück.
	 * Dies kann der Bandname sein - Liedername etc....
	 * @return
	 */
	public String getPictureDescription(){
		if(pictureTag != null){
			return pictureTag.getPictureDescription();
		}
		return "unknown";
	}
	
	/**
	 * Gibt das hinterlegte Bild als ImageIcon zurück.
	 * @return
	 */
	public ImageIcon getCoverIcon(){
		if(pictureTag != null){
			return pictureTag.getIcon();
		}
		return null; 
	}
	
	public String toString(){
		String ret = "";
		if(streaminfoTag != null){
			ret = streaminfoTag.toString();
		}
		
		if(vorbisTag != null){
			ret = ret + vorbisTag.toString();
		}
		
		if(streaminfoTag != null){
			ret = ret + pictureTag.toString();
		}
		
		return ret;
	}

	public static void main(String[] args) {
		try{

			FlacMetaDataReader metaData = new FlacMetaDataReader(PFAD zum FLAC File);
//			System.out.println(metaData);
			String album = metaData.getVorbisTag(FlacMetaDataReader.ALBUM);
			System.out.println(album);
			
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			int sizeX = 500;
			int sizeY = 500;
			frame.setSize(sizeX, sizeY);
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((dim.width - sizeX) / 2, (dim.height - sizeY) / 2);
			frame.add(new JLabel(metaData.getCoverIcon()));
			frame.setVisible(true);
			
			
		}catch(Exception error){
			error.printStackTrace();
		}
	}

}
