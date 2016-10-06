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

    public static long SEED;
    private static final double INTERVAL = 0.01;
    private static double elapsed;
    private Random random;
    private int lastNote = 0;

    private boolean playing;
    private float lastDelta;

    private Synthesizer synth;
    private MidiChannel[] channels;

    public MusicPlayer(long seed){
        playing = false;

        this.SEED = seed;
        random = new Random(seed);

        elapsed = 0;

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
    }

    public void update(float delta){
        if(playing) {
            if (elapsed >= INTERVAL) {
                elapsed = 0;
                channels[0].noteOff(lastNote,500);
                lastNote = random.nextInt(50);
                channels[0].noteOn(lastNote,500);
            }
            elapsed += Math.abs(delta - lastDelta);
            lastDelta = delta;
        }
    }
}
