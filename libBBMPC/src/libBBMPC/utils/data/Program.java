package libBBMPC.utils.data;
import java.io.*;

public class Program {
	private Pad[] pads;
	private int pgm_change;
	private String program_name;

	private int qPad = 0;
	private int qTuneLow = -120;
	private int qTuneHigh = 120;
	private int qFilterLow = -50;
	private int qFilterHigh = 50;
	private int qLevelLow = 0;
	private int qLevelHigh = 100;
	
	public Program copy(){
		final Program p = new Program(pads.length, pads[0].getNumZones());
		final Pad[] pads = new Pad[this.pads.length];
		for(int i=0; i<pads.length; i++){
			pads[i] = this.pads[i].copy();
		}
		p.pgm_change = pgm_change;
		p.program_name = program_name;
		p.qFilterHigh = qFilterHigh;
		p.qFilterLow = qFilterLow;
		p.qLevelHigh = qLevelHigh;
		p.qLevelLow = qLevelLow;
		p.qTuneHigh = qTuneHigh;
		p.qTuneLow = qTuneLow;
		p.qPad = qPad;
		return p;
	}
	/**
	 * @param i The pad number from 0 - NUM_PADS * NUM_BANKS
	 * @param j The zone number from 0 - NUM_ZONES
	 * @return The sample name string
	 */
	public String getSampleName(int i, int j){
		return pads[i].getSampleName(j);
	}
	public String getSampleName(int i){
		return getSampleName(i/4,i%4);
	}
	public int getLowVel(int i, int j){
		return pads[i].getLowVel(j);
	}
	public int getHighVel(int i, int j){
		return pads[i].getHighVel(j);
	}
	public int getLevel(int i,int j){
		return pads[i].getLevel(j);
	}
	public int getPan(int i,int j){
		return pads[i].getPan(j);
	}
	public double getTune(int i, int j){
		return pads[i].getTune(j);
	}
	public boolean getPlayMode(int i, int j){
		return pads[i].getPlayMode(j);
	}
	public int getAttack(int i){
		return pads[i].getAttack();
	}
	public int getDecay(int i){
		return pads[i].getDecay();
	}
	public boolean getOverlapMode(int i){
		return pads[i].getOverlapMode();
	}
	public boolean getDecayMode(int i){
		return pads[i].getDecayMode();
	}
	public int getFrequency(int i){
		return pads[i].getFreq();
	}
	public int getResonance(int i){
		return pads[i].getRes();
	}
	public int getVelToVol(int i){
		return pads[i].getVelamp();
	}
	public int getVelToFilter(int i){
		return pads[i].getVelfilt();
	}
	public int getVelToTune(int i){
		return pads[i].getVeltune();
	}
	public int getVelToStart(int i){
		return pads[i].getVelstart();
	}
	public int getProgramChange(){
		return pgm_change;
	}
	public String getName(){
		return program_name;
	}
	public int getMuteGroup(int i){
		return pads[i].getMutegroup();
	}
	public int getSendLevel(int i){
		return pads[i].getSend_level();
	}
	public int getSend(int i){
		return pads[i].getSend();
	}
	public int getMidiNote(int i){
		return pads[i].getMidi_note();
	}
	public void setTune(int i,int j, double d){
		pads[i].setTune(j,d);
	}
	public void setMidiNote(int i, int n){
		pads[i].setMidiNote(n);
	}
	public void setLowVel(int i, int j, int vel){
		pads[i].setLow_vel(j,vel);
	}
	public void setHighVel(int i, int j, int vel){
		pads[i].setHigh_vel(j,vel);
	}
	public void setSend(int i, int send){
		pads[i].setSend(send);
	}
	public void setFreq(int i, int freq){
		pads[i].setFreq(freq);
	}
	public void setRes(int i, int res){
		pads[i].setRes(res); 
	}
	public void setFilterType(int i, int x){
		if (x>Pad.HIGH_PASS)
			return;
		pads[i].setFilterType(x);
	}
	public int getFilterType(int i){
		return pads[i].getFilterType();
	}
	public void setName(String s){
		if (s.length() > 16)
			s = s.substring(0, 16);
		program_name = s;
	}
	public void setMuteGroup(int pad, int n){
		pads[pad].setMutegroup(n);
	}
	public void setAttack(int pad, int n){
		pads[pad].setAttack(n);
	}
	public void setDecay(int pad, int n){
		pads[pad].setDecay(n);
	}
	public void setVelToVol(int pad, int n){
		pads[pad].setVelAmp(n);
	}
	public void setVelToFilter(int pad, int n){
		pads[pad].setVelFilt(n);
	}
	public void setVelToTune(int pad, int n){
		pads[pad].setVelTune(n);
	}
	public void setVelToStart(int pad, int n){
		pads[pad].setVelStart(n);
	}
	public void setDecayMode(int pad, boolean b){
		pads[pad].setDecay_mode(b);
	}
	public void setOverlapMode(int pad, boolean b){
		pads[pad].setOverlapMode(b);
	}
	public void setLevel(int pad, int zone, int level){
		pads[pad].setLevel(zone,level);
	}
	public void setPan(int pad, int zone, int pan){
		pads[pad].setPan(zone, pan);
	}
	public void setPlayMode(int pad, int zone, boolean b){
		pads[pad].setPlayMode(zone, b);
	}
	public void setSampleName(int pad, int zone, String s){
		pads[pad].setSampleName(zone, s);
	}
	public void setProgramChange(int c){
		pgm_change = c;
	}
	public String toString(){
		return program_name + ".pgm";
	}
	public Program(final int num_pads, final int num_zones){
		pads = new Pad[num_pads];
		program_name = "Program01";
		pgm_change = 0;
		for (int i=0; i<num_pads; i++){
			pads[i] = new Pad(num_zones);
		}
	}
	public static Program loadProgram(final File f, final int num_pads, final int num_zones) throws IOException{
		Program p = new Program(num_pads, num_zones);
		p.program_name = f.getName().substring(0,f.getName().length()-4);
		FileInputStream in = new FileInputStream(f);
		if(!( in.read()==0x04 && in.read() == 0x2A)){
			System.out.println("Akai file version incorrect...");
		}
		in.read(); in.read();//padding
		byte[] filetype = new byte[16];
		char[] ftype = new char[16];
		in.read(filetype);
		for(int i=0; i<16; i++){
			ftype[i] = (char)filetype[i];
		}
		in.read(); in.read();//padding
		in.read(); in.read();//padding
		for (int i=0; i<num_pads; i++){
			p.pads[i].read(in, num_zones);
		}
		for (int x=0; x<num_pads; x++){
			//midi note values
			p.pads[x].setMidiNote(in.read());
		}
		for (int x=0; x<64-num_pads; x++){
			//extras for MPC500
			in.read();
		}
		for (int x=0; x<128; x++){
			in.read(); //more midi crap
		}
		p.pgm_change = in.read();
		p.qPad = in.read();
		in.read(); //pad
		int x = in.read();
		switch(x){
		case 0:
			p.qParam = QLINK_PARAM.Tune;
			break;
		case 1:
			p.qParam = QLINK_PARAM.Filter;
			break;
		case 5:
			p.qParam=QLINK_PARAM.Level;
		}
		p.qTuneLow=in.read();
		p.qTuneHigh=in.read();
		p.qFilterLow=in.read();
		p.qFilterHigh=in.read();
		//p.sliders[0].sliderlayerlow=
		in.read();
		//p.sliders[0].sliderlayerhigh=
		in.read();
		//p.sliders[0].sliderattacklow=
		in.read();
		//p.sliders[0].sliderattackhigh=
		in.read();
		//p.sliders[0].sliderdecaylow=
		in.read();
		//p.sliders[0].sliderdecayhigh=
		in.read();
		in.read();
		//SLIDER 2
		in.read(); //pad
		in.read();
//				p.qLink_param=
		in.read();
		in.read();
		in.read();
		in.read();
		//p.sliders[0].sliderlayerlow=
		in.read();
		//p.sliders[0].sliderlayerhigh=
		in.read();
		//p.sliders[0].sliderattacklow=
		in.read();
		//p.sliders[0].sliderattackhigh=
		in.read();
		//p.sliders[0].sliderdecaylow=
		in.read();
		//p.sliders[0].sliderdecayhigh=
		in.read();
		
		p.qLevelLow = in.read();
		p.qLevelHigh = in.read();
		return p;
	} 
	public void output(final OutputStream out){
		try{
			//LITTLE ENDIAN!
			//file size in bytes
			out.write(0x04);
			out.write(0x2A);
			//padding
			out.write(0x00);
			out.write(0x00);
			// file typestring "MPC1000 PGM 1.00"
			out.write(("MPC1000 PGM 1.00").getBytes());
			//padding
			out.write(0x00);
			out.write(0x00);
			out.write(0x00);
			out.write(0x00);
			
			for (int i=0; i< pads.length; i++){
				pads[i].output(out);
			}
			// write 4 dummy pads per bank to simulate 64 track mpc1000
			// (just copy first pad)
			for (int i=0; i<64-pads.length; i++){
				pads[0].output(out);
			}
			//slider data
			for (int i=0; i< pads.length; i++){
				out.write(pads[i].getMidi_note());
			}
			for (int i=0; i< 64 - pads.length; i++){
				out.write(0x00);
			}
			for (int i=0; i< 128; i++){
				// MIDI note pad values
				out.write(0x55);
			}
			out.write(pgm_change);
			out.write((byte)qPad);//slider 1 pad
			out.write(0x01);//unknown
			int x=0;
			switch(qParam){
			case Filter:
				x=1;
				break;
			case Level:
				x=5;
				break;
			case Tune:
				x=0;
				break;
			}
			out.write((byte)x);//parameter (0="Tune", 1="Filter", 2="Layer", 3="Attack", 4="Decay")
			out.write((byte)qTuneLow);//Slider 1 Tune Low
			out.write((byte)qTuneHigh);//Slider 1 Tune High
			out.write((byte)qFilterLow);//Slider 1 Filter Low
			out.write((byte)qFilterHigh);//Slider 1 Filter High
			out.write(0x00);//Slider 1 Layer Low
			out.write(0x00);//Slider 1 Layer High
			out.write(0x00);//Slider 1 Attack Low
			out.write(0x00);//Slider 1 Attack High
			out.write(0x00);//Slider 1 Decay Low
			out.write(0x00);//Slider 1 Decay High
			out.write(0x00);//slider 2 pad
			out.write(0x01);//unknown
			out.write(0x00);//parameter (0="Tune", 1="Filter", 2="Layer", 3="Attack", 4="Decay")
			out.write(0x00);//Slider 2 Tune Low
			out.write(0x00);//Slider 2 Tune High
			out.write(0x00);//Slider 2 Filter Low
			out.write(0x00);//Slider 2 Filter High
			out.write(0x00);//Slider 2 Layer Low
			out.write(0x00);//Slider 2 Layer High
			out.write(0x00);//Slider 2 Attack Low
			out.write(0x00);//Slider 2 Attack High
			out.write(0x00);//Slider 2 Decay Low
			out.write(0x00);//Slider 2 Decay High
				
			out.write((byte)qLevelLow);
			out.write((byte)qLevelHigh);
			
			for (int i=0; i<15; i++)
				out.write(0x00); //padding;
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public enum QLINK_PARAM{
		Level, Tune, Filter;
	}
	private QLINK_PARAM qParam= QLINK_PARAM.Level;
	public QLINK_PARAM getQParam() {
		return qParam;
	}
	public void setQParam(QLINK_PARAM link_param) {
		qParam = link_param;
	}
	public int getQTuneLow() {
		return qTuneLow;
	}
	public void setQTuneLow(int tuneLow) {
		qTuneLow = tuneLow;
	}
	public int getQTuneHigh() {
		return qTuneHigh;
	}
	public void setQTuneHigh(int tuneHigh) {
		qTuneHigh = tuneHigh;
	}
	public int getQFilterLow() {
		return qFilterLow;
	}
	public void setQFilterLow(int filterLow) {
		qFilterLow = filterLow;
	}
	public int getQFilterHigh() {
		return qFilterHigh;
	}
	public void setQFilterHigh(int filterHigh) {
		qFilterHigh = filterHigh;
	}
	public int getQLevelLow() {
		return qLevelLow;
	}
	public void setQLevelLow(int levelLow) {
		qLevelLow = levelLow;
	}
	public int getQLevelHigh() {
		return qLevelHigh;
	}
	public void setQLevelHigh(int levelHigh) {
		qLevelHigh = levelHigh;
	}
	public int getQPad() {
		return qPad;
	}
	public void setQPad(int pad) {
		qPad = pad;
	}
	/**
	 * Returns the index of a pad which has no samples assigned to it
	 * @return
	 */
	public int getEmptyPad(){
		for (int i=0; i<pads.length; i++){
			boolean flag = true;
			for (int j=0; j<pads[i].getNumZones(); j++){
				String s = getSampleName(i, j);
				if (!s.equals("")) flag = false;
			}
			if(flag) return i;
		}
		return -1;
	}
	public int getNumPads(){
		return pads.length;
	}
	public int getVelToAttack(int rowIndex) {
		return pads[rowIndex].getVelattack();
	}
	public void setVelToAttack(int rowIndex, int value) {
		pads[rowIndex].setVelAttack(value);
	}
	public void setSendLevel(int rowIndex, int value) {
		pads[rowIndex].setSend_level(value);
	}
	public Zone getZone(int pad, int zone) {
		return pads[pad].getZone(zone);
	}
	public void setSampleName(int currentPad, String newSample) {
		setSampleName(currentPad/4, currentPad%4, newSample);
	}
}
