import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelData;
public class ReflectTime {
  public static void main(String[] args) {
    for (var m : ServerLevel.class.getMethods()) {
      if (m.getName().toLowerCase().contains("time") || m.getName().toLowerCase().contains("day")) System.out.println("ServerLevel: " + m);
    }
    for (var m : LevelData.class.getMethods()) {
      if (m.getName().toLowerCase().contains("time") || m.getName().toLowerCase().contains("day")) System.out.println("LevelData: " + m);
    }
  }
}
