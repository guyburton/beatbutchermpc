package libBBMPC.utils.data;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Pad{
	public final static boolean START = true;
	public final static boolean END = false;
	public final static boolean POLY = true;
	public final static boolean MONO = false;
	public static final int MIDI_HIGH_NOTE = 96;
	public static final int MIDI_LOW_NOTE = 36;
	public static int LOW_PASS = 0;
	public static int BAND_PASS = 1;
	public static int HIGH_PASS = 2;
	public static int NONE = 0;
	public static int FX1 = 1;
	public static int FX2 = 2;
	
	private int attack;
	private int decay;
	private boolean overlap;  
	private boolean decay_mode; 
	private int mutegroup;
	private int midi_note;
	private int send; 
	private int send_level; 
	private int freq;
	private int res;
	private int filter_type;
	private int velfilt;
	private int velamp;
	private int veltune;
	private int velattack;
	private int velstart;
	private Zone[] zones;
	
	public Pad copy(){
		final Pad p = new Pad(zones.length);
		for (int i=0; i<zones.length; i++){
			p.zones[i] = zones[i].copy();
		}
		p.setAttack(attack);
		p.setDecay(decay);
		p.setOverlapMode(overlap);
		p.setDecay_mode(decay_mode);
		p.setMutegroup(mutegroup);
		p.setMidiNote(midi_note);
		p.setSend(send);
		p.setSend_level(send_level);
		p.setFreq(freq);
		p.setRes(res);
		p.setFilterType(filter_type);
		p.setVelFilt(velfilt);
		p.setVelAttack(velattack);
		p.setVelAmp(velamp);
		p.setVelStart(velstart);
		p.setVelTune(veltune);
		
		return p;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		if (attack>=0 && attack<=100)
			this.attack = attack;
	}
	public int getDecay() {
		return decay;
	}
	public void setDecay(int decay) {
		if (decay>=0 && decay<=100)
			this.decay = decay;
	}
	public boolean getDecayMode() {
		return decay_mode;
	}
	public void setDecay_mode(boolean decay_mode) {
		this.decay_mode = decay_mode;
	}
	public int getMutegroup() {
		return mutegroup;
	}
	public void setMutegroup(int mutegroup) {
		this.mutegroup = mutegroup;
	}

	public int getMidi_note() {
		return midi_note;
	}

	public void setMidiNote(int midi_note) {
		if (midi_note<= Pad.MIDI_HIGH_NOTE && midi_note >=Pad.MIDI_LOW_NOTE)
			this.midi_note = midi_note;
	}
	public int getSend() {
		return send;
	}
	public void setSend(int send) {
		if (send==Pad.NONE || send == Pad.FX1 || send == Pad.FX2)
			this.send = send;
		else
			throw new RuntimeException("Invalid parameter value!");
	}
	public int getSend_level() {
		return send_level;
	}
	public void setSend_level(int send_level) {
		if (send_level >=0 && send_level <100)
			this.send_level = send_level;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public int getRes() {
		return res;
	}
	public void setRes(int res) {
		this.res = res;
	}
	public int getVelfilt() {
		return velfilt;
	}
	public void setVelFilt(int velfilt) {
		this.velfilt = velfilt;
	}
	public int getVelamp() {
		return velamp;
	}
	public void setVelAmp(int velamp) {
		this.velamp = velamp;
	}
	public int getVeltune() {
		return veltune;
	}
	public void setVelTune(int veltune) {
		this.veltune = veltune;
	}
	public int getVelattack() {
		return velattack;
	}
	public void setVelAttack(int velattack) {
		this.velattack = velattack;
	}
	public int getVelstart() {
		return velstart;
	}
	public void setVelStart(int velstart) {
		this.velstart = velstart;
	}
	public Pad(final int num_zones){
		zones = new Zone[num_zones];
		for(int i=0; i<num_zones; i++){
			zones[i] = new Zone();
		}
		decay = 5;
		mutegroup = 0;
		midi_note = Pad.MIDI_LOW_NOTE;
		send_level = 0;
		freq = 100;
		res = 0;
		velamp = 100;
	}
	
	public void output(OutputStream out) throws IOException{
		for (int k=0; k<zones.length; k++){
			zones[k].output(out);
		}
		//padding
		out.write(0x00);
		out.write(0x00);
		//overlap
		out.write(overlap?1:0);
		//mute group
		out.write((byte)mutegroup);
		//padding
		out.write(0x00);
		//Unknown
		out.write(0x01);
		//Attack
		out.write((byte)attack);
		//Decay
		out.write((byte)decay);
		out.write(decay_mode?0:1);
		//padding
		out.write(0x00);
		out.write(0x00);
		//vel to level
		out.write((byte)velamp);
		//padding
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		//filter1 type
		out.write((byte)filter_type);
		out.write((byte)freq);
		out.write((byte)res);
		//padding
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		//vel to freq
		out.write((byte)velfilt);
		//filter 2 (not on mpc500)
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		//padding
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write((byte)100);
		out.write((byte)50);
		out.write(0);
		//fx send
		out.write(send);
		out.write((byte)send_level);
		//padding
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
		out.write(0x00);
	}
	public String getSampleName(int j) {
		return zones[j].getSampleName();
	}
	public int getLowVel(int j) {
		return zones[j].getLowVel();
	}
	public int getHighVel(int j) {
		return zones[j].getHighVel();
	}
	public void setTune(int j, double d) {
		zones[j].setTune(d);		
	}
	public void setLow_vel(int j, int vel) {
		zones[j].setLowVel(vel);
	}
	public void setHigh_vel(int j, int vel) {
		zones[j].setHighVel(vel);
	}
	public void setFilterType(int x) {
		filter_type = x;
	}
	public int getFilterType() {
		return filter_type;
	}
	public int getLevel(int j) {
		return zones[j].getLevel();
	}
	public int getPan(int j) {
		return zones[j].getPan();
	}
	public double getTune(int j) {
		return zones[j].getTune();
	}
	public void setSampleName(int j, String trim) {
		zones[j].setSampleName(trim);
	}
	public void setLevel(int j, int read) {
		zones[j].setLevel(read);
	}
	public boolean read(final InputStream in, final int num_zones) throws IOException{
		for (int j=0; j<num_zones; j++){
			zones[j].read(in);
		}
		in.read();in.read();
		overlap = in.read()==0?true:false;
		mutegroup = in.read();
		in.read();
		if (in.read()==0x01) return false;
		attack = in.read();
		decay = in.read();
		decay_mode = in.read()==0?true:false;
		in.read();in.read();
		velamp = in.read();
		for (int x=0; x<5; x++)
			in.read();
		filter_type = in.read();//filtertype
		freq = in.read();
		res = in.read();
		for (int x=0; x<4; x++)
			in.read();
		velfilt = in.read();
		for (int x=0; x<25; x++)
			in.read();//filter 2
		send = in.read();
		send_level = in.read();
		for (int x=0; x<16; x++)
			in.read(); // attentuation + padding
		return true;
	}
	public int getNumZones() {
		return zones.length;
	}
	public boolean getOverlapMode() {
		return overlap;
	}
	public boolean getPlayMode(int j) {
		return zones[j].getPlayMode();
	}
	public void setOverlapMode(boolean b) {
		overlap = b;
	}
	public void setPan(int zone, int pan) {
		zones[zone].setPan(pan);
	}
	public void setPlayMode(int zone, boolean b) {
		zones[zone].setPlayMode(b);
	}
	public Zone getZone(int zone) {
		return zones[zone];
	}
	public void setZone(int zone, Zone z) {
		zones[zone]=z;
	}
}