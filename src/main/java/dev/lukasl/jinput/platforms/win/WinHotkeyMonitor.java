package dev.lukasl.jinput.platforms.win;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinUser;
import dev.lukasl.jinput.hotkey.Hotkey;
import dev.lukasl.jinput.hotkey.HotkeyMonitor;
import dev.lukasl.jinput.hotkey.HotkeyRegistrationException;
import dev.lukasl.jinput.hotkey.Modifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WinHotkeyMonitor implements HotkeyMonitor {
  private final @NotNull User32 user32;

  private final Map<Integer, Hotkey> localHotkeys = new HashMap<>();
  private final Map<Integer, Consumer<Hotkey>> listeners = new HashMap<>();

  public WinHotkeyMonitor() {
    this(User32.INSTANCE);
  }

  public WinHotkeyMonitor(@NotNull User32 user32) {
    this.user32 = user32;
  }

  @Override
  public @NotNull Hotkey register(int keyCode, @NotNull Set<@NotNull Modifier> modifiers) throws HotkeyRegistrationException {
    Hotkey hotkey = new Hotkey(keyCode, modifiers);
    try {
      this.user32.RegisterHotKey(null, hotkey.hashCode(), this.combine(modifiers), keyCode);
    } catch (Exception exception) {
      throw new HotkeyRegistrationException("Could not register hotkey.", exception);
    }
    this.localHotkeys.put(hotkey.hashCode(), hotkey);
    return hotkey;
  }

  @Override
  public @NotNull Hotkey register(int keyCode, @NotNull Modifier... modifiers) throws HotkeyRegistrationException {
    return this.register(keyCode, Arrays.stream(modifiers).collect(Collectors.toSet()));
  }

  @Override
  public void unregister(@NotNull Hotkey hotkey) throws HotkeyRegistrationException {
    try {
      this.user32.UnregisterHotKey(null, hotkey.hashCode());
    } catch (Exception exception) {
      throw new HotkeyRegistrationException("Could unregister hotkey.", exception);
    }
  }

  @Override
  public void setOnPress(@NotNull Hotkey hotkey, @NotNull Consumer<@NotNull Hotkey> consumer) {
    this.listeners.put(hotkey.hashCode(), consumer);
  }

  private int combine(@NotNull Collection<@NotNull Modifier> modifiers) {
    return modifiers.stream()
      .filter(WinModifiers::supported)
      .map(WinModifiers::toInt)
      .reduce((i, j) -> i | j)
      .orElse(0);
  }

  @Override
  public void start() {
    WinUser.MSG message = new WinUser.MSG();
    int bRet;
    while ((bRet = User32.INSTANCE.GetMessage(message, null, 0, 0)) != 0) {
      if (bRet == -1) {
        break;
      }
      if (message.message == WinUser.WM_HOTKEY) {
        this.handleMessage(message);
      }
    }
  }

  private void handleMessage(@NotNull WinUser.MSG message) {
    int hotkeyId = message.wParam.intValue();
    this.intercept(hotkeyId);
  }

  private void intercept(int hotkeyId) {
    Hotkey hotkey = this.localHotkeys.get(hotkeyId);
    Consumer<Hotkey> consumer = this.listeners.get(hotkeyId);
    if (hotkey != null && consumer != null) {
      consumer.accept(hotkey);
    }
  }
}
