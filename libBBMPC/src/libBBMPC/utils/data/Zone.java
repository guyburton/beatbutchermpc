package libBBMPC.utils.data;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Zone{
	public static boolean ONE_SHOT = true;
	public static boolean NOTE_ON = false;
	private int level;
	private int pan;
	private int low_vel;
	private int high_vel;
	private String sample_name = "";
	private double tune;
	private boolean play_mode;
	public Zone copy(){
		Zone z = new Zone();
		z.setLevel(level);
		z.setPan(pan);
		z.setSampleName(sample_name);
		z.setHighVel(high_vel);
		z.setLowVel(low_vel);
		z.setPlayMode(play_mode);
		z.setTune(tune);
		return z;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		if (level <=127 && level >=0)
			this.level = level;
	}
	public int getPan() {
		return pan;
	}
	public void setPan(int pan) {
		if (pan <=127 && pan >=0)
			this.pan = pan;
	}
	public int getLowVel() {
		return low_vel;
	}
	public void setLowVel(int low_vel) {
		this.low_vel = low_vel;
	}
	public int getHighVel() {
		return high_vel;
	}
	public void setHighVel(int high_vel) {
		if (high_vel <=127 && high_vel >=0)
			this.high_vel = high_vel;
	}
	public String getSampleName() {
		return sample_name;
	}
	public void setSampleName(String sample_name) {
		this.sample_name = sample_name;
	}
	public double getTune() {
		return tune;
	}
	public void setTune(double tune) {
		if (tune <=24 && tune >= -24)
			this.tune = tune;
	}
	// true is one shot, false is note on
	public Zone(){
		level = 70;
		pan = 50;
		high_vel = 127;
		tune = 0.0;
	}
	public void output(OutputStream out) throws IOException{
		// sample name
		int l = sample_name.length();
		if ( l < 16){
			out.write(sample_name.getBytes());
			for (int i=0; i<16-l; i++)
				out.write(0x00);
		}else {
			out.write(sample_name.substring(0,16).getBytes());
		}
		//padding
		out.write(0x00);
		//level
		out.write((byte)level);
		//range lower
		out.write((byte)low_vel);
		//range upper
		out.write((byte)high_vel);
		//tuning (between -3600 and 3600
		// we store between -36 and 36
		int i = (int)(tune*100);
		byte a = (byte)(i & 0x00FF);
		byte b = (byte)((i & 0xFF00)>>8);
		out.write(a);
		out.write(b);
		// play mode
		out.write(play_mode?1:0);
		//padding
		out.write(0x00);
	}
	public void read(InputStream in) throws IOException {
		byte[] samplename = new byte[16];
		in.read(samplename);
		sample_name = new String(samplename).trim();
		in.read();
		level =  in.read();
		low_vel =  in.read();
		high_vel = in.read();
		tune = (in.read() | (in.read()<<8))/100;
		play_mode = in.read()==0?true:false;
		in.read();
	}
	public boolean getPlayMode() {
		return play_mode;
	}
	public void setPlayMode(boolean b) {
		play_mode=b;
	}
}