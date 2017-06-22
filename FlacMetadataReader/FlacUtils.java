package mediaGate.generalFrontend.flac.metadata;

import java.io.RandomAccessFile;

public class FlacUtils {

	private RandomAccessFile raf = null;
	
	public FlacUtils(RandomAccessFile accessFile){
		raf = accessFile;
	}
	
	public void readBytes(byte[] block)throws Exception{
		raf.read(block);
	}
	
	public int readBlock(int count)throws Exception{
		int byte1 = 0;
		for(int i = 0;i<count;i++){
			int value = raf.read();
			value = value < 0?(int)value+256:value;
			byte1 = byte1 +  (value << (((count-1) - i)*8)) ;
		}
		return byte1;
	}
	
	public int convertByteToInt(byte[] rByte){
		int byte1 = 0;
		for(int i = 0;i<rByte.length;i++){
			int value = rByte[i];
			value = value < 0?(int)value+256:value;
			byte1 = byte1 +  (value << (((rByte.length-1) - i)*8)) ;
		}
		return byte1;
	}
	
    public static long u(final int n){
        return n & 0xffffffffl;
    }

    public static int u(final short n){
        return n & 0xffff;
    }

    public static int u(final byte n){
        return n & 0xff;
    }
	
}
