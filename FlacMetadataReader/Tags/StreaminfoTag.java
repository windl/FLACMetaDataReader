package mediaGate.generalFrontend.flac.metadata.Tags;

import mediaGate.generalFrontend.flac.metadata.FlacUtils;

public class StreaminfoTag {

	/**
	 * <16> 	The minimum block size (in samples) used in the stream.
	 * <16> 	The maximum block size (in samples) used in the stream. (Minimum blocksize == maximum blocksize) implies a fixed-blocksize stream.
	 * <24> 	The minimum frame size (in bytes) used in the stream. May be 0 to imply the value is not known.
	 * <24> 	The maximum frame size (in bytes) used in the stream. May be 0 to imply the value is not known.
	 * <20> 	Sample rate in Hz. Though 20 bits are available, the maximum sample rate is limited by the structure of frame headers to 655350Hz. Also, a value of 0 is invalid.
	 * <3> 		(number of channels)-1. FLAC supports from 1 to 8 channels
	 * <5> 		(bits per sample)-1. FLAC supports from 4 to 32 bits per sample. Currently the reference encoder and decoders only support up to 24 bits per sample.
	 * <36> 	Total samples in stream. 'Samples' means inter-channel sample, i.e. one second of 44.1Khz audio will have 44100 samples regardless of the number of channels. A value of zero here means the number of total samples is unknown.
	 * <128> 	MD5 signature of the unencoded audio data. This allows the decoder to determine if an error exists in the audio data even when the error does not result in an invalid bitstream. 
	 * @throws Exception
	 */
	
	/** hält die komplette Headersize*/
	private int headerSize = 0;
	
	/** hält die minimum Block Size*/
	private int minimumBlockSize = 0;
	
	/**hält die maximale Block Size*/
	private int maximumBlockSize = 0;
	
	/** hält die minimale Frame Size*/
	private int minimumFrameSize = 0;
	
	/** hält die maximale Frame Size*/
	private int maximaleFrameSize = 0;
	
	/** hält die KHZ*/
	private int _khz = 0;
	
	/** hält die Anzahl an Channels*/
	private int _channels = 0;
	
	/** hält die Bits per Sample*/
	private int bitsPerSample = 0;
	
	/** hält die Anzahl der Samples*/
	private int numberOfSamples = 0;
	
	/** hält die Sekunden der Spiellänge des Liedes*/
	private int timeToPlaySec = 0;
	
	public StreaminfoTag(FlacUtils flacReader)throws Exception{
		headerSize = flacReader.readBlock(3);
		//Einlesen des gesamten Blocks
		byte[] block = new byte[headerSize];
		flacReader.readBytes(block);
		
		//<16> 	The minimum block size (in samples) used in the stream.
		byte[] minimumBlock = {block[0],block[1]};
		minimumBlockSize = flacReader.convertByteToInt(minimumBlock);
		
		//<16> 	The maximum block size (in samples) used in the stream.
		byte[] maximumBlock = {block[2],block[3]};
		maximumBlockSize = flacReader.convertByteToInt(maximumBlock);
		
		//<24> 	The minimum frame size (in bytes) used in the stream.
		byte[] minimumFrame = {block[4],block[5],block[6]};
		minimumFrameSize = flacReader.convertByteToInt(minimumFrame);
		
		//<24> 	The maximum frame size (in bytes) used in the stream
		byte[] maximumFrame = {block[7],block[8],block[9]};
		maximaleFrameSize = flacReader.convertByteToInt(maximumFrame);
		
		//<20> 	Sample rate in Hz.
		byte[] khz = {block[10],block[11],block[12]};
		_khz = flacReader.convertByteToInt(khz);
		_khz = _khz >>4;
		
		//<3> 	(number of channels)-1. FLAC supports from 1 to 8 channels
		int blockVal = block[12]<0?block[12]+256:block[12];
		blockVal = blockVal >>1;
		int byte0 = 	(blockVal & (int)Math.pow(2.0, 0))>0?2:0;
		int byte1 = 	(blockVal & (int)Math.pow(2.0, 1))>0?4:0;
		int byte2 =  	(blockVal & (int)Math.pow(2.0, 2))>0?8:0;
		_channels = byte0 + byte1 + byte2;
		bitsPerSample = ((FlacUtils.u(block[12]) & 0x01) << 4)+ ((FlacUtils.u(block[13]) & 0xF0) >>> 4) + 1;
		
		//Eingebettet in der zweiten Hälfte von byte 13 plus bytes 14 - 17
		numberOfSamples = FlacUtils.u(block[17]);
		numberOfSamples += FlacUtils.u(block[16]) << 8;
		numberOfSamples += FlacUtils.u(block[15]) << 16;
		numberOfSamples += FlacUtils.u(block[14]) << 24;
		numberOfSamples += (FlacUtils.u(block[13]) & 0x0F) << 32;
		
		//Ermitteln der Spiellänge
		timeToPlaySec = (int)(numberOfSamples / _khz);
		
	}
	
	
	public int getTimeToPlaySec() {
		return timeToPlaySec;
	}

	public int getBitsPerSample() {
		return bitsPerSample;
	}

	public int getNumberOfSamples() {
		return numberOfSamples;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public int getMinimumBlockSize() {
		return minimumBlockSize;
	}

	public int getMaximumBlockSize() {
		return maximumBlockSize;
	}

	public int getMinimumFrameSize() {
		return minimumFrameSize;
	}

	public int getMaximaleFrameSize() {
		return maximaleFrameSize;
	}

	public int get_khz() {
		return _khz;
	}

	public int get_channels() {
		return _channels;
		
	}
	
	public String toString(){
		
		String ret = "STREAMINFOTAG\r\n-----------------------------\r\n"+  
				"Headersize : " + headerSize + " Byte\r\n" +
				"minimum Blocksize : " + minimumBlockSize + "\r\n" + 
				"maximale Blocksize : " + maximumBlockSize + "\r\n" + 
				"minimum Framesize : " + minimumFrameSize + "\r\n" + 
				"maximale Framesize : " + maximaleFrameSize + "\r\n" + 
				"Khz : " + _khz + "\r\n" + 
				"Channels : " + _channels + "\r\n" + 
				"Bits per Sample : " + bitsPerSample+"\r\n" + 
				"Anzahl der Samples : " + numberOfSamples + "\r\n" + 
				"Sekunden : " + timeToPlaySec+"\r\n";
		return ret;
		
	}

}
