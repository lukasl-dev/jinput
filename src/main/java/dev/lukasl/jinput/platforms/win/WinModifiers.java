package dev.lukasl.jinput.platforms.win;

import dev.lukasl.jinput.hotkey.Modifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

final class WinModifiers {
  private static final Map<Modifier, Integer> modifiers = new HashMap<Modifier, Integer>() {{
    this.put(Modifier.CONTROL, 0x0002);
    this.put(Modifier.COMMAND, 0x0008);
    this.put(Modifier.SHIFT, 0x0004);
    this.put(Modifier.OPTION, 0x0001);
    this.put(Modifier.NO_REPEAT, 0x4000);
  }};

  static boolean supported(@NotNull Modifier modifier) {
    return WinModifiers.modifiers.containsKey(modifier);
  }

  static int toInt(@NotNull Modifier modifier) {
    if (!WinModifiers.supported(modifier)) {
      throw new IllegalArgumentException(String.format("Modifier %s is not supported on windows.", modifier));
    }
    return WinModifiers.modifiers.get(modifier);
  }
}
