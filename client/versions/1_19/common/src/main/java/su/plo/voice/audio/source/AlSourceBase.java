package su.plo.voice.audio.source;

import org.lwjgl.openal.AL11;
import su.plo.voice.api.PlasmoVoiceClient;
import su.plo.voice.api.audio.device.AlAudioDevice;
import su.plo.voice.api.audio.device.AudioDevice;
import su.plo.voice.api.audio.source.AlSource;
import su.plo.voice.api.event.audio.source.AlSourcePauseEvent;
import su.plo.voice.api.event.audio.source.AlSourcePlayEvent;
import su.plo.voice.api.event.audio.source.AlSourceStopEvent;
import su.plo.voice.api.event.audio.source.AlSourceUpdateParamEvent;
import su.plo.voice.api.pos.Pos3d;
import su.plo.voice.audio.AlUtil;

public abstract class AlSourceBase implements AlSource {

    protected final PlasmoVoiceClient client;
    protected final AlAudioDevice device;
    protected final int pointer;
    protected final int format;

    private Pos3d position;

    protected AlSourceBase(PlasmoVoiceClient client, AlAudioDevice device, int pointer) {
        this.client = client;
        this.device = device;
        this.pointer = pointer;
        this.format = AlUtil.getFormatId(device.getFormat().get());
    }

    @Override
    public AudioDevice getDevice() {
        return device;
    }

    @Override
    public long getPointer() {
        return pointer;
    }

    @Override
    public void play() {
        AlUtil.checkDeviceContext(device);

        AlSourcePlayEvent event = new AlSourcePlayEvent(this);
        client.getEventBus().call(event);
        if (event.isCancelled()) return;

        AL11.alSourcePlay(pointer);
        AlUtil.checkErrors("Source pause");
    }

    @Override
    public void stop() {
        AlUtil.checkDeviceContext(device);

        AlSourceStopEvent event = new AlSourceStopEvent(this);
        client.getEventBus().call(event);
        if (event.isCancelled()) return;

        AL11.alSourceStop(pointer);
        AlUtil.checkErrors("Source pause");
    }

    @Override
    public void pause() {
        AlUtil.checkDeviceContext(device);

        AlSourcePauseEvent event = new AlSourcePauseEvent(this);
        client.getEventBus().call(event);
        if (event.isCancelled()) return;

        AL11.alSourcePause(pointer);
        AlUtil.checkErrors("Source pause");
    }

    @Override
    public State getState() {
        return State.fromInt(getInt(AL11.AL_SOURCE_STATE));
    }

    @Override
    public Pos3d getPosition() {
        AlUtil.checkDeviceContext(device);

        return position;
    }

    @Override
    public void setPosition(Pos3d position) {
        AlUtil.checkDeviceContext(device);

        this.position = position;
        setFloatArray(AL11.AL_POSITION, new float[]{
                (float) position.getX(),
                (float) position.getY(),
                (float) position.getZ()
        });
    }

    @Override
    public float getPitch() {
        AlUtil.checkDeviceContext(device);
        return getFloat(AL11.AL_PITCH);
    }

    @Override
    public void setPitch(float pitch) {
        AlUtil.checkDeviceContext(device);
        setFloat(AL11.AL_PITCH, pitch);
    }

    @Override
    public float getVolume() {
        AlUtil.checkDeviceContext(device);
        return getFloat(AL11.AL_GAIN);
    }

    @Override
    public void setVolume(float volume) {
        AlUtil.checkDeviceContext(device);
        setFloat(AL11.AL_GAIN, volume);
    }

    @Override
    public boolean isRelative() {
        AlUtil.checkDeviceContext(device);
        return getInt(AL11.AL_SOURCE_RELATIVE) == 1;
    }

    @Override
    public void setRelative(boolean relative) {
        AlUtil.checkDeviceContext(device);
        setInt(AL11.AL_SOURCE_RELATIVE, relative ? 1 : 0);
    }

    @Override
    public int getInt(int param) {
        AlUtil.checkDeviceContext(device);
        return AL11.alGetSourcei(pointer, param);
    }

    @Override
    public void setInt(int param, int value) {
        AlUtil.checkDeviceContext(device);
        if (!callParamEvent(param, value)) return;
        AL11.alSourcei(pointer, param, value);
    }

    @Override
    public void getIntArray(int param, int[] values) {
        AlUtil.checkDeviceContext(device);
        AL11.alGetSourceiv(pointer, param, values);
    }

    @Override
    public void setIntArray(int param, int[] values) {
        AlUtil.checkDeviceContext(device);
        if (!callParamEvent(param, values)) return;
        AL11.alSourceiv(pointer, param, values);
    }

    @Override
    public float getFloat(int param) {
        AlUtil.checkDeviceContext(device);
        return AL11.alGetSourcef(pointer, param);
    }

    @Override
    public void setFloat(int param, float value) {
        AlUtil.checkDeviceContext(device);
        if (!callParamEvent(param, value)) return;
        AL11.alSourcef(pointer, param, value);
    }

    @Override
    public void getFloatArray(int param, float[] values) {
        AlUtil.checkDeviceContext(device);
        AL11.alGetSourcefv(pointer, param, values);
    }

    @Override
    public void setFloatArray(int param, float[] values) {
        AlUtil.checkDeviceContext(device);
        if (!callParamEvent(param, values)) return;
        AL11.alSourcefv(pointer, param, values);
    }

    private boolean callParamEvent(int param, Object value) {
        AlSourceUpdateParamEvent event = new AlSourceUpdateParamEvent(this, param, value);
        client.getEventBus().call(event);
        return !event.isCancelled();
    }
}
