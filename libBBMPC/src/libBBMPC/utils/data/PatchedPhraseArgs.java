package libBBMPC.utils.data;

public class PatchedPhraseArgs
{
	public final Sample s;
	public final double bpm;
	public final boolean loopSamples;
	public final double warp;
	
	public PatchedPhraseArgs(Sample s, double bpm, boolean loop, double warp)
	{
		this.s = s;
		this.bpm = bpm;
		this.loopSamples = loop;
		this.warp = warp;
	}
}