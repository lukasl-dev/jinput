package dev.lukasl.jinput.hotkey;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Data
public class Hotkey {
  private final int keyCode;
  private final @NotNull Set<@NotNull Modifier> modifiers;
}
