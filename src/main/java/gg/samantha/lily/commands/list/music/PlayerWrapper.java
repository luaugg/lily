package gg.samantha.lily.commands.list.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;

public class PlayerWrapper {
    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;

    public PlayerWrapper(@NotNull AudioPlayer audioPlayer, @NotNull TrackScheduler trackScheduler) {
        this.audioPlayer = audioPlayer;
        this.trackScheduler = trackScheduler;
    }

    @NotNull
    @CheckReturnValue
    public AudioPlayer audioPlayer() {
        return audioPlayer;
    }

    @NotNull
    @CheckReturnValue
    public TrackScheduler trackScheduler() {
        return trackScheduler;
    }
}
