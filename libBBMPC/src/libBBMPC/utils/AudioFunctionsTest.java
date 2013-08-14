package libBBMPC.utils;
/*
import java.security.InvalidParameterException;

import junit.framework.TestCase;

import org.junit.Test;

public class AudioFunctionsTest extends TestCase{
	@Test
	public void testDBtoLinear() {
		if (AudioFunctions.dBtoLinear(3)<1.95 || AudioFunctions.dBtoLinear(3) >2)
			fail("DB to Linear Function Broken");
	}
	
	@Test
	public void testLinearToDB() {
		double x = AudioFunctions.linearToDB(2);
		if (x>3.03 || x<3)
			fail("Linear To DB Function broken! Gain of 2="+x);
	}
	@Test
	public void testLinearToDBandDBToLinear(){
		double x = AudioFunctions.linearToDB(AudioFunctions.dBtoLinear(4));
		if (x < 3.99 || x > 4.01){
				fail("dB and linear Functions are not inverse: x=" + x);
			}
		x = AudioFunctions.dBtoLinear(AudioFunctions.linearToDB(15));
		if ( x< 14.9 || x > 15.1){
				fail("dB and linear Functions are not inverse: x=" + x);
			}
	}
	@Test
	public void testBytesToFloat() {
		try{
			byte[] b0 = {};
			AudioFunctions.bytesToFloat(b0);
			fail("Exception not thrown on 0 byte conversion!");
		}catch(UnsupportedOperationException ex){}
		
		float x;
		//8 bit
		byte[] b8 = {53};
		x = AudioFunctions.bytesToFloat(b8);
		if ( x < 0.20 || x > 0.22 ) 
			fail("8 bit bytes to float failed");
		// 16 bit
		byte[] b16 = {0x0C, 0x02}; // 524d little endian
		x = AudioFunctions.bytesToFloat(b16);
		if (x < 0.0159 || x >0.0161)
			fail("16 bit bytes to float failed");
		// 24 bit
		byte[] b24 = {0x24, 0x6D, 0x03}; // 224548d little endian
		x = AudioFunctions.bytesToFloat(b24); //0.026768213932088358900155758894863
		if (x<0.025 || x > 0.029)
			fail("24 bit bytes to float failed");
		try{
			byte[] b32 = {0x00,0x00,0x00,0x00};
			AudioFunctions.bytesToFloat(b32);
			fail("Exception not thrown on 4 byte conversion!");
		}catch(UnsupportedOperationException ex){}
	}

	@Test
	public void testFloatToBytes() {
		final float x = 0.13f;
		try{
			AudioFunctions.floatToBytes(-2.1f, new byte[2]);
		}catch(InvalidParameterException ex){}
		try{
			AudioFunctions.floatToBytes(2.1f, new byte[2]);
		}catch(InvalidParameterException ex){}
		
		// test 8 bit value
		byte[] b8 = {0x00};
		AudioFunctions.floatToBytes(x, b8);
		if(b8[0]!=33)
			fail("8 bit float to bytes incorrect");
		// test 16 bit value
		byte[] b16 = {0x00,0x00};
		AudioFunctions.floatToBytes(x, b16);
		if (b16[0]!=(byte)0xA3 || b16[1]!=0x10) // 10A3
			fail("16 bit float to bytes incorrect: "+b16[0]+ " "+ b16[1]);
		// test 24 bit value
		byte[] b24 = {0x00,0x00, 0x00};
		AudioFunctions.floatToBytes(x, b24);
		if (b24[0]!=(byte)0xD6 || b24[1] !=(byte)0xA3 || b24[2]!=0x10)
			fail("24 bit float to bytes incorrect: "+b24[0] + b24[1] + b24[2]);
	}
}*/
