package com.minty.leemonmc.games.util;

import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.Packet;
import net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R2.PacketPlayOutScoreboardTeam;

public class ScoreboardTeam {
  private String name;
  
  private String prefix;
  
  public ScoreboardTeam(String name, String prefix) {
    this.name = name;
    this.prefix = prefix;
  }
  
  private PacketPlayOutScoreboardTeam createPacket(int mode) {
    PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
    setField(packet, "a", this.name);
    setField(packet, "b", "");
    setField(packet, "c", this.prefix);
    setField(packet, "d", "");
    setField(packet, "i", Integer.valueOf(0));
    setField(packet, "e", "always");
    setField(packet, "g", Integer.valueOf(0));
    setField(packet, "i", Integer.valueOf(mode));
    return packet;
  }
  
  public PacketPlayOutScoreboardTeam createTeam() {
    return createPacket(0);
  }
  
  public PacketPlayOutScoreboardTeam updateTeam() {
    return createPacket(2);
  }
  
  public PacketPlayOutScoreboardTeam removeTeam() {
    PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
    setField(packet, "a", this.name);
    setField(packet, "i", Integer.valueOf(1));
    return packet;
  }
  
  public PacketPlayOutScoreboardTeam setFriendlyFire(boolean v) {
    PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
    setField(packet, "i", Integer.valueOf(v ? 1 : 0));
    return packet;
  }
  
  public PacketPlayOutScoreboardTeam addOrRemovePlayer(int mode, String playerName) {
    PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
    setField(packet, "a", this.name);
    setField(packet, "i", Integer.valueOf(mode));
    try {
      Field f = packet.getClass().getDeclaredField("h");
      f.setAccessible(true);
      ((List<String>)f.get(packet)).add(playerName);
    } catch (NoSuchFieldException|IllegalAccessException e) {
      e.printStackTrace();
    } 
    return packet;
  }
  
  public static PacketPlayOutPlayerInfo updateDisplayName(EntityPlayer player) {
    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, new EntityPlayer[] { player });
    player.playerConnection.sendPacket((Packet)packet);
    return packet;
  }
  
  private void setField(Object edit, String fieldName, Object value) {
    try {
      Field field = edit.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(edit, value);
    } catch (NoSuchFieldException|IllegalAccessException e) {
      e.printStackTrace();
    } 
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getPrefix() {
    return this.prefix;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
