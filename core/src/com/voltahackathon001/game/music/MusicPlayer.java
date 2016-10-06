package com.voltahackathon001.game.music;

import java.util.Random;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * Created by Nick on 10/5/2016.
 */
public class MusicPlayer {

    // Notes are C, D, E, G, A, C1 (pentatonic scale)
    public static final float[] NOTES = {16.35f, 18.35f, 20.60f, 24.50f, 27.50f, 32.70f};
    public static final int OCTAVE = 2;

    public long SEED;
    private final long MAIN_INTERVAL_MS = 3600;
    private long elapsed;
    private long lastTime;
    private Random random;
    private int lastNote = 0;
    // the number of notes to play in a beat
    private int notes;
    private int notesLeft;

    private boolean playing;

    private Synthesizer synth;
    private MidiChannel[] channels;

    public MusicPlayer(long seed){
        playing = false;

        this.SEED = seed;
        random = new Random(seed);

        elapsed = 0;
        lastTime = System.currentTimeMillis();

        notes = (int)Math.pow(2, random.nextInt(3)+1);
        notesLeft = notes;

        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
            synth.loadInstrument(instruments[90]);
        }catch(MidiUnavailableException e){
            e.printStackTrace();
        }
    }

    // start the music
    public void pumpUpTheMusic(){
        playing = true;
    }

    // stop the music
    public void turnDownForWhat(){
        playing = false;
        channels[0].noteOff(lastNote);
    }

    // toggle music on/off
    public void switchItUp(){
        if(playing){
            turnDownForWhat();
        }else{
            pumpUpTheMusic();
        }
    }

    public void update(){
        if(playing) {
            if (elapsed >= MAIN_INTERVAL_MS/notes) {
                notesLeft--;
                if(notesLeft == 0){
                    // decide how many notes to play this time from {1,2,4}
                    notes = (int)Math.pow(2, random.nextInt(3));
                    notesLeft = notes;
                }
                elapsed = 0;
                channels[0].noteOff(lastNote);
                lastNote = (int)(NOTES[random.nextInt(6)]*OCTAVE);
                channels[0].setChannelPressure(0);
                channels[0].noteOn(lastNote,50);
            }
            long nowTime = System.currentTimeMillis();
            elapsed += nowTime - lastTime;
            lastTime = nowTime;
        }
    }
}
