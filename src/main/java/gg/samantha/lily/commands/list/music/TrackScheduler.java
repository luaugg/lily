package gg.samantha.lily.commands.list.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);
    private final Queue<AudioTrack> trackQueue = new LinkedList<>();

    public void queueTrack(@NotNull AudioTrack track) {
        trackQueue.add(track);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // todo add impl
    }

    @Override
    public void onTrackEnd(@NotNull AudioPlayer player, @NotNull AudioTrack track,
                           @NotNull AudioTrackEndReason endReason) {
        if (endReason.mayStartNext)
            player.playTrack(trackQueue.poll());
    }

    @Override
    public void onTrackException(@NotNull AudioPlayer player, @NotNull AudioTrack track,
                                 @NotNull FriendlyException exception) {

        LOGGER.error("track exception occurred", exception);
        player.playTrack(trackQueue.poll());
    }

    @Override
    public void onTrackStuck(@NotNull AudioPlayer player, @NotNull AudioTrack track, long thresholdMs) {
        if (thresholdMs > 400) {
            LOGGER.warn("track got stuck: " + thresholdMs);
            player.playTrack(trackQueue.poll());
        }
    }
}
