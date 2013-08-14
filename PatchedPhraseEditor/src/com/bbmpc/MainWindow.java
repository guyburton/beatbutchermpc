package com.bbmpc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import libBBMPC.images.Images;
import libBBMPC.mvc.FunctionButton;
import libBBMPC.mvc.FunctionManager;
import libBBMPC.mvc.FunctionMenuItem;
import libBBMPC.utils.FileManager;
import libBBMPC.utils.MIDISequencer;
import libBBMPC.utils.SamplePlayer;
import libBBMPC.utils.data.PatchedPhrase;
import libBBMPC.utils.data.PatchedPhraseArgs;
import libBBMPC.utils.data.Sample;
import libBBMPC.utils.gui.DockLayout;
import libBBMPC.utils.gui.WaveMarkerEditPanel;
import libBBMPC.utils.gui.WaveMarkerEditPanel.MODE;

public class MainWindow extends JFrame{
	private static final long serialVersionUID = -3880026026104218593L;

	public final JToolBar toolbar = new JToolBar();
	public final JToggleButton btnAdd;
	public final JToggleButton btnDelete;
	public final JToggleButton btnMove;
	public final JButton btnLoad;
	public final JButton btnSave;
	public final JButton btnZoomIn, btnZoomOut;
	
	public final JMenuBar menu = new JMenuBar();
	public final JMenu file = new JMenu("File");
	public final JMenuItem load, savePhrase, saveAll, close, exit;
	public final JMenu edit = new JMenu("Edit");
	public final JMenuItem divide, normalize, reverse, reverseAll, delete, clear, calculate;
	public final JMenu help = new JMenu("Help");
	public final JMenuItem about;
	
	public final JButton btnForward, btnBackward, btnPlay, btnStop, btnPlayAll;

	public final JToggleButton btnRepeatAll;
	public final JTextField txtTempo;
	public final JSlider slider;
	
	public final DockLayout dock = new DockLayout();
	public final WaveMarkerEditPanel wavepanel;
	
	private FunctionManager fm;
	private SamplePlayer samplePlayer;
	private FileFunctionsProvider filefunctions;
	private AudioFunctionsProvider audiofunctions;
	private OtherFunctionsProvider otherfunctions;
	
	static final String APP_NAME = "BeatButcherMPC Patched Phrase Editor";
	
	public MainWindow()
	{
		Image t = Images.turntable.getImage();
		
		fm = new FunctionManager(t){
			public void update(){
				MainWindow.this.update();
			}
		};
		samplePlayer = new SamplePlayer();
		
		wavepanel = new WaveMarkerEditPanel(fm, samplePlayer);
		wavepanel.setBackground(Color.lightGray);
		wavepanel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0)
				{
					int onmask = InputEvent.CTRL_DOWN_MASK;
				    if ((e.getModifiersEx() & (onmask)) == onmask) {
				        fm.execute("Zoom In Vertical", null);
				    }
				    else
				    {
				    	fm.execute("Zoom In Horizontal", null);
				    }
				}
				else
				{
					int onmask = InputEvent.CTRL_DOWN_MASK;
				    if ((e.getModifiersEx() & (onmask)) == onmask) {
				        fm.execute("Zoom Out Vertical", null);
				    }
				    else
				    {
				    	fm.execute("Zoom Out Horizontal", null);
				    }
				}
			}
		});

		audiofunctions = new AudioFunctionsProvider(wavepanel, samplePlayer);
		otherfunctions = new OtherFunctionsProvider();
		filefunctions = new FileFunctionsProvider();
		filefunctions.AddSampleListener(new FileFunctionsProvider.SampleListener() {
			@Override
			public void SampleLoaded(Sample s, double tempo) {
				wavepanel.setSample(s);
				samplePlayer.setSample(s);
				saveAll.setEnabled(true);
				savePhrase.setEnabled(true);
				close.setEnabled(true);
				edit.setEnabled(true);
			}
			
			@Override
			public void SampleClosed() {
				fm.execute("Stop", null);
				wavepanel.setSample(null);
				samplePlayer.setSample(null);
				saveAll.setEnabled(false);
				savePhrase.setEnabled(false);
				close.setEnabled(false);
				edit.setEnabled(false);
			}
		});
		
		fm.addFunctionProvider(wavepanel);
		fm.addFunctionProvider(filefunctions);
		fm.addFunctionProvider(audiofunctions);
		fm.addFunctionProvider(otherfunctions);
		
		this.setSize(750,350);
		this.setTitle(APP_NAME);
		this.setIconImage(t);
		this.setLayout(dock);
		this.setJMenuBar(menu);

		this.add(toolbar, DockLayout.NORTH);
		this.add(wavepanel, DockLayout.CENTER);
		
		// set up menu
		menu.add(file);
		menu.add(edit);
		menu.add(help);
		
		load = new FunctionMenuItem(fm, "Load Sample");
		saveAll = new FunctionMenuItem(fm, "Save All Slices");
		savePhrase = new JMenuItem("Save Patched Phrase");
		savePhrase.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					double tempo = Double.parseDouble(txtTempo.getText());
					fm.execute("Save Patched Phrase", tempo);
				}
				catch(Exception ex)
				{
				}
			}
		});
		close = new FunctionMenuItem(fm, "Close");
		exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (wavepanel.getSample() == null)
					System.exit(0);
				
				switch(JOptionPane.showConfirmDialog(
						null, 
						"Would you like to save your work before you quit?", 
						MainWindow.APP_NAME, 
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE)){
				case JOptionPane.YES_OPTION:					
					File f = FileManager.chooseSave();
					if (f==null) return;
					try
					{
						double tempo = Double.parseDouble(txtTempo.getText());
						PatchedPhrase p = new PatchedPhrase(wavepanel.getSample(), tempo);
						p.save( new FileOutputStream(f) );
					}
					catch (FileNotFoundException e) {
						JOptionPane.showMessageDialog(null, "There was an error saving the file");
						e.printStackTrace();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "There was an error saving the file");
						e.printStackTrace();
					}
					catch(Exception ex)
					{
					} 
					System.exit(0);
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
				case JOptionPane.NO_OPTION:
					System.exit(0);
					break;
				}
			}
		});
		
		file.add(load);
		file.add(savePhrase);
		file.add(saveAll);
		file.add(close);
		file.addSeparator();
		file.add(exit);		
		
		divide = new JMenuItem("Divide Sample");
		divide.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JDialog d = new JDialog(MainWindow.this);
				final JSpinner slices = new JSpinner();
				final JSpinner swing = new JSpinner();
				slices.setModel(new SpinnerNumberModel(16,2,128,1));
				swing.setModel(new SpinnerNumberModel(50,50,75,1));
				add (new JLabel("% "));
				d.setModalityType(ModalityType.APPLICATION_MODAL);
				
				final JButton ok = new JButton("OK");
				final JButton cancel = new JButton("Cancel");
				
				ok.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						OtherFunctionsProvider.DivideArgs dd = otherfunctions.new DivideArgs(wavepanel.getSample(),(Integer)slices.getValue(), (Integer)swing.getValue());
						fm.execute("Divide Sample", dd);
						d.setVisible(false);
						d.dispose();
						wavepanel.repaint();
					}
				});
				
				cancel.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						d.setVisible(false);
						d.dispose();
					}
				});
				
				final JPanel root = new JPanel(new BorderLayout());
				d.add(root);
				root.add(new JLabel("Auto Divide Settings:"), BorderLayout.NORTH);
				
				final JPanel main = new JPanel();
				main.add (new JLabel(" Slices:"));
				main.add(slices);
				main.add (new JLabel(" Swing:"));
				main.add(swing);
				root.add(main, BorderLayout.CENTER);
				
				final JPanel buttons = new JPanel(new GridLayout(1,2));
				buttons.add(ok);
				buttons.add(cancel);
				root.add(buttons, BorderLayout.SOUTH);
				
				d.setSize(300, 120);	
				d.setTitle(APP_NAME);
				d.setVisible(true);
			
			}
		});
		reverse = new FunctionMenuItem(fm, "Reverse Slice");
		reverseAll = new FunctionMenuItem(fm, "Reverse All Slices");
		delete = new FunctionMenuItem(fm, "Delete Slice");
		clear = new JMenuItem("Clear Markers");
		clear.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fm.execute("Clear Markers", wavepanel.getSample());
				wavepanel.repaint();
			}
		});
		normalize = new FunctionMenuItem(fm, "Normalize");
		calculate = new JMenuItem("Calculate Tempo");
		calculate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String result = JOptionPane.showInputDialog(MainWindow.this, "How many (4 beat) bars is the clip in length?", APP_NAME, JOptionPane.QUESTION_MESSAGE);
				try
				{
					double bars = Double.parseDouble(result);
					if (bars < 1 || bars > 100)
						throw new RuntimeException();
					
					AudioFormat format = wavepanel.getSample().getFormat();
					double seconds = wavepanel.getSample().getSamples() / format.getFrameRate();
				//	double old_bpm = DoutxtTempo.getTempo();
					double new_bpm = (bars * 4) * 60 / seconds;
					if (new_bpm<40) new_bpm = 40;
					if (new_bpm>250) new_bpm = 250;	
					DecimalFormat df = new DecimalFormat("#.##");
					txtTempo.setText(df.format(new_bpm));
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(MainWindow.this, "Not a valid number", APP_NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		});		
		
		edit.add(calculate);
		edit.add(divide);
		edit.add(clear);
		edit.addSeparator();
		edit.add(reverse);
		edit.add(reverseAll);		
		edit.add(delete);
		edit.add(normalize);
		
		about = new FunctionMenuItem(fm, "About");
		help.add(about);
		
		// menu behaviour
		saveAll.setEnabled(false);
		savePhrase.setEnabled(false);
		close.setEnabled(false);
		edit.setEnabled(false);
		
		// set up toolbar
		btnLoad = new FunctionButton(fm, "Load Sample", Images.load);
		btnSave = new JButton(Images.save);
		btnSave.setToolTipText("Save Patched Phrase");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					double tempo = Double.parseDouble(txtTempo.getText());
					fm.execute("Save Patched Phrase", tempo);
				}
				catch(Exception ex)
				{
				}
			}
		});
		btnForward = new FunctionButton(fm, "Next Slice", Images.forward);
		btnBackward = new FunctionButton(fm, "Previous Slice", Images.back);
		btnPlay = new FunctionButton(fm, "Play Current Slice", Images.go);
		btnStop = new FunctionButton(fm, "Stop", Images.stop);
		btnPlayAll = new JButton(Images.video);
		btnRepeatAll = new JToggleButton(Images.refresh);
		txtTempo = new JTextField(5);
		slider = new JSlider();
		
		btnZoomIn = new FunctionButton(fm, "Zoom In Horizontal", Images.zoom_in);
		btnZoomOut = new FunctionButton(fm, "Zoom Out Horizontal", Images.zoom_out);
		
		btnAdd = new JToggleButton(Images.add);
		btnMove = new JToggleButton(Images.move);
		btnDelete = new JToggleButton(Images.delete);
		InitToolbar();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void update()
	{
		//wavepanel.repaint();
	}
	
	private void InitToolbar()
	{
		ActionListener chk = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (!btnAdd.isSelected() && !btnMove.isSelected() && !btnDelete.isSelected()){
					fm.execute("Select Marker Mode", null);
				}
			}
		};
		btnAdd.setToolTipText("Add Marker Mode");
		btnAdd.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (btnAdd.isSelected())
					fm.execute("Add Marker Mode", null);
			}
		});
		btnAdd.addActionListener(chk);
		
		btnDelete.setToolTipText("Delete Marker Mode");
		btnDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (btnDelete.isSelected())
					fm.execute("Delete Marker Mode", null);
			}
		});
		btnDelete.addActionListener(chk);
		
		btnMove.setToolTipText("Move Marker Mode");
		btnMove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (btnMove.isSelected())
					fm.execute("Move Marker Mode", null);
			}
		});
		btnMove.addActionListener(chk);
		
		wavepanel.addModeChangeListener(new WaveMarkerEditPanel.ModeChangeListener()
		{
			public void WavePanelModeChanged(MODE oldMode, MODE newMode) {
				switch (newMode)
				{
				case ADD:
					btnAdd.setSelected(true);
					btnMove.setSelected(false);
					btnDelete.setSelected(false);
					break;
				case DELETE:
					btnAdd.setSelected(true);
					btnMove.setSelected(false);
					btnDelete.setSelected(true);
					break;
				case MOVE:
					btnAdd.setSelected(false);
					btnMove.setSelected(true);
					btnDelete.setSelected(false);
					break;
				case SELECT:
					btnAdd.setSelected(false);
					btnMove.setSelected(false);
					btnDelete.setSelected(false);
					break;
				}
			}		
		});
		
		btnPlayAll.setToolTipText("Play Patched Phrase");
		btnRepeatAll.setToolTipText("Repeat All Slices");
		
		toolbar.add(btnLoad);
		toolbar.add(btnSave);
		
		toolbar.addSeparator();
		
		toolbar.add(btnAdd);
		toolbar.add(btnMove);
		toolbar.add(btnDelete);
		
		toolbar.addSeparator();
		
		toolbar.add(btnPlay);
		toolbar.add(btnStop);
		toolbar.add(btnBackward);
		toolbar.add(btnForward);
		
		toolbar.addSeparator();
		
		toolbar.add(btnZoomIn);
		toolbar.add(btnZoomOut);
		
		toolbar.addSeparator();
		
		toolbar.add(btnPlayAll);
		toolbar.add(btnRepeatAll);

		toolbar.add(txtTempo);
		txtTempo.setToolTipText("Base tempo of track");
		txtTempo.setMaximumSize(new Dimension(50,20));
		txtTempo.setText("100.00");
		toolbar.add(new JLabel(" bpm "));
		toolbar.addSeparator();
		toolbar.add(new JLabel(" Warp:"));
		toolbar.add(slider);
		slider.setToolTipText("Factor to warp playback speed");
		
		slider.setMinimum(1);
		slider.setMaximum(40);
		
		btnPlayAll.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (wavepanel.getSample() == null)
					return;
				try
				{
					double tempo = Double.parseDouble(txtTempo.getText());
					double warp = (slider.getValue()/10.0d);
					boolean loop = btnRepeatAll.isSelected();
					fm.execute("Play Patched Phrase", new PatchedPhraseArgs(wavepanel.getSample(), tempo, loop, warp));
				}
				catch(Exception e)
				{
				}
			}
		});
		txtTempo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try
				{
					double d = Double.parseDouble(txtTempo.getText());
					if (d < 40)
						txtTempo.setText("40");
					if (d > 250)
						txtTempo.setText("250");
					return;
				}
				catch(Exception ez)
				{
					
				}
				txtTempo.setText("100");
			}			
		});
		btnRepeatAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MIDISequencer.SetRepeatAll(btnRepeatAll.isSelected());
			}
		});
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				  JSlider source = (JSlider)e.getSource();
				  double warp = (source.getValue()/10.0d);
				    if (!source.getValueIsAdjusting()) {
				    	MIDISequencer.SetWarp(warp);
				    }
			}
		});
		slider.setValue(10);
		  
	}
	
	public static void main(String[] args)
	{
		try {
		    // Set System L&F
			UIManager.setLookAndFeel(org.jvnet.substance.skin.SubstanceCremeLookAndFeel.class.getCanonicalName());	
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    }
	    catch (ClassNotFoundException e) {
	    }
	    catch (InstantiationException e) {
	    }
	    catch (IllegalAccessException e) {
	    }
	    SwingUtilities.invokeLater(new Runnable(){
	    	public void run(){
	    		MainWindow w = new MainWindow();
	    		w.setVisible(true);
	    	}
	    });	    
	
	}
}
