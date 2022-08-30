package su.plo.voice.api.server.audio.capture;

import org.jetbrains.annotations.NotNull;
import su.plo.voice.proto.data.capture.Activation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// todo: doc
public interface ActivationManager {

    @NotNull ServerActivation getProximityActivation();

    Optional<ServerActivation> getActivationById(@NotNull UUID id);

    Optional<ServerActivation> getActivationByName(@NotNull String name);

    Collection<ServerActivation> getActivations();

    @NotNull ServerActivation register(@NotNull String name, List<Integer> distances, int defaultDistance, boolean transitive, Activation.Order order);

    boolean unregister(@NotNull UUID id);

    boolean unregister(@NotNull String name);

    boolean unregister(@NotNull ServerActivation activation);
}
