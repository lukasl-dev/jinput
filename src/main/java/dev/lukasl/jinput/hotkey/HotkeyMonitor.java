package dev.lukasl.jinput.hotkey;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;

public interface HotkeyMonitor {
  @NotNull Hotkey register(int keyCode, @NotNull Set<@NotNull Modifier> modifiers) throws HotkeyRegistrationException;

  @NotNull Hotkey register(int keyCode, @NotNull Modifier... modifiers) throws HotkeyRegistrationException;

  void unregister(@NotNull Hotkey hotkey) throws HotkeyRegistrationException;

  void setOnPress(@NotNull Hotkey hotkey, @NotNull Consumer<@NotNull Hotkey> consumer);

  void start();
}
