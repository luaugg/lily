package gg.samantha.lily.commands.list.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
    private static final Map<String, PlayerWrapper> PLAYER_MAP = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioManager.class);

    private AudioManager() { }

    @NotNull
    @CheckReturnValue
    public static PlayerWrapper computePlayerWrapper(@NotNull String guildId) {
        return PLAYER_MAP.computeIfAbsent(guildId, ignored -> {
            final var audioPlayer = AUDIO_PLAYER_MANAGER.createPlayer();
            final var trackScheduler = new TrackScheduler();
            audioPlayer.addListener(trackScheduler);
            return new PlayerWrapper(audioPlayer, trackScheduler);
        });
    }

    public static void loadTrack(@NotNull String guildId, @NotNull String identifier) {
        final var playerWrapper = computePlayerWrapper(guildId);

        AUDIO_PLAYER_MANAGER.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@NotNull AudioTrack track) {
                playerWrapper.trackScheduler().queueTrack(track);
            }

            @Override
            public void playlistLoaded(@NotNull AudioPlaylist playlist) {
                for (final var track : playlist.getTracks())
                    trackLoaded(track);
            }

            @Override
            public void noMatches() {
                // todo: impl
            }

            @Override
            public void loadFailed(@NotNull FriendlyException exception) {
                LOGGER.error("audio track load fail", exception);
                // todo: send message to user
            }
        });
    }

    static {
        AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
        AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager(true));
        AUDIO_PLAYER_MANAGER.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
    }
}
